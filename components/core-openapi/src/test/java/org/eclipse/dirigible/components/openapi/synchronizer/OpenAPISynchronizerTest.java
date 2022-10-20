package org.eclipse.dirigible.components.openapi.synchronizer;

import org.eclipse.dirigible.components.openapi.domain.OpenAPI;
import org.eclipse.dirigible.components.openapi.repository.OpenAPIRepository;
import org.eclipse.dirigible.components.openapi.repository.OpenAPIRepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {OpenAPIRepository.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class OpenAPISynchronizerTest {

    /**
     * The openapi point repository.
     */
    @Autowired
    private OpenAPIRepository openAPIRepository;

    /**
     * The openapi points synchronizer.
     */
    @Autowired
    private OpenAPISynchronizer openAPISynchronizer;

    /**
     * The entity manager.
     */
    @Autowired
    EntityManager entityManager;

    /**
     * Setup.
     *
     * @throws Exception the exception
     */
    @BeforeEach
    public void setup() throws Exception {
        // create test OpenAPIs
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test1.openapi", "test1", "description", "test1"));
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test2.openapi", "test2", "description", "test2"));
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test3.openapi", "test3", "description", "test3"));
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test4.openapi", "test4", "description", "test4"));
        openAPIRepository.save(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test5.openapi", "test5", "description", "test5"));
    }

    /**
     * Cleanup.
     *
     * @throws Exception the exception
     */
    @AfterEach
    public void cleanup() throws Exception {
        // delete test OpenAPIs
        openAPIRepository.findAll().stream().forEach(openAPI -> openAPIRepository.delete(openAPI));
    }

    /**
     * Checks if is accepted.
     */
    @Test
    public void isAcceptedPath() {
        assertTrue(openAPISynchronizer.isAccepted(Path.of("/a/b/c/test.openapi"), null));
    }

    /**
     * Checks if is accepted.
     */
    @Test
    public void isAcceptedArtefact() {
        assertTrue(openAPISynchronizer.isAccepted(OpenAPIRepositoryTest.createOpenAPI("/a/b/c/test.openapi", "test", "description", "test").getType()));
    }

    /**
     * Load the artefact.
     */
    @Test
    public void load() {
        String content = "{\"location\":\"/test/test.openapi\",\"name\":\"/test/test\",\"description\":\"Test OpenAPI\", \"hash\":\"test\", \"createdBy\":\"system\",\"createdAt\":\"2017-07-06T2:53:01+0000\"}";
        List<OpenAPI> list = openAPISynchronizer.load("/test/test.openapi", content.getBytes());
        assertNotNull(list);
        assertEquals("/test/test.openapi", list.get(0).getLocation());
    }

    /**
     * The Class TestConfiguration.
     */
    @SpringBootApplication
    static class TestConfiguration {
    }

}