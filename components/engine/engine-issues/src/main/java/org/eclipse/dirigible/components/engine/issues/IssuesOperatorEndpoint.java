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
import java.util.List;

import org.eclipse.dirigible.components.base.http.roles.Roles.RoleNames;
import org.eclipse.dirigible.components.engine.issues.IssueCaller.Caller;
import org.eclipse.dirigible.components.engine.issues.IssueViews.AssignmentDraft;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Attachment;
import org.eclipse.dirigible.components.engine.issues.IssueViews.DecisionDraft;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Detail;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Message;
import org.eclipse.dirigible.components.engine.issues.IssueViews.MessageDraft;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Summary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.security.RolesAllowed;

/**
 * The support desk of the instance (#7948): every tenant's issues, assignment, the operator's
 * decisions, replies to the reporter and internal memos.
 *
 * <p>
 * Reserved to the instance's operators: ADMINISTRATOR or OPERATOR, working in the default tenant. A
 * tenant's own administrator holds ADMINISTRATOR in that tenant only and must not read the other
 * tenants' reports, so a request running in any other tenant is refused.
 */
@RestController
@RequestMapping(IssuesOperatorEndpoint.PATH)
@RolesAllowed({RoleNames.ADMINISTRATOR, RoleNames.OPERATOR})
class IssuesOperatorEndpoint {

    /** The endpoint's path. */
    static final String PATH = IssuesEndpoint.PATH + "/operator";

    private static final String DEFAULT_LIMIT = "200";

    private final IssueService service;

    private final IssueCaller caller;

    IssuesOperatorEndpoint(IssueService service, IssueCaller caller) {
        this.service = service;
        this.caller = caller;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<Summary> search(@RequestParam(name = "tenant", required = false) String tenantId,
            @RequestParam(name = "status", required = false) IssueStatus status,
            @RequestParam(name = "assignee", required = false) String assignee,
            @RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit) {
        operator();
        return service.search(tenantId, status, assignee, limit);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Detail one(@PathVariable("id") Long id) {
        operator();
        return service.detail(id);
    }

    /**
     * The issue a lifecycle process instance belongs to - what the Inbox's task form link resolves.
     *
     * @param processInstanceId the process instance
     * @return the issue
     */
    @GetMapping(path = "/process/{processInstanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Summary byProcessInstance(@PathVariable("processInstanceId") String processInstanceId) {
        operator();
        return service.byProcessInstance(processInstanceId);
    }

    @PostMapping(path = "/{id}/assignee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Summary assign(@PathVariable("id") Long id, @RequestBody AssignmentDraft draft) {
        operator();
        return service.assign(id, draft.assignee());
    }

    @PostMapping(path = "/{id}/decision", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Summary decide(@PathVariable("id") Long id, @RequestBody DecisionDraft draft) {
        return service.decide(operator().principal(), id, draft.decision(), draft.message());
    }

    @PostMapping(path = "/{id}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Message reply(@PathVariable("id") Long id, @RequestBody MessageDraft draft) {
        return service.reply(operator().principal(), id, draft.body(), draft.isInternal());
    }

    @PostMapping(path = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Attachment attach(@PathVariable("id") Long id, @RequestParam(name = "messageId", required = false) Long messageId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return service.attachAsEngineer(operator().principal(), id, messageId, file.getOriginalFilename(), file.getContentType(),
                file.getBytes());
    }

    @GetMapping(path = "/{id}/attachments/{attachmentId}")
    ResponseEntity<byte[]> download(@PathVariable("id") Long id, @PathVariable("attachmentId") Long attachmentId) {
        operator();
        return IssueDownloads.response(service.downloadAsEngineer(id, attachmentId));
    }

    private Caller operator() {
        Caller current = caller.current();
        if (!current.inDefaultTenant()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "The issues of every tenant are read by the instance's operators, in the default tenant");
        }
        return current;
    }
}
