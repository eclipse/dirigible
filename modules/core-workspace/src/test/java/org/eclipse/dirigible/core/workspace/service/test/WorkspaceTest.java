package org.eclipse.dirigible.core.workspace.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Test;

public class WorkspaceTest extends AbstractGuiceTest {

	@Inject
	private IWorkspacesCoreService workspacesCoreService;

	@Before
	public void setUp() throws Exception {
		this.workspacesCoreService = getInjector().getInstance(WorkspacesCoreService.class);
	}

	@Test
	public void createProjectTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		assertNotNull(project1);
		assertNotNull(project1.getInternal());
		assertEquals("Project1", project1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1.getInternal().getPath());
		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	@Test
	public void getProjectTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		assertNotNull(project1);
		assertNotNull(project1.getInternal());
		assertEquals("Project1", project1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1.getInternal().getPath());

		IProject project1_1 = workspace1.getProject("Project1");
		assertNotNull(project1_1);
		assertNotNull(project1_1.getInternal());
		assertEquals("Project1", project1_1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1_1.getInternal().getPath());

		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	@Test
	public void getProjectsTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IProject project2 = workspace1.createProject("Project2");
		assertNotNull(project1);
		assertNotNull(project1.getInternal());
		assertEquals("Project1", project1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1.getInternal().getPath());

		List<IProject> projects = workspace1.getProjects();
		assertNotNull(projects);
		assertEquals(2, projects.size());
		IProject project3 = projects.get(0);
		assertNotNull(project3.getInternal());
		if (project3.getName().equals("Project1")) {
			assertEquals("/users/guest/TestWorkspace1/Project1", project3.getInternal().getPath());
		} else {
			assertEquals("/users/guest/TestWorkspace1/Project2", project3.getInternal().getPath());
		}

		workspace1.deleteProject("Project1");
		workspace1.deleteProject("Project2");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	@Test
	public void deleteProjectTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		assertNotNull(project1);
		assertNotNull(project1.getInternal());
		assertEquals("Project1", project1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1.getInternal().getPath());
		workspace1.deleteProject("Project1");
		IProject project2 = workspace1.getProject("Project1");
		assertNotNull(project2);
		assertNotNull(project2.getInternal());
		assertEquals(false, project2.exists());
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

}
