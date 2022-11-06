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

import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.security.repository.RoleRepository;

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

import static org.eclipse.dirigible.components.security.repository.RoleRepositoryTest.createSecurityRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {RoleRepository.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class SecurityRoleSynchronizerTest {
    /**
     * The security role repository.
     */
    @Autowired
    private RoleRepository securityRoleRepository;

    /**
     * The sercurity role synchronizer.
     */
    @Autowired
    private RoleSynchronizer securityRoleSynchronizer;

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
        // Create test security accesses
        securityRoleRepository.save(createSecurityRole("/a/b/c/test1.access", "test1", "description"));
        securityRoleRepository.save(createSecurityRole("/a/b/c/test2.access", "test2", "description"));
        securityRoleRepository.save(createSecurityRole("/a/b/c/test3.access", "test3", "description"));
        securityRoleRepository.save(createSecurityRole("/a/b/c/test4.access", "test4", "description"));
        securityRoleRepository.save(createSecurityRole("/a/b/c/test5.access", "test5", "description"));
    }

    /**
     * Cleanup.
     */
    @AfterEach
    public void cleanup() {
        // Delete test security roles
        securityRoleRepository.findAll().stream().forEach(securityRole -> securityRoleRepository.delete(securityRole));
    }

    /**
     * Checks if is accepted.
     */
    @Test
    public void testIsAcceptedPath() {
        assertTrue(securityRoleSynchronizer.isAccepted(Path.of("/a/b/c/test.role"), null));
    }

    /**
     * Checks if is accepted.
     */
    @Test
    public void testIsAcceptedArtefact() {
        assertTrue(securityRoleSynchronizer.isAccepted(createSecurityRole("/a/b/c/test.role", "test", "description").getType()));
    }

    /**
     * Load the artefact.
     */
    @Test
    public void testLoad() throws IOException {
        byte[] content =
                SecurityRoleSynchronizerTest.class.getResourceAsStream("/META-INF/dirigible/test/test.role").readAllBytes();
        List<Role> list = securityRoleSynchronizer.load("/META-INF/dirigible/test/test.role", content);
        assertNotNull(list);
        assertEquals("/META-INF/dirigible/test/test.role", list.get(0).getLocation());
    }

    /**
     * The Class TestConfiguration.
     */
    @SpringBootApplication
    static class TestConfiguration {
    }
}