package org.eclipse.dirigible.core.git.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.git.command.ResetCommand;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

public class ResetComandTest extends AbstractGuiceTest {

	@Inject
	private CloneCommand cloneCommand;

	@Inject
	private ResetCommand resetCommand;

	@Inject
	private IWorkspacesCoreService workspacesCoreService;

	@Before
	public void setUp() throws Exception {
		this.cloneCommand = getInjector().getInstance(CloneCommand.class);
		this.resetCommand = getInjector().getInstance(ResetCommand.class);
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
			if (username != null) {
				resetCommand.execute(workspace1, new IProject[] { project1 }, username, password);
			}
		}
	}

}
