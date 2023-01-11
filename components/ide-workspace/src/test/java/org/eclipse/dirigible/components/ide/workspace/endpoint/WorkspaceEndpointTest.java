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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.ide.workspace.domain.File;
import org.eclipse.dirigible.components.ide.workspace.domain.Folder;
import org.eclipse.dirigible.components.ide.workspace.domain.Project;
import org.eclipse.dirigible.components.ide.workspace.domain.Workspace;
import org.eclipse.dirigible.components.ide.workspace.domain.WorkspaceSourceTargetPair;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
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
import org.springframework.web.context.WebApplicationContext;

@WithMockUser
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan(basePackages = { "org.eclipse.dirigible.components" })
@EntityScan("org.eclipse.dirigible.components")
public class WorkspaceEndpointTest {
	
	@Autowired
	private WorkspaceService workspaceService; 
	
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
	public void copy() throws Exception {
		Workspace workspace = workspaceService.createWorkspace("workspace1");
		Project project = workspace.createProject("project1");
		Folder folder = project.createFolder("folder1");
		folder.createFile("file1.txt", "hello".getBytes());
		WorkspaceSourceTargetPair pair = new WorkspaceSourceTargetPair();
		pair.setSourceWorkspace("workspace1");
		pair.setTargetWorkspace("workspace1");
		pair.setSource("project1/folder1/file1.txt");
		pair.setTarget("project1");
		mockMvc.perform(post("/services/v8/ide/workspace/workspace1/copy")
					.contentType(MediaType.APPLICATION_JSON)
					.content(GsonHelper.toJson(pair))
					.with(csrf()))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		File file = project.getFile("file1.txt");
		assertNotNull(file);
		assertEquals("file1.txt", file.getName());
		assertTrue(file.exists());
		mockMvc.perform(delete("/services/v8/ide/workspaces/workspace1")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void move() throws Exception {
		Workspace workspace = workspaceService.createWorkspace("workspace1");
		Project project = workspace.createProject("project1");
		Folder folder = project.createFolder("folder1");
		folder.createFile("file1.txt", "hello".getBytes());
		WorkspaceSourceTargetPair pair = new WorkspaceSourceTargetPair();
		pair.setSourceWorkspace("workspace1");
		pair.setTargetWorkspace("workspace1");
		pair.setSource("project1/folder1/file1.txt");
		pair.setTarget("project1/file1.txt");
		mockMvc.perform(post("/services/v8/ide/workspace/workspace1/move")
					.contentType(MediaType.APPLICATION_JSON)
					.content(GsonHelper.toJson(pair))
					.with(csrf()))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		File file = project.getFile("file1.txt");
		assertNotNull(file);
		assertEquals("file1.txt", file.getName());
		assertTrue(file.exists());
		mockMvc.perform(delete("/services/v8/ide/workspaces/workspace1")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is2xxSuccessful());
	}

	@SpringBootApplication
	static class TestConfiguration {
	}
}
