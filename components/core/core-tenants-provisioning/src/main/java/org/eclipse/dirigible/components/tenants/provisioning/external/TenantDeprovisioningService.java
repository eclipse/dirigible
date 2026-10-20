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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.server.ResponseStatusException;

/**
 * Removes a tenant on behalf of an external provisioner - everything this application holds of it.
 *
 * <p>
 * The tenant is first moved to the persisted {@link TenantStatus#DEPROVISIONING} status. From that
 * moment no per-tenant fan-out reaches it ({@code executeForEachTenant} visits provisioned tenants
 * only), so nothing new starts for it - not after a restart either. Then every
 * {@link TenantTeardownParticipant} runs, in order: what runs for the tenant, its data source with
 * the connection pool, the rows the system database holds for it. The tenant's platform users and
 * the tenant row go last.
 *
 * <p>
 * Idempotent, because the caller is a retrying process. An unknown tenant is not an error - it was
 * removed already, or never got this far - and a run that stopped half way left the tenant
 * {@code DEPROVISIONING}, so the next call resumes it from the start; every participant treats
 * finding nothing as success.
 *
 * <p>
 * The tenant's data schema and the database user that owns it are not touched: the provisioner
 * created them, and drops them once this has returned - never before, or the pool would still hold
 * connections into the schema being dropped.
 *
 * <p>
 * The runtime teardown (jobs, listeners, processes) acts on this instance only, so an application
 * is expected to run as a single instance.
 */
@Service
@Conditional(TenantProvisioningApiEnabledCondition.class)
class TenantDeprovisioningService {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantDeprovisioningService.class);

    /** The tenant service. */
    private final TenantService tenantService;

    /** The platform users of a tenant. */
    private final UserService userService;

    /** The initialization of each activated tenant. */
    private final TenantInitializationService initializationService;

    /** Every participant, in the order they run. */
    private final List<TenantTeardownParticipant> participants;

    /** The default tenant's id - the one tenant that can never be deprovisioned. */
    private final String defaultTenantId;

    /** The clock the staleness of an activation is measured with. */
    private final Clock clock;

    /**
     * Instantiates a new tenant deprovisioning service.
     *
     * @param tenantService the tenant service
     * @param userService the user service
     * @param initializationService the initialization of each activated tenant
     * @param participants every teardown participant
     * @param defaultTenant the default tenant
     */
    @Autowired
    TenantDeprovisioningService(TenantService tenantService, UserService userService, TenantInitializationService initializationService,
            List<TenantTeardownParticipant> participants,
            @DefaultTenant org.eclipse.dirigible.components.base.tenant.Tenant defaultTenant) {
        this(tenantService, userService, initializationService, participants, defaultTenant.getId(), Clock.systemUTC());
    }

    /**
     * Instantiates a new tenant deprovisioning service with a given clock - the seam the tests use.
     *
     * @param tenantService the tenant service
     * @param userService the user service
     * @param initializationService the initialization of each activated tenant
     * @param participants every teardown participant
     * @param defaultTenantId the default tenant's id
     * @param clock the clock
     */
    TenantDeprovisioningService(TenantService tenantService, UserService userService, TenantInitializationService initializationService,
            List<TenantTeardownParticipant> participants, String defaultTenantId, Clock clock) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.initializationService = initializationService;
        this.participants = participants.stream()
                                        .sorted(Comparator.comparingInt(TenantTeardownParticipant::order))
                                        .toList();
        this.defaultTenantId = defaultTenantId;
        this.clock = clock;
    }

    /**
     * Removes the tenant.
     *
     * @param tenantId the tenant id
     * @return true when a tenant was removed, false when there was none
     */
    boolean deprovision(String tenantId) {
        if (!TenantIds.isValid(tenantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, TenantIds.invalidMessage(tenantId));
        }
        if (defaultTenantId.equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The default tenant [" + tenantId + "] can never be deprovisioned");
        }
        Optional<Tenant> found = tenantService.findById(tenantId);
        if (found.isEmpty()) {
            LOGGER.info("There is no tenant [{}] - nothing to deprovision.", tenantId);
            return false;
        }
        Tenant tenant = found.get();
        refuseRunningActivation(tenantId);

        if (TenantStatus.DEPROVISIONING != tenant.getStatus()) {
            LOGGER.info("Deprovisioning tenant [{}] - moving it from [{}] to [{}].", tenantId, tenant.getStatus(),
                    TenantStatus.DEPROVISIONING);
            tenant.setStatus(TenantStatus.DEPROVISIONING);
            // persisted before anything is torn down, and saving evicts the tenant caches as well
            tenant = tenantService.save(tenant);
        } else {
            LOGGER.info("Tenant [{}] is already being deprovisioned. Resuming.", tenantId);
        }

        for (TenantTeardownParticipant participant : participants) {
            String name = ClassUtils.getUserClass(participant)
                                    .getSimpleName();
            try {
                participant.teardown(tenantId);
            } catch (RuntimeException ex) {
                LOGGER.error("Deprovisioning tenant [{}] stopped at [{}]. The tenant stays [{}]; repeating the call resumes it.", tenantId,
                        name, TenantStatus.DEPROVISIONING, ex);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Deprovisioning tenant [" + tenantId + "] stopped at ["
                        + name + "]: " + ex.getMessage() + " - the tenant stays DEPROVISIONING, and repeating the call resumes it", ex);
            }
            LOGGER.info("Deprovisioning tenant [{}]: [{}] done.", tenantId, name);
        }

        userService.findUsersByTenantId(tenantId)
                   .forEach(user -> userService.deleteUser(user.getId()));
        tenantService.delete(tenant);
        LOGGER.info("Deprovisioned tenant [{}].", tenantId);
        return true;
    }

    /**
     * Refuses to tear down a tenant whose initialization is still running: the teardown would race the
     * initialization it has to undo. An initialization open for longer than
     * {@link DirigibleConfig#TENANT_PROVISIONING_DEPROVISION_ACTIVATION_STALE_MINUTES} is taken to be
     * stuck - one a restart could not resume, say - and no longer holds the removal off.
     *
     * @param tenantId the tenant id
     */
    private void refuseRunningActivation(String tenantId) {
        Optional<TenantInitialization> initialization = initializationService.find(tenantId)
                                                                             .filter(TenantInitialization::isOpen);
        if (initialization.isEmpty()) {
            return;
        }
        Duration bound = Duration.ofMinutes(DirigibleConfig.TENANT_PROVISIONING_DEPROVISION_ACTIVATION_STALE_MINUTES.getIntValue());
        Instant requestedAt = initialization.get()
                                            .getRequestedAt();
        if (requestedAt != null && requestedAt.plus(bound)
                                              .isBefore(clock.instant())) {
            LOGGER.warn("The initialization of tenant [{}] has been open since [{}], longer than {} - deprovisioning it anyway.", tenantId,
                    requestedAt, bound);
            return;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Tenant [" + tenantId + "] is being activated - retry once its initialization has settled");
    }
}
