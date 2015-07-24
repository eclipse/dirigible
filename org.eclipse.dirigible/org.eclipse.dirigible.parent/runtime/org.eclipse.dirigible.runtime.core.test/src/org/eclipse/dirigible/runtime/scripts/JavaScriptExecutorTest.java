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

package org.eclipse.dirigible.runtime.scripts;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.runtime.js.JavaScriptExecutor;
import org.eclipse.dirigible.runtime.utils.DataSourceUtils;

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
			repository.createResource(
					REPOSITORY_JS_DEPLOY_PATH + "/testSum.js", ("var testSum = function(){ var a=2;" //$NON-NLS-1$ //$NON-NLS-2$
							+ "var b=2; " + "var c=a+b; " + "return c;}; testSum(); ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							.getBytes());
			
			repository.createResource(
					REPOSITORY_SANDBOX_DEPLOY_PATH + "/testSumSandbox.js", ("var testSum = function(){ var a=2;" //$NON-NLS-1$ //$NON-NLS-2$
							+ "var b=2; " + "var c=a+b; " + "return c;}; testSum(); ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							.getBytes());
			
			
			repository.createResource(REPOSITORY_JS_DEPLOY_PATH
					+ "/test/testLibrary.js", //$NON-NLS-1$
					("exports.add = function(a,b) {return a+b};").getBytes()); //$NON-NLS-1$
			repository
					.createResource(
							REPOSITORY_JS_DEPLOY_PATH + "/testExports.js", //$NON-NLS-1$
							("var testExports = function() {var add = require('test/testLibrary').add; return add(5, 6); }; testExports();") //$NON-NLS-1$
									.getBytes());
			repository
			.createResource(
					REPOSITORY_JS_DEPLOY_PATH + "/testDefaultObjects.js", //$NON-NLS-1$
					(" var testDefaultObjects = function() { return (datasource !== null) ? true : false}; testDefaultObjects();") //$NON-NLS-1$
							.getBytes());
			

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteFunctionModule() {
		JavaScriptExecutor executor = new JavaScriptExecutor(repository,
				REPOSITORY_JS_DEPLOY_PATH, null);
		// TODO mock request and response
		try {
			Object object = executor.executeServiceModule(null, null,
					"/testSum.js", null); //$NON-NLS-1$
			assertNotNull(object);
			assertTrue(((Double) object) == 4);

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testExecuteFunctionModuleSandbox() {
		JavaScriptExecutor executor = new JavaScriptExecutor(repository,
				REPOSITORY_SANDBOX_DEPLOY_PATH, REPOSITORY_JS_DEPLOY_PATH);
		// TODO mock request and response
		try {
			Object object = executor.executeServiceModule(null, null,
					"/testSumSandbox.js", null); //$NON-NLS-1$
			assertNotNull(object);
			assertTrue(((Double) object) == 4);

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteExports() {
		JavaScriptExecutor executor = new JavaScriptExecutor(repository,
				REPOSITORY_JS_DEPLOY_PATH, null);
		// TODO mock request and response
		try {
			Object object = executor.executeServiceModule(null, null,
					"/testExports.js", null); //$NON-NLS-1$
			assertNotNull(object);
			assertTrue(((Double) object) == 11);

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDefaultObjects() {
		JavaScriptExecutor executor = new JavaScriptExecutor(repository,
				REPOSITORY_JS_DEPLOY_PATH, null);
		// TODO mock request and response
		try {
			Object object = executor.executeServiceModule(null, null,
					"/testDefaultObjects.js", null); //$NON-NLS-1$
			assertNotNull(object);
			assertTrue(((Boolean) object));

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
