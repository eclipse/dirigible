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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.zip.ZipInputStream;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.Test;

public class GenericImportZipTest {

	protected IRepository repository;

	@Test
	public void testImportZip() {
		if (repository == null) {
			return;
		}

		ZipInputStream zipInputStream = new ZipInputStream(GenericImportZipTest.class.getResourceAsStream("/testImport.zip")); //$NON-NLS-1$
		try {

			ICollection collection = repository.getCollection("/root1/import"); //$NON-NLS-1$
			if (collection.exists()) {
				collection.delete();
			}

			repository.importZip(zipInputStream, "/root1/import"); //$NON-NLS-1$

			IResource resource = repository.getResource("/root1/import/folder1/text1.txt"); //$NON-NLS-1$
			String read = new String(resource.getContent());
			assertEquals("text1", read); //$NON-NLS-1$

			resource = repository.getResource("/root1/import/folder1/folder2/image1.png"); //$NON-NLS-1$
			assertTrue(resource.isBinary());
			assertEquals("image/png", resource.getContentType()); //$NON-NLS-1$

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
