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
package org.eclipse.dirigible.components.security.oauth2;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.eclipse.dirigible.components.base.http.access.HttpSecurityURIConfigurator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@ConditionalOnProperty(name = "oauth2.enabled", havingValue = "true")
public class OAuth2SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http.authorizeRequests()
        // .anyRequest().authenticated()
        // .and()
        // .oauth2Login();

        http.cors()
            .and()
            .csrf()
            .disable()
            .oauth2Login()
            .and()
            // .authorizeRequests()
            // .antMatchers("/login/**").permitAll()
            // .antMatchers("/error/**").permitAll()
            // .antMatchers("/error.html").permitAll()
            // .antMatchers("/index-busy.html").permitAll()
            // .antMatchers("/stomp").permitAll()
            // .antMatchers("/actuator/**").permitAll()
            // .antMatchers("/*").fullyAuthenticated()
            // .anyRequest().authenticated()
            // .and()
            // .formLogin()
            // .and()
            // .logout().deleteCookies("JSESSIONID")
            // .and()
            .headers()
            .frameOptions()
            .disable();

        HttpSecurityURIConfigurator.configure(http);

        return http.build();
    }

    // @Bean
    // public InMemoryUserDetailsManager userDetailsService() {
    // String username =
    // org.eclipse.dirigible.commons.config.Configuration.get("DIRIGIBLE_BASIC_USERNAME", "YWRtaW4=");
    // // admin
    // String password =
    // org.eclipse.dirigible.commons.config.Configuration.get("DIRIGIBLE_BASIC_PASSWORD", "YWRtaW4=");
    // // admin
    // UserDetails user = User
    // .withUsername(new String(new Base64().decode(username.getBytes()),
    // StandardCharsets.UTF_8).trim())
    // .password("{noop}" + new String(new Base64().decode(password.getBytes()),
    // StandardCharsets.UTF_8).trim())
    // .roles("DEVELOPER", "OPERATOR")
    // .build();
    // return new InMemoryUserDetailsManager(user);
    // }

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    // CorsConfiguration configuration = new CorsConfiguration();
    // configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    // configuration.setAllowCredentials(true);
    // configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers","Access-Control-Allow-Origin","Access-Control-Request-Method",
    // "Access-Control-Request-Headers", "Origin", "Cache-Control", "Content-Type", "Authorization"));
    // configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Headers","Access-Control-Allow-Origin","Access-Control-Request-Method",
    // "Access-Control-Request-Headers", "Origin", "Cache-Control", "Content-Type", "Authorization"));
    // configuration.setAllowedMethods(Arrays.asList("HEAD", "DELETE", "GET", "POST", "PATCH", "PUT"));
    // UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // source.registerCorsConfiguration("/**", configuration);
    // return source;
    // }

}
