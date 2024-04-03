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
package org.eclipse.dirigible.integration.tests.api.rest;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.eclipse.dirigible.integration.tests.TenantCreator;
import org.eclipse.dirigible.tests.DirigibleTestTenant;
import org.eclipse.dirigible.tests.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.equalTo;

class TenantResolutionIT extends IntegrationTest {

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private TenantCreator tenantCreator;

    @Test
    void testDefaultTenantResolution() {
        testHealthIsUp("localhost", null);
        testHealthIsUp("default.localhost", null);
    }

    private void testHealthIsUp(String host, String xForwardedHost) {
        restAssuredExecutor.execute(() -> testHealthIsUp(xForwardedHost), host);
    }

    private void testHealthIsUp(String xForwardedHost) {
        RequestSpecification requestSpec = RestAssured.given();

        if (null != xForwardedHost) {
            requestSpec = requestSpec.header("x-forwarded-host", xForwardedHost);
        }
        requestSpec.when()
                   .get("/actuator/health")
                   .then()
                   .statusCode(200)
                   .body("status", equalTo("UP"));
    }

    @Test
    void testRegisteredTenantResolution() {
        DirigibleTestTenant tenant1 = new DirigibleTestTenant("test-tenant-1");
        tenantCreator.createTenant(tenant1);
        waitForTenantProvisioning(tenant1);

        testHealthIsUp(tenant1.getHost(), null);
        testHealthIsUp("212.39.89.114", tenant1.getHost());
    }

}
