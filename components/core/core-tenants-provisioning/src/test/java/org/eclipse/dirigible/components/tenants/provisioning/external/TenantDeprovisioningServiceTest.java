/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.provisioning.external;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Deprovisioning runs every participant in order after the tenant is moved to
 * {@link TenantStatus#DEPROVISIONING}, deletes the users and the tenant last, and is safe to
 * repeat.
 */
class TenantDeprovisioningServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-19T12:00:00Z");

    private final TenantService tenantService = mock(TenantService.class);
    private final UserService userService = mock(UserService.class);
    private final TenantInitializationService initializationService = mock(TenantInitializationService.class);
    private final List<String> calls = new ArrayList<>();

    private RecordingParticipant runtime;
    private RecordingParticipant dataSource;
    private RecordingParticipant rows;
    private TenantDeprovisioningService service;

    @BeforeEach
    void wire() {
        runtime = new RecordingParticipant("runtime", 100);
        dataSource = new RecordingParticipant("dataSource", 200);
        rows = new RecordingParticipant("rows", 300);
        // registered out of order on purpose: the service sorts them
        service = new TenantDeprovisioningService(tenantService, userService, initializationService, List.of(rows, runtime, dataSource),
                "default-tenant", Clock.fixed(NOW, ZoneOffset.UTC));
        when(tenantService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(initializationService.find(any())).thenReturn(Optional.empty());
    }

    @Test
    void theParticipantsRunInOrderAfterTheStatusMoveAndBeforeTheUsersAndTheTenant() {
        Tenant tenant = tenant("acme", TenantStatus.PROVISIONED);
        when(tenantService.findById("acme")).thenReturn(Optional.of(tenant));
        User user = mock(User.class);
        when(user.getId()).thenReturn("u1");
        when(userService.findUsersByTenantId("acme")).thenReturn(List.of(user));

        assertTrue(service.deprovision("acme"));

        assertEquals(List.of("runtime", "dataSource", "rows"), calls);
        assertEquals(TenantStatus.DEPROVISIONING, tenant.getStatus());
        InOrder order = inOrder(tenantService, userService);
        order.verify(tenantService)
             .save(tenant);
        order.verify(userService)
             .deleteUser("u1");
        order.verify(tenantService)
             .delete(tenant);
    }

    @Test
    void anUnknownTenantIsNotAnErrorAndTouchesNothing() {
        when(tenantService.findById("ghost")).thenReturn(Optional.empty());

        assertFalse(service.deprovision("ghost"));

        assertTrue(calls.isEmpty());
        verify(tenantService, never()).delete(any());
    }

    @Test
    void theDefaultTenantIsRefusedAndNothingIsTouched() {
        ResponseStatusException refused = assertThrows(ResponseStatusException.class, () -> service.deprovision("default-tenant"));

        assertEquals(HttpStatus.BAD_REQUEST, refused.getStatusCode());
        assertTrue(calls.isEmpty());
        verify(tenantService, never()).findById(any());
    }

    @Test
    void anInvalidIdIsRefused() {
        ResponseStatusException refused = assertThrows(ResponseStatusException.class, () -> service.deprovision("not.a.tenant"));

        assertEquals(HttpStatus.BAD_REQUEST, refused.getStatusCode());
        assertTrue(calls.isEmpty());
    }

    @Test
    void aRunningActivationIsAConflictAndNothingIsTouched() {
        Tenant tenant = tenant("acme", TenantStatus.PROVISIONED);
        when(tenantService.findById("acme")).thenReturn(Optional.of(tenant));
        when(initializationService.find("acme")).thenReturn(Optional.of(openInitialization(NOW.minus(Duration.ofMinutes(5)))));

        ResponseStatusException refused = assertThrows(ResponseStatusException.class, () -> service.deprovision("acme"));

        assertEquals(HttpStatus.CONFLICT, refused.getStatusCode());
        assertTrue(calls.isEmpty());
        assertEquals(TenantStatus.PROVISIONED, tenant.getStatus());
        verify(tenantService, never()).save(any());
    }

    @Test
    void anActivationOpenLongerThanTheBoundNoLongerHoldsTheRemovalOff() {
        Tenant tenant = tenant("acme", TenantStatus.PROVISIONED);
        when(tenantService.findById("acme")).thenReturn(Optional.of(tenant));
        when(initializationService.find("acme")).thenReturn(Optional.of(openInitialization(NOW.minus(Duration.ofMinutes(31)))));

        assertTrue(service.deprovision("acme"));

        assertEquals(List.of("runtime", "dataSource", "rows"), calls);
    }

    @Test
    void aFailingParticipantStopsTheRunAndARepeatedCallRunsEverythingAgain() {
        Tenant tenant = tenant("acme", TenantStatus.PROVISIONED);
        when(tenantService.findById("acme")).thenReturn(Optional.of(tenant));
        dataSource.failNext = true;

        ResponseStatusException stopped = assertThrows(ResponseStatusException.class, () -> service.deprovision("acme"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, stopped.getStatusCode());
        assertTrue(stopped.getReason()
                          .contains("RecordingParticipant"),
                "the reason names the participant: " + stopped.getReason());
        assertEquals(List.of("runtime", "dataSource"), calls);
        assertEquals(TenantStatus.DEPROVISIONING, tenant.getStatus());
        verify(tenantService, never()).delete(any());

        calls.clear();
        assertTrue(service.deprovision("acme"));

        assertEquals(List.of("runtime", "dataSource", "rows"), calls);
        verify(tenantService).delete(tenant);
    }

    private static TenantInitialization openInitialization(Instant requestedAt) {
        TenantInitialization initialization = new TenantInitialization("acme");
        initialization.request(requestedAt);
        return initialization;
    }

    private static Tenant tenant(String id, TenantStatus status) {
        Tenant tenant = new Tenant("-", id, "", id, status);
        tenant.setId(id);
        return tenant;
    }

    /** A participant that records its run, and can fail once. */
    private final class RecordingParticipant implements TenantTeardownParticipant {

        private final String name;
        private final int order;
        private boolean failNext;

        RecordingParticipant(String name, int order) {
            this.name = name;
            this.order = order;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public void teardown(String tenantId) {
            calls.add(name);
            if (failNext) {
                failNext = false;
                throw new IllegalStateException("the broker is away");
            }
        }
    }
}
