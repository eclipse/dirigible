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
package org.eclipse.dirigible.core.workspace.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.api.IWorkspacesCoreService;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The Class WorkspaceTest.
 */

@Ignore
public class WorkspaceTest extends AbstractDirigibleTest {

	/** The workspaces core service. */
	private IWorkspacesCoreService workspacesCoreService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.workspacesCoreService = new WorkspacesCoreService();
	}

	/**
	 * Creates the project test.
	 */
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

	/**
	 * Gets the project test.
	 *
	 * @return the project test
	 */
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

	/**
	 * Gets the projects test.
	 *
	 * @return the projects test
	 */
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

	/**
	 * Delete project test.
	 */
	@Test
	public void deleteProjectTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		assertNotNull(project1);
		assertNotNull(project1.getInternal());
		assertEquals("Project1", project1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1.getInternal().getPath());
		assertTrue(project1.exists());
		workspace1.deleteProject("Project1");
		IProject project2 = workspace1.getProject("Project1");
		assertNotNull(project2);
		assertNotNull(project2.getInternal());
		assertFalse(project2.exists());

		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Copy project test.
	 */
	@Test
	public void copyProjectTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1");
		assertNotNull(project1);
		assertNotNull(project1.getInternal());
		assertEquals("Project1", project1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1.getInternal().getPath());

		workspace1.copyProject("Project1", "Project2");

		IProject project1_1 = workspace1.getProject("Project1");
		assertNotNull(project1_1);
		assertNotNull(project1_1.getInternal());
		assertEquals("Project1", project1_1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1_1.getInternal().getPath());
		assertTrue(project1_1.exists());
		IFolder folder1_1 = project1_1.getFolder("Folder1");
		assertNotNull(folder1_1);
		assertNotNull(folder1_1.getInternal());
		assertEquals("Folder1", folder1_1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder1_1.getInternal().getPath());
		assertTrue(folder1_1.exists());

		IProject project1_2 = workspace1.getProject("Project2");
		assertNotNull(project1_2);
		assertNotNull(project1_2.getInternal());
		assertEquals("Project2", project1_2.getName());
		assertEquals("/users/guest/TestWorkspace1/Project2", project1_2.getInternal().getPath());
		assertTrue(project1_2.exists());
		IFolder folder1_2 = project1_2.getFolder("Folder1");
		assertNotNull(folder1_2);
		assertNotNull(folder1_2.getInternal());
		assertEquals("Folder1", folder1_2.getName());
		assertEquals("/users/guest/TestWorkspace1/Project2/Folder1", folder1_2.getInternal().getPath());
		assertTrue(folder1_2.exists());

		workspace1.deleteProject("Project1");
		workspace1.deleteProject("Project2");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Move project test.
	 */
	@Test
	public void moveProjectTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1");
		assertNotNull(project1);
		assertNotNull(project1.getInternal());
		assertEquals("Project1", project1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1.getInternal().getPath());

		workspace1.moveProject("Project1", "Project2");

		IProject project1_1 = workspace1.getProject("Project1");
		assertNotNull(project1_1);
		assertNotNull(project1_1.getInternal());
		assertEquals("Project1", project1_1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1", project1_1.getInternal().getPath());
		IFolder folder1_1 = project1_1.getFolder("Folder1");
		assertNotNull(folder1_1);
		assertNotNull(folder1_1.getInternal());
		assertEquals("Folder1", folder1_1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder1_1.getInternal().getPath());
		assertFalse(folder1_1.exists());

		IProject project1_2 = workspace1.getProject("Project2");
		assertNotNull(project1_2);
		assertNotNull(project1_2.getInternal());
		assertEquals("Project2", project1_2.getName());
		assertEquals("/users/guest/TestWorkspace1/Project2", project1_2.getInternal().getPath());
		IFolder folder1_2 = project1_2.getFolder("Folder1");
		assertNotNull(folder1_2);
		assertNotNull(folder1_2.getInternal());
		assertEquals("Folder1", folder1_2.getName());
		assertEquals("/users/guest/TestWorkspace1/Project2/Folder1", folder1_2.getInternal().getPath());
		assertTrue(folder1_2.exists());

		workspace1.deleteProject("Project2");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Creates the folder test.
	 */
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

	/**
	 * Gets the folder test.
	 *
	 * @return the folder test
	 */
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

	/**
	 * Gets the folders test.
	 *
	 * @return the folders test
	 */
	@Test
	public void getFoldersTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1");
		IFolder folder2 = project1.createFolder("Folder2");
		assertNotNull(folder1);
		assertNotNull(folder1.getInternal());
		assertEquals("Folder1", folder1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder1.getInternal().getPath());

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

	/**
	 * Delete folder test.
	 */
	@Test
	public void deleteFolderTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFolder folder1 = project1.createFolder("Folder1");
		assertNotNull(folder1);
		assertNotNull(folder1.getInternal());
		assertEquals("Folder1", folder1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/Folder1", folder1.getInternal().getPath());
		assertTrue(folder1.exists());
		project1.deleteFolder("Folder1");
		IFolder folder2 = project1.getFolder("Folder1");
		assertNotNull(folder2);
		assertNotNull(folder2.getInternal());
		assertFalse(folder2.exists());

		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Creates the file test.
	 */
	@Test
	public void createFileTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFile file1 = project1.createFile("File1.txt", "test".getBytes());
		assertNotNull(file1);
		assertNotNull(file1.getInternal());
		assertEquals("File1.txt", file1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/File1.txt", file1.getInternal().getPath());

		project1.deleteFile("File1.txt");
		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Gets the file test.
	 *
	 * @return the file test
	 */
	@Test
	public void getFileTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFile file1 = project1.createFile("File1.txt", "test".getBytes());
		assertNotNull(file1);
		assertNotNull(file1.getInternal());
		assertEquals("File1.txt", file1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/File1.txt", file1.getInternal().getPath());

		IFile file1_1 = project1.getFile("File1.txt");
		assertNotNull(file1_1);
		assertNotNull(file1_1.getInternal());
		assertEquals("File1.txt", file1_1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/File1.txt", file1_1.getInternal().getPath());
		assertTrue("test".equals(new String(file1_1.getContent())));

		project1.deleteFile("File1.txt");
		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Gets the files test.
	 *
	 * @return the files test
	 */
	@Test
	public void getFilesTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFile file1 = project1.createFile("File1.txt", "test".getBytes());
		IFile file2 = project1.createFile("File2.txt", "test".getBytes());
		assertNotNull(file1);
		assertNotNull(file1.getInternal());
		assertEquals("File1.txt", file1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/File1.txt", file1.getInternal().getPath());

		List<IFile> files = project1.getFiles();
		assertNotNull(files);
		assertEquals(2, files.size());
		IFile file3 = files.get(0);
		assertNotNull(file3.getInternal());
		if (file3.getName().equals("File1.txt")) {
			assertEquals("/users/guest/TestWorkspace1/Project1/File1.txt", file3.getInternal().getPath());
		} else {
			assertEquals("/users/guest/TestWorkspace1/Project1/File2.txt", file3.getInternal().getPath());
		}

		project1.deleteFile("File1.txt");
		project1.deleteFile("File2.txt");
		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

	/**
	 * Delete file test.
	 */
	@Test
	public void deleteFileTest() {
		IWorkspace workspace1 = workspacesCoreService.createWorkspace("TestWorkspace1");
		IProject project1 = workspace1.createProject("Project1");
		IFile file1 = project1.createFile("File1.txt", "test".getBytes());
		assertNotNull(file1);
		assertNotNull(file1.getInternal());
		assertEquals("File1.txt", file1.getName());
		assertEquals("/users/guest/TestWorkspace1/Project1/File1.txt", file1.getInternal().getPath());
		assertTrue(file1.exists());
		project1.deleteFile("File1.txt");
		IFile file2 = project1.getFile("File1.txt");
		assertNotNull(file2);
		assertNotNull(file2.getInternal());
		assertFalse(file2.exists());

		workspace1.deleteProject("Project1");
		workspacesCoreService.deleteWorkspace("TestWorkspace1");
	}

}
