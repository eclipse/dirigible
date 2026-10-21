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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A file attached to an issue. The bytes live in the default tenant's CMS - the instance's own
 * store, internal or S3 - under {@link #getStorageReference()}; the row records what the file is
 * and who uploaded it. An attachment of an {@link IssueMessage#isInternal() internal} message is as
 * internal as its message.
 */
@Entity
@Table(name = "DIRIGIBLE_ISSUE_ATTACHMENTS")
public class IssueAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ISSUEATTACHMENT_ID", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    @Column(name = "ISSUEATTACHMENT_ISSUE_ID", columnDefinition = "BIGINT", nullable = false)
    private Long issueId;

    @Column(name = "ISSUEATTACHMENT_MESSAGE_ID", columnDefinition = "BIGINT")
    private Long messageId;

    @Column(name = "ISSUEATTACHMENT_FILE_NAME", columnDefinition = "VARCHAR", length = 255, nullable = false)
    private String fileName;

    @Column(name = "ISSUEATTACHMENT_MIME_TYPE", columnDefinition = "VARCHAR", length = 255)
    private String mimeType;

    @Column(name = "ISSUEATTACHMENT_SIZE", columnDefinition = "BIGINT", nullable = false)
    private long size;

    /**
     * The definition carries its length: Hibernate's schema update compares the definition string with
     * the column Liquibase created (CHAR(64)) and, on PostgreSQL, alters a bare {@code CHAR} down to
     * {@code character(1)}, after which no digest fits (#7948 follow-up).
     */
    @Column(name = "ISSUEATTACHMENT_SHA256", columnDefinition = "CHAR(64)", length = 64, nullable = false)
    private String sha256;

    @Column(name = "ISSUEATTACHMENT_STORAGE_REF", columnDefinition = "VARCHAR", length = 2000, nullable = false)
    private String storageReference;

    @Column(name = "ISSUEATTACHMENT_UPLOADED_BY", columnDefinition = "VARCHAR", length = 320, nullable = false)
    private String uploadedBy;

    @Column(name = "ISSUEATTACHMENT_UPLOADED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private Instant uploadedAt;

    /** For JPA. */
    protected IssueAttachment() {}

    /**
     * A new attachment.
     *
     * @param issueId the issue
     * @param messageId the message it belongs to, {@code null} for one attached to the issue itself
     * @param fileName the stored file name
     * @param mimeType the MIME type
     * @param size the size in bytes
     * @param sha256 the content's SHA-256, hex
     * @param storageReference where the bytes are in the CMS
     * @param uploadedBy who uploaded it
     * @param at when
     */
    IssueAttachment(Long issueId, Long messageId, String fileName, String mimeType, long size, String sha256, String storageReference,
            String uploadedBy, Instant at) {
        this.issueId = issueId;
        this.messageId = messageId;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.size = size;
        this.sha256 = sha256;
        this.storageReference = storageReference;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = at;
    }

    /** @return the id */
    public Long getId() {
        return id;
    }

    /** @return the issue */
    public Long getIssueId() {
        return issueId;
    }

    /** @return the message it belongs to, {@code null} for one attached to the issue itself */
    public Long getMessageId() {
        return messageId;
    }

    /** @return the stored file name */
    public String getFileName() {
        return fileName;
    }

    /** @return the MIME type */
    public String getMimeType() {
        return mimeType;
    }

    /** @return the size in bytes */
    public long getSize() {
        return size;
    }

    /** @return the content's SHA-256, hex */
    public String getSha256() {
        return sha256;
    }

    /** @return where the bytes are in the CMS */
    public String getStorageReference() {
        return storageReference;
    }

    /** @return who uploaded it */
    public String getUploadedBy() {
        return uploadedBy;
    }

    /** @return when it was uploaded */
    public Instant getUploadedAt() {
        return uploadedAt;
    }
}
