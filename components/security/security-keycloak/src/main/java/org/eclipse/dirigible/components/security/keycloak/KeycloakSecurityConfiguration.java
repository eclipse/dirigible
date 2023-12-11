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
package org.eclipse.dirigible.components.security.keycloak;

// TODO to be adapted
//
// @KeycloakConfiguration
// @ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
// class KeycloakSecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {
//
// @Autowired
// public void configureGlobal(AuthenticationManagerBuilder auth) {
// KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
// keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper()); //
// prefix = "ROLE_
// auth.authenticationProvider(keycloakAuthenticationProvider);
// }
//
// @Override
// protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
// return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
// }
//
// @Override
// protected void configure(HttpSecurity http) throws Exception {
// super.configure(http);
//
// http.cors(Customizer.withDefaults())
// .csrf((csrf) -> csrf.disable())
// .headers(headers -> headers.frameOptions(frameOpts -> frameOpts.sameOrigin()));
//
// HttpSecurityURIConfigurator.configure(http);
// }
//
// @Override
// public void init(WebSecurity builder) throws Exception {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public void configure(WebSecurity builder) throws Exception {
// // TODO Auto-generated method stub
// }
//
// }
