package org.eclipse.dirigible.components.openapi.service;

import org.eclipse.dirigible.components.openapi.domain.OpenAPI;
import org.eclipse.dirigible.components.openapi.repository.OpenAPIRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.eclipse.dirigible.components.openapi.repository.OpenAPIRepositoryTest.createOpenAPI;

@SpringBootTest(classes = {OpenAPIRepository.class, OpenAPIService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class OpenAPIServiceTest {

    @Autowired
    private OpenAPIRepository openAPIRepository;

    @Autowired
    private OpenAPIService openAPIService;

    @BeforeEach
    public void setup() {
        // Create test OpenAPIs
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
    void getAll() {
        List<OpenAPI> openAPIList = openAPIService.getAll();
        assertEquals(5, openAPIList.size());
    }

    @Test
    void findAll() {
        Page<OpenAPI> openApiPage = openAPIService.findAll(Pageable.ofSize(1));
        assertEquals(5, openApiPage.getTotalElements());
    }

    @Test
    void findById() {
        OpenAPI openAPI = new OpenAPI("/a/b/c/test.openapi", "test", "description");
        openAPIService.save(openAPI);
        OpenAPI openAPIServiceById = openAPIService.findById(openAPI.getId());
        assertEquals("test", openAPIServiceById.getName());
    }

    @Test
    void findByName() {
        OpenAPI openAPI = new OpenAPI("/a/b/c/test.openapi", "test", "description");
        openAPIService.save(openAPI);
        OpenAPI openAPIServiceByName = openAPIService.findByName("test");
        assertEquals(openAPI.getId(), openAPIServiceByName.getId());
    }

    @Test
    void save() {
        OpenAPI openAPI = new OpenAPI("/a/b/c/test.openapi", "test", "description");
        openAPIService.save(openAPI);
        assertNotNull(openAPIService.findByName("test"));
    }

    @Test
    void delete() {
        try {
            OpenAPI openAPI = new OpenAPI("/a/b/c/test.openapi", "test", "description");
            openAPIService.save(openAPI);
            openAPIService.delete(openAPI);
        } catch (Exception e) {
            assertEquals("OpenAPI with name does not exist: test", e.getMessage());
        }
    }
}