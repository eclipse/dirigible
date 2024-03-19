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

public class TestTenantContext implements TenantContext {

    private static final String TENANT_ID = "1e7252b1-3bca-4285-bd4e-60e19886d063";
    private static final String TENANT_NAME = "test-tenant";
    private static final String TENANT_SUBDOMAIN = "test";
    private static final boolean DEFUALT_TENANT = false;


    private static class TestTenantResult<Result> implements TenantResult<Result> {

        private final Tenant tenant;

        private final Result result;

        public TestTenantResult(Tenant tenant, Result result) {
            this.tenant = tenant;
            this.result = result;
        }

        @Override
        public Tenant getTenant() {
            return tenant;
        }

        @Override
        public Result getResult() {
            return result;
        }
    }


    private static class TestTenant implements Tenant {
        private final String id;
        private final String name;
        private final String subdomain;
        private final boolean defaultTenant;

        public TestTenant(String id, String name, String subdomain, boolean defaultTenant) {
            this.id = id;
            this.name = name;
            this.subdomain = subdomain;
            this.defaultTenant = defaultTenant;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isDefault() {
            return defaultTenant;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getSubdomain() {
            return subdomain;
        }
    }

    @Override
    public boolean isNotInitialized() {
        return false;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public Tenant getCurrentTenant() {
        return null;
    }

    @Override
    public <Result> Result execute(Tenant tenant, CallableResultAndNoException<Result> callable) {
        return callable.call();
    }

    @Override
    public <Result> Result execute(String tenantId, CallableResultAndNoException<Result> callable) {
        return callable.call();
    }

    @Override
    public <Result> Result executeWithPossibleException(Tenant tenant, CallableResultAndException<Result> callable) throws Exception {
        return callable.call();
    }

    @Override
    public <Result> List<TenantResult<Result>> executeForEachTenant(CallableResultAndNoException<Result> callable) {
        Result result = callable.call();
        Tenant tenant = new TestTenant(TENANT_ID, TENANT_NAME, TENANT_SUBDOMAIN, DEFUALT_TENANT);
        TenantResult<Result> tr = new TestTenantResult<>(tenant, result);
        return List.of(tr);
    }
}
