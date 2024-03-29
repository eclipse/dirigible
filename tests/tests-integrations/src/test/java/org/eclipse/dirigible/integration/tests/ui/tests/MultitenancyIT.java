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
package org.eclipse.dirigible.integration.tests.ui.tests;

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.integration.tests.TenantCreator;
import org.eclipse.dirigible.integration.tests.ui.TestProject;
import org.eclipse.dirigible.tests.DirigibleTestTenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

class MultitenancyIT extends UserInterfaceIntegrationTest {

    private DirigibleTestTenant defaultTenant;

    private DirigibleTestTenant testTenant1;

    private DirigibleTestTenant testTenant2;

    private List<DirigibleTestTenant> testTenants;

    @Autowired
    private TenantCreator tenantCreator;

    @Autowired
    private TestProject testProject;

    @Autowired
    @DefaultTenant
    private Tenant defTenant;

    @BeforeEach
    void initTestTenants() {
        defaultTenant = fromTenantEntity(defTenant);
        testTenant1 = new DirigibleTestTenant("test-tenant-1");
        testTenant2 = new DirigibleTestTenant("test-tenant-2");
        testTenants = List.of(defaultTenant, testTenant1, testTenant2);
    }

    private DirigibleTestTenant fromTenantEntity(Tenant tenant) {
        return new DirigibleTestTenant(tenant.isDefault(), //
                tenant.getName(), //
                tenant.getId(), //
                tenant.getSubdomain(), //
                UUID.randomUUID()
                    .toString(), //
                UUID.randomUUID()
                    .toString());
    }

    @Test
    void test() {
        createTestTenants();

        testProject.publish();
        waitForTenantsProvisioning();

        testTenants.forEach(testProject::verify);
    }

    private void createTestTenants() {
        testTenants.stream()
                   .forEach(t -> tenantCreator.createTenant(t));
    }

    private void waitForTenantsProvisioning() {
        testTenants.stream()
                   .forEach(t -> waitForTenantProvisioning(t, 30));
    }

    private void waitForTenantProvisioning(DirigibleTestTenant tenant, int waitSeconds) {
        await().atMost(waitSeconds, TimeUnit.SECONDS)
               .until(() -> tenantCreator.isTenantProvisioned(tenant));
    }

}
