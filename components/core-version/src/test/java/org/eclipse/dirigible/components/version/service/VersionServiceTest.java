package org.eclipse.dirigible.components.version.service;

import org.eclipse.dirigible.components.version.domain.Version;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {VersionService.class})
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
class VersionServiceTest {

    @Autowired
    VersionService versionService;

    @Test
    void getVersion() throws IOException {
        Version version = versionService.getVersion();
        assertEquals("dirigible", version.getProductName());
        assertEquals("0.0.1", version.getProductVersion());
        assertEquals("test", version.getProductCommitId());
        assertEquals("https://github.com/eclipse/dirigible", version.getProductRepository());
        assertEquals("all", version.getProductType());
        assertEquals("server-spring-boot", version.getInstanceName());
        assertEquals("local", version.getDatabaseProvider());
        assertEquals(0, version.getModules().size());
        //TODO: Аdd assertion for engines.
    }
}