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

import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.security.repository.RoleRepository;
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

import static org.eclipse.dirigible.components.security.repository.RoleRepositoryTest.createSecurityRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {RoleRepository.class, RoleService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class RoleServiceTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @BeforeEach
    public void setup() {
    	
    	cleanup();
    	
        // Create test security roles
        roleRepository.save(createSecurityRole("/a/b/c/test1.role", "test1", "description"));
        roleRepository.save(createSecurityRole("/a/b/c/test2.role", "test2", "description"));
        roleRepository.save(createSecurityRole("/a/b/c/test3.role", "test3", "description"));
        roleRepository.save(createSecurityRole("/a/b/c/test4.role", "test4", "description"));
        roleRepository.save(createSecurityRole("/a/b/c/test5.role", "test5", "description"));
    }

    @AfterEach
    public void cleanup() {
        // Delete test security roles
        roleRepository.deleteAll();
    }

    @Test
    void testGetAll() {
        List<Role> securityRoleList = roleService.getAll();
        assertEquals(5, securityRoleList.size());
    }

    @Test
    void testFindAll() {
        Page<Role> securityRolePage = roleService.getPages(Pageable.ofSize(1));
        assertEquals(5, securityRolePage.getTotalElements());
    }

    @Test
    void testFindById() {
        Role securityRole = new Role("/a/b/c/test.role", "test", "description");
        roleService.save(securityRole);
        Role securityRoleServiceById = roleService.findById(securityRole.getId());
        assertEquals("test", securityRoleServiceById.getName());
    }

    @Test
    void testFindByName() {
        Role securityRole = new Role("/a/b/c/test.role", "test", "description");
        roleService.save(securityRole);
        Role securityRoleServiceByName = roleService.findByName("test");
        assertEquals(securityRole.getId(), securityRoleServiceByName.getId());
    }

    @Test
    void testSave() {
        Role securityRole = new Role("/a/b/c/test.role", "test", "description");
        roleService.save(securityRole);
        assertNotNull(roleService.findByName("test"));
    }

    @Test
    void testDelete() {
        try {
            Role securityRole = new Role("/a/b/c/test.role", "test", "description");
            roleService.save(securityRole);
            roleService.delete(securityRole);
            roleService.findByName("test");
        } catch (Exception e) {
            assertEquals("SecurityRole with name does not exist: test", e.getMessage());
        }
    }
}