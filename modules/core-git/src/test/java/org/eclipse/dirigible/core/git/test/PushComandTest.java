/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.git.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

public class PushComandTest extends AbstractGuiceTest {

	@Inject
	private CloneCommand cloneCommand;

	@Inject
	private PushCommand pushCommand;

	@Inject
	private IWorkspacesCoreService workspacesCoreService;

	@Before
	public void setUp() throws Exception {
		this.cloneCommand = getInjector().getInstance(CloneCommand.class);
		this.pushCommand = getInjector().getInstance(PushCommand.class);
		this.workspacesCoreService = getInjector().getInstance(WorkspacesCoreService.class);
	}

	@Test
	public void createWorkspaceTest() throws GitConnectorException {
		String gitEnabled = System.getProperty("dirigibleTestGitEnabled");
		if (gitEnabled != null) {
			cloneCommand.execute("https://github.com/dirigiblelabs/sample_git_test.git", IGitConnector.GIT_MASTER, null, null, "workspace1", true);
			IWorkspace workspace1 = workspacesCoreService.getWorkspace("workspace1");
			assertNotNull(workspace1);
			assertTrue(workspace1.exists());
			IProject project1 = workspace1.getProject("project1");
			assertNotNull(project1);
			assertTrue(project1.exists());
			String username = System.getProperty("dirigibleTestGitUsername");
			String password = System.getProperty("dirigibleTestGitPassword");
			String email = System.getProperty("dirigibleTestGitEmail");
			if (username != null) {
				pushCommand.execute(workspace1, new IProject[] { project1 }, "test", username, password, email);
			}
		}
	}

}
