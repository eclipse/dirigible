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

import org.eclipse.dirigible.components.security.domain.SecurityRole;
import org.eclipse.dirigible.components.security.repository.SecurityRoleRepository;
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

import static org.eclipse.dirigible.components.security.repository.SecurityRoleRepositoryTest.createSecurityRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {SecurityRoleRepository.class, SecurityRoleService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class SecurityRoleServiceTest {

    @Autowired
    private SecurityRoleRepository securityRoleRepository;

    @Autowired
    private SecurityRoleService securityRoleService;

    @BeforeEach
    public void setup() {
        // Create test security roles
        securityRoleRepository.save(createSecurityRole("/a/b/c/test1.role", "test1", "description"));
        securityRoleRepository.save(createSecurityRole("/a/b/c/test2.role", "test2", "description"));
        securityRoleRepository.save(createSecurityRole("/a/b/c/test3.role", "test3", "description"));
        securityRoleRepository.save(createSecurityRole("/a/b/c/test4.role", "test4", "description"));
        securityRoleRepository.save(createSecurityRole("/a/b/c/test5.role", "test5", "description"));
    }

    @AfterEach
    public void cleanup() {
        // Delete test security roles
        securityRoleRepository.findAll().stream().forEach(securityRole -> securityRoleRepository.delete(securityRole));
    }

    @Test
    void testGetAll() {
        List<SecurityRole> securityRoleList = securityRoleService.getAll();
        assertEquals(5, securityRoleList.size());
    }

    @Test
    void testFindAll() {
        Page<SecurityRole> securityRolePage = securityRoleService.findAll(Pageable.ofSize(1));
        assertEquals(5, securityRolePage.getTotalElements());
    }

    @Test
    void testFindById() {
        SecurityRole securityRole = new SecurityRole("/a/b/c/test.role", "test", "description");
        securityRoleService.save(securityRole);
        SecurityRole securityRoleServiceById = securityRoleService.findById(securityRole.getId());
        assertEquals("test", securityRoleServiceById.getName());
    }

    @Test
    void testFindByName() {
        SecurityRole securityRole = new SecurityRole("/a/b/c/test.role", "test", "description");
        securityRoleService.save(securityRole);
        SecurityRole securityRoleServiceByName = securityRoleService.findByName("test");
        assertEquals(securityRole.getId(), securityRoleServiceByName.getId());
    }

    @Test
    void testSave() {
        SecurityRole securityRole = new SecurityRole("/a/b/c/test.role", "test", "description");
        securityRoleService.save(securityRole);
        assertNotNull(securityRoleService.findByName("test"));
    }

    @Test
    void testDelete() {
        try {
            SecurityRole securityRole = new SecurityRole("/a/b/c/test.role", "test", "description");
            securityRoleService.save(securityRole);
            securityRoleService.delete(securityRole);
            securityRoleService.findByName("test");
        } catch (Exception e) {
            assertEquals("SecurityRole with name does not exist: test", e.getMessage());
        }
    }
}