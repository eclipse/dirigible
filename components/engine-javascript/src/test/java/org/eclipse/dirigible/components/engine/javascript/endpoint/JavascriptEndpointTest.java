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
package org.eclipse.dirigible.components.engine.javascript.endpoint;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.jupiter.api.AfterEach;
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
@ComponentScan(basePackages = { "org.eclipse.dirigible.components.*" })
public class JavascriptEndpointTest {
	
	@Autowired
	private JavascriptService javascriptService;
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private IRepository repository;
	
	@BeforeEach
    public void setup() throws Exception {

    	// create test javascript service
		repository.createResource("/registry/public/test/hello-world.js",
				"org.eclipse.dirigible.components.base.http.access.UserResponseVerifier.getResponse().getOutputStream().println(\"Hello World!\");".getBytes());
		repository.createResource("/registry/public/test1/test2/hello-world.js",
				"org.eclipse.dirigible.components.base.http.access.UserResponseVerifier.getResponse().getOutputStream().println(\"Hello World!\");".getBytes());
    }
	
	@AfterEach
    public void cleanup() throws Exception {
		
		// delete test javascript service
		repository.removeResource("/registry/public/test/hello-world.js");
		repository.removeResource("/registry/public/test1/test2/hello-world.js");
    }

//	@Test
//	public void handleRequest() {
//		assertNotNull(javascriptService.handleRequest("test", "hello-world.js", null, null, false));
//	}
	
	@Test
	public void getStatus() throws Exception {

		mockMvc.perform(get("/services/js/test/hello-world.js"))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
	}
	
	@Test
	public void getStatusDeep() throws Exception {

		mockMvc.perform(get("/services/js/test1/test2/hello-world.js"))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
	}
	
	@Test
	public void getStatusDeepQuery() throws Exception {

		mockMvc.perform(get("/services/js/test1/test2/hello-world.js?a=1&b=2"))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
	}
	
	@Test
	public void getResult() throws Exception {

		mockMvc.perform(get("/services/js/test/hello-world.js"))
				.andDo(print())
				.andExpect(content().string(containsString("Hello World!")));
		
	}
	
	@Test
	public void getResultDeep() throws Exception {

		mockMvc.perform(get("/services/js/test1/test2/hello-world.js"))
				.andDo(print())
				.andExpect(content().string(containsString("Hello World!")));
		
	}
	
	@Test
	public void getResultDeepQuery() throws Exception {

		mockMvc.perform(get("/services/js/test1/test2/hello-world.js?a=1&b=2"))
				.andDo(print())
				.andExpect(content().string(containsString("Hello World!")));
		
	}
	
	@Test
	public void getResultDeepPath() throws Exception {

		mockMvc.perform(get("/services/js/test1/test2/hello-world.js/1/2"))
				.andDo(print())
				.andExpect(content().string(containsString("Hello World!")));
		
	}

	@SpringBootApplication
	static class TestConfiguration {
	}
}
