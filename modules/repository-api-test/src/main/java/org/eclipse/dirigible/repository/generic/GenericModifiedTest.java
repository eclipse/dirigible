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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.Test;

public class GenericModifiedTest {

	protected IRepository repository1;
	protected IRepository repository2;
	protected IRepository repository3;

	@Test
	public void testModified() {
		if (repository1 == null) {
			return;
		}

		try {
			IResource resource = repository1.createResource("/testCollection/toBeModified.txt", //$NON-NLS-1$
					"Some content".getBytes()); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			// TODO
//			assertEquals("guest1", resource.getInformation().getModifiedBy());
			assertEquals("nobody", resource.getInformation().getModifiedBy());

			Date firstModified = resource.getInformation().getModifiedAt();

			resource = repository2.getResource("/testCollection/toBeModified.txt"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());

			resource.setContent("Some modified content".getBytes());

			resource = repository2.getResource("/testCollection/toBeModified.txt"); //$NON-NLS-1$

			// TODO
//			assertEquals("guest2", resource.getInformation().getModifiedBy());
			assertEquals("nobody", resource.getInformation().getModifiedBy());
			assertTrue(resource.getInformation().getModifiedAt().after(firstModified));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				repository3.removeResource("/testCollection/toBeModified.txt");
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

}
