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
package org.eclipse.dirigible.integration.tests.ui.tests.multitenancy;

import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class TenantCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantCreator.class);

    private final TenantService tenantService;
    private final UserService userService;

    TenantCreator(TenantService tenantService, UserService userService) {
        this.tenantService = tenantService;
        this.userService = userService;
    }

    void createTenant(DirigibleTestTenant tenant) {
        LOGGER.info("Creating tenant [{}]", tenant);
        Tenant tenantEntity = new Tenant();
        tenantEntity.setId(tenant.getId());
        tenantEntity.setName(tenant.getName());
        tenantEntity.setSubdomain(tenant.getSubdomain());
        tenantEntity.setStatus(TenantStatus.INITIAL);

        tenantService.save(tenantEntity);

        userService.createNewUser(tenant.getUsername(), tenant.getPassword(), tenant.getId());
    }

    boolean isTenantProvisioned(DirigibleTestTenant tenant) {
        return tenantService.findById(tenant.getId())
                            .get()
                            .getStatus() == TenantStatus.PROVISIONED;
    }
}
