/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.tenant;

import java.util.List;

/**
 * The Class TenantContext.
 */
public class TenantContext {

    /** The Constant currentTenant. */
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    /** The Constant currentTenantId. */
    private static final ThreadLocal<Long> currentTenantId = new ThreadLocal<>();

    /** The Constant currentTenants. */
    private static final ThreadLocal<List<String>> currentTenants = new ThreadLocal<>();

    /**
     * Gets the current tenant.
     *
     * @return the current tenant
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Sets the current tenant.
     *
     * @param tenant the new current tenant
     */
    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    /**
     * Gets the current tenant id.
     *
     * @return the current tenant id
     */
    public static Long getCurrentTenantId() {
        return currentTenantId.get();
    }

    /**
     * Sets the current tenant id.
     *
     * @param tenantId the new current tenant id
     */
    public static void setCurrentTenantId(Long tenantId) {
        currentTenantId.set(tenantId);
    }

    /**
     * Gets the current tenants.
     *
     * @return the current tenants
     */
    public static List<String> getCurrentTenants() {
        return currentTenants.get();
    }

    /**
     * Sets the current tenants.
     *
     * @param tenants the new current tenants
     */
    public static void setCurrentTenants(List<String> tenants) {
        currentTenants.set(tenants);
    }

    /**
     * Clear.
     */
    public static void clear() {
        currentTenant.set(null);
        currentTenantId.set(null);
        currentTenants.set(null);
    }
}
