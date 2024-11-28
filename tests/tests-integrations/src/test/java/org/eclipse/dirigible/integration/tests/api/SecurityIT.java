package org.eclipse.dirigible.integration.tests.api;

import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIT extends IntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testPublicEndpoint() throws Exception {
        Set<String> paths = Set.of("/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness", "/login", "/error.html");
        for (String path : paths) {
            mvc.perform(get(path))
               .andExpect(status().is(HttpStatus.OK.value()));
        }

    }

    @Test
    void testProtectedEndpointWithoutAuthentication() throws Exception {
        Set<String> paths = Set.of("/spring-admin", "/actuator/info");
        for (String path : paths) {
            mvc.perform(get(path))
               .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @WithMockUser(username = "user_without_roles", roles = {"SOME_UNUSED_ROLE"})
    void testProtectedEndpointsWithUnauthorizedUser() throws Exception {
        Set<String> paths = Set.of("/actuator/info");
        for (String path : paths) {
            mvc.perform(get(path))
               .andExpect(status().isForbidden());
        }
    }

    @Test
    @WithMockUser(username = "operator", roles = {Roles.RoleNames.OPERATOR})
    void testOperatorEndpointIsAccessible() {
        Map<String, HttpStatus> paths = Map.of("/spring-admin", HttpStatus.NOT_FOUND, "/actuator/info", HttpStatus.OK);
        paths.forEach((path, expectedStatus) -> {
            try {
                mvc.perform(get(path))
                   .andExpect(status().is(expectedStatus.value()));
            } catch (Exception e) {
                fail(e);
            }
        });
    }

    @Test
    @WithMockUser(username = "developer", roles = {Roles.RoleNames.DEVELOPER})
    void testDeveloperEndpointIsAccessible() throws Exception {
        Set<String> paths = Set.of("/services/ide/123", "/websockets/ide/123");
        for (String path : paths) {
            mvc.perform(get(path))
               .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        }
    }

}
