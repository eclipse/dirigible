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
package org.eclipse.dirigible.components.registry.endpoint;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.eclipse.dirigible.components.registry.service.RegistryService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@WithMockUser
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"org.eclipse.dirigible.components.*"})
public class RegistryEndpointTest {

	@Autowired
	private IRepository repository;

	@Autowired
	private RegistryService registryService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@BeforeEach
	public void setup() throws Exception {
		repository.createResource("/registry/public/test1.txt");
		repository.createResource("/registry/public/a/test2.txt");
		repository.createResource("/registry/public/a/test3.txt");
	}

	@Test
	public void getResource() {
		assertNotNull(registryService.getResource("test1.txt"));
	}

	@Test
	public void getResourceByPath() throws Exception {
		mockMvc.perform(get("/services/core/registry/{path}", "a/test2.txt")).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@SpringBootApplication
	static class TestConfiguration {
	}
}
