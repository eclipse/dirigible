/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.audit;

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
 * One entry of the cross-tenant audit trail: a user entering, switching into, being refused or
 * losing a tenant, or one tenant's visit of a cross-tenant run.
 *
 * <p>
 * The row lives in the SystemDB with an explicit tenant id, like the tenant registry, because it is
 * written while the request is scoped to another tenant - or to none - and it has to be readable by
 * the administrator of the tenant it names. Rows are appended only; nothing updates or deletes one.
 */
@Entity
@Table(name = "DIRIGIBLE_CROSS_TENANT_AUDIT",
        indexes = {@Index(name = "IX_DIRIGIBLE_CROSS_TENANT_AUDIT_TENANT", columnList = "CTAUDIT_TENANT_ID, CTAUDIT_AT"),
                @Index(name = "IX_DIRIGIBLE_CROSS_TENANT_AUDIT_PRINCIPAL", columnList = "CTAUDIT_PRINCIPAL, CTAUDIT_AT")})
public class CrossTenantAuditRecord {

    /** The longest detail kept - the column's length. */
    static final int MAX_DETAIL = 1024;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CTAUDIT_ID", columnDefinition = "BIGINT", nullable = false)
    private Long id;

    /** When it happened. */
    @Column(name = "CTAUDIT_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private Instant at;

    /** Who it happened to - the principal name. */
    @Column(name = "CTAUDIT_PRINCIPAL", columnDefinition = "VARCHAR", nullable = false, length = 320)
    private String principal;

    /** What happened. */
    @Enumerated(EnumType.STRING)
    @Column(name = "CTAUDIT_ACTION", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private CrossTenantAuditAction action;

    /** The tenant it happened in - the one entered, refused, revoked or visited. */
    @Column(name = "CTAUDIT_TENANT_ID", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String tenantId;

    /** The tenant a switch came from; null otherwise. */
    @Column(name = "CTAUDIT_FROM_TENANT_ID", columnDefinition = "VARCHAR", length = 255)
    private String fromTenantId;

    /** The operation of a cross-tenant run; null otherwise. */
    @Column(name = "CTAUDIT_OPERATION", columnDefinition = "VARCHAR", length = 255)
    private String operation;

    /** Why a refusal or a run failed; null otherwise. */
    @Column(name = "CTAUDIT_DETAIL", columnDefinition = "VARCHAR", length = MAX_DETAIL)
    private String detail;

    /**
     * For JPA.
     */
    protected CrossTenantAuditRecord() {}

    /**
     * Instantiates a new record.
     *
     * @param at when
     * @param principal who
     * @param action what
     * @param tenantId in which tenant
     * @param fromTenantId the tenant a switch came from, or null
     * @param operation the operation of a run, or null
     * @param detail why a refusal or a run failed, or null; cut to {@value #MAX_DETAIL} characters
     */
    public CrossTenantAuditRecord(Instant at, String principal, CrossTenantAuditAction action, String tenantId, String fromTenantId,
            String operation, String detail) {
        this.at = at;
        this.principal = principal;
        this.action = action;
        this.tenantId = tenantId;
        this.fromTenantId = fromTenantId;
        this.operation = operation;
        this.detail = detail == null || detail.length() <= MAX_DETAIL ? detail : detail.substring(0, MAX_DETAIL);
    }

    public Long getId() {
        return id;
    }

    public Instant getAt() {
        return at;
    }

    public String getPrincipal() {
        return principal;
    }

    public CrossTenantAuditAction getAction() {
        return action;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getFromTenantId() {
        return fromTenantId;
    }

    public String getOperation() {
        return operation;
    }

    public String getDetail() {
        return detail;
    }
}
