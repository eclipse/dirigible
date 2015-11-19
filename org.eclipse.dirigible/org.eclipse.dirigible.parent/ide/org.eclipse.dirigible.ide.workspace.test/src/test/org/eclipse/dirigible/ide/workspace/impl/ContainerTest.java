/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package test.org.eclipse.dirigible.ide.workspace.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dirigible.ide.workspace.impl.Container;
import org.eclipse.dirigible.ide.workspace.impl.Workspace;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ContainerTest {
	@Mock
	private IPath path;
	@Mock
	private Workspace workspace;
	@Mock
	private IWorkspaceRoot root;
	@Mock
	private IRepository repository;
	@Mock
	private ICollection expectedEntity;
	@Mock
	private IPath location;

	private Container container;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(workspace.getRepository()).thenReturn(repository);
		when(workspace.getRoot()).thenReturn(root);
		when(workspace.getLocation()).thenReturn(location);

		when(repository.getCollection(anyString())).thenReturn(expectedEntity);
		when(root.getLocation()).thenReturn(path);
		when(root.toString()).thenReturn("");

		when(path.append(any(IPath.class))).thenReturn(path);
		when(path.append(anyString())).thenReturn(path);

		when(location.append(any(IPath.class))).thenReturn(location);
		when(location.append(anyString())).thenReturn(location);

		container = new Container(path, workspace);
	}

	@Test
	public void getEntity() {
		IEntity actualEntity = container.getEntity();

		assertNotNull(actualEntity);
		assertEquals(expectedEntity, actualEntity);
	}

	@Test
	public void exists() {
		boolean exists = container.exists(location);
		assertFalse(exists);

		when(workspace.hasResource(location)).thenReturn(true);

		exists = container.exists(location);
		assertTrue(exists);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getFolderWithPathWithLessThanTwoSegmentsShouldThrowException() {
		container.getFolder(path);
	}

	@Test
	public void getFolder() {
		when(path.segmentCount()).thenReturn(2);

		IFolder folder = container.getFolder(path);

		assertNotNull(folder);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getFileWithPathWithLessThanTwoSegmentsShouldThrowException() {
		container.getFile(path);
	}

	@Test
	public void getFile() {
		when(path.segmentCount()).thenReturn(2);

		IFile file = container.getFile(path);

		assertNotNull(file);
	}

	@Test
	public void findMemberByIPath() {
		IResource expectedMember = Mockito.mock(IResource.class);

		when(workspace.newResource(any(IPath.class))).thenReturn(expectedMember);
		when(expectedMember.exists()).thenReturn(true);

		IResource actualMember = container.findMember(path);

		assertNotNull(actualMember);
		assertEquals(expectedMember, actualMember);
	}

	@Test
	public void findMemberByString() {
		IResource expectedMember = Mockito.mock(IResource.class);

		when(workspace.newResource(any(IPath.class))).thenReturn(expectedMember);
		when(expectedMember.exists()).thenReturn(true);

		IResource actualMember = container.findMember("test");

		assertNotNull(actualMember);
		assertEquals(expectedMember, actualMember);
	}

	@Test
	public void whenFindingMemberThatDoNotExsitsShouldReturnNull() {
		IResource expectedMember = Mockito.mock(IResource.class);

		when(workspace.newResource(any(IPath.class))).thenReturn(expectedMember);
		when(workspace.newResource(any(IPath.class))).thenReturn(null);

		IResource actualMember = container.findMember(path);

		assertNull(actualMember);

		actualMember = container.findMember(path);

		assertNull(actualMember);

	}

	@Test
	public void emptyMembers() throws Exception {
		IResource[] members = container.members();

		assertNotNull(members);
		assertEquals(0, members.length);
	}

	@Test
	public void members() throws Exception {
		List<String> collectionsNames = new ArrayList<String>();
		collectionsNames.add("member1");
		collectionsNames.add("member2");

		when(expectedEntity.getCollectionsNames()).thenReturn(collectionsNames);

		IResource[] members = container.members(true);

		assertNotNull(members);
		assertEquals(2, members.length);
	}

	@Test(expected = CoreException.class)
	public void whenErrorOccursDuringExtractingMembersCoreExceptionShouldBeThrows() throws Exception {
		doThrow(new IOException()).when(expectedEntity).getCollectionsNames();

		container.members(true);

	}

	@Test
	public void getFilters() throws Exception {
		IResourceFilterDescription[] filterDescription = container.getFilters();
		assertNotNull(filterDescription);
	}

	// @Test
	// public void whenUsingUnsupportedMethodExceptionShouldBeThrown() throws Exception {
	// try {
	// container.createFilter(0, null, 0, null);
	// fail("container.createFilter(int, FileInfoMatcherDescription, int, IProgressMonitor) should be unsupported");
	// } catch (UnsupportedOperationException e) {
	// // Expected
	// }
	// try {
	// container.removeFilter(null, 0, null);
	// fail("container.removeFilter(IResourceFilterDescription, int, IProgressMonitor) should be unsupported");
	// } catch (UnsupportedOperationException e) {
	// // Expected
	// }
	// }

}
