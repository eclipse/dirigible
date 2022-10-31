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
package org.eclipse.dirigible.components.security.service;

import org.eclipse.dirigible.components.security.domain.SecurityAccess;
import org.eclipse.dirigible.components.security.repository.SecurityAccessRepository;
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

import static org.eclipse.dirigible.components.security.repository.SecurityAccessRepositoryTest.createSecurityAccess;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {SecurityAccessRepository.class, SecurityAccessService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class SecurityAccessServiceTest {

    @Autowired
    private SecurityAccessRepository securityAccessRepository;

    @Autowired
    private SecurityAccessService securityAccessService;

    @BeforeEach
    public void setup() {
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

    @AfterEach
    public void cleanup() {
        // Delete test security accesses
        securityAccessRepository.findAll().stream().forEach(securityAccess -> securityAccessRepository.delete(securityAccess));
    }

    @Test
    void testGetAll() {
        List<SecurityAccess> securityAccessList = securityAccessService.getAll();
        assertEquals(5, securityAccessList.size());
    }

    @Test
    void testFindAll() {
        Page<SecurityAccess> securityAccessPage = securityAccessService.findAll(Pageable.ofSize(1));
        assertEquals(5, securityAccessPage.getTotalElements());
    }

    @Test
    void testFindById() {
        SecurityAccess securityAccess = new SecurityAccess("/a/b/c/test.access", "test", "description", "HTTP", "/a/b" +
                "/c/test.txt", "GET", "test_role");
        securityAccessService.save(securityAccess);
        SecurityAccess securityAccessServiceById = securityAccessService.findById(securityAccess.getId());
        assertEquals("test", securityAccessServiceById.getName());
    }

    @Test
    void testFindByName() {
        SecurityAccess securityAccess = new SecurityAccess("/a/b/c/test.access", "test", "description", "HTTP", "/a/b" +
                "/c/test.txt", "GET", "test_role");
        securityAccessService.save(securityAccess);
        SecurityAccess securityAccessServiceByName = securityAccessService.findByName("test");
        assertEquals(securityAccess.getId(), securityAccessServiceByName.getId());
    }

    @Test
    void testSave() {
        SecurityAccess securityAccess = new SecurityAccess("/a/b/c/test.access", "test", "description", "HTTP", "/a/b" +
                "/c/test.txt", "GET", "test_role");
        securityAccessService.save(securityAccess);
        assertNotNull(securityAccessService.findByName("test"));
    }

    @Test
    void testDelete() {
        try {
            SecurityAccess securityAccess = new SecurityAccess("/a/b/c/test.access", "test", "description", "HTTP",
                    "/a/b/c/test.txt", "GET", "test_role");
            securityAccessService.save(securityAccess);
            securityAccessService.delete(securityAccess);
            securityAccessService.findByName("test");
        } catch (Exception e) {
            assertEquals("SecurityAccess with name does not exist: test", e.getMessage());
        }
    }
}