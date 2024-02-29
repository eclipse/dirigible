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

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.eclipse.dirigible.components.tenants.tenant.TenantContext.CallableNoResultAndException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class TenantContext.
 */
@Component
public class TenantContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantContext.class);

    /** The Constant currentTenantId. */
    private static final ThreadLocal<Tenant> currentTenantHolder = new ThreadLocal<>();

    private static TenantRepository tenantRepository;

    @Autowired
    TenantContext(TenantRepository tenantRepository) {
        TenantContext.tenantRepository = tenantRepository;
    }

    /**
     * Checks if is not initialized.
     *
     * @return true, if is not initialized
     */
    public static boolean isNotInitialized() {
        return !isInitialized();
    }

    /**
     * Checks if is initialized.
     *
     * @return true, if is initialized
     */
    public static boolean isInitialized() {
        return null != currentTenantHolder.get();
    }

    /**
     * Gets the current tenant.
     *
     * @return the current tenant
     */
    public static Tenant getCurrentTenant() {
        Tenant tenant = currentTenantHolder.get();
        if (null == tenant) {
            throw new IllegalStateException("Attempting to get current tenant but it is not initialized yet.");
        }
        LOGGER.debug("Getting current tenant [{}]", tenant);
        return tenant;
    }

    /**
     * This method will execute callable.call() method on behalf of the specified tenant.
     *
     * @param <Result> the result of the called callable
     * @param tenant the tenant
     * @param callable the callable
     * @return the result
     * @throws Exception the exception which is thrown by the passed callable
     */
    public static <Result> Result execute(Tenant tenant, java.util.concurrent.Callable<Result> callable) throws Exception {
        Tenant currentTenant = isInitialized() ? getCurrentTenant() : null;
        setCurrentTenant(tenant);
        try {
            return callable.call();
        } finally {
            setCurrentTenant(currentTenant);
        }
    }

    /**
     * This method will execute callable.call() method on behalf of the specified tenant.
     *
     * @param tenant the tenant
     * @param callable the callable
     * @throws Exception the exception which is thrown by the passed callable
     */
    public static void execute(Tenant tenant, CallableNoResultAndException callable) throws Exception {
        Tenant currentTenant = isInitialized() ? getCurrentTenant() : null;
        setCurrentTenant(tenant);
        try {
            callable.call();
        } finally {
            setCurrentTenant(currentTenant);
        }
    }

    /**
     * Sets the current tenant.
     *
     * @param tenantId the new current tenant
     */
    private static void setCurrentTenant(Tenant tenant) {
        LOGGER.debug("Setting current tenant to [{}]", tenant);
        currentTenantHolder.set(tenant);
    }

    public static void executeForEachTenant(CallableNoResultAndException callable) throws Exception {
        List<Tenant> tenants = tenantRepository.findByStatus(TenantStatus.PROVISIONED)
                                               .stream()
                                               .map(TenantImpl::createFromEntity)
                                               .collect(Collectors.toList());
        LOGGER.info("Will execute code for tenants [{}]", tenants);
        for (Tenant tenant : tenants) {
            execute(tenant, callable);
        }
    }

    @FunctionalInterface
    public interface CallableNoResultAndException {

        void call() throws Exception;
    }

}
