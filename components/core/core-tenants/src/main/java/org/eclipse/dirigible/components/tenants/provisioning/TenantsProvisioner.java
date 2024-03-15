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
package org.eclipse.dirigible.components.tenants.provisioning;

import java.util.Set;
import org.eclipse.dirigible.components.base.tenant.TenantPostProvisioningStep;
import org.eclipse.dirigible.components.base.tenant.TenantProvisioningStep;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.eclipse.dirigible.components.tenants.tenant.TenantFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class TenantsProvisioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantsProvisioner.class);

    private final TenantService tenantService;
    private final Set<TenantProvisioningStep> provisioningSteps;
    private final Set<TenantPostProvisioningStep> postProvisioningSteps;
    private final TenantFactory tenantFactory;

    TenantsProvisioner(TenantService tenantService, Set<TenantProvisioningStep> provisioningSteps,
            Set<TenantPostProvisioningStep> postProvisioningSteps, TenantFactory tenantFactory) {
        this.tenantService = tenantService;
        this.provisioningSteps = provisioningSteps;
        this.postProvisioningSteps = postProvisioningSteps;
        this.tenantFactory = tenantFactory;
    }

    void provision() {
        LOGGER.info("Starting tenants provisioning...");
        Set<Tenant> tenants = tenantService.findByStatus(TenantStatus.INITIAL);
        LOGGER.info("Tenants applicable for provisioning [{}]", tenants);

        tenants.forEach(this::provisionTenant);

        if (!tenants.isEmpty()) {
            LOGGER.info("Starting post provisioning process...");
            postProvisioningSteps.forEach(this::callPostProvisioningStep);
            LOGGER.info("Post provisioning process has completed.");
        }
        LOGGER.info("Tenants [{}] have been provisioned successfully.", tenants);
    }

    private void provisionTenant(Tenant tenant) {
        LOGGER.info("Starting provisioning process for tenant [{}]...", tenant);

        try {
            org.eclipse.dirigible.components.base.tenant.Tenant t = tenantFactory.createFromEntity(tenant);
            provisioningSteps.forEach(step -> step.execute(t));

            tenant.setStatus(TenantStatus.PROVISIONED);
            tenantService.save(tenant);

            LOGGER.info("Tenant [{}] has been provisioned successfully.", tenant);
        } catch (RuntimeException ex) {
            LOGGER.error("Failed to provision tenant [{}]. Continue with the next one.", tenant, ex);
        }
    }

    private void callPostProvisioningStep(TenantPostProvisioningStep step) {
        try {
            step.execute();
        } catch (RuntimeException ex) {
            LOGGER.error("PostProvisioning step [{}] has failed.", step, ex);
        }
    }

}
