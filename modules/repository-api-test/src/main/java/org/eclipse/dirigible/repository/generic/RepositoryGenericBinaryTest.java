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

import java.util.Arrays;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.Test;

public class RepositoryGenericBinaryTest {

	protected IRepository repository;

	@Test
	public void testCreateBinary() {
		if (repository == null) {
			return;
		}

		try {
			IResource resource = repository.createResource("/testCollection/toBeRemoved.bin", //$NON-NLS-1$
					new byte[] { 0, 1, 1, 0 }, true, "application/bin"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertTrue(resource.isBinary());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetBinary() {
		if (repository == null) {
			return;
		}

		try {
			repository.createResource("/testCollection/toBeRemoved.bin", //$NON-NLS-1$
					new byte[] { 0, 1, 1, 0 }, true, "application/bin"); //$NON-NLS-1$
			IResource resource = repository.getResource("/testCollection/toBeRemoved.bin"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertTrue(resource.isBinary());
			assertTrue(Arrays.equals(resource.getContent(), new byte[] { 0, 1, 1, 0 }));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveBinary() {
		if (repository == null) {
			return;
		}

		try {
			repository.createResource("/testCollection/toBeRemoved.bin", //$NON-NLS-1$
					new byte[] { 0, 1, 1, 0 }, true, "application/bin"); //$NON-NLS-1$
			repository.removeResource("/testCollection/toBeRemoved.bin"); //$NON-NLS-1$
			IResource resource = repository.getResource("/testCollection/toBeRemoved.bin"); //$NON-NLS-1$
			assertNotNull(resource);
			assertFalse(resource.exists());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
