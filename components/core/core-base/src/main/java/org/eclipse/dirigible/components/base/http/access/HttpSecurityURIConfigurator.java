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
package org.eclipse.dirigible.components.base.http.access;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * The Class HttpSecurityURIConfigurator.
 */
public class HttpSecurityURIConfigurator {

    private static final String[] PUBLIC_PATTERNS = {//
            "/", //
            "/home", //
            "/index.html", //
            "/logout", //
            "/index-busy.html", //
            "/stomp", //
            "/error/**", //
            "/error.html", //
            "/favicon.ico", //
            "/public/**", //
            "/webjars/**", //
            "/services/core/theme/**", //
            "/services/core/version/**", //
            "/services/core/healthcheck/**", //
            "/services/web/resources/**", //
            "/services/web/resources-core/**", //
            "/services/js/resources-core/**", //
            "/services/js/resources-core/**", //
            "/services/integrations/**", //
            "/actuator/**"};

    private static final String[] AUTHENTICATED_PATTERNS = {//
            "/services/**", //
            "/websockets/**", //
            "/api-docs/swagger-config", //
            "/api-docs/**", //
            "/odata/**", //
            "/swagger-ui/**"};

    private static final String[] DEVELOPER_PATTERNS = {//
            "/services/ide/**", //
            "/websockets/ide/**"};

    /**
     * Configure.
     *
     * @param http the http
     * @throws Exception the exception
     */
    public static void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> //
        authz.requestMatchers(PUBLIC_PATTERNS)
             .permitAll()

             // Authenticated
             .requestMatchers(AUTHENTICATED_PATTERNS)
             .authenticated()

             // "Developer" role required
             .requestMatchers(DEVELOPER_PATTERNS)
             .hasRole("Developer")

             // Deny all other requests
             .anyRequest()
             .denyAll());
    }

}
