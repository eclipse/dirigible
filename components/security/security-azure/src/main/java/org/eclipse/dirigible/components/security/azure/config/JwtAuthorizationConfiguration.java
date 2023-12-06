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
package org.eclipse.dirigible.components.security.azure.config;

import org.eclipse.dirigible.components.security.azure.AzureOidcUserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(name = "azure.authz.enabled", havingValue = "true")
@EnableConfigurationProperties(JwtAuthorizationProperties.class)
public class JwtAuthorizationConfiguration {

    @Autowired
    AzureOidcUserFactory userServiceFactory;

    @Bean
    SecurityFilterChain customJwtSecurityChain(HttpSecurity http, JwtAuthorizationProperties props) throws Exception {
        // @formatter:off
        return http
          .authorizeRequests( r -> r.anyRequest().authenticated())
          .oauth2Login(oauth2 -> {
              oauth2.userInfoEndpoint(ep ->
                ep.oidcUserService(userServiceFactory.createUserService(props)));
          })
        .build();
        // @formatter:on
    }

}
