/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.eclipse.dirigible.runtime.commons.LocalHttpServletRequest;
import org.eclipse.dirigible.runtime.commons.LocalHttpServletResponse;
import org.eclipse.dirigible.runtime.registry.AbstractRegistryServlet;
import org.junit.Before;
import org.junit.Test;

public class JavaServiceTest {

	protected IRepository repository;

	@Before
	public void setUp() {
		try {
			repository = new LocalRepository("java");
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

		String javaPath = IRepositoryPaths.DB_DIRIGIBLE_REGISTRY_PUBLIC + IRepositoryPaths.SEPARATOR
				+ ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES + IRepositoryPaths.SEPARATOR + "test/test1.java";

		IResource resource = null;
		try {

			byte[] bytes = ("package test;" + "import java.util.Map;" + "import javax.servlet.http.HttpServletRequest;"
					+ "import javax.servlet.http.HttpServletResponse;" + "public class test1 {"
					+ "public void service(HttpServletRequest request, HttpServletResponse response, Map<String, Object> scope) throws Exception {"
					+ "response.getWriter().println(\"Hello World!\");" + "response.getWriter().flush();" + "response.getWriter().close();" + "}"
					+ "}").getBytes();

			resource = repository.createResource(javaPath, bytes, false, "text/plain");
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			JavaServlet javaServlet = new JavaServlet();

			String userDir = System.getProperty("user.dir");
			File userDirFile = new File(userDir);
			File libDir = new File(
					userDirFile.getParentFile().getParentFile().getCanonicalPath() + "/p2.external/external.p2/target/repository/plugins");
			System.setProperty("osgi.syspath", libDir.getCanonicalPath());

			javaServlet.init();

			LocalHttpServletRequest request = new LocalHttpServletRequest(new URL("http://local/test/test1.java"));
			request.getSession().setAttribute(AbstractRegistryServlet.REPOSITORY_ATTRIBUTE, repository);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			LocalHttpServletResponse response = new LocalHttpServletResponse(out);
			javaServlet.doGet(request, response);
			response.getWriter().flush();
			String output = new String(out.toByteArray());
			output = output.trim();
			assertTrue("Hello World!".equals(output));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if ((resource != null) && resource.exists()) {
					repository.removeResource(javaPath);
					resource = repository.getResource(javaPath);
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
