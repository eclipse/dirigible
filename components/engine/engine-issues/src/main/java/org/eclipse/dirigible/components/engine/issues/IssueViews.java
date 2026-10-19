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

import java.util.List;
import java.util.Map;

/**
 * What the issues endpoints read and answer. Timestamps travel as ISO-8601 text; a storage
 * reference never leaves the server.
 */
final class IssueViews {

    private IssueViews() {}

    /**
     * A new issue, as the report form sends it.
     *
     * @param subject the subject
     * @param description what happened and what was expected
     * @param severity the severity, {@code MEDIUM} when absent
     * @param category the category
     * @param context the context bundle the reporter confirmed: request ids, URL, shell, entity, build,
     *        browser, the failed request, the console tail
     */
    record IssueDraft(String subject, String description, IssueSeverity severity, String category, Map<String, Object> context) {
    }

    /**
     * A message to post.
     *
     * @param body the text
     * @param internal whether it is an engineers' memo, absent for a reply; ignored for a reporter
     */
    record MessageDraft(String body, Boolean internal) {

        boolean isInternal() {
            return Boolean.TRUE.equals(internal);
        }
    }

    /**
     * An operator's decision.
     *
     * @param decision the decision
     * @param message what to tell the reporter, required when asking them for something
     */
    record DecisionDraft(IssueDecision decision, String message) {
    }

    /**
     * An assignment.
     *
     * @param assignee the engineer, blank to unassign
     */
    record AssignmentDraft(String assignee) {
    }

    /**
     * The request ids whose log excerpt a reporter previews.
     *
     * @param requestIds the request ids
     */
    record ExcerptRequest(List<String> requestIds) {
    }

    /**
     * An issue in a list.
     *
     * @param id the id
     * @param tenantId the tenant it was filed in
     * @param reporter the reporter's identity
     * @param reporterName the reporter's display name
     * @param subject the subject
     * @param severity the severity
     * @param category the category
     * @param status the status
     * @param assignee the assigned engineer
     * @param createdAt when it was filed
     * @param updatedAt when it last changed
     */
    record Summary(Long id, String tenantId, String reporter, String reporterName, String subject, IssueSeverity severity, String category,
            IssueStatus status, String assignee, String createdAt, String updatedAt) {

        static Summary of(Issue issue) {
            return new Summary(issue.getId(), issue.getTenantId(), issue.getReporter(), issue.getReporterName(), issue.getSubject(),
                    issue.getSeverity(), issue.getCategory(), issue.getStatus(), issue.getAssignee(), issue.getCreatedAt()
                                                                                                           .toString(),
                    issue.getUpdatedAt()
                         .toString());
        }
    }

    /**
     * An issue with its thread.
     *
     * @param issue the issue
     * @param description the description
     * @param context the stored context bundle
     * @param processInstanceId the lifecycle instance, for an operator; {@code null} for a reporter
     * @param messages the thread the caller may read
     * @param attachments the attachments the caller may read
     */
    record Detail(Summary issue, String description, Object context, String processInstanceId, List<Message> messages,
            List<Attachment> attachments) {
    }

    /**
     * One message.
     *
     * @param id the id
     * @param sequence the position in the thread
     * @param authorKind which side wrote it
     * @param author who wrote it
     * @param body the text
     * @param internal whether it is an engineers' memo
     * @param createdAt when
     */
    record Message(Long id, int sequence, IssueAuthorKind authorKind, String author, String body, boolean internal, String createdAt) {

        static Message of(IssueMessage message) {
            return new Message(message.getId(), message.getSequence(), message.getAuthorKind(), message.getAuthor(), message.getBody(),
                    message.isInternal(), message.getCreatedAt()
                                                 .toString());
        }
    }

    /**
     * One attachment.
     *
     * @param id the id
     * @param messageId the message it belongs to
     * @param fileName the file name
     * @param mimeType the MIME type
     * @param size the size in bytes
     * @param sha256 the content's SHA-256
     * @param uploadedBy who uploaded it
     * @param uploadedAt when
     */
    record Attachment(Long id, Long messageId, String fileName, String mimeType, long size, String sha256, String uploadedBy,
            String uploadedAt) {

        static Attachment of(IssueAttachment attachment) {
            return new Attachment(attachment.getId(), attachment.getMessageId(), attachment.getFileName(), attachment.getMimeType(),
                    attachment.getSize(), attachment.getSha256(), attachment.getUploadedBy(), attachment.getUploadedAt()
                                                                                                        .toString());
        }
    }

    /**
     * A log line of the reporter's own requests, redacted.
     *
     * @param at when
     * @param requestId the request
     * @param level the level
     * @param logger the logger
     * @param message the message
     */
    record ExcerptLine(String at, String requestId, String level, String logger, String message) {
    }

    /**
     * An attachment's content.
     *
     * @param fileName the file name
     * @param mimeType the MIME type
     * @param content the bytes
     */
    record Download(String fileName, String mimeType, byte[] content) {
    }
}
