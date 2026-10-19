/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.users;

import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: deletes the tenant's user replica - every user row and, through the cascade,
 * their role rows.
 *
 * <p>
 * Needed because the replica's tenant id is a plain column with no foreign key to the tenant:
 * deleting the tenant alone would leave the rows behind, and a tenant registered again under the
 * same id would find them.
 */
@Component
class TenantUsersTenantTeardown implements TenantTeardownParticipant {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantUsersTenantTeardown.class);

    /** The order of this participant: a row describing the tenant. */
    static final int ORDER = 300;

    /** The users. */
    private final TenantUserRepository users;

    /**
     * Instantiates a new tenant users teardown.
     *
     * @param users the users
     */
    TenantUsersTenantTeardown(TenantUserRepository users) {
        this.users = users;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        long deleted = users.deleteByTenantId(tenantId);
        LOGGER.info("Deleted [{}] replica user(s) of tenant [{}].", deleted, tenantId);
    }
}
