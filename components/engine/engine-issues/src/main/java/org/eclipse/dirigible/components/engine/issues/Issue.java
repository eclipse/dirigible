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
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * An issue a user reported (#7948).
 *
 * <p>
 * The store is one cross-tenant table on SystemDB, like Flowable's: every row names its tenant and
 * its reporter, and every read a tenant user makes is filtered by both, server-side. The captured
 * context is the redacted JSON bundle the reporter confirmed before sending.
 */
@Entity
@Table(name = "DIRIGIBLE_ISSUES", indexes = {@Index(name = "IX_DIRIGIBLE_ISSUES_REPORTER", columnList = "ISSUE_TENANT_ID, ISSUE_REPORTER"),
        @Index(name = "IX_DIRIGIBLE_ISSUES_STATUS", columnList = "ISSUE_STATUS")})
public class Issue {

    /** The longest subject. */
    static final int SUBJECT_LENGTH = 255;

    /** The longest category. */
    static final int CATEGORY_LENGTH = 64;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ISSUE_ID", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    @Column(name = "ISSUE_TENANT_ID", columnDefinition = "VARCHAR", length = 255, nullable = false)
    private String tenantId;

    @Column(name = "ISSUE_REPORTER", columnDefinition = "VARCHAR", length = 320, nullable = false)
    private String reporter;

    @Column(name = "ISSUE_REPORTER_NAME", columnDefinition = "VARCHAR", length = 320)
    private String reporterName;

    @Column(name = "ISSUE_SUBJECT", columnDefinition = "VARCHAR", length = SUBJECT_LENGTH, nullable = false)
    private String subject;

    @Column(name = "ISSUE_DESCRIPTION", columnDefinition = "CLOB")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "ISSUE_SEVERITY", columnDefinition = "VARCHAR", length = 16, nullable = false)
    private IssueSeverity severity;

    @Column(name = "ISSUE_CATEGORY", columnDefinition = "VARCHAR", length = CATEGORY_LENGTH)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "ISSUE_STATUS", columnDefinition = "VARCHAR", length = 32, nullable = false)
    private IssueStatus status;

    @Column(name = "ISSUE_CONTEXT", columnDefinition = "CLOB")
    private String context;

    @Column(name = "ISSUE_ASSIGNEE", columnDefinition = "VARCHAR", length = 320)
    private String assignee;

    @Column(name = "ISSUE_PROCESS_INSTANCE_ID", columnDefinition = "VARCHAR", length = 64)
    private String processInstanceId;

    @Column(name = "ISSUE_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private Instant createdAt;

    @Column(name = "ISSUE_UPDATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private Instant updatedAt;

    /** For JPA. */
    protected Issue() {}

    /**
     * A new issue.
     *
     * @param tenantId the tenant it was filed in
     * @param reporter the reporter's identity
     * @param reporterName the reporter's display name
     * @param subject the subject
     * @param description the description
     * @param severity the severity
     * @param category the category
     * @param context the redacted context bundle, JSON
     * @param at when it was filed
     */
    Issue(String tenantId, String reporter, String reporterName, String subject, String description, IssueSeverity severity,
            String category, String context, Instant at) {
        this.tenantId = tenantId;
        this.reporter = reporter;
        this.reporterName = reporterName;
        this.subject = subject;
        this.description = description;
        this.severity = severity;
        this.category = category;
        this.context = context;
        this.status = IssueStatus.NEW;
        this.createdAt = at;
        this.updatedAt = at;
    }

    void moveTo(IssueStatus status, Instant at) {
        this.status = status;
        this.updatedAt = at;
    }

    void assignTo(String assignee, Instant at) {
        this.assignee = assignee;
        this.updatedAt = at;
    }

    void touch(Instant at) {
        this.updatedAt = at;
    }

    void linkProcess(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /** @return the id */
    public Long getId() {
        return id;
    }

    /** @return the tenant the issue was filed in */
    public String getTenantId() {
        return tenantId;
    }

    /** @return the reporter's identity */
    public String getReporter() {
        return reporter;
    }

    /** @return the reporter's display name */
    public String getReporterName() {
        return reporterName;
    }

    /** @return the subject */
    public String getSubject() {
        return subject;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /** @return the severity */
    public IssueSeverity getSeverity() {
        return severity;
    }

    /** @return the category */
    public String getCategory() {
        return category;
    }

    /** @return the status */
    public IssueStatus getStatus() {
        return status;
    }

    /** @return the redacted context bundle, JSON */
    public String getContext() {
        return context;
    }

    /** @return the assigned engineer */
    public String getAssignee() {
        return assignee;
    }

    /** @return the instance of the issue's lifecycle process */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /** @return when the issue was filed */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** @return when the issue last changed */
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
