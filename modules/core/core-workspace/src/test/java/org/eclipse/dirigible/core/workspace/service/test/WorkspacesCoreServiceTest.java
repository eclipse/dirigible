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
package org.eclipse.dirigible.core.workspace.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The Class WorkspacesCoreServiceTest.
 */
@Ignore
public class WorkspacesCoreServiceTest extends AbstractDirigibleTest {

	/** The workspaces core service. */
	private IWorkspacesCoreService workspacesCoreService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.workspacesCoreService = new WorkspacesCoreService();
	}

	/**
	 * Creates the workspace test.
	 */
	@Test
	public void createWorkspaceTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		assertNotNull(workspace1);
		assertNotNull(workspace1.getInternal());
		assertEquals("TestWorkspace1", workspace1.getName());
		assertEquals("/users/guest/TestWorkspace1", workspace1.getInternal().getPath());
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Gets the workspace test.
	 *
	 * @return the workspace test
	 */
	@Test
	public void getWorkspaceTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		assertNotNull(workspace1);
		assertNotNull(workspace1.getInternal());
		assertEquals("TestWorkspace1", workspace1.getName());
		assertEquals("/users/guest/TestWorkspace1", workspace1.getInternal().getPath());
		IWorkspace workspace = workspacesCoreService.getWorkspace("TestWorkspace1");
		assertNotNull(workspace);
		assertNotNull(workspace.getInternal());
		assertEquals("TestWorkspace1", workspace.getName());
		assertEquals("/users/guest/TestWorkspace1", workspace.getInternal().getPath());
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Gets the workspaces test.
	 *
	 * @return the workspaces test
	 */
	@Test
	public void getWorkspacesTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IWorkspace workspace2 = workspacesCoreService.createWorkspace("TestWorkspace2");
		List<IWorkspace> workspaces = workspacesCoreService.getWorkspaces();
		assertNotNull(workspaces);
		assertEquals(2, workspaces.size());
		IWorkspace worskapce3 = workspaces.get(0);
		assertNotNull(worskapce3.getInternal());
		if (worskapce3.getName().equals("TestWorkspace1")) {
			assertEquals("/users/guest/TestWorkspace1", workspace1.getInternal().getPath());
		} else {
			assertEquals("/users/guest/TestWorkspace2", workspace2.getInternal().getPath());
		}
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
		workspacesCoreService.deleteWorkspace("TestWorkspace2");
	}

	/**
	 * Delete workspace test.
	 */
	@Test
	public void deleteWorkspaceTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		assertNotNull(workspace1);
		assertNotNull(workspace1.getInternal());
		assertEquals("TestWorkspace1", workspace1.getName());
		assertEquals("/users/guest/TestWorkspace1", workspace1.getInternal().getPath());
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
		IWorkspace workspace2 = workspacesCoreService.getWorkspace("TestWorkspace1");
		assertNotNull(workspace2);
		assertNotNull(workspace2.getInternal());
		assertEquals(false, workspace2.exists());
	}

}
