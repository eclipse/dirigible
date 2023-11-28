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
package org.eclipse.dirigible.integration.tests.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.stream.Stream;
import org.eclipse.dirigible.components.base.http.access.DirigibleRole.RoleNames;
import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

class SecurityIT extends IntegrationTest {

    @Autowired
    private MockMvc mvc;

    @ParameterizedTest
    @MethodSource("providePublicEndpointsParams")
    void testPublicEndpoint(String path, HttpStatus expectedStatusCode) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().is(expectedStatusCode.value()));
    }

    private static Stream<Arguments> providePublicEndpointsParams() {
        return Stream.of(Arguments.of("/actuator/health", HttpStatus.OK), //
                Arguments.of("/error.html", HttpStatus.OK));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/spring-admin", "/actuator/heapdump"})
    void testProtectedEndpointWithoutUnauthenticatedRequest(String path) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/heapdump"})
    @WithMockUser(username = "user_without_roles", roles = {"SOME_UNUSED_ROLE"})
    void testProtectedEndpointsWithUnauthorizedUser(String path) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("provideOperatorEndpointsParams")
    @WithMockUser(username = "operator", roles = {RoleNames.OPERATOR})
    void testOperatorEndpointIsAccessible(String path, HttpStatus expectedStatusCode) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().is(expectedStatusCode.value()));
    }

    private static Stream<Arguments> provideOperatorEndpointsParams() {
        return Stream.of(Arguments.of("/spring-admin", HttpStatus.NOT_FOUND), //
                Arguments.of("/actuator/heapdump", HttpStatus.OK));
    }

    @ParameterizedTest
    @MethodSource("provideDeveloperEndpointsParams")
    @WithMockUser(username = "developer", roles = {RoleNames.DEVELOPER})
    void testDeveloperEndpointIsAccessible(String path, HttpStatus expectedStatusCode) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().is(expectedStatusCode.value()));
    }

    private static Stream<Arguments> provideDeveloperEndpointsParams() {
        return Stream.of(Arguments.of("/services/ide/123", HttpStatus.NOT_FOUND),
                Arguments.of("/websockets/ide/123", HttpStatus.NOT_FOUND));
    }

}
