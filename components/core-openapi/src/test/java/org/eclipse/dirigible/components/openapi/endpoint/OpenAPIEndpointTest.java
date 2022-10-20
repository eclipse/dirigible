package org.eclipse.dirigible.components.openapi.endpoint;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.openapi.repository.OpenAPIRepository;
import org.eclipse.dirigible.components.openapi.repository.OpenAPIRepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {OpenAPIRepository.class})
@AutoConfigureMockMvc
@ComponentScan(basePackages = { "org.eclipse.dirigible.components" })
class OpenAPIEndpointTest {

    @Autowired
    private OpenAPIRepository openAPIRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        // create test OpenAPI
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test1.openapi", "test1", "description", "test1"));
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test2.openapi", "test2", "description", "test2"));
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test3.openapi", "test3", "description", "test3"));
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test4.openapi", "test4", "description", "test4"));
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test5.openapi", "test5", "description", "test5"));
    }

    @AfterEach
    public void cleanup() throws Exception {
        // delete test OpenAPIs
        openAPIRepository.findAll().stream().forEach(openAPI -> openAPIRepository.delete(openAPI));
    }

    @Test
    public void testGetVersion() throws Exception {
        mockMvc.perform(get("/services/v8/core/openapi"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @SpringBootApplication
    static class TestConfiguration {
    }
}