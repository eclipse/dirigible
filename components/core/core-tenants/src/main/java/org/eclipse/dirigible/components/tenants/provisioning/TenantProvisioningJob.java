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

/**
 * The Class TenantProvisioningJob.
 */
@Component
class TenantProvisioningJob {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantProvisioningJob.class);

    /** The Constant _15_MINS. */
    private static final long _15_MINS = 15 * 60 * 1000;

    /** The Constant JOB_EXECUTION_INTERVAL_MINUTES. */
    private static final long JOB_EXECUTION_INTERVAL_MINUTES = _15_MINS;

    /** The Constant _30_SECONDS. */
    private static final long _30_SECONDS = 30 * 1000;

    /** The Constant JOB_EXECUTION_INITIAL_DELAY_SECONDS. */
    private static final long JOB_EXECUTION_INITIAL_DELAY_SECONDS = _30_SECONDS;

    /** The tenants provisioner. */
    private final TenantsProvisioner tenantsProvisioner;

    /**
     * Instantiates a new tenant provisioning job.
     *
     * @param tenantsProvisioner the tenants provisioner
     */
    TenantProvisioningJob(TenantsProvisioner tenantsProvisioner) {
        this.tenantsProvisioner = tenantsProvisioner;
    }

    /**
     * Provision tenants.
     */
    @Scheduled(initialDelay = JOB_EXECUTION_INITIAL_DELAY_SECONDS, fixedDelay = JOB_EXECUTION_INTERVAL_MINUTES)
    void provisionTenants() {
        LOGGER.info("Triggered tenants provisioning job...");
        tenantsProvisioner.provision();
        LOGGER.info("Tenants provisioning job has completed.");
    }

}
