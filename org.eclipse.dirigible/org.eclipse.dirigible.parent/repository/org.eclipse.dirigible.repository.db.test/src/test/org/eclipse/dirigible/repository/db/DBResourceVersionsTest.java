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

package test.org.eclipse.dirigible.repository.db;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.db.DBRepository;

public class DBResourceVersionsTest {

	private static IRepository repository;

	@Before
	public void setUp() {
		DataSource dataSource = DBRepositoryTest.createLocal();
		try {
			repository = new DBRepository(dataSource, "guest", false); //$NON-NLS-1$
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testCheckVersions() {
		try {
			IResource resource = repository
					.getResource("/testCollection/versionedFile.txt"); //$NON-NLS-1$
			if (resource.exists()) {
				resource.delete();
			}
			resource = repository
					.createResource("/testCollection/versionedFile.txt", //$NON-NLS-1$
							"Version 1".getBytes()); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			List<IResourceVersion> versions = repository
					.getResourceVersions("/testCollection/versionedFile.txt"); //$NON-NLS-1$
			assertEquals(versions.size(), 1);
			IResourceVersion version = versions.get(0);
			assertArrayEquals(version.getContent(), "Version 1".getBytes()); //$NON-NLS-1$
			assertEquals(version.getVersion(), 1);

			resource.setContent("Version 2".getBytes()); //$NON-NLS-1$

			versions = repository
					.getResourceVersions("/testCollection/versionedFile.txt"); //$NON-NLS-1$
			assertEquals(versions.size(), 2);
			version = versions.get(0);
			assertArrayEquals(version.getContent(), "Version 1".getBytes()); //$NON-NLS-1$
			assertEquals(version.getVersion(), 1);
			version = versions.get(1);
			assertArrayEquals(version.getContent(), "Version 2".getBytes()); //$NON-NLS-1$
			assertEquals(version.getVersion(), 2);

			resource.delete();

			versions = repository
					.getResourceVersions("/testCollection/versionedFile.txt"); //$NON-NLS-1$
			assertEquals(versions.size(), 0);

		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

}
