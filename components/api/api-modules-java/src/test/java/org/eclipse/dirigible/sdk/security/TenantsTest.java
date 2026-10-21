/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner.CrossTenantResult;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner.GrantedTenant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;

/**
 * {@link Tenants} hands the platform's cross-tenant runner to Java typed, and refuses where the
 * tenant resolution provides none.
 */
class TenantsTest {

    private static final GrantedTenant ACME = new GrantedTenant("acme", "Acme Ltd.", Set.of("Accountant"));
    private static final GrantedTenant GLOBEX = new GrantedTenant("globex", "Globex", Set.of("Auditor"));

    @AfterEach
    void forgetTheContext() {
        new BeanProvider().setApplicationContext(null);
    }

    @Test
    void withoutACrossTenantRunnerRunsAcrossTenantsAreUnavailableAndRefused() {
        install(new StaticApplicationContext());

        assertFalse(Tenants.isAvailable());
        assertThrows(IllegalStateException.class, Tenants::getGrantedTenants);
        assertThrows(IllegalStateException.class, () -> Tenants.forEachGrantedTenant("period-close", tenantId -> tenantId));
    }

    @Test
    void theGrantedTenantsAndTheRunComeFromTheRunnerTyped() {
        RecordingRunner runner = new RecordingRunner(List.of(ACME, GLOBEX));
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory()
               .registerSingleton("crossTenantRunner", runner);
        install(context);

        assertTrue(Tenants.isAvailable());
        assertEquals(List.of(ACME, GLOBEX), Tenants.getGrantedTenants());

        List<CrossTenantResult<Integer>> results = Tenants.forEachGrantedTenant("period-close", String::length);

        assertEquals(List.of(new CrossTenantResult<>("acme", true, 4, null), new CrossTenantResult<>("globex", true, 6, null)), results);
        assertEquals("period-close", runner.operation);
    }

    private static void install(StaticApplicationContext context) {
        context.refresh();
        new BeanProvider().setApplicationContext(context);
    }

    /** Visits the tenants it was given, in order, and remembers the operation it ran. */
    private static final class RecordingRunner implements CrossTenantRunner {

        private final List<GrantedTenant> tenants;
        private String operation;

        RecordingRunner(List<GrantedTenant> tenants) {
            this.tenants = tenants;
        }

        @Override
        public List<GrantedTenant> grantedTenants() {
            return tenants;
        }

        @Override
        public <R> List<CrossTenantResult<R>> forEachGrantedTenant(String operation, Function<String, R> work) {
            this.operation = operation;
            List<CrossTenantResult<R>> results = new ArrayList<>();
            for (GrantedTenant tenant : tenants) {
                results.add(new CrossTenantResult<>(tenant.id(), true, work.apply(tenant.id()), null));
            }
            return results;
        }
    }
}
