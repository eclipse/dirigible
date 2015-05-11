/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package test.org.eclipse.dirigible.ide.workspace.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.dirigible.ide.workspace.impl.Workspace;
import org.eclipse.dirigible.repository.api.IRepository;

public class WorkspaceTest {
	private static final int TYPE_ROOT = IResource.ROOT;
	private static final int TYPE_FILE = IResource.FILE;
	private static final int TYPE_FOLDER = IResource.FOLDER;
	private static final int TYPE_PROJECT = IResource.PROJECT;

	@Mock
	private IRepository repository;

	private Workspace workspace;

	@Before
	public void setUp() throws Exception {
		workspace = new Workspace(repository);
	}

	private static IStatus createOkStatus() {
		return new Status(Status.OK, RemoteResourcesPlugin.PLUGIN_ID, "");
	}

	private static IStatus createErrorStatus() {
		return new Status(Status.ERROR, RemoteResourcesPlugin.PLUGIN_ID, "");
	}

	private static void assertEqualsStatus(IStatus expected, IStatus actual) {
		assertEquals(expected.getSeverity(), actual.getSeverity());
		assertEquals(expected.getPlugin(), actual.getPlugin());
	}

	@Test(expected = IllegalArgumentException.class)
	public void validatingNullPathShouldThrowException() throws Exception {
		workspace.validatePath(null, TYPE_ROOT);
	}

	@Test
	public void validatingEmptyPathShouldReturnErrorStatus() throws Exception {
		IStatus expectedStatus = createErrorStatus();
		IStatus actualStatus = workspace.validatePath("", TYPE_ROOT);

		assertEqualsStatus(expectedStatus, actualStatus);
	}

	@Test
	public void validatingInvalidPath_Type_1_ShouldReturnErrorStatus() throws Exception {
		IStatus expectedStatus = createErrorStatus();
		IStatus actualStatus = workspace.validatePath("Some\\Invalid\\Path", TYPE_ROOT);

		assertEqualsStatus(expectedStatus, actualStatus);
	}

	@Test
	public void validatingInvalidPath_Type_2_ShouldReturnErrorStatus() throws Exception {
		IStatus expectedStatus = createErrorStatus();
		IStatus actualStatus = workspace.validatePath("Some\\Invalid\\Path", TYPE_ROOT);

		assertEqualsStatus(expectedStatus, actualStatus);
	}

	@Test
	public void validatingInvalidPath_Type_3_ShouldReturnErrorStatus() throws Exception {
		IStatus expectedStatus = createErrorStatus();
		IStatus actualStatus = workspace.validatePath("SomeNotAbsolutePath", TYPE_FILE);

		assertEqualsStatus(expectedStatus, actualStatus);
	}

	@Test
	public void validatingInvalidPath_Type_4_ShouldReturnErrorStatus() throws Exception {
		IStatus expectedStatus = createErrorStatus();
		IStatus actualStatus = workspace.validatePath("C:/Path/With/Device", TYPE_FILE);

		assertEqualsStatus(expectedStatus, actualStatus);
	}

	@Test
	public void validatingInvalidPath_Type_5_ShouldReturnErrorStatus() throws Exception {
		List<String> invalidFilePaths = new ArrayList<String>();
		invalidFilePaths.add("/invalidFile.txt");
		invalidFilePaths.add("/Project/@invalid.txt");
//		invalidFilePaths.add("/Project/ValidFolder/invalid.txt.txt");
//		invalidFilePaths.add("/Project/ValidFolder/.invalid.txt");
		invalidFilePaths.add("/Project/ValidFolder/invalid.!");
//		invalidFilePaths.add("/Project/ValidFolder/_._");
		invalidFilePaths.add("/Project/ValidFolder/Abc#d.mp3");

		IStatus expectedStatus = createErrorStatus();
		for (String invalidFilePath : invalidFilePaths) {
			IStatus actualStatus = workspace.validatePath(invalidFilePath, TYPE_FILE);

			assertEqualsStatus(expectedStatus, actualStatus);
		}
	}

	@Test
	public void validatingInvalidPath_Type_6_ShouldReturnErrorStatus() throws Exception {
		List<String> invalidFolderPaths = new ArrayList<String>();
		invalidFolderPaths.add("/invalidFolder");
		invalidFolderPaths.add("/Project/1myFolder");
		invalidFolderPaths.add("/Project/Folder/!Folder");
		invalidFolderPaths.add("/Project/Folder/AlmostValid!");
		invalidFolderPaths.add("/Project/Folder/AlmostValid#Too");
		invalidFolderPaths.add("/Project/Folder/Definitly.Not.Valid");
		IStatus expectedStatus = createErrorStatus();
		for (String invalidFolderPath : invalidFolderPaths) {
			IStatus actualStatus = workspace.validatePath(invalidFolderPath, TYPE_FOLDER);

			assertEqualsStatus(expectedStatus, actualStatus);
		}
	}

	@Test
	public void validatingInvalidPath_Type_8_ShouldReturnErrorStatus() throws Exception {
		List<String> invalidProjectPaths = new ArrayList<String>();
		invalidProjectPaths.add("/ProjectVersion2.0");
		invalidProjectPaths.add("/ProjectVersion2_0_1!");
		invalidProjectPaths.add("/!ProjectVersion2_0_1");
		invalidProjectPaths.add("/1stProject");
		invalidProjectPaths.add("/First@Project");
		invalidProjectPaths.add("/Test=Project");
		invalidProjectPaths.add("/Too/Many/Segments");

		IStatus expectedStatus = createErrorStatus();
		for (String invalidProjectPath : invalidProjectPaths) {
			IStatus actualStatus = workspace.validatePath(invalidProjectPath, TYPE_PROJECT);

			assertEqualsStatus(expectedStatus, actualStatus);
		}
	}

	@Test
	public void validatingValidPath_Type_1_ShouldReturnOkStatus() throws Exception {
		List<String> validFilePaths = new ArrayList<String>();
		validFilePaths.add("/Project/Valid.txt");
		validFilePaths.add("/Project/ValidFolder/_valid_file_.txt");
		validFilePaths.add("/Project/ValidFolder/1st_Song.mp3");
		validFilePaths.add("/Project/ValidFolder/_SOME_rUby_CoDe.RB");
		validFilePaths.add("/Project/ValidFolder/dump");

		IStatus expectedStatus = createOkStatus();
		for (String validFilePath : validFilePaths) {
			IStatus actualStatus = workspace.validatePath(validFilePath, TYPE_FILE);

			assertEqualsStatus(expectedStatus, actualStatus);
		}
	}

	@Test
	public void validatingValidPath_Type_2_ShouldReturnOkStatus() throws Exception {
		List<String> validFolderPaths = new ArrayList<String>();
		validFolderPaths.add("/Project/validFolder");
		validFolderPaths.add("/Project/Valid_Folder");
		validFolderPaths.add("/Project/ValidFolder2");

		IStatus expectedStatus = createOkStatus();
		for (String validFolderPath : validFolderPaths) {
			IStatus actualStatus = workspace.validatePath(validFolderPath, TYPE_FOLDER);

			assertEqualsStatus(expectedStatus, actualStatus);
		}
	}

	@Test
	public void validatingValidPath_Type_3_ShouldReturnOkStatus() throws Exception {
		List<String> validProjectPaths = new ArrayList<String>();
		validProjectPaths.add("/Project");
		validProjectPaths.add("/project2");
		validProjectPaths.add("/Project_Version_2_0");

		IStatus expectedStatus = createOkStatus();
		for (String validProjectPath : validProjectPaths) {
			IStatus actualStatus = workspace.validatePath(validProjectPath, TYPE_PROJECT);

			assertEqualsStatus(expectedStatus, actualStatus);
		}
	}
}