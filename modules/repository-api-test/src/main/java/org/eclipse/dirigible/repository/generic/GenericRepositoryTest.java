/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.generic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.After;
import org.junit.Test;

public class GenericRepositoryTest {

	protected IRepository repository;

	@Test
	public void testGetRoot() {
		if (repository == null) {
			return;
		}

		assertNotNull(repository.getRoot());
	}

	@Test
	public void testCreateCollection() {
		if (repository == null) {
			return;
		}

		try {
			ICollection collection = repository.createCollection("/testCollection"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetCollection() {
		if (repository == null) {
			return;
		}

		try {
			repository.createCollection("/testCollection"); //$NON-NLS-1$
			ICollection collection = repository.getCollection("/testCollection"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveCollection() {
		if (repository == null) {
			return;
		}

		try {
			ICollection collection = repository.createCollection("/toBeRemoved"); //$NON-NLS-1$
			assertNotNull(collection);
			repository.removeCollection("/toBeRemoved"); //$NON-NLS-1$
			collection = repository.getCollection("/toBeRemoved"); //$NON-NLS-1$
			assertNotNull(collection);
			assertFalse(collection.exists());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testHasCollection() {
		if (repository == null) {
			return;
		}

		try {
			repository.createCollection("/testCollection"); //$NON-NLS-1$
			assertTrue(repository.hasCollection("/testCollection")); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateResourceString() {
		if (repository == null) {
			return;
		}

		try {
			IResource resource = repository.createResource("/testCollection/testResourceEmpty.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertTrue(resource.getContent().length == 0);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateResourceStringByteArray() {
		if (repository == null) {
			return;
		}

		try {
			IResource resource = repository.createResource("/testCollection/testResourceContent.txt", //$NON-NLS-1$
					"test content".getBytes()); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.getContent().length == 0);
			assertFalse(resource.isBinary());
			assertTrue(Arrays.equals(resource.getContent(), "test content".getBytes(Charset.defaultCharset()))); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetResource() {
		if (repository == null) {
			return;
		}

		try {
			repository.createResource("/testCollection/testResourceEmpty.txt"); //$NON-NLS-1$
			IResource resource = repository.getResource("/testCollection/testResourceEmpty.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveResource() {
		if (repository == null) {
			return;
		}

		try {
			IResource resource = repository.createResource("/testCollection/toBeRemoved.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			repository.removeResource("/testCollection/toBeRemoved.txt"); //$NON-NLS-1$
			resource = repository.getResource("/testCollection/toBeRemoved.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertFalse(resource.exists());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testHasResource() {
		if (repository == null) {
			return;
		}

		try {
			if (repository.hasResource("/testCollection/checkExists.txt")) { //$NON-NLS-1$
				repository.removeResource("/testCollection/checkExists.txt"); //$NON-NLS-1$
			}
			IResource resource = repository.createResource("/testCollection/checkExists.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@After
	public void cleanUp() {
		if (repository == null) {
			return;
		}

		try {
			if (repository.hasCollection("/testCollection")) { //$NON-NLS-1$
				repository.removeCollection("/testCollection"); //$NON-NLS-1$
			}
			ICollection collection = repository.getCollection("/testCollection"); //$NON-NLS-1$
			assertNotNull(collection);
			assertFalse(collection.exists());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDispose() {
		// fail("Not yet implemented");
	}

}
