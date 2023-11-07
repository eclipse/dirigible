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
package org.eclipse.dirigible.components.engine.web.synchronizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.dirigible.components.engine.web.domain.Expose;
import org.eclipse.dirigible.components.engine.web.exposure.ExposeManager;
import org.eclipse.dirigible.components.engine.web.repository.ExposeRepository;
import org.eclipse.dirigible.components.initializers.definition.DefinitionRepository;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationWatcher;
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
 * The Class ExposesSynchronizerTest.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
public class ExposesSynchronizerTest {

	/** The expose repository. */
	@Autowired
	private ExposeRepository exposeRepository;

	/** The expose synchronizer. */
	@Autowired
	private ExposesSynchronizer exposesSynchronizer;

	/** The synchronization processor. */
	@Autowired
	private SynchronizationProcessor synchronizationProcessor;

	/** The synchronization watcher. */
	@Autowired
	private SynchronizationWatcher synchronizationWatcher;

	/** The entity manager. */
	@Autowired
	EntityManager entityManager;

	/** The definition repository. */
	@MockBean
	DefinitionRepository definitionRepository;

	/** The content. */
	private String content = "{\n" + "    \"guid\":\"sync\",\n" + "    \"repository\":{\n" + "        \"type\":\"git\",\n"
			+ "        \"branch\":\"master\",\n" + "        \"url\":\"https://github.com/dirigiblelabs/sync.git\"\n" + "    },\n"
			+ "    \"exposes\": [\n" + "        \"ui\",\n" + "        \"samples\"\n" + "    ]\n" + "}\n" + "";

	/**
	 * Setup.
	 *
	 * @throws Exception the exception
	 */
	@BeforeEach
	public void setup() throws Exception {

		cleanup();

		// create test Exposes
		exposeRepository.save(
				createExpose(exposeRepository, "/a/b/c/p1/project.json", "p1", "description", new String[] {"ui", "samples"}));
		exposeRepository.save(
				createExpose(exposeRepository, "/a/b/c/p2/project.json", "p2", "description", new String[] {"ui", "samples"}));
		exposeRepository.save(
				createExpose(exposeRepository, "/a/b/c/p3/project.json", "p3", "description", new String[] {"ui", "samples"}));
		exposeRepository.save(
				createExpose(exposeRepository, "/a/b/c/p4/project.json", "p4", "description", new String[] {"ui", "samples"}));
		exposeRepository.save(
				createExpose(exposeRepository, "/a/b/c/p5/project.json", "p5", "description", new String[] {"ui", "samples"}));
	}

	/**
	 * Cleanup.
	 *
	 * @throws Exception the exception
	 */
	@AfterEach
	public void cleanup() throws Exception {
		exposeRepository.deleteAll();
	}



	/**
	 * Checks if is accepted.
	 */
	@Test
	public void isAcceptedPath() {
		assertTrue(exposesSynchronizer.isAccepted(Path.of("/a/b/c/p1/project.json"), null));
	}

	/**
	 * Checks if is accepted.
	 */
	@Test
	public void isAcceptedArtefact() {
		assertTrue(exposesSynchronizer.isAccepted(
				createExpose(exposeRepository, "/a/b/c/p1/project.json", "p1", "description", new String[] {"ui", "samples"}).getType()));
	}

	/**
	 * Load the artefact.
	 *
	 * @throws ParseException the parse exception
	 */
	@Test
	public void load() throws ParseException {
		List<Expose> list = exposesSynchronizer.parse("/load/project.json", content.getBytes());
		assertNotNull(list);
		assertEquals("/load/project.json", list	.get(0)
												.getLocation());
		assertEquals(2, list.get(0)
							.getExposes().length);
	}

	/**
	 * Load the artefact.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void process() throws IOException {
		String registyrFolder = synchronizationProcessor.getRegistryFolder();
		Paths	.get(registyrFolder, "sync")
				.toFile()
				.mkdirs();
		Files.writeString(Paths.get(registyrFolder, "sync", "project.json"), content, StandardOpenOption.CREATE);
		try {
			synchronizationWatcher.force();
			synchronizationProcessor.processSynchronizers();
			String e = null;
			for (String p : ExposeManager.listRegisteredProjects()) {
				if (p.equals("sync")) {
					e = p;
				}
			} ;
			assertNotNull(e);
			assertTrue(ExposeManager.listRegisteredProjects()
									.size() > 0);
			assertTrue(ExposeManager.isPathExposed("sync/ui"));
		} finally {
			Files.deleteIfExists(Paths.get(registyrFolder, "sync", "project.json"));
			synchronizationProcessor.processSynchronizers();
		}
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
	public static Expose createExpose(ExposeRepository exposeRepository, String location, String name, String description,
			String[] exposes) {
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
