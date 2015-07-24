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

package test.org.eclipse.dirigible.repository.ext;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.utils.FileUtils;

public class FileUtilsTest {

	private static IRepository repository;
	
	@Before
	public void setUp() {
		DBRepositoryTest.setUp();
		repository = DBRepositoryTest.getRepository();
	}

	@Test
	public void testcopyCollectionToDirectory() {
		try {
			File temp = FileUtils.createTempDirectory("test");
			System.out.println(temp.getCanonicalPath());
			repository.createResource("/db/dirigible/root/test", "test".getBytes());
			repository.createResource("/db/dirigible/root/test2", "test2".getBytes());
			repository.createResource("/db/dirigible/root/subfolder/test3", "test3".getBytes());
			ICollection collection = repository.getCollection("/db/dirigible/root/");
			FileUtils.copyCollectionToDirectory(collection, temp, new String[]{"/db/dirigible/root"});
			//assertTrue(contains(extensionPoints,"extensionPoint1"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
		
}
