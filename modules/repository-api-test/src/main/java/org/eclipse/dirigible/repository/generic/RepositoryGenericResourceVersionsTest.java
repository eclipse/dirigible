/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.generic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class RepositoryGenericResourceVersionsTest.
 */
public class RepositoryGenericResourceVersionsTest {

	/** The repository. */
	protected IRepository repository;

	/**
	 * Test check versions.
	 */
	@Test
	public void testCheckVersions() {
		if (repository == null) {
			return;
		}

		try {
			IResource resource = repository.getResource("/testCollection/versionedFile.txt"); //$NON-NLS-1$
			if (resource.exists()) {
				resource.delete();
			}
			resource = repository.createResource("/testCollection/versionedFile.txt", //$NON-NLS-1$
					"Version 1".getBytes()); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			List<IResourceVersion> versions = repository.getResourceVersions("/testCollection/versionedFile.txt"); //$NON-NLS-1$
			assertEquals(versions.size(), 1);
			IResourceVersion version = versions.get(0);
			assertArrayEquals(new String(version.getContent()).getBytes(), "Version 1".getBytes()); //$NON-NLS-1$
			assertEquals(version.getVersion(), 1);

			resource.setContent("Version  2".getBytes()); //$NON-NLS-1$

			versions = repository.getResourceVersions("/testCollection/versionedFile.txt"); //$NON-NLS-1$
			assertEquals(versions.size(), 2);
			version = versions.get(0);
			assertArrayEquals(new String(version.getContent()).getBytes(), "Version 1".getBytes()); //$NON-NLS-1$
			assertEquals(version.getVersion(), 1);
			version = versions.get(1);
			assertArrayEquals(new String(version.getContent()).getBytes(), "Version  2".getBytes()); //$NON-NLS-1$
			assertEquals(version.getVersion(), 2);

			resource.delete();

			versions = repository.getResourceVersions("/testCollection/versionedFile.txt"); //$NON-NLS-1$
			assertEquals(versions.size(), 0);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
