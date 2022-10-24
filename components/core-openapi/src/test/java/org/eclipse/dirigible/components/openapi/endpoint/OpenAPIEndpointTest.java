package org.eclipse.dirigible.components.openapi.endpoint;

import org.eclipse.dirigible.components.openapi.repository.OpenAPIRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import static org.eclipse.dirigible.components.openapi.repository.OpenAPIRepositoryTest.createOpenAPI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {OpenAPIRepository.class})
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
class OpenAPIEndpointTest {

    @Autowired
    private OpenAPIRepository openAPIRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // Create test OpenAPI
        openAPIRepository.save(createOpenAPI("/a/b/c/test1.openapi", "test1", "description"));
        openAPIRepository.save(createOpenAPI("/a/b/c/test2.openapi", "test2", "description"));
        openAPIRepository.save(createOpenAPI("/a/b/c/test3.openapi", "test3", "description"));
        openAPIRepository.save(createOpenAPI("/a/b/c/test4.openapi", "test4", "description"));
        openAPIRepository.save(createOpenAPI("/a/b/c/test5.openapi", "test5", "description"));
    }

    @AfterEach
    public void cleanup() {
        // Delete test OpenAPIs
        openAPIRepository.findAll().stream().forEach(openAPI -> openAPIRepository.delete(openAPI));
    }

    @Test
    public void testGetVersion() throws Exception {
        mockMvc.perform(get("/services/v8/core/openapi"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SpringBootApplication
    static class TestConfiguration {
    }
}