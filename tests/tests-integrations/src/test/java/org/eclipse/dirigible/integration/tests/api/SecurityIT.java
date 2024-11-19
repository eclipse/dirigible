package org.eclipse.dirigible.integration.tests.api;

import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        return Stream.of(//
                Arguments.of("/actuator/health", HttpStatus.OK), //
                Arguments.of("/actuator/health/liveness", HttpStatus.OK), //
                Arguments.of("/actuator/health/readiness", HttpStatus.OK), //
                Arguments.of("/login", HttpStatus.OK), //
                Arguments.of("/error.html", HttpStatus.OK));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/spring-admin", "/actuator/info"})
    void testProtectedEndpointWithoutAuthentication(String path) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/info"})
    @WithMockUser(username = "user_without_roles", roles = {"SOME_UNUSED_ROLE"})
    void testProtectedEndpointsWithUnauthorizedUser(String path) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("provideOperatorEndpointsParams")
    @WithMockUser(username = "operator", roles = {Roles.RoleNames.OPERATOR})
    void testOperatorEndpointIsAccessible(String path, HttpStatus expectedStatusCode) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().is(expectedStatusCode.value()));
    }

    private static Stream<Arguments> provideOperatorEndpointsParams() {
        return Stream.of(Arguments.of("/spring-admin", HttpStatus.NOT_FOUND), //
                Arguments.of("/actuator/info", HttpStatus.OK));
    }

    @ParameterizedTest
    @MethodSource("provideDeveloperEndpointsParams")
    @WithMockUser(username = "developer", roles = {Roles.RoleNames.DEVELOPER})
    void testDeveloperEndpointIsAccessible(String path, HttpStatus expectedStatusCode) throws Exception {
        mvc.perform(get(path))
           .andExpect(status().is(expectedStatusCode.value()));
    }

    private static Stream<Arguments> provideDeveloperEndpointsParams() {
        return Stream.of(Arguments.of("/services/ide/123", HttpStatus.NOT_FOUND),
                Arguments.of("/websockets/ide/123", HttpStatus.NOT_FOUND));
    }

}
