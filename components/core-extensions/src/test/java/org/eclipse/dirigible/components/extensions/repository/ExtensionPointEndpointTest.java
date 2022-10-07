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

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;

import org.eclipse.dirigible.components.base.BaseEndpoint;
import org.eclipse.dirigible.components.extensions.domain.ExtensionPoint;
import org.eclipse.dirigible.components.extensions.service.ExtensionPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnableJpaRepositories("org.eclipse.dirigible.components.*")
@ComponentScan(basePackages = { "org.eclipse.dirigible.components.*" })
@EntityScan("org.eclipse.dirigible.components.*")
public class ExtensionPointEndpointTest {
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private ExtensionPointService extensionPointService;
	
	private ExtensionPoint testExtensionPoint;
	
	@Autowired
	MockMvc mockMvc;
	
	@BeforeEach
    public void setup() throws Exception {

    	// create test ExtensionPoints
		extensionPointService.save(ExtensionPointRepositoryTest.createExtensionPoint("/a/b/c/e1.extensionpoint", "e1", "description"));
		extensionPointService.save(ExtensionPointRepositoryTest.createExtensionPoint("/a/b/c/e2.extensionpoint", "e2", "description"));
		extensionPointService.save(ExtensionPointRepositoryTest.createExtensionPoint("/a/b/c/e3.extensionpoint", "e3", "description"));
		extensionPointService.save(ExtensionPointRepositoryTest.createExtensionPoint("/a/b/c/e4.extensionpoint", "e4", "description"));
		extensionPointService.save(ExtensionPointRepositoryTest.createExtensionPoint("/a/b/c/e5.extensionpoint", "e5", "description"));
		
		Page<ExtensionPoint> extensionPoints = extensionPointService.findAll(PageRequest.of(0, BaseEndpoint.DEFAULT_PAGE_SIZE));
		assertNotNull(extensionPoints);
		assertEquals(5L, extensionPoints.getTotalElements());
		
		testExtensionPoint = extensionPoints.getContent().get(0);
		
		entityManager.refresh(testExtensionPoint);
    }

	@Test
	public void findAllExtensionPoints() {
		Integer size = 10;
		Integer page = 0;
		Pageable pageable = PageRequest.of(page, size);
		assertNotNull(extensionPointService.findAll(pageable));
	}
	
	@Test
	public void getExtensionPointById() throws Exception {
		Long id = testExtensionPoint.getId();

		mockMvc.perform(get("/services/v8/extensionpoints/{id}", id))
				.andDo(print())
				.andExpect(status().is2xxSuccessful())
		;
	}
	
	@Test
	public void getExtensionPointByName() throws Exception {
		String name = testExtensionPoint.getName();

		mockMvc.perform(get("/services/v8/extensionpoints/search?name={name}", name))
				.andDo(print())
				.andExpect(status().is2xxSuccessful())
		;
	}

	@SpringBootApplication
	static class TestConfiguration {
	}
}
