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
package org.eclipse.dirigible.components.engine.web.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.eclipse.dirigible.components.engine.web.domain.Expose;
import org.eclipse.dirigible.components.initializers.definition.DefinitionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class ExposeRepositoryTest.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = { "org.eclipse.dirigible.components" })
@EntityScan("org.eclipse.dirigible.components")
@Transactional
public class ExposeRepositoryTest {
	
	/** The table repository. */
	@Autowired
	private ExposeRepository exposeRepository;
	
	/** The entity manager. */
	@Autowired
	EntityManager entityManager;
	
	/** The definition repository. */
	@MockBean
	DefinitionRepository definitionRepository;
	
	/**
	 * Setup.
	 *
	 * @throws Exception the exception
	 */
	@BeforeEach
    public void setup() throws Exception {
		
		cleanup();

    	// create test Expose
		exposeRepository.save(createExpose(exposeRepository, "/a/b/c/p1/project.json", "p1", "description", new String[]{"ui", "samples"}));
		exposeRepository.save(createExpose(exposeRepository, "/a/b/c/p2/project.json", "p2", "description", new String[]{"ui", "samples"}));
		exposeRepository.save(createExpose(exposeRepository, "/a/b/c/p3/project.json", "p3", "description", new String[]{"ui", "samples"}));
		exposeRepository.save(createExpose(exposeRepository, "/a/b/c/p4/project.json", "p4", "description", new String[]{"ui", "samples"}));
		exposeRepository.save(createExpose(exposeRepository, "/a/b/c/p5/project.json", "p5", "description", new String[]{"ui", "samples"}));
    }
	
	/**
	 * Cleanup.
	 *
	 * @throws Exception the exception
	 */
	@AfterEach
    public void cleanup() throws Exception {
		// delete test Tables
		exposeRepository.deleteAll();
    }
	

	/**
	 * Gets the one.
	 *
	 * @return the one
	 */
	@Test
    public void getOne() {
		Long id = exposeRepository.findAll().get(0).getId();
		Optional<Expose> optional = exposeRepository.findById(id);
		Expose expose = optional.isPresent() ? optional.get() : null;
        assertNotNull(expose);
        assertNotNull(expose.getLocation());
        assertNotNull(expose.getCreatedBy());
        assertEquals("SYSTEM", expose.getCreatedBy());
        assertNotNull(expose.getCreatedAt());
        assertNotNull(expose.getExposes());
        assertNotNull(expose.getExposes()[0]);
        assertEquals("ui", expose.getExposes()[0]);
    }
	
	/**
	 * Gets the reference using entity manager.
	 *
	 * @return the reference using entity manager
	 */
	@Test
    public void getReferenceUsingEntityManager() {
		Long id = exposeRepository.findAll().get(0).getId();
		Expose expose = entityManager.getReference(Expose.class, id);
        assertNotNull(expose);
        assertNotNull(expose.getLocation());
    }
	
	/**
	 * Creates the table.
	 *
	 * @param exposeRepository the table repository
	 * @param location the location
	 * @param name the name
	 * @param description the description
	 * @param exposes the exposes
	 * @return the expose
	 */
	public static Expose createExpose(ExposeRepository exposeRepository, String location, String name, String description, String[] exposes) {
		Expose expose = new Expose(location, name, description, exposes);
		return expose;
	}
	
	/**
	 * The Class TestConfiguration.
	 */
	@SpringBootApplication
	static class TestConfiguration {
	}
	
}
