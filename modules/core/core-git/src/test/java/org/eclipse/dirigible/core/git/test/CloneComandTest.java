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
package org.eclipse.dirigible.core.git.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.git.model.GitCloneModel;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class CloneComandTest.
 */
public class CloneComandTest extends AbstractDirigibleTest {

	/** The clone command. */
	private CloneCommand cloneCommand;

	/** The workspaces core service. */
	private IWorkspacesCoreService workspacesCoreService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.cloneCommand = new CloneCommand();
		this.workspacesCoreService = new WorkspacesCoreService();
	}

	/**
	 * Creates the workspace test.
	 *
	 * @throws GitConnectorException the git connector exception
	 */
	@Test
	public void createWorkspaceTest() throws GitConnectorException {
		String gitEnabled = System.getenv(GitConnectorTest.DIRIGIBLE_TEST_GIT_ENABLED);
		if (gitEnabled != null) {
			IWorkspace workspace1 = workspacesCoreService.getWorkspace("workspace1");
			GitCloneModel model = new GitCloneModel();
			model.setRepository("https://github.com/dirigiblelabs/sample_git_test.git");
			model.setBranch(IGitConnector.GIT_MASTER);
			model.setPublish(true);
			cloneCommand.execute(workspace1, model);
			assertNotNull(workspace1);
			assertTrue(workspace1.exists());
			IProject project1 = workspace1.getProject("project1");
			assertNotNull(project1);
			assertTrue(project1.exists());
			IFolder folder1 = project1.getFolder("folder1");
			assertNotNull(folder1);
			assertTrue(folder1.exists());
			IFile file1 = folder1.getFile("service1.js");
			assertNotNull(file1);
			assertTrue(file1.exists());
			workspace1.delete();
		}
	}
	
	/**
	 * Creates the workspace test.
	 *
	 * @throws GitConnectorException the git connector exception
	 */
	@Test
	public void createWorkspaceNoGitTest() throws GitConnectorException {
		String gitEnabled = System.getenv(GitConnectorTest.DIRIGIBLE_TEST_GIT_ENABLED);
		if (gitEnabled != null) {
			IWorkspace workspace1 = workspacesCoreService.getWorkspace("workspace1");
			GitCloneModel model = new GitCloneModel();
			model.setRepository("https://github.com/dirigiblelabs/sample_git_test");
			model.setBranch(IGitConnector.GIT_MASTER);
			model.setPublish(true);
			cloneCommand.execute(workspace1, model);
			assertNotNull(workspace1);
			assertTrue(workspace1.exists());
			IProject project1 = workspace1.getProject("project1");
			assertNotNull(project1);
			assertTrue(project1.exists());
			IFolder folder1 = project1.getFolder("folder1");
			assertNotNull(folder1);
			assertTrue(folder1.exists());
			IFile file1 = folder1.getFile("service1.js");
			assertNotNull(file1);
			assertTrue(file1.exists());
			workspace1.delete();
		}
	}
	
	/**
	 * Creates the workspace test.
	 *
	 * @throws GitConnectorException the git connector exception
	 */
	@Test
	public void createWorkspaceNoProjectTest() throws GitConnectorException {
		String gitEnabled = System.getenv(GitConnectorTest.DIRIGIBLE_TEST_GIT_ENABLED);
		if (gitEnabled != null) {
			IWorkspace workspace1 = workspacesCoreService.getWorkspace("workspace1");
			GitCloneModel model = new GitCloneModel();
			model.setRepository("https://github.com/dirigiblelabs/sample_git_no_project_test.git");
			model.setBranch(IGitConnector.GIT_MASTER);
			model.setPublish(true);
			cloneCommand.execute(workspace1, model);
			assertNotNull(workspace1);
			assertTrue(workspace1.exists());
			List<IProject> projects = workspace1.getProjects();
			for (IProject project : projects) {
				if (project.getName().startsWith("sample_git_no_project_test")) {
					workspace1.delete();
					return;
				}
			}
			workspace1.delete();
			fail("No project has been created implicitly");
		}
	}

}
