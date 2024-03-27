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
package org.eclipse.dirigible.tests.framework.restassured;

import io.restassured.RestAssured;
import io.restassured.authentication.AuthenticationScheme;
import org.eclipse.dirigible.tests.framework.DirigibleTestTenant;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class RestAssuredExecutor {

    private final int port;

    public RestAssuredExecutor(@LocalServerPort int port) {
        this.port = port;
    }

    /**
     * Execute REST Assured validation in tenant scope.
     *
     * @param tenant tenant
     * @param callable rest assured validation
     */
    public void execute(DirigibleTestTenant tenant, CallableNoResultAndNoException callable) {
        String configuredBaseURI = RestAssured.baseURI;
        int configuredPort = RestAssured.port;
        AuthenticationScheme configuredAuthentication = RestAssured.authentication;
        try {
            RestAssured.baseURI = "http://" + tenant.getSubdomain() + ".localhost";
            RestAssured.port = port;

            RestAssured.authentication = RestAssured.preemptive()
                                                    .basic(tenant.getUsername(), tenant.getPassword());

            callable.call();
        } finally {
            RestAssured.baseURI = configuredBaseURI;
            RestAssured.port = configuredPort;
            RestAssured.authentication = configuredAuthentication;
        }
    }
}
