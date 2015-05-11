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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.eclipse.dirigible.ide.workspace.impl.Folder;
import org.eclipse.dirigible.ide.workspace.impl.Workspace;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;

public class FolderTest {
	private static final String TASK_DELETION = "deletion";
	private static final int TASK_TOTAL_WORK_UNKNOWN = IProgressMonitor.UNKNOWN;
	private static final String TASK_FOLDER_CREATION = "folder creation";
	@Mock
	private IPath path;
	@Mock
	private Workspace workspace;
	@Mock
	private IProgressMonitor progressMonitor;
	@Mock
	private IStatus status;
	@Mock
	private IPath location;
	@Mock
	private IWorkspaceRoot root;
	@Mock
	private IProject project;
	@Mock
	private IRepository repository;
	@Mock
	private ICollection folderEntity;

	private Folder folder;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(workspace.validatePath(anyString(), anyInt())).thenReturn(status);
		when(workspace.getLocation()).thenReturn(location);
		when(workspace.getRoot()).thenReturn(root);
		when(workspace.getRepository()).thenReturn(repository);

		when(path.removeLastSegments(anyInt())).thenReturn(path);
		when(path.segmentCount()).thenReturn(1);
		when(path.append(any(IPath.class))).thenReturn(path);
		when(path.append(anyString())).thenReturn(path);

		when(root.getProject(anyString())).thenReturn(project);
		when(root.getFolder(any(IPath.class))).thenReturn(folder);
		when(root.getLocation()).thenReturn(location);

		when(location.append(any(IPath.class))).thenReturn(location);

		when(repository.getCollection(anyString())).thenReturn(folderEntity);

		folder = new Folder(path, workspace);
	}

	@Test(expected = CoreException.class)
	public void creatingFolderWithInvalidPathShouldThrowException() throws Exception {
		folder.create(0, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_FOLDER_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(progressMonitor, times(1)).done();
	}

	@Test(expected = CoreException.class)
	public void creatingFolderWithSecondMethodWithInvalidPathShouldThrowException()
			throws Exception {
		folder.create(true, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_FOLDER_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(progressMonitor, times(1)).done();
	}

	@Test(expected = CoreException.class)
	public void creatingFolderWhenAlreadyThereAreResourcesWithThatPathShouldThrowException()
			throws Exception {
		when(status.isOK()).thenReturn(true);
		when(workspace.hasResource(location)).thenReturn(true);

		folder.create(0, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_FOLDER_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(progressMonitor, times(1)).done();
	}

	@Test(expected = CoreException.class)
	public void creatingFolderWithoutParentShouldThrowException() throws Exception {
		when(status.isOK()).thenReturn(true);

		folder.create(0, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_FOLDER_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(progressMonitor, times(1)).done();
	}

	@Test(expected = CoreException.class)
	public void creatingFolderWithingProjectThatIsNotOpenShouldThrowException() throws Exception {
		when(status.isOK()).thenReturn(true);
		when(project.exists()).thenReturn(true);

		folder.create(0, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_FOLDER_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(progressMonitor, times(1)).done();
	}

	@Test(expected = CoreException.class)
	public void whenDuringCreatingFolderOccursExceptionCoreExceptionShouldBeThrown()
			throws Exception {
		when(status.isOK()).thenReturn(true);
		when(project.exists()).thenReturn(true);
		when(project.isOpen()).thenReturn(true);
		doThrow(new IOException()).when(folderEntity).create();

		folder.create(0, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_FOLDER_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(workspace, times(1)).notifyResourceChanged(any(IResourceChangeEvent.class));
		verify(progressMonitor, times(1)).done();
	}

	@Test
	public void creatingFolder() throws Exception {
		when(status.isOK()).thenReturn(true);
		when(project.exists()).thenReturn(true);
		when(project.isOpen()).thenReturn(true);

		folder.create(0, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_FOLDER_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(workspace, times(1)).notifyResourceChanged(any(IResourceChangeEvent.class));
		verify(progressMonitor, times(1)).done();
	}

	@Test
	public void deletingNotExistingFolderShouldNotDeleteAnything() throws Exception {
		folder.delete(true, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_DELETION, TASK_TOTAL_WORK_UNKNOWN);
		verify(folderEntity, never()).delete();
		verify(progressMonitor, times(1)).done();
	}

	@Test
	public void deletingFolder() throws Exception {
		when(project.isOpen()).thenReturn(true);
		when(folderEntity.exists()).thenReturn(true);

		folder.delete(true, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_DELETION, TASK_TOTAL_WORK_UNKNOWN);
		verify(folderEntity, times(1)).delete();
		verify(progressMonitor, times(1)).done();
	}

	@Test
	public void getFolder() {
		IFolder folderEntity = folder.getFolder("TestFolder");
		assertNotNull(folderEntity);
	}

	@Test
	public void getFile() {
		IFile file = folder.getFile("testFile.txt");
		assertNotNull(file);
	}

	@Test
	public void whenUsingUnsupportedMethodExceptionShouldBeThrown() throws Exception {
		try {
			folder.createLink(location, 0, progressMonitor);
			fail("folder.(IPath, int, IProgressMonitor) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			folder.createLink(new URI(""), 0, progressMonitor);
			fail("folder.(IPath, int, IProgressMonitor) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
	}
}
