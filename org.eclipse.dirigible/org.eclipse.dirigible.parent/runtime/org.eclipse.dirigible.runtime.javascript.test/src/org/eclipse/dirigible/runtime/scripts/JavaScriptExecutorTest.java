/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripts;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.runtime.js.JavaScriptExecutor;
import org.eclipse.dirigible.runtime.js.rhino.RhinoJavaScriptEngineProvider;
import org.eclipse.dirigible.runtime.mock.LocalHttpServletRequest;
import org.eclipse.dirigible.runtime.mock.LocalHttpServletResponse;
import org.eclipse.dirigible.runtime.registry.AbstractRegistryServlet;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineExecutor;
import org.eclipse.dirigible.runtime.scripting.IJavaScriptEngineProvider;
import org.eclipse.dirigible.runtime.utils.DataSourceUtils;
import org.junit.Before;
import org.junit.Test;

public class JavaScriptExecutorTest {

	private static final String REPOSITORY_JS_DEPLOY_PATH = "/db/dirigible/registry/public/" //$NON-NLS-1$
			+ ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;

	private static final String REPOSITORY_SANDBOX_DEPLOY_PATH = "/db/dirigible/sandbox/GUEST/" //$NON-NLS-1$
			+ ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;

	private static IRepository repository;

	@Before
	public void setUp() {
		DataSource dataSource = DataSourceUtils.createLocal();
		try {
			repository = new DBRepository(dataSource, "guest", false); //$NON-NLS-1$
			repository.createResource(REPOSITORY_JS_DEPLOY_PATH + "/testSum.js", //$NON-NLS-1$
					("var testSum = function(){ var a=2;" //$NON-NLS-1$
							+ "var b=2; " + "var c=a+b; " + "return c;}; testSum(); ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									.getBytes());

			repository.createResource(REPOSITORY_SANDBOX_DEPLOY_PATH + "/testSumSandbox.js", //$NON-NLS-1$
					("var testSum = function(){ var a=2;" //$NON-NLS-1$
							+ "var b=2; " + "var c=a+b; " + "return c;}; testSum(); ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									.getBytes());

			repository.createResource(REPOSITORY_JS_DEPLOY_PATH + "/test/testLibrary.js", //$NON-NLS-1$
					("exports.add = function(a,b) {return a+b};").getBytes()); //$NON-NLS-1$
			repository.createResource(REPOSITORY_JS_DEPLOY_PATH + "/testExports.js", //$NON-NLS-1$
					("var testExports = function() {var add = require('test/testLibrary').add; return add(5, 6); }; testExports();") //$NON-NLS-1$
							.getBytes());
			repository.createResource(REPOSITORY_JS_DEPLOY_PATH + "/testDefaultObjects.js", //$NON-NLS-1$
					(" var testDefaultObjects = function() { return (datasource !== null) ? true : false}; testDefaultObjects();") //$NON-NLS-1$
							.getBytes());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteFunctionModule() {
		JavaScriptExecutor executor = new JavaScriptExecutor(repository, REPOSITORY_JS_DEPLOY_PATH, null);
		LocalHttpServletRequest request = createRequest(executor);
		LocalHttpServletResponse response = createResponse();

		try {
			Object object = executor.executeServiceModule(request, response, "/testSum.js", null); //$NON-NLS-1$
			assertNotNull(object);
			assertTrue(((Double) object) == 4);

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	protected LocalHttpServletRequest createRequest(JavaScriptExecutor executor) {
		try {
			LocalHttpServletRequest request = new LocalHttpServletRequest(new URL("http://local/js/test1.js"));
			request.getSession().setAttribute(AbstractRegistryServlet.REPOSITORY_ATTRIBUTE, repository);

			IJavaScriptEngineProvider javascriptEngineProvider = new RhinoJavaScriptEngineProvider();
			IJavaScriptEngineExecutor javaScriptEngineExecutor = javascriptEngineProvider.create(executor);
			request.setAttribute("IJavaScriptEngineExecutor", javaScriptEngineExecutor);
			return request;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return null;
	}

	protected LocalHttpServletResponse createResponse() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LocalHttpServletResponse response = new LocalHttpServletResponse(out);
		return response;
	}

	@Test
	public void testExecuteFunctionModuleSandbox() {
		JavaScriptExecutor executor = new JavaScriptExecutor(repository, REPOSITORY_SANDBOX_DEPLOY_PATH, REPOSITORY_JS_DEPLOY_PATH);
		LocalHttpServletRequest request = createRequest(executor);
		LocalHttpServletResponse response = createResponse();
		try {
			Object object = executor.executeServiceModule(request, response, "/testSumSandbox.js", null); //$NON-NLS-1$
			assertNotNull(object);
			assertTrue(((Double) object) == 4);

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteExports() {
		JavaScriptExecutor executor = new JavaScriptExecutor(repository, REPOSITORY_JS_DEPLOY_PATH, null);
		LocalHttpServletRequest request = createRequest(executor);
		LocalHttpServletResponse response = createResponse();
		try {
			Object object = executor.executeServiceModule(request, response, "/testExports.js", null); //$NON-NLS-1$
			assertNotNull(object);
			assertTrue(((Double) object) == 11);

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDefaultObjects() {
		JavaScriptExecutor executor = new JavaScriptExecutor(repository, REPOSITORY_JS_DEPLOY_PATH, null);
		LocalHttpServletRequest request = createRequest(executor);
		LocalHttpServletResponse response = createResponse();
		try {
			Object object = executor.executeServiceModule(request, response, "/testDefaultObjects.js", null); //$NON-NLS-1$
			assertNotNull(object);
			assertTrue(((Boolean) object));

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
