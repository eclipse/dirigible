/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package test.org.eclipse.dirigible.repository.local;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.local.ZipRepository;
import org.junit.Before;
import org.junit.Test;

public class ZipRepositoryTest {

	protected ZipRepository repository;

	@Before
	public void setUp() {
		try {
			String rootPath = new File(".").getCanonicalPath();
			repository = new ZipRepository("zip", rootPath + File.separator + "src/master_repository_test-master.zip");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testText() {

		if (repository == null) {
			return;
		}

		try {
			IResource resource = repository
					.getResource("/db/dirigible/users/delchev/workspace/delchev_MyFirstProject/DataStructures/delchev_books/delchev_books.table"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			System.out.println(new String(resource.getContent()));
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
