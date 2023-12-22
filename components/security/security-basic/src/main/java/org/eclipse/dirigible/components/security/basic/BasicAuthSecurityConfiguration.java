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
package org.eclipse.dirigible.components.security.basic;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.components.base.http.access.DirigibleRole;
import org.eclipse.dirigible.components.base.http.access.HttpSecurityURIConfigurator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@ConditionalOnProperty(name = "basic.enabled", havingValue = "true")
public class BasicAuthSecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())
            .logout(logout -> logout.deleteCookies("JSESSIONID"))
            .headers(headers -> headers.frameOptions(frameOpts -> frameOpts.disable()));

        HttpSecurityURIConfigurator.configure(http);

        return http.build();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        String username = org.eclipse.dirigible.commons.config.Configuration.get("DIRIGIBLE_BASIC_USERNAME", "YWRtaW4="); // admin
        String password = org.eclipse.dirigible.commons.config.Configuration.get("DIRIGIBLE_BASIC_PASSWORD", "YWRtaW4="); // admin
        UserDetails user = User.withUsername(new String(new Base64().decode(username.getBytes()), StandardCharsets.UTF_8).trim())
                               .password("{noop}" + new String(new Base64().decode(password.getBytes()), StandardCharsets.UTF_8).trim())
                               .roles(DirigibleRole.DEVELOPER.getName(), DirigibleRole.OPERATOR.getName())
                               .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(
                Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Access-Control-Request-Method",
                        "Access-Control-Request-Headers", "Origin", "Cache-Control", "Content-Type", "Authorization"));
        configuration.setExposedHeaders(
                Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Access-Control-Request-Method",
                        "Access-Control-Request-Headers", "Origin", "Cache-Control", "Content-Type", "Authorization"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "DELETE", "GET", "POST", "PATCH", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
