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

import java.util.Arrays;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.git.command.PushCommand;
import org.eclipse.dirigible.core.git.model.GitCloneModel;
import org.eclipse.dirigible.core.git.model.GitPushModel;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class PushComandTest.
 */
public class PushComandTest extends AbstractDirigibleTest {

	private static final String DIRIGIBLE_TEST_GIT_EMAIL = "DIRIGIBLE_TEST_GIT_EMAIL";
	
	private static final String DIRIGIBLE_TEST_GIT_USERNAME = "DIRIGIBLE_TEST_GIT_USERNAME";

	private static final String DIRIGIBLE_TEST_GIT_PASSWORD = "DIRIGIBLE_TEST_GIT_PASSWORD";

	/** The clone command. */
	private CloneCommand cloneCommand;

	/** The push command. */
	private PushCommand pushCommand;

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
		this.pushCommand = new PushCommand();
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
			GitCloneModel cloneModel = new GitCloneModel();
			cloneModel.setRepository("https://github.com/dirigiblelabs/sample_git_test.git");
			cloneModel.setBranch(IGitConnector.GIT_MASTER);
			cloneModel.setPublish(true);
			cloneCommand.execute(workspace1, cloneModel);
			assertNotNull(workspace1);
			assertTrue(workspace1.exists());
			IProject project1 = workspace1.getProject("project1");
			assertNotNull(project1);
			assertTrue(project1.exists());
			String username = System.getProperty(DIRIGIBLE_TEST_GIT_USERNAME);
			String password = System.getProperty(DIRIGIBLE_TEST_GIT_PASSWORD);
			String email = System.getProperty(DIRIGIBLE_TEST_GIT_EMAIL);
			if (username != null && password != null && email != null) {
				GitPushModel pushModel = new GitPushModel();
				pushModel.setProjects(Arrays.asList("sample_git_test"));
				pushModel.setUsername(username);
				pushModel.setPassword(password);
				pushModel.setEmail(email);
				pushModel.setAutoAdd(true);
				pushModel.setAutoCommit(true);
				pushCommand.execute(workspace1, pushModel);
			}
		}
	}

}
