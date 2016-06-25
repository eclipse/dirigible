/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.web;

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

public class WebServiceTest {

	protected IRepository repository;

	@Before
	public void setUp() {
		try {
			repository = new LocalRepository("web");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testWebService() {
		if (repository == null) {
			fail("Repository has not been created.");
		}

		String webPath = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC + IRepositoryPaths.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT
				+ IRepositoryPaths.SEPARATOR + "web/test1.html";

		IResource resource = null;
		try {

			byte[] bytes = "<html></html>".getBytes();

			resource = repository.createResource(webPath, bytes, false, "text/plain");
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			WebRegistryServlet webRegistryServlet = new WebRegistryServlet();

			LocalHttpServletRequest request = new LocalHttpServletRequest(new URL("http://local/web/test1.html"));
			request.getSession().setAttribute(AbstractRegistryServlet.REPOSITORY_ATTRIBUTE, repository);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			LocalHttpServletResponse response = new LocalHttpServletResponse(out);
			webRegistryServlet.doGet(request, response);
			response.getWriter().flush();
			assertEquals("<html></html>", new String(out.toByteArray()));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if ((resource != null) && resource.exists()) {
					repository.removeResource(webPath);
					resource = repository.getResource(webPath);
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
