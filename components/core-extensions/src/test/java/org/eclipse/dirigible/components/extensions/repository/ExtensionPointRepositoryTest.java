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
package org.eclipse.dirigible.components.extensions.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.eclipse.dirigible.components.extensions.domain.ExtensionPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes=ExtensionPointRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories("org.eclipse.dirigible.components.*")
@ComponentScan(basePackages = { "org.eclipse.dirigible.components.*" })
@EntityScan("org.eclipse.dirigible.components.*")
@Transactional
public class ExtensionPointRepositoryTest {
	
	@Autowired
	private ExtensionPointRepository extensionPointRepository;
	
	@Autowired
	EntityManager entityManager;
	
	@BeforeEach
    public void setup() throws Exception {

    	// create test ExtensionPoints
		extensionPointRepository.save(createExtensionPoint("e1", "/a/b/c/e1.extensionpoint", "description"));
		extensionPointRepository.save(createExtensionPoint("e2", "/a/b/c/e2.extensionpoint", "description"));
		extensionPointRepository.save(createExtensionPoint("e3", "/a/b/c/e3.extensionpoint", "description"));
		extensionPointRepository.save(createExtensionPoint("e4", "/a/b/c/e4.extensionpoint", "description"));
		extensionPointRepository.save(createExtensionPoint("e5", "/a/b/c/e5.extensionpoint", "description"));
    }

	@Test
    public void getOne() {
		Optional<ExtensionPoint> optional = extensionPointRepository.findById("e1");
		ExtensionPoint extensionPoint = optional.isPresent() ? optional.get() : null;
        assertNotNull(extensionPoint);
        assertNotNull(extensionPoint.getLocation());
        assertNotNull(extensionPoint.getCreatedBy());
        assertEquals("SYSTEM", extensionPoint.getCreatedBy());
        assertNotNull(extensionPoint.getCreatedAt());
    }
	
	@Test
    public void getReferenceUsingEntityManager() {
		ExtensionPoint extensionPoint = entityManager.getReference(ExtensionPoint.class, "e1");
        assertNotNull(extensionPoint);
        assertNotNull(extensionPoint.getLocation());
    }
	
	public static ExtensionPoint createExtensionPoint(String location, String name, String description) {
		ExtensionPoint extensionPoint = new ExtensionPoint(location, name, description);
		return extensionPoint;
	}
	
}
