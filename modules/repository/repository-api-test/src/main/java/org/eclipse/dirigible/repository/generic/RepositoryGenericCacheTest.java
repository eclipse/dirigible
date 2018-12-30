/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.repository.generic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class RepositoryGenericCacheTest.
 */
public class RepositoryGenericCacheTest {

	/** The repository. */
	protected IRepository repository;

	/** The disabled. */
	protected static boolean disabled = false;

	/**
	 * Test cache text.
	 */
	@Test
	public void testCacheText() {
		if (repository == null) {
			return;
		}

		IResource resource = null;
		try {
			resource = repository.createResource("/testCollection/toBeRemoved1Cached.txt", "cached file".getBytes()); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			// TODO

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if ((resource != null) && resource.exists()) {
					repository.removeResource("/testCollection/toBeRemoved1Cached.txt"); //$NON-NLS-1$
					resource = repository.getResource("/testCollection/toBeRemoved1Cached.txt"); //$NON-NLS-1$
					assertNotNull(resource);
					assertFalse(resource.exists());
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

}
