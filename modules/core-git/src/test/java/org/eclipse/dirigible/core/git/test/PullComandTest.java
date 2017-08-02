package org.eclipse.dirigible.core.git.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.git.command.PullCommand;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

public class PullComandTest extends AbstractGuiceTest {

	@Inject
	private CloneCommand cloneCommand;

	@Inject
	private PullCommand pullCommand;

	@Inject
	private IWorkspacesCoreService workspacesCoreService;

	@Before
	public void setUp() throws Exception {
		this.cloneCommand = getInjector().getInstance(CloneCommand.class);
		this.pullCommand = getInjector().getInstance(PullCommand.class);
		this.workspacesCoreService = getInjector().getInstance(WorkspacesCoreService.class);
	}

	@Test
	public void createWorkspaceTest() throws GitConnectorException {
		cloneCommand.execute("https://github.com/dirigiblelabs/sample_git_test.git", IGitConnector.GIT_MASTER, null, null, "workspace1", true);
		IWorkspace workspace1 = workspacesCoreService.getWorkspace("workspace1");
		assertNotNull(workspace1);
		assertTrue(workspace1.exists());
		IProject project1 = workspace1.getProject("project1");
		assertNotNull(project1);
		assertTrue(project1.exists());
		pullCommand.execute(workspace1, new IProject[] { project1 }, true);
	}

}
