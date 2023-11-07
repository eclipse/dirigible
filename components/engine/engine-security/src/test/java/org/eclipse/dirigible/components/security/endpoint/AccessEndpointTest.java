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
package org.eclipse.dirigible.components.security.endpoint;

import org.eclipse.dirigible.components.security.repository.AccessRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.eclipse.dirigible.components.security.repository.AccessRepositoryTest.createSecurityAccess;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AccessRepository.class})
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"org.eclipse.dirigible.components"})
@EntityScan("org.eclipse.dirigible.components")
@Transactional
class AccessEndpointTest {

	@Autowired
	private AccessRepository accessRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@BeforeEach
	public void setup() {

		cleanup();

		// Create test security accesses
		accessRepository.save(
				createSecurityAccess("/a/b/c/test1.access", "test1", "description", "HTTP", "/a" + "/b/c/test1.txt", "GET", "test_role_1"));
		accessRepository.save(
				createSecurityAccess("/a/b/c/test2.access", "test2", "description", "HTTP", "/a" + "/b/c/test2.txt", "GET", "test_role_2"));
	}

	@AfterEach
	public void cleanup() {
		// Delete test security accesses
		accessRepository.deleteAll();
	}

	@Test
	public void testGetSecurityAccesses() throws Exception {
		mockMvc.perform(get("/services/security/access").accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
	}

	@SpringBootApplication
	static class TestConfiguration {
	}
}
