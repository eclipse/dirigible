/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.git.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.git.command.PushCommand;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class PushComandTest.
 */
public class PushComandTest extends AbstractGuiceTest {

	private static final String DIRIGIBLE_TEST_GIT_EMAIL = "DIRIGIBLE_TEST_GIT_EMAIL";
	
	private static final String DIRIGIBLE_TEST_GIT_USERNAME = "DIRIGIBLE_TEST_GIT_USERNAME";

	private static final String DIRIGIBLE_TEST_GIT_PASSWORD = "DIRIGIBLE_TEST_GIT_PASSWORD";

	/** The clone command. */
	@Inject
	private CloneCommand cloneCommand;

	/** The push command. */
	@Inject
	private PushCommand pushCommand;

	/** The workspaces core service. */
	@Inject
	private IWorkspacesCoreService workspacesCoreService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.cloneCommand = getInjector().getInstance(CloneCommand.class);
		this.pushCommand = getInjector().getInstance(PushCommand.class);
		this.workspacesCoreService = getInjector().getInstance(WorkspacesCoreService.class);
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
			String repositoryName = "sample_git_test";
			cloneCommand.execute("https://github.com/dirigiblelabs/" + repositoryName + ".git", IGitConnector.GIT_MASTER, null, null, "workspace1", true, null);
			IWorkspace workspace1 = workspacesCoreService.getWorkspace("workspace1");
			assertNotNull(workspace1);
			assertTrue(workspace1.exists());
			IProject project1 = workspace1.getProject("project1");
			assertNotNull(project1);
			assertTrue(project1.exists());
			String username = System.getProperty(DIRIGIBLE_TEST_GIT_USERNAME);
			String password = System.getProperty(DIRIGIBLE_TEST_GIT_PASSWORD);
			String email = System.getProperty(DIRIGIBLE_TEST_GIT_EMAIL);
			if (username != null) {
				pushCommand.execute(workspace1, Arrays.asList(repositoryName), "test", username, password, email, IGitConnector.GIT_MASTER, true, true);
			}
		}
	}

}
