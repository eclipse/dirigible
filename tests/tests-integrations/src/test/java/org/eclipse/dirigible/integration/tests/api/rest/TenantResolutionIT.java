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
import org.hamcrest.Matchers;
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
        testHealthIsAccessible("localhost", null);
        testHealthIsAccessible("default.localhost", null);
    }

    private void testHealthIsAccessible(String host, String xForwardedHost) {
        restAssuredExecutor.execute(() -> testHealthIsAccessible(xForwardedHost), host);
    }

    private void testHealthIsAccessible(String xForwardedHost) {
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
    void testHostWitchDoesNotMatchTheDefaultRegexIsResolvedAsDefaultTenant() {
        testHealthIsAccessible("host-which-does-not-match-the-default-tenant-regex", null);
    }

    @Test
    void testRegisteredTenantResolution() {
        DirigibleTestTenant tenant1 = new DirigibleTestTenant("test-tenant-1");
        tenantCreator.createTenant(tenant1);
        waitForTenantProvisioning(tenant1);

        testHealthIsAccessible(tenant1.getHost(), null);
        testHealthIsAccessible("212.39.89.114", tenant1.getHost());
    }

    @Test
    void testUnregisteredTenantResolution() {
        testHealthIsNotAccessible("unregistered-tenant.localhost", null);
        testHealthIsNotAccessible("212.39.89.114", "unregistered-tenant.localhost");
    }

    private void testHealthIsNotAccessible(String host, String xForwardedHost) {
        restAssuredExecutor.execute(() -> testHealthIsNotAccessible(xForwardedHost), host);
    }

    private void testHealthIsNotAccessible(String xForwardedHost) {
        RequestSpecification requestSpec = RestAssured.given();

        if (null != xForwardedHost) {
            requestSpec = requestSpec.header("x-forwarded-host", xForwardedHost);
        }
        requestSpec.when()
                   .get("/actuator/health")
                   .then()
                   .statusCode(404)
                   .body("status", Matchers.equalTo(404))
                   .body("message", Matchers.equalTo("There is no registered tenant for the current host"));
    }
}
