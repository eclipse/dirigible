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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.git.GitMasterRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.junit.Before;
import org.junit.Test;

public class LocalCopyGitTest {

	IRepository repositorySrc;
	IRepository repositoryDst;

	@Before
	public void setUp() {
		try {
			repositorySrc = new GitMasterRepository("testUser", "testSrcGit", "https://github.com/delchev/master_repository_test", null, null,
					"origin/master");
			repositoryDst = new LocalRepository(null, "testDst", "testDst");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGit() {
		String PATH = "/db/dirigible/registry/conf/datasources/xxx.properties";
		IResource resource = null;
		try {

			IResource resourceBack = repositorySrc.getResource(PATH);
			String path = resourceBack.getPath();

			assertEquals(PATH, path);

			copyRepository(repositorySrc, repositoryDst);

			resourceBack = repositoryDst.getResource(PATH);
			path = resourceBack.getPath();

			assertTrue(resourceBack.exists());
			assertEquals(PATH, path);

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if ((resource != null) && resource.exists()) {
					repositorySrc.removeResource(PATH);
					resource = repositorySrc.getResource(PATH);
					assertNotNull(resource);
					assertFalse(resource.exists());

					repositoryDst.removeResource(PATH);
					resource = repositoryDst.getResource(PATH);
					assertNotNull(resource);
					assertFalse(resource.exists());
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

	private void copyRepository(IRepository sourceRepository, IRepository targetRepository) throws IOException {
		ICollection root = sourceRepository.getRoot();
		copyCollection(root, targetRepository);
	}

	private void copyCollection(ICollection parent, IRepository targetRepository) throws IOException {
		List<IEntity> entities = parent.getChildren();
		for (IEntity entity : entities) {
			if (entity instanceof ICollection) {
				ICollection collection = (ICollection) entity;
				copyCollection(collection, targetRepository);
			} else {
				IResource resource = (IResource) entity;
				try {
					targetRepository.createResource(resource.getPath(), resource.getContent(), resource.isBinary(), resource.getContentType(), true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
