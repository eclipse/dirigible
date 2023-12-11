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

    /**
     * Configure.
     *
     * @param http the http
     * @throws Exception the exception
     */
    public static void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> //
        authz.requestMatchers("/")
             .permitAll()
             .requestMatchers("/home")
             .permitAll()
             .requestMatchers("/logout")
             .permitAll()
             .requestMatchers("/index-busy.html")
             .permitAll()

             .requestMatchers("/stomp")
             .permitAll()

             .requestMatchers("/error/**")
             .permitAll()
             .requestMatchers("/error.html")
             .permitAll()

             // Public
             .requestMatchers("/favicon.ico")
             .permitAll()
             .requestMatchers("/public/**")
             .permitAll()
             .requestMatchers("/webjars/**")
             .permitAll()

             .requestMatchers("/services/core/theme/**")
             .permitAll()
             .requestMatchers("/services/core/version/**")
             .permitAll()
             .requestMatchers("/services/core/healthcheck/**")
             .permitAll()
             .requestMatchers("/services/web/resources/**")
             .permitAll()
             .requestMatchers("/services/web/resources-core/**")
             .permitAll()
             .requestMatchers("/services/js/resources-core/**")
             .permitAll()
             .requestMatchers("/services/integrations/**")
             .permitAll()

             .requestMatchers("/actuator/**")
             .permitAll()

             // Authenticated
             .requestMatchers("/services/**")
             .authenticated()
             .requestMatchers("/websockets/**")
             .authenticated()
             .requestMatchers("/odata/**")
             .authenticated()

             // Swagger UI
             .requestMatchers("/swagger-ui/**")
             .authenticated()
             .requestMatchers("/v3/api-docs/swagger-config")
             .authenticated()
             .requestMatchers("/v3/api-docs/**")
             .authenticated()

             // "Developer" role required
             .requestMatchers("/services/ide/**")
             .hasRole("Developer")
             .requestMatchers("/websockets/ide/**")
             .hasRole("Developer")

             // "Operator" role required
             // .requestMatchers("/services/ops/**").hasRole("Operator")
             // .requestMatchers("/services/transport/**").hasRole("Operator")
             // .requestMatchers("/websockets/ops/**").hasRole("Operator")

             // Deny all other requests
             .anyRequest()
             .denyAll());
    }

}
