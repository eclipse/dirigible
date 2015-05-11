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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.db.DBRepository;

public class DBRepositoryTest {

	private static IRepository repository;

	@Before
	public void setUp() {
		DataSource dataSource = createLocal();
		try {
			repository = new DBRepository(dataSource, "guest", false); //$NON-NLS-1$
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testGetRoot() {
		assertNotNull(repository.getRoot());
	}

	@Test
	public void testCreateCollection() {
		try {
			ICollection collection = repository
					.createCollection("/testCollection"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testGetCollection() {
		try {
			repository.createCollection("/testCollection"); //$NON-NLS-1$
			ICollection collection = repository
					.getCollection("/testCollection"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testRemoveCollection() {
		try {
			ICollection collection = repository
					.createCollection("/toBeRemoved"); //$NON-NLS-1$
			assertNotNull(collection);
			repository.removeCollection("/toBeRemoved"); //$NON-NLS-1$
			collection = repository.getCollection("/toBeRemoved"); //$NON-NLS-1$
			assertNotNull(collection);
			assertFalse(collection.exists());
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testHasCollection() {
		try {
			repository.createCollection("/testCollection"); //$NON-NLS-1$
			assertTrue(repository.hasCollection("/testCollection")); //$NON-NLS-1$
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateResourceString() {
		try {
			IResource resource = repository
					.createResource("/testCollection/testResourceEmpty.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertTrue(resource.getContent().length == 0);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateResourceStringByteArray() {
		try {
			IResource resource = repository.createResource(
					"/testCollection/testResourceContent.txt", //$NON-NLS-1$
					"test content".getBytes()); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.getContent().length == 0);
			assertFalse(resource.isBinary());
			assertTrue(Arrays.equals(resource.getContent(),
					"test content".getBytes(Charset.defaultCharset()))); //$NON-NLS-1$
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testGetResource() {
		try {
			repository.createResource("/testCollection/testResourceEmpty.txt"); //$NON-NLS-1$
			IResource resource = repository
					.getResource("/testCollection/testResourceEmpty.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testRemoveResource() {
		try {
			IResource resource = repository
					.createResource("/testCollection/toBeRemoved.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			repository.removeResource("/testCollection/toBeRemoved.txt"); //$NON-NLS-1$
			resource = repository
					.getResource("/testCollection/toBeRemoved.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertFalse(resource.exists());
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testHasResource() {
		try {
			if (repository.hasResource("/testCollection/checkExists.txt")) { //$NON-NLS-1$
				repository.removeResource("/testCollection/checkExists.txt"); //$NON-NLS-1$
			}
			IResource resource = repository
					.createResource("/testCollection/checkExists.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testDispose() {
		// fail("Not yet implemented");
	}

	@Test
	public void testGetRepositoryDAO() {
		assertNotNull(((DBRepository) repository).getRepositoryDAO());
	}

	@Test
	public void testGetDataSource() {
		assertNotNull(((DBRepository) repository).getDataSource());
	}

	@Test
	public void testGetDbUtils() {
		assertNotNull(((DBRepository) repository).getDbUtils());
	}

	@Test
	public void testGetUser() {
		assertNotNull(((DBRepository) repository).getUser());
	}

	public static DataSource createLocal() {
		EmbeddedDataSource dataSource = new EmbeddedDataSource();
		dataSource.setDatabaseName("derby"); //$NON-NLS-1$
		dataSource.setCreateDatabase("create"); //$NON-NLS-1$
		return dataSource;
	}
}
