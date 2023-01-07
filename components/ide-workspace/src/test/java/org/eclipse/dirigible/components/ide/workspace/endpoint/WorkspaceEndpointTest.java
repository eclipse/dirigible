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
package org.eclipse.dirigible.components.ide.workspace.endpoint;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@WithMockUser
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan(basePackages = { "org.eclipse.dirigible.components" })
@EntityScan("org.eclipse.dirigible.components")
public class WorkspaceEndpointTest {
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;
	
	@BeforeEach
    public void setup() throws Exception {
		
		cleanup();

    	
    }
	
	@AfterEach
    public void cleanup() throws Exception {
    }
	
	@Test
	public void getAllWorkspaces() throws Exception {
		mockMvc.perform(get("/services/v8/ide/workspaces"))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void createGetDeleteWorkspace() throws Exception {
		mockMvc.perform(post("/services/v8/ide/workspaces")
					.content("workspace1")
					.with(csrf()))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/services/v8/ide/workspaces", "workspace1"))
			.andDo(print())
			.andExpect(status().is2xxSuccessful());
		mockMvc.perform(delete("/services/v8/ide/workspaces")
				.content("workspace1")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is2xxSuccessful());
	}

	@SpringBootApplication
	static class TestConfiguration {
	    @Bean
	    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenFilterRegistrationBean() {
	        FilterRegistrationBean<HiddenHttpMethodFilter> filterRegistrationBean = new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
	
	        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
	
	        return filterRegistrationBean;
	    }
	}
}
