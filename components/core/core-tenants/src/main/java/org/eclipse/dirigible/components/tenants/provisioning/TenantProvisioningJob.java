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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class TenantProvisioningJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantProvisioningJob.class);

    // TODO return to 15min before merge
    private static final long _15_MINS = 1 * 60 * 1000;
    private static final long JOB_EXECUTION_INTERVAL_MINUTES = _15_MINS;
    private static final long _10_SECONDS = 10 * 1000;
    private static final long JOB_EXECUTION_INITIAL_DELAY_SECONDS = _10_SECONDS;

    private final TenantsProvisioner tenantsProvisioner;

    TenantProvisioningJob(TenantsProvisioner tenantsProvisioner) {
        this.tenantsProvisioner = tenantsProvisioner;
    }

    @Scheduled(initialDelay = JOB_EXECUTION_INITIAL_DELAY_SECONDS, fixedDelay = JOB_EXECUTION_INTERVAL_MINUTES)
    void provisionTenants() {
        LOGGER.info("Triggered tenants provisioning job...");
        tenantsProvisioner.provision();
        LOGGER.info("Tenants provisioning job has completed.");
    }

}
