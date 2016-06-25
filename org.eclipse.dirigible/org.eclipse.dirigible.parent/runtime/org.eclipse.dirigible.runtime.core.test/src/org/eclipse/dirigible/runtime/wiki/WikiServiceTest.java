/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.wiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.eclipse.dirigible.runtime.mock.LocalHttpServletRequest;
import org.eclipse.dirigible.runtime.mock.LocalHttpServletResponse;
import org.eclipse.dirigible.runtime.registry.AbstractRegistryServlet;
import org.junit.Before;
import org.junit.Test;

public class WikiServiceTest {

	protected IRepository repository;

	@Before
	public void setUp() {
		try {
			repository = new LocalRepository("wiki");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testWikiService() {
		if (repository == null) {
			fail("Repository has not been created.");
		}

		String wikiPath = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC + IRepositoryPaths.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT
				+ IRepositoryPaths.SEPARATOR + "wiki/test1.md";

		IResource resource = null;
		try {

			byte[] bytes = "First Level Header\n====================".getBytes();

			resource = repository.createResource(wikiPath, bytes, false, "text/plain");
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			WikiRegistryServlet wikiRegistryServlet = new WikiRegistryServlet();

			LocalHttpServletRequest request = new LocalHttpServletRequest(new URL("http://local/wiki/test1.md"));
			request.getSession().setAttribute(AbstractRegistryServlet.REPOSITORY_ATTRIBUTE, repository);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			LocalHttpServletResponse response = new LocalHttpServletResponse(out);
			wikiRegistryServlet.doGet(request, response);
			response.getWriter().flush();
			assertEquals("<h1 id=\"first-level-header\">First Level Header</h1>", new String(out.toByteArray()));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if ((resource != null) && resource.exists()) {
					repository.removeResource(wikiPath);
					resource = repository.getResource(wikiPath);
					assertNotNull(resource);
					assertFalse(resource.exists());
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

}
