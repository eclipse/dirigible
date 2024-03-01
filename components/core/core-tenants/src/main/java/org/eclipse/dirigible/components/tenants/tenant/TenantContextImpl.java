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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.dirigible.components.base.tenant.CallableNoResultAndException;
import org.eclipse.dirigible.components.base.tenant.CallableResultAndNoException;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.base.tenant.TenantResult;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class TenantContextImpl implements TenantContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantContextImpl.class);

    private static final ThreadLocal<Tenant> currentTenantHolder = new ThreadLocal<>();

    private final TenantRepository tenantRepository;

    TenantContextImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public boolean isNotInitialized() {
        return !isInitialized();
    }

    @Override
    public boolean isInitialized() {
        return null != currentTenantHolder.get();
    }

    @Override
    public Tenant getCurrentTenant() {
        Tenant tenant = currentTenantHolder.get();
        if (null == tenant) {
            throw new IllegalStateException("Attempting to get current tenant but it is not initialized yet.");
        }
        LOGGER.debug("Getting current tenant [{}]", tenant);
        return tenant;
    }

    @Override
    public <Result> Result execute(Tenant tenant, CallableResultAndNoException<Result> callable) {
        Tenant currentTenant = isInitialized() ? getCurrentTenant() : null;
        setCurrentTenant(tenant);
        try {
            return callable.call();
        } finally {
            setCurrentTenant(currentTenant);
        }
    }

    @Override
    public void execute(Tenant tenant, CallableNoResultAndException callable) throws Exception {
        Tenant currentTenant = isInitialized() ? getCurrentTenant() : null;
        setCurrentTenant(tenant);
        try {
            callable.call();
        } finally {
            setCurrentTenant(currentTenant);
        }
    }

    private void setCurrentTenant(Tenant tenant) {
        LOGGER.debug("Setting current tenant to [{}]", tenant);
        currentTenantHolder.set(tenant);
    }

    @Override
    public <Result> List<TenantResult<Result>> executeForEachTenant(CallableResultAndNoException<Result> callable) {
        List<Tenant> tenants = tenantRepository.findByStatus(TenantStatus.PROVISIONED)
                                               .stream()
                                               .map(TenantImpl::createFromEntity)
                                               .collect(Collectors.toList());
        LOGGER.info("Will execute code for tenants [{}]...", tenants);
        List<TenantResult<Result>> results = new ArrayList<>(tenants.size());

        tenants.forEach(tenant -> {
            Result result = execute(tenant, callable);
            results.add(new TenantResultImpl<>(tenant, result));
        });
        return results;
    }

}
