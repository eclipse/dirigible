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
package org.eclipse.dirigible.components.engine.web.endpoint;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.components.engine.web.exposure.ExposeManager;
import org.eclipse.dirigible.components.engine.web.repository.ExposeRepository;
import org.eclipse.dirigible.components.initializers.definition.DefinitionRepository;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = { "org.eclipse.dirigible.components" })
@EntityScan("org.eclipse.dirigible.components")
@Transactional
public class WebEndpointTest {
	
	@Autowired
	private ExposeRepository exposeRepository;
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	private SynchronizationProcessor synchronizationProcessor;
	
	@MockBean
	DefinitionRepository definitionRepository;
	
	private String projectJson = "{\n"
			+ "    \"guid\":\"demo\",\n"
			+ "    \"repository\":{\n"
			+ "        \"type\":\"git\",\n"
			+ "        \"branch\":\"master\",\n"
			+ "        \"url\":\"https://github.com/dirigiblelabs/demo.git\"\n"
			+ "    },\n"
			+ "    \"exposes\": [\n"
			+ "        \"ui\",\n"
			+ "        \"samples\"\n"
			+ "    ]\n"
			+ "}\n"
			+ "";
	
	/**
	 * Setup.
	 *
	 * @throws Exception the exception
	 */
	@BeforeEach
    public void setup() throws Exception {
		cleanup();
    }
	
	@AfterEach
    public void cleanup() throws Exception {
		exposeRepository.deleteAll();
    }
	
	/**
	 * Load the artefact.
	 * @throws Exception 
	 */
	@Test
    public void process() throws Exception {
		String registyrFolder = synchronizationProcessor.getRegistryFolder();
		Paths.get(registyrFolder, "demo").toFile().mkdirs();
		Paths.get(registyrFolder, "demo", "hidden").toFile().mkdirs();
		Paths.get(registyrFolder, "demo", "ui").toFile().mkdirs();
		Files.writeString(Paths.get(registyrFolder, "demo", "project.json"), projectJson, StandardOpenOption.CREATE);
		Files.writeString(Paths.get(registyrFolder, "demo", "ui", "hello-world.txt"), "Hello World!", StandardOpenOption.CREATE);
		Files.writeString(Paths.get(registyrFolder, "demo", "hidden", "hidden.txt"), "Hidden", StandardOpenOption.CREATE);
		Files.writeString(Paths.get(registyrFolder, "demo", "ui", "index.html"), "Hidden", StandardOpenOption.CREATE);
		try {
			synchronizationProcessor.processSynchronizers();
			assertEquals(1, ExposeManager.listRegisteredProjects().size());
			assertTrue(ExposeManager.isPathExposed("demo/ui"));
			assertFalse(ExposeManager.isPathExposed("demo/hidden"));
			mockMvc.perform(get("/services/v8/web/demo/ui/hello-world.txt")).andDo(print())
					.andExpect(content().string(containsString("Hello World!"))).andExpect(status().is2xxSuccessful());
			mockMvc.perform(get("/services/v8/web/demo/hidden/hidden.txt")).andDo(print())
					.andExpect(status().isForbidden());
			mockMvc.perform(get("/services/v8/web/demo/ui/not-existing.txt")).andDo(print())
					.andExpect(status().isNotFound());
			mockMvc.perform(get("/services/v8/web/demo/ui")).andDo(print()).andExpect(status().isNotFound());
			mockMvc.perform(get("/services/v8/web/demo/ui/")).andDo(print()).andExpect(status().is2xxSuccessful());
		} finally {
			FileUtils.deleteDirectory(Paths.get(registyrFolder, "demo").toFile());
			synchronizationProcessor.processSynchronizers();
		}
    }
	
	@SpringBootApplication
	static class TestConfiguration {
	}
}
