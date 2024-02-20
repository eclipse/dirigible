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
package org.eclipse.dirigible.components.tenants.security;

import org.eclipse.dirigible.components.base.http.access.HttpSecurityURIConfigurator;
import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.eclipse.dirigible.components.tenants.tenant.TenantAuthorizationFilter;
import org.eclipse.dirigible.components.tenants.tenant.TenantFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The Class WebSecurityConfig.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class WebSecurityConfig {

    /** The tenant repository. */
    private final TenantRepository tenantRepository;

    /**
     * Instantiates a new web security config.
     *
     * @param tenantRepository the tenant repository
     */
    public WebSecurityConfig(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * Filter chain.
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.cors(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
//                    .authenticationEntryPoint(httpStatusEntryPoint())
//                    .and()
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(new TenantFilter(tenantRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new TenantAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout.deleteCookies("JSESSIONID"))
                .headers(headers -> headers.frameOptions(frameOpts -> frameOpts.disable()));

        HttpSecurityURIConfigurator.configure(http);

        // @formatter:on
        return http.build();
    }

    /**
     * Password encoder.
     *
     * @return the b crypt password encoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // private HttpStatusEntryPoint httpStatusEntryPoint() {
    // return new HttpStatusEntryPoint(HttpStatus.FORBIDDEN);
    // }
}
