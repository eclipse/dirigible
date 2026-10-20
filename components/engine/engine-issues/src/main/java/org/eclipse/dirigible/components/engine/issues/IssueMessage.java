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

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * One message of an issue's thread, append-only. An {@link #isInternal() internal} message is a
 * memo between engineers and is never served to the reporter.
 */
@Entity
@Table(name = "DIRIGIBLE_ISSUE_MESSAGES", uniqueConstraints = @UniqueConstraint(name = "UK_DIRIGIBLE_ISSUE_MESSAGES_SEQUENCE",
        columnNames = {"ISSUEMESSAGE_ISSUE_ID", "ISSUEMESSAGE_SEQUENCE"}))
public class IssueMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ISSUEMESSAGE_ID", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    @Column(name = "ISSUEMESSAGE_ISSUE_ID", columnDefinition = "BIGINT", nullable = false)
    private Long issueId;

    @Column(name = "ISSUEMESSAGE_SEQUENCE", columnDefinition = "INT", nullable = false)
    private int sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "ISSUEMESSAGE_AUTHOR_KIND", columnDefinition = "VARCHAR", length = 16, nullable = false)
    private IssueAuthorKind authorKind;

    @Column(name = "ISSUEMESSAGE_AUTHOR", columnDefinition = "VARCHAR", length = 320, nullable = false)
    private String author;

    @Column(name = "ISSUEMESSAGE_BODY", columnDefinition = "CLOB", nullable = false)
    private String body;

    @Column(name = "ISSUEMESSAGE_INTERNAL", columnDefinition = "BOOLEAN", nullable = false)
    private boolean internal;

    @Column(name = "ISSUEMESSAGE_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private Instant createdAt;

    /** For JPA. */
    protected IssueMessage() {}

    /**
     * A new message.
     *
     * @param issueId the issue
     * @param sequence its position in the thread, from 1
     * @param authorKind which side wrote it
     * @param author who wrote it
     * @param body the text
     * @param internal whether it is an engineers' memo
     * @param at when it was written
     */
    IssueMessage(Long issueId, int sequence, IssueAuthorKind authorKind, String author, String body, boolean internal, Instant at) {
        this.issueId = issueId;
        this.sequence = sequence;
        this.authorKind = authorKind;
        this.author = author;
        this.body = body;
        this.internal = internal;
        this.createdAt = at;
    }

    /** @return the id */
    public Long getId() {
        return id;
    }

    /** @return the issue */
    public Long getIssueId() {
        return issueId;
    }

    /** @return the position in the thread, from 1 */
    public int getSequence() {
        return sequence;
    }

    /** @return which side wrote it */
    public IssueAuthorKind getAuthorKind() {
        return authorKind;
    }

    /** @return who wrote it */
    public String getAuthor() {
        return author;
    }

    /** @return the text */
    public String getBody() {
        return body;
    }

    /** @return whether it is an engineers' memo, never served to the reporter */
    public boolean isInternal() {
        return internal;
    }

    /** @return when it was written */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
