package org.eclipse.dirigible.core.workspace.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

public class ProjectTest extends AbstractGuiceTest {

	@Inject
	private IWorkspacesCoreService workspacesCoreService;

	@Before
	public void setUp() throws Exception {
		this.workspacesCoreService = getInjector().getInstance(WorkspacesCoreService.class);
	}

	@Test
	public void createFolderTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1");
		assertNotNull(folder1);
		assertNotNull(folder1.getInternal());
		assertEquals("Folder1", folder1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder1.getInternal().getPath());
		project1.deleteFolder("Folder1");
		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	@Test
	public void createFolderDeepTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1/Folder2/Folder3");
		assertNotNull(folder1);
		assertNotNull(folder1.getInternal());
		assertEquals("Folder3", folder1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1/Folder2/Folder3", folder1.getInternal().getPath());
		project1.deleteFolder("Folder1");
		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	@Test
	public void getFolderTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1");
		assertNotNull(folder1);
		assertNotNull(folder1.getInternal());
		assertEquals("Folder1", folder1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder1.getInternal().getPath());

		IFolder folder1_1 = project1.getFolder("Folder1");
		assertNotNull(folder1_1);
		assertNotNull(folder1_1.getInternal());
		assertEquals("Folder1", folder1_1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder1_1.getInternal().getPath());

		project1.deleteFolder("Folder1");
		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	@Test
	public void getFoldersTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1");
		IFolder folder2 = project1.createFolder("Folder2");

		List<IFolder> folders = project1.getFolders();
		assertNotNull(folders);
		assertEquals(2, folders.size());
		IFolder folder3 = folders.get(0);
		assertNotNull(folder3.getInternal());
		if (folder3.getName().equals("Folder1")) {
			assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder3.getInternal().getPath());
		} else {
			assertEquals("/users/guest/TestWorkspace1/Project1/Folder2", folder3.getInternal().getPath());
		}

		project1.deleteFolder("Folder1");
		project1.deleteFolder("Folder2");
		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	@Test
	public void deleteFolderTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1");
		assertNotNull(folder1);
		assertNotNull(folder1.getInternal());
		assertEquals("Folder1", folder1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder1.getInternal().getPath());
		project1.deleteFolder("Folder1");
		IFolder folder2 = workspace1.getProject("Folder1");
		assertNotNull(folder2);
		assertNotNull(folder2.getInternal());
		assertEquals(false, folder2.exists());
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

}
