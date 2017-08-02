package org.eclipse.dirigible.core.git.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.git.command.ShareCommand;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

public class ShareComandTest extends AbstractGuiceTest {

	@Inject
	private CloneCommand cloneCommand;

	@Inject
	private ShareCommand shareCommand;

	@Inject
	private IWorkspacesCoreService workspacesCoreService;

	@Before
	public void setUp() throws Exception {
		this.cloneCommand = getInjector().getInstance(CloneCommand.class);
		this.shareCommand = getInjector().getInstance(ShareCommand.class);
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
				shareCommand.execute(workspace1, project1, "https://github.com/dirigiblelabs/sample_git_test.git", IGitConnector.GIT_MASTER,
						"test commit", username, password, email);
			}
		}
	}

}
