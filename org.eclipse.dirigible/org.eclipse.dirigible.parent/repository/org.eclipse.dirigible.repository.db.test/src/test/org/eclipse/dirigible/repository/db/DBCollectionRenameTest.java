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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.db.DBRepository;

public class DBCollectionRenameTest {

	private static IRepository repository;

	@Before
	public void setUp() {
		DataSource dataSource = DBRepositoryTest.createLocal();
		try {
			repository = new DBRepository(dataSource, "guest", false); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreate() {
		ICollection collection = null;
		try {
			collection = repository.createCollection(
					"/testCollectionToBeRemoved1"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
			assertEquals(0, collection.getChildren().size());

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if (collection != null && collection.exists()) {
					repository.removeCollection("/testCollectionToBeRemoved1"); //$NON-NLS-1$
					collection = repository
							.getCollection("/testCollectionToBeRemoved1"); //$NON-NLS-1$
					assertNotNull(collection);
					assertFalse(collection.exists());
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testRename() {
		ICollection collection = null;
		try {
			collection = repository.createCollection(
					"/a/b/c"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
			
			collection = repository.createCollection(
					"/a/b/c/d"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
			
			collection = repository.createCollection(
					"/c/b/a"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
			
			collection = repository.getCollection(
					"/a/b/c"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
			
			collection.renameTo("x");
			
			collection = repository.getCollection(
					"/a/b/x"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
			assertEquals("x", collection.getName());
			
			collection = repository.getCollection(
					"/a/b/x/d"); //$NON-NLS-1$
			assertNotNull(collection);
			assertTrue(collection.exists());
			
			
			collection = repository.getCollection(
					"/a/b/c"); //$NON-NLS-1$
			assertNotNull(collection);
			assertFalse(collection.exists());
			

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				repository.removeCollection("/a");
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
	
}
