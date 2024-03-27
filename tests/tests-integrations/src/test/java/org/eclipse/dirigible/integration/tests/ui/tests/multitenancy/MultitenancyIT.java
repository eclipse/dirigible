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
package org.eclipse.dirigible.integration.tests.ui.tests.multitenancy;

import org.awaitility.Awaitility;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.integration.tests.ui.DirigibleWorkbench;
import org.eclipse.dirigible.integration.tests.ui.EdmView;
import org.eclipse.dirigible.integration.tests.ui.tests.UserInterfaceIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private Dirigible dirigible;

    @Autowired
    private EdmView edmView;

    @Autowired
    @DefaultTenant
    private Tenant defTenant;

    @BeforeEach
    void initTestTenants() {
        defaultTenant = new DirigibleTestTenant(defTenant);
        testTenant1 = new DirigibleTestTenant("test-tenant-1");
        testTenant2 = new DirigibleTestTenant("test-tenant-2");
        testTenants = List.of(defaultTenant, testTenant1, testTenant2);
    }

    @Test
    void test() {
        createTestTenants();
        prepareTestProject();

        waitForTenantsProvisioning();

        assertTestProjectAccessibleByTenants();
    }

    private void createTestTenants() {
        testTenants.stream()
                   .forEach(t -> tenantCreator.createTenant(t));
    }

    private void prepareTestProject() {
        testProject.copyToRepository();

        dirigible.openHomePage();

        DirigibleWorkbench workbench = dirigible.openWorkbench();
        workbench.expandProject(testProject.getRootFolderName());
        workbench.openFile(testProject.getEdmFileName());

        edmView.regenerate();

        workbench.publishAll();
    }

    private void waitForTenantsProvisioning() {
        testTenants.stream()
                   .forEach(t -> waitForTenantProvisioning(t, 30));
    }

    private void assertTestProjectAccessibleByTenants() {
        testTenants.stream()
                   .forEach(t -> testProject.assertHomePageAccessibleByTenant(t));
    }

    private void waitForTenantProvisioning(DirigibleTestTenant tenant, int waitSeconds) {
        Awaitility.await()
                  .atMost(waitSeconds, TimeUnit.SECONDS)
                  .until(() -> tenantCreator.isTenantProvisioned(tenant));
    }
}
