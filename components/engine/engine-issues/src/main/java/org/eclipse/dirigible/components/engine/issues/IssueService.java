/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Collection;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.api.cms.AttachmentsFacade.StoredAttachment;
import org.eclipse.dirigible.components.base.logging.RequestLogBuffer;
import org.eclipse.dirigible.components.engine.issues.IssueCaller.Caller;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Attachment;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Detail;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Download;
import org.eclipse.dirigible.components.engine.issues.IssueViews.ExcerptLine;
import org.eclipse.dirigible.components.engine.issues.IssueViews.IssueDraft;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Message;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * The issues engine (#7948): the only writer of an issue, its thread, its attachments and its
 * status, and the only one that moves its lifecycle process - both in one transaction, since
 * Flowable runs on the same SystemDB transaction manager.
 *
 * <p>
 * Every reporter-side method takes the {@link Caller} and finds the issue by id, tenant AND
 * reporter, so an issue of another user or another tenant is answered exactly like a missing one:
 * 404. The operator-side methods are reached only through the operator endpoint.
 */
@Service
class IssueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueService.class);

    /** The longest description or message. */
    private static final int TEXT_LENGTH = 20_000;

    /** How many request ids a report may name for its log excerpt. */
    private static final int MAX_REQUEST_IDS = 20;

    /** The context property listing the report's request ids. */
    static final String CONTEXT_REQUEST_IDS = "requestIds";

    /** The context property the server writes the log excerpt into. */
    static final String CONTEXT_LOG_EXCERPT = "logExcerpt";

    private static final JsonMapper JSON = JsonMapper.builder()
                                                     .build();

    private final IssueRepository issues;

    private final IssueMessageRepository messages;

    private final IssueAttachmentRepository attachments;

    private final IssueLifecycle lifecycle;

    private final IssueAttachmentStore attachmentStore;

    IssueService(IssueRepository issues, IssueMessageRepository messages, IssueAttachmentRepository attachments, IssueLifecycle lifecycle,
            IssueAttachmentStore attachmentStore) {
        this.issues = issues;
        this.messages = messages;
        this.attachments = attachments;
        this.lifecycle = lifecycle;
        this.attachmentStore = attachmentStore;
    }

    // ---- reporter -------------------------------------------------------------------------------

    /**
     * Files an issue and starts its lifecycle. The context is redacted, and the log excerpt of the
     * caller's own request ids is added to it unless {@code DIRIGIBLE_ISSUES_BUNDLE_LOGS} is off.
     *
     * @param caller the reporter
     * @param draft the report
     * @return the issue
     */
    @Transactional
    Issue file(Caller caller, IssueDraft draft) {
        String subject = required(draft.subject(), "subject", Issue.SUBJECT_LENGTH);
        String description = optional(draft.description(), "description", TEXT_LENGTH);
        String category = optional(draft.category(), "category", Issue.CATEGORY_LENGTH);
        IssueSeverity severity = draft.severity() == null ? IssueSeverity.MEDIUM : draft.severity();

        Map<String, Object> context = new LinkedHashMap<>();
        if (draft.context() != null) {
            context.putAll(redactedMap(draft.context()));
        }
        context.remove(CONTEXT_LOG_EXCERPT);
        List<ExcerptLine> excerpt = excerpt(caller, requestIdsOf(context));
        if (!excerpt.isEmpty()) {
            context.put(CONTEXT_LOG_EXCERPT, excerpt);
        }

        Issue issue = issues.save(new Issue(caller.tenantId(), caller.principal(), caller.principal(), subject, description, severity,
                category, toJson(context), Instant.now()));
        issue.linkProcess(lifecycle.start(issue));
        LOGGER.info("Issue [{}] filed in tenant [{}] by [{}]", issue.getId(), issue.getTenantId(), issue.getReporter());
        return issue;
    }

    /**
     * The log lines the caller's own requests wrote, redacted - what a report carries, shown to the
     * reporter before sending.
     *
     * @param caller the caller
     * @param requestIds the request ids
     * @return the lines, empty when log bundling is off
     */
    List<ExcerptLine> excerpt(Caller caller, Collection<String> requestIds) {
        if (!DirigibleConfig.ISSUES_BUNDLE_LOGS.getBooleanValue() || requestIds.isEmpty()) {
            return List.of();
        }
        Set<String> wanted = requestIds.stream()
                                       .limit(MAX_REQUEST_IDS)
                                       .collect(Collectors.toSet());
        return RequestLogBuffer.get()
                               .excerpt(wanted, caller.tenantId(), caller.principal())
                               .stream()
                               .map(line -> new ExcerptLine(line.at()
                                                                .toString(),
                                       line.requestId(), line.level(), line.logger(), IssueContextRedactor.redactText(line.message())))
                               .toList();
    }

    /**
     * The caller's issues in the current tenant, latest change first.
     *
     * @param caller the reporter
     * @return the issues
     */
    @Transactional(readOnly = true)
    List<Summary> mine(Caller caller) {
        return issues.findByTenantIdAndReporterOrderByUpdatedAtDescIdDesc(caller.tenantId(), caller.principal())
                     .stream()
                     .map(Summary::of)
                     .toList();
    }

    /**
     * One of the caller's issues, with the thread a reporter may read: no memo, and no attachment of a
     * memo.
     *
     * @param caller the reporter
     * @param id the issue
     * @return the issue
     */
    @Transactional(readOnly = true)
    Detail mine(Caller caller, Long id) {
        Issue issue = own(caller, id);
        List<IssueMessage> thread = messages.findByIssueIdAndInternalFalseOrderBySequenceAsc(id);
        Set<Long> visible = thread.stream()
                                  .map(IssueMessage::getId)
                                  .collect(Collectors.toSet());
        List<Attachment> files = attachments.findByIssueIdOrderByIdAsc(id)
                                            .stream()
                                            .filter(file -> file.getMessageId() == null || visible.contains(file.getMessageId()))
                                            .map(Attachment::of)
                                            .toList();
        return new Detail(Summary.of(issue), issue.getDescription(), fromJson(issue.getContext()), null, thread.stream()
                                                                                                               .map(Message::of)
                                                                                                               .toList(),
                files);
    }

    /**
     * Posts the reporter's message. An issue waiting for its reporter goes back to work.
     *
     * @param caller the reporter
     * @param id the issue
     * @param body the text
     * @return the message
     */
    @Transactional
    Message answer(Caller caller, Long id, String body) {
        Issue issue = own(caller, id);
        refuseClosed(issue);
        IssueMessage message = append(issue, IssueAuthorKind.REPORTER, caller.principal(), body, false);
        if (issue.getStatus() == IssueStatus.WAITING_FOR_REPORTER) {
            lifecycle.resume(issue);
            issue.moveTo(IssueStatus.OPEN, message.getCreatedAt());
        }
        return Message.of(message);
    }

    /**
     * Withdraws an issue.
     *
     * @param caller the reporter
     * @param id the issue
     * @return the issue
     */
    @Transactional
    Summary withdraw(Caller caller, Long id) {
        Issue issue = own(caller, id);
        refuseClosed(issue);
        lifecycle.cancel(issue, "Withdrawn by its reporter");
        issue.moveTo(IssueStatus.CANCELLED, Instant.now());
        return Summary.of(issue);
    }

    /**
     * Attaches a file of the reporter's, to the issue or to one of the reporter-visible messages.
     *
     * @param caller the reporter
     * @param id the issue
     * @param messageId the message, {@code null} for the issue itself
     * @param fileName the file name
     * @param mimeType the MIME type
     * @param content the bytes
     * @return the attachment
     */
    @Transactional
    Attachment attach(Caller caller, Long id, Long messageId, String fileName, String mimeType, byte[] content) {
        Issue issue = own(caller, id);
        refuseClosed(issue);
        if (messageId != null && messages.findByIdAndIssueId(messageId, id)
                                         .filter(message -> !message.isInternal())
                                         .isEmpty()) {
            throw notFound("message", messageId);
        }
        return store(issue, messageId, fileName, mimeType, content, caller.principal());
    }

    /**
     * Reads an attachment the reporter may see.
     *
     * @param caller the reporter
     * @param id the issue
     * @param attachmentId the attachment
     * @return the content
     */
    @Transactional(readOnly = true)
    Download download(Caller caller, Long id, Long attachmentId) {
        Issue issue = own(caller, id);
        IssueAttachment attachment = attachments.findByIdAndIssueId(attachmentId, issue.getId())
                                                .filter(file -> file.getMessageId() == null
                                                        || messages.findByIdAndIssueId(file.getMessageId(), issue.getId())
                                                                   .filter(message -> !message.isInternal())
                                                                   .isPresent())
                                                .orElseThrow(() -> notFound("attachment", attachmentId));
        return read(attachment);
    }

    // ---- operator -------------------------------------------------------------------------------

    /**
     * Every tenant's issues, filtered.
     *
     * @param tenantId the tenant, {@code null} for all
     * @param status the status, {@code null} for all
     * @param assignee the assignee, {@code null} for all
     * @param limit how many
     * @return the issues, latest change first
     */
    @Transactional(readOnly = true)
    List<Summary> search(String tenantId, IssueStatus status, String assignee, int limit) {
        return issues.search(blankToNull(tenantId), status, blankToNull(assignee), PageRequest.of(0, Math.max(1, limit)))
                     .stream()
                     .map(Summary::of)
                     .toList();
    }

    /**
     * An issue with its whole thread, memos included.
     *
     * @param id the issue
     * @return the issue
     */
    @Transactional(readOnly = true)
    Detail detail(Long id) {
        Issue issue = find(id);
        return new Detail(Summary.of(issue), issue.getDescription(), fromJson(issue.getContext()), issue.getProcessInstanceId(),
                messages.findByIssueIdOrderBySequenceAsc(id)
                        .stream()
                        .map(Message::of)
                        .toList(),
                attachments.findByIssueIdOrderByIdAsc(id)
                           .stream()
                           .map(Attachment::of)
                           .toList());
    }

    /**
     * The issue a lifecycle task belongs to - how the Inbox's form link finds its issue.
     *
     * @param processInstanceId the process instance
     * @return the issue
     */
    @Transactional(readOnly = true)
    Summary byProcessInstance(String processInstanceId) {
        return issues.findByProcessInstanceId(processInstanceId)
                     .map(Summary::of)
                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                             "No issue runs in process instance [" + processInstanceId + "]"));
    }

    /**
     * Assigns an issue, and its waiting task, to an engineer.
     *
     * @param id the issue
     * @param assignee the engineer, blank to unassign
     * @return the issue
     */
    @Transactional
    Summary assign(Long id, String assignee) {
        Issue issue = find(id);
        refuseClosed(issue);
        String engineer = optional(assignee, "assignee", 320);
        lifecycle.assign(issue, engineer);
        issue.assignTo(engineer, Instant.now());
        return Summary.of(issue);
    }

    /**
     * Records an operator's decision and moves the lifecycle on. A message, when given, is posted to
     * the reporter first; asking the reporter for something requires one.
     *
     * @param operator the engineer
     * @param id the issue
     * @param decision the decision
     * @param message what to tell the reporter
     * @return the issue
     */
    @Transactional
    Summary decide(String operator, Long id, IssueDecision decision, String message) {
        if (decision == null) {
            throw badRequest("A decision is required: one of CONFIRM, REJECT, RESOLVE, REQUEST");
        }
        Issue issue = find(id);
        String task = decision.taskFor(issue.getStatus());
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Issue [" + id + "] is " + issue.getStatus() + " and cannot be decided " + decision + " now");
        }
        String text = optional(message, "message", TEXT_LENGTH);
        if (decision == IssueDecision.REQUEST && text == null) {
            throw badRequest("Asking the reporter for something needs a message saying what");
        }
        if (text != null) {
            append(issue, IssueAuthorKind.ENGINEER, operator, text, false);
        }
        lifecycle.complete(issue, task, decision);
        issue.moveTo(decision.outcome(), Instant.now());
        LOGGER.info("Issue [{}] decided [{}] by [{}], now [{}]", id, decision, operator, issue.getStatus());
        return Summary.of(issue);
    }

    /**
     * Posts an engineer's reply to the reporter, or an internal memo.
     *
     * @param operator the engineer
     * @param id the issue
     * @param body the text
     * @param internal whether it is a memo the reporter never sees
     * @return the message
     */
    @Transactional
    Message reply(String operator, Long id, String body, boolean internal) {
        Issue issue = find(id);
        if (!internal) {
            refuseClosed(issue);
        }
        return Message.of(append(issue, IssueAuthorKind.ENGINEER, operator, body, internal));
    }

    /**
     * Attaches an engineer's file, to the issue or to any of its messages.
     *
     * @param operator the engineer
     * @param id the issue
     * @param messageId the message, {@code null} for the issue itself
     * @param fileName the file name
     * @param mimeType the MIME type
     * @param content the bytes
     * @return the attachment
     */
    @Transactional
    Attachment attachAsEngineer(String operator, Long id, Long messageId, String fileName, String mimeType, byte[] content) {
        Issue issue = find(id);
        if (messageId != null && messages.findByIdAndIssueId(messageId, id)
                                         .isEmpty()) {
            throw notFound("message", messageId);
        }
        return store(issue, messageId, fileName, mimeType, content, operator);
    }

    /**
     * Reads any attachment of an issue.
     *
     * @param id the issue
     * @param attachmentId the attachment
     * @return the content
     */
    @Transactional(readOnly = true)
    Download downloadAsEngineer(Long id, Long attachmentId) {
        return read(attachments.findByIdAndIssueId(attachmentId, find(id).getId())
                               .orElseThrow(() -> notFound("attachment", attachmentId)));
    }

    // ---- shared ---------------------------------------------------------------------------------

    private Issue own(Caller caller, Long id) {
        return issues.findByIdAndTenantIdAndReporter(id, caller.tenantId(), caller.principal())
                     .orElseThrow(() -> notFound("issue", id));
    }

    private Issue find(Long id) {
        return issues.findById(id)
                     .orElseThrow(() -> notFound("issue", id));
    }

    private IssueMessage append(Issue issue, IssueAuthorKind authorKind, String author, String body, boolean internal) {
        String text = required(body, "body", TEXT_LENGTH);
        Instant now = Instant.now();
        IssueMessage message = messages.save(
                new IssueMessage(issue.getId(), messages.lastSequence(issue.getId()) + 1, authorKind, author, text, internal, now));
        issue.touch(now);
        return message;
    }

    private Attachment store(Issue issue, Long messageId, String fileName, String mimeType, byte[] content, String uploadedBy) {
        if (content.length == 0) {
            throw badRequest("The attachment is empty");
        }
        int limit = DirigibleConfig.ISSUES_ATTACHMENT_MAX_BYTES.getIntValue();
        if (content.length > limit) {
            throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE,
                    "The attachment is " + content.length + " bytes; an issue attachment may be at most " + limit);
        }
        String name = required(fileName, "file name", 255);
        StoredAttachment stored;
        try {
            stored = attachmentStore.store(name, mimeType, content);
        } catch (IOException e) {
            throw new IllegalStateException("Could not store the attachment [" + name + "] of issue [" + issue.getId() + "]", e);
        }
        try {
            IssueAttachment attachment = attachments.saveAndFlush(new IssueAttachment(issue.getId(), messageId, stored.fileName(),
                    stored.contentType(), stored.size(), sha256(content), stored.path(), uploadedBy, Instant.now()));
            issue.touch(attachment.getUploadedAt());
            return Attachment.of(attachment);
        } catch (RuntimeException e) {
            discard(stored.path(), e);
            throw e;
        }
    }

    private void discard(String storageReference, RuntimeException cause) {
        try {
            attachmentStore.delete(storageReference);
        } catch (IOException e) {
            cause.addSuppressed(e);
            LOGGER.warn("Could not remove the attachment [{}] whose row was not saved", storageReference, e);
        }
    }

    private Download read(IssueAttachment attachment) {
        try {
            return new Download(attachment.getFileName(), attachment.getMimeType(), attachmentStore.read(attachment.getStorageReference()));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "The content of attachment [" + attachment.getId() + "] of issue [" + attachment.getIssueId() + "] is missing", e);
        }
    }

    private static void refuseClosed(Issue issue) {
        if (issue.getStatus()
                 .isClosed()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Issue [" + issue.getId() + "] is " + issue.getStatus());
        }
    }

    private static List<String> requestIdsOf(Map<String, Object> context) {
        if (context.get(CONTEXT_REQUEST_IDS) instanceof List<?> ids) {
            return ids.stream()
                      .filter(String.class::isInstance)
                      .map(String.class::cast)
                      .distinct()
                      .toList();
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> redactedMap(Map<String, Object> context) {
        return (Map<String, Object>) IssueContextRedactor.redact(context);
    }

    private static String toJson(Map<String, Object> context) {
        try {
            return JSON.writeValueAsString(context);
        } catch (JacksonException e) {
            throw badRequest("The context of the report cannot be stored as JSON: " + e.getOriginalMessage());
        }
    }

    private static Object fromJson(String context) {
        if (context == null) {
            return Map.of();
        }
        return JSON.readValue(context, Object.class);
    }

    private static String sha256(byte[] content) {
        try {
            return HexFormat.of()
                            .formatHex(MessageDigest.getInstance("SHA-256")
                                                    .digest(content));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Every Java platform provides SHA-256", e);
        }
    }

    private static String required(String value, String name, int maxLength) {
        String text = optional(value, name, maxLength);
        if (text == null) {
            throw badRequest("The " + name + " is required");
        }
        return text;
    }

    private static String optional(String value, String name, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String text = value.strip();
        if (text.length() > maxLength) {
            throw badRequest("The " + name + " is " + text.length() + " characters; at most " + maxLength + " are allowed");
        }
        return text;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private static ResponseStatusException badRequest(String reason) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
    }

    private static ResponseStatusException notFound(String what, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No " + what + " [" + id + "]");
    }
}
