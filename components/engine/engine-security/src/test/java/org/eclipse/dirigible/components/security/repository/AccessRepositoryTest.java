/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.security.repository;

import org.eclipse.dirigible.components.security.domain.Access;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
public class AccessRepositoryTest {

    @Autowired
    private AccessRepository securityAccessRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void setup() {
        // Create test security accesses
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test1.access", "test1", "description", "HTTP", "/a/b/c/test1.txt", "GET", "test_role_1"));
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test2.access", "test2", "description", "HTTP", "/a/b/c/test2.txt", "GET", "test_role_2"));
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test3.access", "test3", "description", "HTTP", "/a/b/c/test3.txt", "GET", "test_role_3"));
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test4.access", "test4", "description", "HTTP", "/a/b/c/test4.txt", "GET", "test_role_4"));
        securityAccessRepository.save(createSecurityAccess("/a/b/c/test5.access", "test5", "description", "HTTP", "/a/b/c/test5.txt", "GET", "test_role_5"));
    }

    @AfterEach
    public void cleanup() {
        // Delete test security accesses
        securityAccessRepository.findAll().stream().forEach(securityAccess -> securityAccessRepository.delete(securityAccess));
    }

    @Test
    public void getOne() {
        Long id = securityAccessRepository.findAll().get(0).getId();
        Optional<Access> optional = securityAccessRepository.findById(id);
        Access securityAccess = optional.isPresent() ? optional.get() : null;
        assertNotNull(securityAccess);
        assertNotNull(securityAccess.getLocation());
        assertNotNull(securityAccess.getCreatedBy());
        assertEquals("SYSTEM", securityAccess.getCreatedBy());
        assertNotNull(securityAccess.getCreatedAt());
    }

    @Test
    public void getReferenceUsingEntityManager() {
        Long id = securityAccessRepository.findAll().get(0).getId();
        Access securityAccess = entityManager.getReference(Access.class, id);
        assertNotNull(securityAccess);
        assertNotNull(securityAccess.getLocation());
    }

    public static Access createSecurityAccess(String location, String name, String description, String scope, String path, String method, String role) {
        Access securityAccess = new Access(location, name, description, scope, path, method, role);
        return securityAccess;
    }

    @SpringBootApplication
    static class TestConfiguration {
    }
}