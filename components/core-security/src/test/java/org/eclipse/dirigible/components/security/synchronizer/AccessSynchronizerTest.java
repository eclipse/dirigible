/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.security.synchronizer;

import org.eclipse.dirigible.components.security.domain.Access;
import org.eclipse.dirigible.components.security.repository.AccessRepository;

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
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.eclipse.dirigible.components.security.repository.AccessRepositoryTest.createSecurityAccess;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AccessRepository.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class AccessSynchronizerTest {
    /**
     * The security access repository.
     */
    @Autowired
    private AccessRepository securityAccessRepository;

    /**
     * The security access synchronizer.
     */
    @Autowired
    private AccessSynchronizer securityAccessSynchronizer;

    /**
     * The entity manager.
     */
    @Autowired
    EntityManager entityManager;

    /**
     * Setup.
     */
    @BeforeEach
    public void setup() {
    	
    	cleanup();
    	
        // Create test security accesses
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test1.access", "test1", "description", "HTTP", "/a" +
                "/b/c/test1.txt", "GET", "test_role_1"));
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test2.access", "test2", "description", "HTTP", "/a" +
                "/b/c/test2.txt", "GET", "test_role_2"));
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test3.access", "test3", "description", "HTTP", "/a" +
                "/b/c/test3.txt", "GET", "test_role_3"));
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test4.access", "test4", "description", "HTTP", "/a" +
                "/b/c/test4.txt", "GET", "test_role_4"));
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test5.access", "test5", "description", "HTTP", "/a" +
                "/b/c/test5.txt", "GET", "test_role_5"));
    }

    /**
     * Cleanup.
     */
    @AfterEach
    public void cleanup() {
        // Delete test security accesses
        securityAccessRepository.deleteAll();
    }

    /**
     * Checks if is accepted.
     */
    @Test
    public void testIsAcceptedPath() {
        assertTrue(securityAccessSynchronizer.isAccepted(Path.of("/a/b/c/test.access"), null));
    }

    /**
     * Checks if is accepted.
     */
    @Test
    public void testIsAcceptedArtefact() {
        assertTrue(securityAccessSynchronizer.isAccepted(createSecurityAccess("/a/b/c/test.access", "test",
                "description", "HTTP", "/a/b/c/test.txt", "GET", "test_role").getType()));
    }

    /**
     * Load the artefact.
     */
    @Test
    public void testLoad() throws IOException {
        byte[] content = AccessSynchronizerTest.class.getResourceAsStream("/META-INF/dirigible/test/test" +
                ".access").readAllBytes();
        List<Access> list = securityAccessSynchronizer.load("/META-INF/dirigible/test/test.access", content);
        assertNotNull(list);
        assertEquals("/META-INF/dirigible/test/test.access", list.get(0).getLocation());
    }

    /**
     * The Class TestConfiguration.
     */
    @SpringBootApplication
    static class TestConfiguration {
    }
}