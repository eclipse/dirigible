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
package org.eclipse.dirigible.components.tenants.init;

import java.util.Optional;
import org.eclipse.dirigible.components.base.ApplicationListenersOrder.ApplicationReadyEventListeners;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
@Order(ApplicationReadyEventListeners.DEFAULT_TENANT_INITIALIZER)
@Component
class DefaultTenantInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTenantInitializer.class);

    private final Tenant defaultTenant;
    private final TenantService tenantService;

    DefaultTenantInitializer(@DefaultTenant Tenant defaultTenant, TenantService tenantService) {
        this.defaultTenant = defaultTenant;
        this.tenantService = tenantService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("Executing...");
        createDefaultTenant();
        LOGGER.info("Completed...");

    }

    private void createDefaultTenant() {
        Optional<org.eclipse.dirigible.components.tenants.domain.Tenant> existingTenant = tenantService.findById(defaultTenant.getId());
        if (existingTenant.isPresent()) {
            LOGGER.info("Default tenant is already registed [{}]. Skipping its creation.", existingTenant.get());
            return;
        }
        org.eclipse.dirigible.components.tenants.domain.Tenant tenantEntity = new org.eclipse.dirigible.components.tenants.domain.Tenant();
        tenantEntity.setId(defaultTenant.getId());
        tenantEntity.setName(defaultTenant.getName());
        tenantEntity.setStatus(TenantStatus.PROVISIONED);
        tenantEntity.setSubdomain(defaultTenant.getSubdomain());

        org.eclipse.dirigible.components.tenants.domain.Tenant savedTenant = tenantService.save(tenantEntity);
        LOGGER.info("Created default tenant [{}]", savedTenant);
    }

}
