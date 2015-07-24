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
import java.util.Date;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;

public class DBModifiedTest {

	private static DataSource dataSource;

	@Before
	public void setUp() {
		dataSource = DBRepositoryTest.createLocal();
	}

	@Test
	public void testModified() {
		try {
			DBRepository repository = new DBRepository(dataSource, "guest1", false); //$NON-NLS-1$
			IResource resource = repository.createResource(
					"/testCollection/toBeModified.txt", //$NON-NLS-1$
					"Some content".getBytes()); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());
			
			assertEquals("guest1", resource.getInformation().getModifiedBy());
			
			Date firstModified = resource.getInformation().getModifiedAt();
			
			repository = new DBRepository(dataSource, "guest2", false); //$NON-NLS-1$
			resource = repository.getResource(
					"/testCollection/toBeModified.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			
			resource.setContent("Some modified content".getBytes());
			
			resource = repository.getResource(
					"/testCollection/toBeModified.txt"); //$NON-NLS-1$
			
			assertEquals("guest2", ((DBRepository) resource.getRepository()).getUser());
			assertEquals("guest2", resource.getInformation().getModifiedBy());			
			assertTrue(resource.getInformation().getModifiedAt().after(firstModified));
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				DBRepository repository = new DBRepository(dataSource, "guest3", false); //$NON-NLS-1$
				repository.removeResource(
						"/testCollection/toBeModified.txt"); //$NON-NLS-1$
			} catch (DBBaseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
