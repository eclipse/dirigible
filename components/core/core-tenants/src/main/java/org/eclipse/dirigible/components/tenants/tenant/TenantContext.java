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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TenantContext.
 */
public class TenantContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantContext.class);

    /** The Constant currentTenantId. */
    private static final ThreadLocal<Tenant> currentTenant = new ThreadLocal<>();

    public static boolean isNotInitialized() {
        return !isInitialized();
    }

    public static boolean isInitialized() {
        return null != currentTenant.get();
    }

    /**
     * Gets the current tenant.
     *
     * @return the current tenant
     */
    public static Tenant getCurrentTenant() {
        Tenant tenant = currentTenant.get();
        if (null == tenant) {
            throw new IllegalStateException("Attempting to get current tenant but it is not initialized yet.");
        }
        LOGGER.debug("Getting current tenant [{}]", tenant);
        return tenant;
    }

    /**
     * Sets the current tenant.
     *
     * @param tenantId the new current tenant
     */
    public static void setCurrentTenant(Tenant tenant) {
        LOGGER.debug("Setting current tenant to [{}]", tenant);
        currentTenant.set(tenant);
    }

    /**
     * Clear.
     */
    public static void clear() {
        LOGGER.debug("Clearing current tenant [{}]", currentTenant.get());
        currentTenant.set(null);
    }

}
