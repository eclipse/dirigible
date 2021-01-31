/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.engine.js.graalvm.processor.GraalVMJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraalVMApiSuiteTest.
 */
public class GraalVMCustomTest extends AbstractApiSuiteTest {
	
	private static final Logger logger = LoggerFactory.getLogger(GraalVMCustomTest.class);

	/** The repository. */
	@Inject
	private IRepository repository;

	/** The GraalVM javascript engine executor. */
	private GraalVMJavascriptEngineExecutor graalVMJavascriptEngineExecutor;

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest#setUp()
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.repository = getInjector().getInstance(IRepository.class);
		this.graalVMJavascriptEngineExecutor = getInjector().getInstance(GraalVMJavascriptEngineExecutor.class);
	}

	@Override
	public void registerModules() {
		registerModulesV4();
	}
	/**
	 * Custom custom package
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
			
			Object result = null;
			result = runTest(graalVMJavascriptEngineExecutor, repository, testModule);
			
			assertNotNull(result);
			assertTrue("API test failed: " + testModule, Boolean.parseBoolean(result.toString()));
			logger.info("API test passed successfully: " + testModule);
				 
		} finally {
			ThreadContextFacade.tearDown();
		}
	}

}
