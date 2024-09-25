/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.security.snowflake;

import org.eclipse.dirigible.components.base.http.access.CorsConfigurationSourceProvider;
import org.eclipse.dirigible.components.base.http.access.HttpSecurityURIConfigurator;
import org.eclipse.dirigible.components.tenants.tenant.TenantContextInitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Profile("snowflake")
@Configuration
@EnableWebSecurity
public class SnowflakeSecurityConfig {

    private final SnowflakeAuthFilter snowflakeAuthFilter;
    private final SnowflakeLogoutHandler snowflakeLogoutHandler;

    SnowflakeSecurityConfig(SnowflakeAuthFilter snowflakeAuthFilter, SnowflakeLogoutHandler snowflakeLogoutHandler) {
        this.snowflakeAuthFilter = snowflakeAuthFilter;
        this.snowflakeLogoutHandler = snowflakeLogoutHandler;
    }

    /**
     * Filter chain.
     *
     * @param http the http
     * @param tenantContextInitFilter the tenant context init filter
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, TenantContextInitFilter tenantContextInitFilter) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .logout(logout -> logout.deleteCookies("JSESSIONID")
                                    .addLogoutHandler(snowflakeLogoutHandler)
                                    .logoutSuccessUrl("/"))
            .headers(headers -> headers.frameOptions(frameOpts -> frameOpts.disable()))
            .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .addFilterBefore(tenantContextInitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(snowflakeAuthFilter, UsernamePasswordAuthenticationFilter.class);

        HttpSecurityURIConfigurator.configure(http);

        return http.build();
    }

    /**
     * Cors configuration source.
     *
     * @return the cors configuration source
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return CorsConfigurationSourceProvider.get();
    }

}
