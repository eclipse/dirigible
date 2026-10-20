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
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: forgets the tenant's {@link TenantInitialization} - the record its activation
 * opened.
 */
@Component
@Conditional(TenantProvisioningApiEnabledCondition.class)
class TenantInitializationTenantTeardown implements TenantTeardownParticipant {

    /** The order of this participant: a row describing the tenant, after its data source. */
    static final int ORDER = 400;

    /** The initialization of each activated tenant. */
    private final TenantInitializationService initializationService;

    /**
     * Instantiates a new tenant initialization teardown.
     *
     * @param initializationService the initialization of each activated tenant
     */
    TenantInitializationTenantTeardown(TenantInitializationService initializationService) {
        this.initializationService = initializationService;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        initializationService.delete(tenantId);
    }
}
