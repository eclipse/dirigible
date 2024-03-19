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

import org.eclipse.dirigible.components.base.tenant.*;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Class TenantContextImpl.
 */
@Component
class TenantContextImpl implements TenantContext {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantContextImpl.class);

    /** The Constant currentTenantHolder. */
    private static final ThreadLocal<Tenant> currentTenantHolder = new ThreadLocal<>();

    /** The tenant service. */
    private final TenantService tenantService;

    /**
     * Instantiates a new tenant context impl.
     *
     * @param tenantService the tenant service
     */
    TenantContextImpl(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Checks if is not initialized.
     *
     * @return true, if is not initialized
     */
    @Override
    public boolean isNotInitialized() {
        return !isInitialized();
    }

    /**
     * Checks if is initialized.
     *
     * @return true, if is initialized
     */
    @Override
    public boolean isInitialized() {
        return null != currentTenantHolder.get();
    }

    /**
     * Execute.
     *
     * @param <Result> the generic type
     * @param tenantId the tenant id
     * @param callable the callable
     * @return the result
     * @throws TenantNotFoundException the tenant not found exception
     */
    @Override
    public <Result> Result execute(String tenantId, CallableResultAndNoException<Result> callable) throws TenantNotFoundException {
        org.eclipse.dirigible.components.tenants.domain.Tenant tenantEntity = tenantService.findById(tenantId)
                                                                                           .orElseThrow(() -> new TenantNotFoundException(
                                                                                                   tenantId));
        Tenant tenant = TenantImpl.createFromEntity(tenantEntity);
        return execute(tenant, callable);
    }

    /**
     * Execute.
     *
     * @param <Result> the generic type
     * @param tenant the tenant
     * @param callable the callable
     * @return the result
     */
    @Override
    public <Result> Result execute(Tenant tenant, CallableResultAndNoException<Result> callable) {
        Tenant currentTenant = safelyGetCurrentTenant();
        setCurrentTenant(tenant);
        try {
            return callable.call();
        } finally {
            setCurrentTenant(currentTenant);
        }
    }

    /**
     * Safely get current tenant.
     *
     * @return the tenant
     */
    private Tenant safelyGetCurrentTenant() {
        return isInitialized() ? getCurrentTenant() : null;
    }

    /**
     * Execute for each tenant.
     *
     * @param <Result> the generic type
     * @param callable the callable
     * @return the list
     */
    @Override
    public <Result> List<TenantResult<Result>> executeForEachTenant(CallableResultAndNoException<Result> callable) {
        Set<Tenant> tenants = getProvisionedTenants();
        LOGGER.debug("Will execute code for [{}] tenants [{}]...", tenants.size(), tenants);
        List<TenantResult<Result>> results = new ArrayList<>(tenants.size());

        tenants.forEach(tenant -> {
            Result result = execute(tenant, callable);
            results.add(new TenantResultImpl<>(tenant, result));
        });
        return results;
    }

    /**
     * Gets the provisioned tenants.
     *
     * @return the provisioned tenants
     */
    private Set<Tenant> getProvisionedTenants() {
        Set<Tenant> tenants = tenantService.findByStatus(TenantStatus.PROVISIONED)
                                           .stream()
                                           .map(TenantImpl::createFromEntity)
                                           .collect(Collectors.toSet());
        Set<Tenant> allTenants = new HashSet<>(tenants);
        allTenants.add(TenantImpl.getDefaultTenant());
        allTenants.addAll(tenants);
        return allTenants;
    }

    /**
     * Execute with possible exception.
     *
     * @param <Result> the generic type
     * @param tenant the tenant
     * @param callable the callable
     * @return the result
     * @throws Exception the exception
     */
    @Override
    public <Result> Result executeWithPossibleException(Tenant tenant, CallableResultAndException<Result> callable) throws Exception {
        Tenant currentTenant = safelyGetCurrentTenant();
        setCurrentTenant(tenant);
        try {
            return callable.call();
        } finally {
            setCurrentTenant(currentTenant);
        }
    }

    /**
     * Gets the current tenant.
     *
     * @return the current tenant
     */
    @Override
    public Tenant getCurrentTenant() {
        Tenant tenant = currentTenantHolder.get();
        if (null == tenant) {
            throw new IllegalStateException("Attempting to get current tenant but it is not initialized yet.");
        }
        LOGGER.debug("Getting current tenant [{}]", tenant);
        return tenant;
    }

    /**
     * Sets the current tenant.
     *
     * @param tenant the new current tenant
     */
    private void setCurrentTenant(Tenant tenant) {
        LOGGER.debug("Setting current tenant to [{}]", tenant);
        currentTenantHolder.set(tenant);
    }

}
