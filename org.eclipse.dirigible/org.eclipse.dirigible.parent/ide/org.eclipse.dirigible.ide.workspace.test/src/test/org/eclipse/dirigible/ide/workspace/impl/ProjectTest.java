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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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

import org.eclipse.dirigible.ide.workspace.impl.Project;
import org.eclipse.dirigible.ide.workspace.impl.Workspace;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;

public class ProjectTest {
	private static final String TASK_PROJECT_CREATION = "project creation";
	private static final String TASK_PROJECT_DELETION = "deletion";
	private static final int TASK_TOTAL_WORK_UNKNOWN = IProgressMonitor.UNKNOWN;

	@Mock
	private Workspace workspace;
	@Mock
	private IPath path;
	@Mock
	private IProgressMonitor progressMonitor;
	@Mock
	private IStatus pathValidationStatus;
	@Mock
	private IWorkspaceRoot root;
	@Mock
	private IRepository repository;
	@Mock
	private IPath location;
	@Mock
	private ICollection projectEntity;

	private Project project;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(workspace.validatePath(anyString(), anyInt())).thenReturn(pathValidationStatus);
		when(workspace.getRepository()).thenReturn(repository);
		when(workspace.getRoot()).thenReturn(root);
		when(root.getLocation()).thenReturn(location);

		when(path.append(anyString())).thenReturn(path);
		when(path.append(any(IPath.class))).thenReturn(path);
		when(path.segmentCount()).thenReturn(2);

		when(location.append(anyString())).thenReturn(location);
		when(location.append(any(IPath.class))).thenReturn(location);
		when(location.segmentCount()).thenReturn(2);

		when(repository.getCollection(anyString())).thenReturn(projectEntity);

		project = new Project(path, workspace);
	}

	@Test(expected = CoreException.class)
	public void creatingProjectWithInvalidPathShouldThrowException() throws Exception {
		when(pathValidationStatus.isOK()).thenReturn(false);

		project.create(progressMonitor);
	}

	@Test(expected = CoreException.class)
	public void creatingProjectThatAlreadyExistsShouldThrowException() throws Exception {
		when(pathValidationStatus.isOK()).thenReturn(true);
		when(projectEntity.exists()).thenReturn(true);

		project.create(progressMonitor);
	}

	@Test
	public void creatingProjectWithValidPath() throws Exception {
		when(pathValidationStatus.isOK()).thenReturn(true);

		project.create(progressMonitor);

		verify(progressMonitor).beginTask(TASK_PROJECT_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(projectEntity, times(1)).create();
		verify(progressMonitor, times(1)).done();
	}

	@Test(expected = CoreException.class)
	public void whileCreatingProjectOccursIOExceptionCoreExceptiponThrown() throws Exception {
		when(pathValidationStatus.isOK()).thenReturn(true);
		doThrow(new IOException()).when(projectEntity).create();

		project.create(progressMonitor);

		verify(progressMonitor).beginTask(TASK_PROJECT_CREATION, TASK_TOTAL_WORK_UNKNOWN);
		verify(projectEntity, times(1)).create();
		verify(progressMonitor, times(1)).done();
	}

	@Test
	public void deletingExistingProject() throws Exception {
		when(projectEntity.exists()).thenReturn(true);

		project.delete(0, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_PROJECT_DELETION, TASK_TOTAL_WORK_UNKNOWN);
		verify(projectEntity, times(1)).delete();
		verify(workspace, atLeastOnce()).notifyResourceChanged(any(IResourceChangeEvent.class));
		verify(progressMonitor, times(1)).done();
	}

	@Test
	public void deletingExistingProjectSecondMethod() throws Exception {
		when(projectEntity.exists()).thenReturn(true);

		project.delete(true, true, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_PROJECT_DELETION, TASK_TOTAL_WORK_UNKNOWN);
		verify(projectEntity, times(1)).delete();
		verify(workspace, atLeastOnce()).notifyResourceChanged(any(IResourceChangeEvent.class));
		verify(progressMonitor, times(1)).done();
	}

	@Test
	public void deletingNotExistingProjectShouldNotDeleteAnything() throws Exception {
		when(projectEntity.exists()).thenReturn(false);

		project.delete(0, progressMonitor);

		verify(progressMonitor, times(1)).beginTask(TASK_PROJECT_DELETION, TASK_TOTAL_WORK_UNKNOWN);
		verify(projectEntity, never()).delete();
		verify(progressMonitor, times(1)).done();
	}

	@Test
	public void getFolder() {
		IFolder folder = project.getFolder(location);
		assertNotNull(folder);

		folder = project.getFolder("TestFolder");
		assertNotNull(folder);
	}

	@Test
	public void getFile() throws Exception {
		IFile file = project.getFile(location);
		assertNotNull(file);

		file = project.getFile("testFile.txt");
		assertNotNull(file);
	}

	@Test
	public void whenUsingUnsupportedMethodExceptionShouldBeThrown() throws Exception {
		try {
			project.create(null, null);
			fail("project.create(IProjectDescription, IProgressMonitor) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.create(null, 0, null);
			fail("project.create(IProjectDescription, int, IProgressMonitor) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.build(0, null);
			fail("project.build(int, IProgressMonitor) should be unsupoported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.build(0, null, null, null);
			fail("project.build(int, String, Map, IProgressMonitor) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.getContentTypeMatcher();
			fail("project.getContentTypeMatcher() should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.getDescription();
			fail("project.getDescription() should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.getNature(null);
			fail("project.getNature(String) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.getPathVariableManager();
			fail("project.getPathVariableManager() should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.getWorkingLocation(null);
			fail("project.getWorkingLocation(String) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.hasNature(null);
			fail("project.hasNature(String) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.isNatureEnabled(null);
			fail("project.isNatureEnabled(String) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.setDescription(null, null);
			fail("project.setDescription(IProjectDescription, IProgressMonitor) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
		try {
			project.setDescription(null, 0, null);
			fail("project.setDescription(IProjectDescription, int, IProgressMonitor) should be unsupported");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
	}
}
