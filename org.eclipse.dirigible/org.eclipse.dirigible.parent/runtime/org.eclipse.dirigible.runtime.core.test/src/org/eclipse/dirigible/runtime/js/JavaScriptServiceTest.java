/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js;

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
import org.eclipse.dirigible.runtime.js.rhino.RhinoJavaScriptEngineProvider;
import org.eclipse.dirigible.runtime.mock.LocalHttpServletRequest;
import org.eclipse.dirigible.runtime.mock.LocalHttpServletResponse;
import org.eclipse.dirigible.runtime.registry.AbstractRegistryServlet;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineProvider;
import org.junit.Before;
import org.junit.Test;

public class JavaScriptServiceTest {

	protected IRepository repository;

	@Before
	public void setUp() {
		try {
			repository = new LocalRepository("js");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testJavaScriptService() {
		if (repository == null) {
			fail("Repository has not been created.");
		}

		String jsPath = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC + IRepositoryPaths.SEPARATOR + ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES
				+ IRepositoryPaths.SEPARATOR + "js/test1.js";

		IResource resource = null;
		try {

			byte[] bytes = "var a=5; var b=6; var c = a + b; c;".getBytes();

			resource = repository.createResource(jsPath, bytes, false, "text/plain");
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			JavaScriptServlet jsServlet = new JavaScriptServlet();

			LocalHttpServletRequest request = new LocalHttpServletRequest(new URL("http://local/js/test1.js"));
			request.getSession().setAttribute(AbstractRegistryServlet.REPOSITORY_ATTRIBUTE, repository);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			LocalHttpServletResponse response = new LocalHttpServletResponse(out);
			IJavaScriptEngineProvider javascriptEngineProvider = new RhinoJavaScriptEngineProvider();
			IJavaScriptEngineExecutor javaScriptEngineExecutor = javascriptEngineProvider
					.create(((JavaScriptExecutor) new JavaScriptScriptExecutorProvider().createExecutor(request)));
			request.setAttribute("IJavaScriptEngineExecutor", javaScriptEngineExecutor);

			jsServlet.doGet(request, response);
			response.getWriter().flush();
			String output = new String(out.toByteArray());
			output = output.trim();
			assertTrue("11.0".equals(output));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if ((resource != null) && resource.exists()) {
					repository.removeResource(jsPath);
					resource = repository.getResource(jsPath);
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
