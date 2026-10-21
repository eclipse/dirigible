/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.configurations.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A job fire or a process step runs in its tenant's scope but is not a request, so nothing loaded
 * the tenant's configuration overrides for it (#7878): a job's notify mail went out from the
 * instance's sender. {@link TenantConfigurationService#executeWithTenantOverrides} loads them for
 * the work and clears them afterwards, and leaves alone the ones a request already carries.
 */
class TenantConfigurationServiceTenantOverridesTest {

    private TenantConfigurationStore store;
    private TenantConfigurationService service;

    @BeforeEach
    void setUp() throws Exception {
        store = mock(TenantConfigurationStore.class);
        when(store.readAll()).thenReturn(Map.of( //
                "DIRIGIBLE_MAIL_SENDER", "Acme Billing <billing@acme.com>", //
                "DIRIGIBLE_MAIL_REPLY_TO", "accounts@acme.com", //
                "DIRIGIBLE_MAIL_SMTP_HOST", "smtp.acme.com"));
        Tenant tenant = mock(Tenant.class);
        when(tenant.getId()).thenReturn("acme");
        TenantContext tenantContext = mock(TenantContext.class);
        when(tenantContext.isNotInitialized()).thenReturn(false);
        when(tenantContext.getCurrentTenant()).thenReturn(tenant);
        service = new TenantConfigurationService(store, new TenantConfigurationCache(), new TenantConfigurationKeyPolicy(), tenantContext);
        Configuration.removeThreadConfiguration();
    }

    @AfterEach
    void tearDown() {
        Configuration.removeThreadConfiguration();
    }

    @Test
    void theWorkReadsTheTenantsMailIdentityAndTheThreadIsClearedAfterwards() {
        String[] seen = service.executeWithTenantOverrides(() -> new String[] { //
                DirigibleConfig.MAIL_SENDER.getStringValue(), //
                DirigibleConfig.MAIL_REPLY_TO.getStringValue(), //
                Configuration.get("DIRIGIBLE_MAIL_SMTP_HOST")});

        assertEquals("Acme Billing <billing@acme.com>", seen[0]);
        assertEquals("accounts@acme.com", seen[1]);
        assertNull(seen[2], "the SMTP account is the instance's - a tenant's value is never injected");
        assertTrue(Configuration.getThreadConfiguration()
                                .isEmpty(),
                "the overrides must not leak onto the pooled thread");
        assertEquals("noreply@dirigible.io", DirigibleConfig.MAIL_SENDER.getStringValue());
    }

    @Test
    void overridesAThreadAlreadyCarriesAreKept() {
        Configuration.setThreadConfiguration(Map.of("DIRIGIBLE_MAIL_SENDER", "request@acme.com"));

        String seen = service.executeWithTenantOverrides(DirigibleConfig.MAIL_SENDER::getStringValue);

        assertEquals("request@acme.com", seen);
        assertEquals(Map.of("DIRIGIBLE_MAIL_SENDER", "request@acme.com"), Configuration.getThreadConfiguration(),
                "the request's own overrides must survive the step");
        verifyNoInteractions(store);
    }
}
