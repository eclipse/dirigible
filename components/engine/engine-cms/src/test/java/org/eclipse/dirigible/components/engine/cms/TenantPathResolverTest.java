/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.cms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.junit.jupiter.api.Test;

/**
 * Every CMS path is placed under the current tenant's id - which is what lets a tenant's teardown
 * delete the root {@code /} of its own documents and nobody else's.
 */
class TenantPathResolverTest {

    private final TenantContext tenantContext = mock(TenantContext.class);
    private final TenantPathResolver resolver = new TenantPathResolver(tenantContext, tenant("default"));

    @Test
    void theRootInsideATenantIsThatTenantsFolder() {
        inside("acme");

        assertEquals("acme/", resolver.resolve("/"));
    }

    @Test
    void aPathInsideATenantIsPlacedUnderItsFolder() {
        inside("acme");

        assertEquals("acme/documents/a.txt", resolver.resolve("/documents/a.txt"));
    }

    @Test
    void outsideATenantTheDefaultTenantIsUsed() {
        when(tenantContext.isInitialized()).thenReturn(false);

        assertEquals("default/", resolver.resolve("/"));
    }

    private void inside(String tenantId) {
        when(tenantContext.isInitialized()).thenReturn(true);
        Tenant current = tenant(tenantId);
        when(tenantContext.getCurrentTenant()).thenReturn(current);
    }

    private static Tenant tenant(String id) {
        Tenant tenant = mock(Tenant.class);
        when(tenant.getId()).thenReturn(id);
        return tenant;
    }
}
