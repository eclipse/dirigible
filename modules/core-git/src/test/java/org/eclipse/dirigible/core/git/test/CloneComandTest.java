package org.eclipse.dirigible.core.git.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.IGitConnector;
import org.eclipse.dirigible.core.git.command.CloneCommand;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

public class CloneComandTest extends AbstractGuiceTest {

	@Inject
	private CloneCommand cloneCommand;

	@Inject
	private IWorkspacesCoreService workspacesCoreService;

	@Before
	public void setUp() throws Exception {
		this.cloneCommand = getInjector().getInstance(CloneCommand.class);
		this.workspacesCoreService = getInjector().getInstance(WorkspacesCoreService.class);
	}

	@Test
	public void createWorkspaceTest() throws GitConnectorException {
		cloneCommand.execute("https://github.com/dirigiblelabs/sample_git_test.git", IGitConnector.GIT_MASTER, null, null, "workspace1", true);
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("workspace1");
		assertNotNull(workspace1);
		assertTrue(workspace1.exists());
		IProject project1 = workspace1.createProject("project1");
		assertNotNull(project1);
		assertTrue(project1.exists());
		IFolder folder1 = project1.getFolder("folder1");
		assertNotNull(folder1);
		assertTrue(folder1.exists());
		IFile file1 = folder1.getFile("service1.js");
		assertNotNull(file1);
		assertTrue(file1.exists());
	}

}
