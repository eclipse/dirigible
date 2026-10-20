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

import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: removes the tenant's default data source, which closes its connection pool.
 *
 * <p>
 * It lives beside the registration it reverses rather than in the data sources module: the data
 * source was registered by this API, under the name {@link TenantDataSourceRegistrationService}
 * derives, and the same service removes it. It runs after what runs for the tenant (nothing may
 * still be using the pool) and before the rows that describe the tenant.
 */
@Component
@Conditional(TenantProvisioningApiEnabledCondition.class)
class TenantDataSourceTenantTeardown implements TenantTeardownParticipant {

    /** The order of this participant. */
    static final int ORDER = 200;

    /** The tenant service. */
    private final TenantService tenantService;

    /** The data source registration service. */
    private final TenantDataSourceRegistrationService registrationService;

    /**
     * Instantiates a new tenant data source teardown.
     *
     * @param tenantService the tenant service
     * @param registrationService the data source registration service
     */
    TenantDataSourceTenantTeardown(TenantService tenantService, TenantDataSourceRegistrationService registrationService) {
        this.tenantService = tenantService;
        this.registrationService = registrationService;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        tenantService.findById(tenantId)
                     .ifPresent(registrationService::deregister);
    }
}
