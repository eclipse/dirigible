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
package org.eclipse.dirigible.components.initializers.definition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.eclipse.dirigible.components.initializers.definition.Definition;
import org.eclipse.dirigible.components.initializers.definition.DefinitionRepository;
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
@ComponentScan(basePackages = { "org.eclipse.dirigible.components" })
@EntityScan("org.eclipse.dirigible.components")
@Transactional
public class DefinitionRepositoryTest {
	
	@Autowired
	private DefinitionRepository definitionRepository;
	
	@Autowired
	EntityManager entityManager;
	
	@BeforeEach
    public void setup() throws Exception {

    	// create test ExtensionPoints
		definitionRepository.save(createDefinition("/a/b/c/e1.extensionpoint", "e1", "extensionpoint"));
		definitionRepository.save(createDefinition("/a/b/c/e2.extension", "e2", "extension"));
		definitionRepository.save(createDefinition("/a/b/c/e3.job", "e3", "job"));
		definitionRepository.save(createDefinition("/a/b/c/e4.listener", "e4", "listener"));
		definitionRepository.save(createDefinition("/a/b/c/e5.odata", "e5", "odata"));
    }
	
	@AfterEach
    public void cleanup() throws Exception {
		
		// delete test ExtensionPoints
		definitionRepository.findAll().stream().forEach(e -> definitionRepository.delete(e));
    }
	

	@Test
    public void getOne() {
		Long id = definitionRepository.findAll().get(0).getId();
		Optional<Definition> optional = definitionRepository.findById(id);
		Definition definition = optional.isPresent() ? optional.get() : null;
        assertNotNull(definition);
        assertNotNull(definition.getLocation());
        assertNotNull(definition.getCreatedBy());
        assertEquals("SYSTEM", definition.getCreatedBy());
        assertNotNull(definition.getCreatedAt());
        assertEquals("extensionpoint:/a/b/c/e1.extensionpoint:e1",definition.getKey());
        assertEquals("2A7E378A9D6EB6051F3A0D733263C78F", definition.getChecksum());
    }
	
	@Test
    public void getReferenceUsingEntityManager() {
		Long id = definitionRepository.findAll().get(0).getId();
		Definition definition = entityManager.getReference(Definition.class, id);
        assertNotNull(definition);
        assertNotNull(definition.getLocation());
    }
	
	public static Definition createDefinition(String location, String name, String type) {
		Definition definition = new Definition(location, name, type, 
				("{'location':" + location + ", 'name':" + name + ",  'type':" + type + "}").getBytes());
		return definition;
	}
	
	@SpringBootApplication
	static class TestConfiguration {
	}
	
}
