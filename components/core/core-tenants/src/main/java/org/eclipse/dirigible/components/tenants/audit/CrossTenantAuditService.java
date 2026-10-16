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

import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.LogSanitizer;
import org.eclipse.dirigible.components.tenants.tenant.CrossTenantRunEvent;
import org.eclipse.dirigible.components.tenants.tenant.TenantAccessRefusedEvent;
import org.eclipse.dirigible.components.tenants.tenant.TenantEnteredEvent;
import org.eclipse.dirigible.components.tenants.tenant.TenantSelectionRevokedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Keeps the cross-tenant audit trail: who entered, switched into, was refused or lost which tenant,
 * and what each tenant's visit of a cross-tenant run came to. The records are written from the
 * events the tenant selection and the cross-tenant runner publish.
 *
 * <p>
 * Bookkeeping must never break a sign-in or a run, so each record is written in a transaction of
 * its own inside a try/catch, and a failure is a warning.
 */
@Component
public class CrossTenantAuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrossTenantAuditService.class);

    /** The most records one read returns. */
    static final int MAX_RECORDS = 500;

    /** The trail. */
    private final CrossTenantAuditRepository records;

    /** A new transaction, whatever the caller runs in. */
    private final TransactionTemplate transactions;

    /**
     * Instantiates the service.
     *
     * @param records the trail
     * @param transactionManager the SystemDB transaction manager
     */
    CrossTenantAuditService(CrossTenantAuditRepository records,
            @Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
        this.records = records;
        this.transactions = new TransactionTemplate(transactionManager);
        this.transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /**
     * Records a tenant entered - a first selection, or a switch from another tenant.
     *
     * @param event the event
     */
    @EventListener
    void onTenantEntered(TenantEnteredEvent event) {
        CrossTenantAuditAction action = event.isSwitch() ? CrossTenantAuditAction.SWITCHED : CrossTenantAuditAction.ENTERED;
        append(new CrossTenantAuditRecord(event.at(), event.principal(), action, event.tenantId(),
                event.isSwitch() ? event.previousTenantId() : null, null, null));
    }

    /**
     * Records a refused entry.
     *
     * @param event the event
     */
    @EventListener
    void onAccessRefused(TenantAccessRefusedEvent event) {
        append(new CrossTenantAuditRecord(event.at(), event.principal(), CrossTenantAuditAction.REFUSED, event.tenantId(), null, null,
                event.reason()));
    }

    /**
     * Records a selection dropped because its membership was revoked.
     *
     * @param event the event
     */
    @EventListener
    void onSelectionRevoked(TenantSelectionRevokedEvent event) {
        append(new CrossTenantAuditRecord(event.at(), event.principal(), CrossTenantAuditAction.REVOKED, event.tenantId(), null, null,
                null));
    }

    /**
     * Records one tenant's visit of a cross-tenant run.
     *
     * @param event the event
     */
    @EventListener
    void onRun(CrossTenantRunEvent event) {
        append(new CrossTenantAuditRecord(event.at(), event.principal(),
                event.succeeded() ? CrossTenantAuditAction.RUN_SUCCEEDED : CrossTenantAuditAction.RUN_FAILED, event.tenantId(), null,
                event.operation(), event.error()));
    }

    /**
     * What happened in a tenant, latest first - including the switches away from it.
     *
     * @param tenantId the tenant
     * @param limit how many, capped at {@value #MAX_RECORDS}
     * @return the records
     */
    public List<CrossTenantAuditRecord> ofTenant(String tenantId, int limit) {
        return records.findLatestOfTenant(tenantId, PageRequest.of(0, cap(limit)));
    }

    /**
     * What happened to a user, in any tenant, latest first.
     *
     * @param principal the principal name
     * @param limit how many, capped at {@value #MAX_RECORDS}
     * @return the records
     */
    public List<CrossTenantAuditRecord> ofPrincipal(String principal, int limit) {
        return records.findLatestOfPrincipal(principal, PageRequest.of(0, cap(limit)));
    }

    private static int cap(int limit) {
        return Math.max(1, Math.min(limit, MAX_RECORDS));
    }

    private void append(CrossTenantAuditRecord record) {
        if (record.getPrincipal() == null || record.getTenantId() == null) {
            return;
        }
        try {
            transactions.executeWithoutResult(status -> records.save(record));
        } catch (RuntimeException e) {
            LOGGER.warn("Could not record the cross-tenant audit entry [{}] of [{}] in tenant [{}]", record.getAction(),
                    LogSanitizer.sanitize(record.getPrincipal()), LogSanitizer.sanitize(record.getTenantId()), e);
        }
    }
}
