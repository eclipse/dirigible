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
import java.net.URI;
import java.util.List;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Attachment;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Detail;
import org.eclipse.dirigible.components.engine.issues.IssueViews.ExcerptLine;
import org.eclipse.dirigible.components.engine.issues.IssueViews.ExcerptRequest;
import org.eclipse.dirigible.components.engine.issues.IssueViews.IssueDraft;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Message;
import org.eclipse.dirigible.components.engine.issues.IssueViews.MessageDraft;
import org.eclipse.dirigible.components.engine.issues.IssueViews.Summary;
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

/**
 * The issues of the calling user (#7948): file one, read one's own, talk to the support engineers,
 * attach files both ways, withdraw.
 *
 * <p>
 * Every read and write is filtered by the tenant the request runs in and by the caller,
 * server-side, whatever the request says: another user's or another tenant's issue is a 404,
 * exactly like a missing one.
 */
@RestController
@RequestMapping(IssuesEndpoint.PATH)
class IssuesEndpoint {

    /** The endpoint's path. */
    static final String PATH = BaseEndpoint.PREFIX_ENDPOINT_SECURED + "issues";

    private final IssueService service;

    private final IssueCaller caller;

    IssuesEndpoint(IssueService service, IssueCaller caller) {
        this.service = service;
        this.caller = caller;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<Summary> mine() {
        return service.mine(caller.current());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Summary> file(@RequestBody IssueDraft draft) {
        Issue issue = service.file(caller.current(), draft);
        return ResponseEntity.created(URI.create("/" + PATH + "/" + issue.getId()))
                             .body(Summary.of(issue));
    }

    /**
     * The log excerpt a report of these request ids would carry, for the reporter to see before
     * sending.
     *
     * @param request the request ids
     * @return the lines
     */
    @PostMapping(path = "/excerpt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<ExcerptLine> excerpt(@RequestBody ExcerptRequest request) {
        return service.excerpt(caller.current(), request.requestIds() == null ? List.of() : request.requestIds());
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Detail one(@PathVariable("id") Long id) {
        return service.mine(caller.current(), id);
    }

    @PostMapping(path = "/{id}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Message answer(@PathVariable("id") Long id, @RequestBody MessageDraft draft) {
        return service.answer(caller.current(), id, draft.body());
    }

    @PostMapping(path = "/{id}/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    Summary withdraw(@PathVariable("id") Long id) {
        return service.withdraw(caller.current(), id);
    }

    @PostMapping(path = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Attachment attach(@PathVariable("id") Long id, @RequestParam(name = "messageId", required = false) Long messageId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return service.attach(caller.current(), id, messageId, file.getOriginalFilename(), file.getContentType(), file.getBytes());
    }

    @GetMapping(path = "/{id}/attachments/{attachmentId}")
    ResponseEntity<byte[]> download(@PathVariable("id") Long id, @PathVariable("attachmentId") Long attachmentId) {
        return IssueDownloads.response(service.download(caller.current(), id, attachmentId));
    }
}
