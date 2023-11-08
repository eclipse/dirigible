/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.eclipse.dirigible.components.extensions.domain.ExtensionPoint;
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

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
public class ExtensionPointRepositoryTest {

    @Autowired
    private ExtensionPointRepository extensionPointRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void setup() throws Exception {

        cleanup();

        // create test ExtensionPoints
        extensionPointRepository.save(createExtensionPoint("/a/b/c/e1.extensionpoint", "e1", "description"));
        extensionPointRepository.save(createExtensionPoint("/a/b/c/e2.extensionpoint", "e2", "description"));
        extensionPointRepository.save(createExtensionPoint("/a/b/c/e3.extensionpoint", "e3", "description"));
        extensionPointRepository.save(createExtensionPoint("/a/b/c/e4.extensionpoint", "e4", "description"));
        extensionPointRepository.save(createExtensionPoint("/a/b/c/e5.extensionpoint", "e5", "description"));
    }

    @AfterEach
    public void cleanup() throws Exception {
        extensionPointRepository.deleteAll();
    }


    @Test
    public void getOne() {
        Long id = extensionPointRepository.findAll()
                                          .get(0)
                                          .getId();
        Optional<ExtensionPoint> optional = extensionPointRepository.findById(id);
        ExtensionPoint extensionPoint = optional.isPresent() ? optional.get() : null;
        assertNotNull(extensionPoint);
        assertNotNull(extensionPoint.getLocation());
        assertNotNull(extensionPoint.getCreatedBy());
        assertEquals("SYSTEM", extensionPoint.getCreatedBy());
        assertNotNull(extensionPoint.getCreatedAt());
        // assertEquals("extensionpoint:/a/b/c/e1.extensionpoint:e1",extensionPoint.getKey());
    }

    @Test
    public void getReferenceUsingEntityManager() {
        Long id = extensionPointRepository.findAll()
                                          .get(0)
                                          .getId();
        ExtensionPoint extensionPoint = entityManager.getReference(ExtensionPoint.class, id);
        assertNotNull(extensionPoint);
        assertNotNull(extensionPoint.getLocation());
    }

    public static ExtensionPoint createExtensionPoint(String location, String name, String description) {
        ExtensionPoint extensionPoint = new ExtensionPoint(location, name, description);
        return extensionPoint;
    }

    @SpringBootApplication
    static class TestConfiguration {
    }

}
