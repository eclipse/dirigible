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

import org.awaitility.Awaitility;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.integration.tests.TenantCreator;
import org.eclipse.dirigible.integration.tests.ui.TestProject;
import org.eclipse.dirigible.tests.DirigibleTestTenant;
import org.eclipse.dirigible.tests.framework.Browser;
import org.eclipse.dirigible.tests.framework.BrowserFactory;
import org.eclipse.dirigible.tests.framework.HtmlElementType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

class MultitenancyIT extends UserInterfaceIntegrationTest {

    @Autowired
    private TenantCreator tenantCreator;

    @Autowired
    private TestProject testProject;

    @Autowired
    @DefaultTenant
    private Tenant defTenant;

    @Autowired
    private BrowserFactory browserFactory;

    @Test
    void testOpenNotRegisteredTenant() {
        Browser browser = browserFactory.createBySubdomain("unregistered-tenant");
        browser.openPath("/index.html");
        browser.assertElementExistsByTypeAndText(HtmlElementType.FD_MESSAGE_PAGE_TITLE, "Page Not Found");
    }

    @Test
    void verifyTestProject() {
        List<DirigibleTestTenant> tenants = createTenants();

        testProject.publish();
        waitForTenantsProvisioning(tenants);

        verifyTenants(tenants);
    }

    private List<DirigibleTestTenant> createTenants() {
        DirigibleTestTenant defaultTenant = createDefaultTenant();
        DirigibleTestTenant tenant1 = new DirigibleTestTenant("test-tenant-1");
        DirigibleTestTenant tenant2 = new DirigibleTestTenant("test-tenant-2");

        List<DirigibleTestTenant> tenants = List.of(defaultTenant, tenant1, tenant2);

        tenants.stream()
               .forEach(tenantCreator::createTenant);

        return tenants;
    }

    private DirigibleTestTenant createDefaultTenant() {
        return new DirigibleTestTenant(true, //
                defTenant.getName(), //
                defTenant.getId(), //
                defTenant.getSubdomain(), //
                DirigibleConfig.BASIC_ADMIN_USERNAME.getFromBase64Value(), //
                DirigibleConfig.BASIC_ADMIN_PASS.getFromBase64Value());
    }

    private void waitForTenantsProvisioning(List<DirigibleTestTenant> tenants) {
        tenants.stream()
               .forEach(this::waitForTenantProvisioning);
    }

    private void waitForTenantProvisioning(DirigibleTestTenant tenant) {
        Awaitility.await()
                  .atMost(30, TimeUnit.SECONDS)
                  .until(() -> tenantCreator.isTenantProvisioned(tenant));
    }

    private void verifyTenants(List<DirigibleTestTenant> tenants) {
        tenants.forEach(testProject::verify);
    }

}
