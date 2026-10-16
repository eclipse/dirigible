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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.eclipse.dirigible.components.tenants.tenant.CrossTenantRunEvent;
import org.eclipse.dirigible.components.tenants.tenant.TenantAccessRefusedEvent;
import org.eclipse.dirigible.components.tenants.tenant.TenantEnteredEvent;
import org.eclipse.dirigible.components.tenants.tenant.TenantSelectionRevokedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Every tenant entry, switch, refusal, revocation and cross-tenant visit becomes one audit record,
 * and a failure to write one never reaches the sign-in or the run that caused it.
 */
@ExtendWith(MockitoExtension.class)
class CrossTenantAuditServiceTest {

    private static final Instant AT = Instant.parse("2026-10-16T09:30:00Z");
    private static final String USER = "accountant@practice.example";

    @Mock
    private CrossTenantAuditRepository records;

    @Mock
    private PlatformTransactionManager transactionManager;

    private CrossTenantAuditService audit;

    @BeforeEach
    void setUp() {
        audit = new CrossTenantAuditService(records, transactionManager);
    }

    @Test
    void aFirstSelectionIsAnEntry() {
        audit.onTenantEntered(new TenantEnteredEvent("acme", USER, null, AT));

        CrossTenantAuditRecord record = saved();
        assertThat(record.getAction()).isEqualTo(CrossTenantAuditAction.ENTERED);
        assertThat(record.getTenantId()).isEqualTo("acme");
        assertThat(record.getFromTenantId()).isNull();
        assertThat(record.getPrincipal()).isEqualTo(USER);
        assertThat(record.getAt()).isEqualTo(AT);
    }

    @Test
    void aSelectionFromAnotherTenantIsASwitchThatNamesWhereItCameFrom() {
        audit.onTenantEntered(new TenantEnteredEvent("globex", USER, null, AT, "acme"));

        CrossTenantAuditRecord record = saved();
        assertThat(record.getAction()).isEqualTo(CrossTenantAuditAction.SWITCHED);
        assertThat(record.getTenantId()).isEqualTo("globex");
        assertThat(record.getFromTenantId()).isEqualTo("acme");
    }

    @Test
    void reselectingTheSameTenantIsNoSwitch() {
        audit.onTenantEntered(new TenantEnteredEvent("acme", USER, null, AT, "acme"));

        assertThat(saved().getAction()).isEqualTo(CrossTenantAuditAction.ENTERED);
    }

    @Test
    void aRefusalKeepsItsReason() {
        audit.onAccessRefused(new TenantAccessRefusedEvent("acme", USER, "NOT_A_MEMBER", AT));

        CrossTenantAuditRecord record = saved();
        assertThat(record.getAction()).isEqualTo(CrossTenantAuditAction.REFUSED);
        assertThat(record.getDetail()).isEqualTo("NOT_A_MEMBER");
    }

    @Test
    void aRevokedSelectionIsRecorded() {
        audit.onSelectionRevoked(new TenantSelectionRevokedEvent("acme", USER, AT));

        assertThat(saved().getAction()).isEqualTo(CrossTenantAuditAction.REVOKED);
    }

    @Test
    void aRunVisitKeepsItsOperationAndOutcome() {
        audit.onRun(new CrossTenantRunEvent("acme", USER, "period-close", false, "x".repeat(5000), AT));

        CrossTenantAuditRecord record = saved();
        assertThat(record.getAction()).isEqualTo(CrossTenantAuditAction.RUN_FAILED);
        assertThat(record.getOperation()).isEqualTo("period-close");
        assertThat(record.getDetail()).hasSize(CrossTenantAuditRecord.MAX_DETAIL);
    }

    @Test
    void aFailingWriteNeverReachesTheCaller() {
        when(records.save(any())).thenThrow(new IllegalStateException("SystemDB is down"));

        assertThatCode(() -> audit.onRun(new CrossTenantRunEvent("acme", USER, "period-close", true, null, AT))).doesNotThrowAnyException();
    }

    @Test
    void aReadIsCapped() {
        audit.ofTenant("acme", 100_000);

        verify(records).findLatestOfTenant(eq("acme"), eq((Pageable) PageRequest.of(0, CrossTenantAuditService.MAX_RECORDS)));
    }

    private CrossTenantAuditRecord saved() {
        ArgumentCaptor<CrossTenantAuditRecord> record = ArgumentCaptor.forClass(CrossTenantAuditRecord.class);
        verify(records).save(record.capture());
        return record.getValue();
    }
}
