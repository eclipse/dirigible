/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.repository.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalTextTest.
 */
public class LocalVirtualTest {
	
	/** The repository. */
	protected LocalRepository repository;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			repository = new LocalRepository("target/test");
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

		IResource resourceA = null;
		IResource resourceB = null;
		try {
			String content = "test1";
			
			Files.createDirectories(Paths.get("target/linked/b").toAbsolutePath());
			Files.createFile(Paths.get("target/linked/b/testB.txt").toAbsolutePath());
			
			resourceA = repository.createResource("/a/testA.txt", content.getBytes(), false, //$NON-NLS-1$
					"text/plain"); //$NON-NLS-1$
			assertNotNull(resourceA);
			assertTrue(resourceA.exists());
			
			repository.linkPath("/a/b", Paths.get("target/linked/b").toAbsolutePath().toString());
			

			resourceB = repository.getResource("/a/b/testB.txt"); //$NON-NLS-1$
			assertNotNull(resourceB);
			assertTrue(resourceB.exists());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if ((resourceA != null) && resourceA.exists()) {
					repository.removeResource("/a/testA.txt"); //$NON-NLS-1$
					resourceA = repository.getResource("/a/testA.txt"); //$NON-NLS-1$
					assertNotNull(resourceA);
					assertFalse(resourceA.exists());
				}
				if ((resourceB != null) && resourceB.exists()) {
					repository.removeResource("/a/b/testB.txt"); //$NON-NLS-1$
					resourceB = repository.getResource("/a/b/testB.txt"); //$NON-NLS-1$
					assertNotNull(resourceB);
					assertFalse(resourceB.exists());
				}
				repository.removeCollection("/a"); //$NON-NLS-1$
				Files.deleteIfExists(Paths.get("target/linked/b").toAbsolutePath());
				Files.deleteIfExists(Paths.get("target/linked").toAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

}
