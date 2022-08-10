/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.service;

import java.io.IOException;

import org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.engine.js.graalvm.processor.GraalVMJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraalVMApiSuiteTest.
 */
public class GraalVMCustomTest extends AbstractApiSuiteTest {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(GraalVMCustomTest.class);

	/** The repository. */
	private IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

	/** The GraalVM javascript engine executor. */
	private GraalVMJavascriptEngineExecutor graalVMJavascriptEngineExecutor;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest#setUp()
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		this.graalVMJavascriptEngineExecutor = new GraalVMJavascriptEngineExecutor();
	}

	/**
	 * Register modules.
	 */
	@Override
	public void registerModules() {
		registerModulesV4();
	}
	
	/**
	 * Custom custom package.
	 *
	 * @throws RepositoryWriteException the repository write exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ScriptingException the scripting exception
	 * @throws ContextException the context exception
	 * @throws ExtensionsException the extensions exception
	 */
	@Test
	public void customPackage() throws RepositoryWriteException, IOException, ScriptingException, ContextException, ExtensionsException {
		
		String testModule = "graalvm/customPackage.js";
		
		try {
			ThreadContextFacade.setUp();

			logger.info("API test starting... " + testModule);

			runTest(graalVMJavascriptEngineExecutor, repository, testModule);
			logger.info("API test passed successfully: " + testModule);
				 
		} finally {
			ThreadContextFacade.tearDown();
		}
	}
	
	/**
	 * Custom custom package.
	 *
	 * @throws RepositoryWriteException the repository write exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ScriptingException the scripting exception
	 * @throws ContextException the context exception
	 * @throws ExtensionsException the extensions exception
	 */
	@Test
	public void customPackageImport() throws RepositoryWriteException, IOException, ScriptingException, ContextException, ExtensionsException {
		
		String testModule = "graalvm/customPackageImport.js";

		try {
			ThreadContextFacade.setUp();

			logger.info("API test starting... " + testModule);

			Object result = null;
			runTest(graalVMJavascriptEngineExecutor, repository, testModule);

			logger.info("API test passed successfully: " + testModule);
				 
		} finally {
			ThreadContextFacade.tearDown();
		}
	}

	/**
	 * Dirigible api ecma import.
	 *
	 * @throws ContextException the context exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ScriptingException the scripting exception
	 */
	@Test
	public void dirigibleApiEcmaImport() throws ContextException, IOException, ScriptingException {
		String testModule = "graalvm/ecmascript/importDirigibleApi.mjs";

		try {
			ThreadContextFacade.setUp();

			logger.info("API test starting... " + testModule);

			Object result = null;
			runTest(graalVMJavascriptEngineExecutor, repository, testModule);

			logger.info("API test passed successfully: " + testModule);

		} finally {
			ThreadContextFacade.tearDown();
		}
	}

	/**
	 * Relative path ecma import.
	 *
	 * @throws ContextException the context exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ScriptingException the scripting exception
	 */
	@Test
	public void relativePathEcmaImport() throws ContextException, IOException, ScriptingException {
		String testModule = "graalvm/ecmascript/relativeImports/l12/l12.mjs";

		try {
			ThreadContextFacade.setUp();

			logger.info("API test starting... " + testModule);

			Object result = null;
			runTest(graalVMJavascriptEngineExecutor, repository, testModule);

			logger.info("API test passed successfully: " + testModule);

		} finally {
			ThreadContextFacade.tearDown();
		}
	}
}
