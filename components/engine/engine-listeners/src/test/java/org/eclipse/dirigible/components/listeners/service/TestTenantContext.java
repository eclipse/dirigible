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
package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.components.base.tenant.*;

import java.util.List;

/**
 * The Class TestTenantContext.
 */
public class TestTenantContext implements TenantContext {

    /** The Constant TENANT_ID. */
    private static final String TENANT_ID = "1e7252b1-3bca-4285-bd4e-60e19886d063";

    /** The Constant TENANT_NAME. */
    private static final String TENANT_NAME = "test-tenant";

    /** The Constant TENANT_SUBDOMAIN. */
    private static final String TENANT_SUBDOMAIN = "test";

    /** The Constant DEFUALT_TENANT. */
    private static final boolean DEFUALT_TENANT = false;


    /**
     * The Class TestTenantResult.
     *
     * @param <Result> the generic type
     */
    private static class TestTenantResult<Result> implements TenantResult<Result> {

        /** The tenant. */
        private final Tenant tenant;

        /** The result. */
        private final Result result;

        /**
         * Instantiates a new test tenant result.
         *
         * @param tenant the tenant
         * @param result the result
         */
        public TestTenantResult(Tenant tenant, Result result) {
            this.tenant = tenant;
            this.result = result;
        }

        /**
         * Gets the tenant.
         *
         * @return the tenant
         */
        @Override
        public Tenant getTenant() {
            return tenant;
        }

        /**
         * Gets the result.
         *
         * @return the result
         */
        @Override
        public Result getResult() {
            return result;
        }
    }


    /**
     * The Class TestTenant.
     */
    private static class TestTenant implements Tenant {

        /** The id. */
        private final String id;

        /** The name. */
        private final String name;

        /** The subdomain. */
        private final String subdomain;

        /** The default tenant. */
        private final boolean defaultTenant;

        /**
         * Instantiates a new test tenant.
         *
         * @param id the id
         * @param name the name
         * @param subdomain the subdomain
         * @param defaultTenant the default tenant
         */
        public TestTenant(String id, String name, String subdomain, boolean defaultTenant) {
            this.id = id;
            this.name = name;
            this.subdomain = subdomain;
            this.defaultTenant = defaultTenant;
        }

        /**
         * Gets the id.
         *
         * @return the id
         */
        @Override
        public String getId() {
            return id;
        }

        /**
         * Checks if is default.
         *
         * @return true, if is default
         */
        @Override
        public boolean isDefault() {
            return defaultTenant;
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * Gets the subdomain.
         *
         * @return the subdomain
         */
        @Override
        public String getSubdomain() {
            return subdomain;
        }
    }

    /**
     * Checks if is not initialized.
     *
     * @return true, if is not initialized
     */
    @Override
    public boolean isNotInitialized() {
        return false;
    }

    /**
     * Checks if is initialized.
     *
     * @return true, if is initialized
     */
    @Override
    public boolean isInitialized() {
        return false;
    }

    /**
     * Gets the current tenant.
     *
     * @return the current tenant
     */
    @Override
    public Tenant getCurrentTenant() {
        return null;
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
        return callable.call();
    }

    /**
     * Execute.
     *
     * @param <Result> the generic type
     * @param tenantId the tenant id
     * @param callable the callable
     * @return the result
     */
    @Override
    public <Result> Result execute(String tenantId, CallableResultAndNoException<Result> callable) {
        return callable.call();
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
        return callable.call();
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
        Result result = callable.call();
        Tenant tenant = new TestTenant(TENANT_ID, TENANT_NAME, TENANT_SUBDOMAIN, DEFUALT_TENANT);
        TenantResult<Result> tr = new TestTenantResult<>(tenant, result);
        return List.of(tr);
    }
}
