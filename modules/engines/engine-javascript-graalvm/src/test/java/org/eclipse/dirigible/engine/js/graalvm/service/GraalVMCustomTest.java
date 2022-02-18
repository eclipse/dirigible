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
import java.nio.file.Path;

import org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.GraalJSCodeRunner;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills.nodejs.RequirePolyfill;
import org.eclipse.dirigible.engine.js.graalvm.processor.GraalVMJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraalVMApiSuiteTest.
 */
public class GraalVMCustomTest extends AbstractApiSuiteTest {

	private static final Logger logger = LoggerFactory.getLogger(GraalVMCustomTest.class);

	/** The repository. */
	private IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

	/** The GraalVM javascript engine executor. */
	private GraalVMJavascriptEngineExecutor graalVMJavascriptEngineExecutor;

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

	@Override
	public void registerModules() {
		registerModulesV4();
	}

	@Test
	public void testNewEngineWithEventLooper() {
		Path workingDir = Path.of("/Users/c5326377/work/dirigible/dirigible/modules/engines/engine-javascript-graalvm/src/test/resources/META-INF/dirigible/graalvm/timers");
		GraalJSCodeRunner codeRunner = createCodeRunner(workingDir);

		Path codePath = Path.of("timers-test.mjs");
		codeRunner.runLooped(codePath);
	}

	@Test
	public void testNewEngineWithDirigibleImports() {
		Path workingDir = Path.of("/Users/c5326377/work/dirigible/dirigible/modules/engines/engine-javascript-graalvm/src/test/resources/META-INF/dirigible/graalvm/ecmascript");
		GraalJSCodeRunner codeRunner = createCodeRunner(workingDir);

		Path codePath = Path.of("importDirigibleApi.mjs");
		codeRunner.run(codePath);
	}

	@Test
	public void testNewEngineWithRelativeImports() {
		Path workingDir = Path.of("/Users/c5326377/work/dirigible/dirigible/modules/engines/engine-javascript-graalvm/src/test/resources/META-INF/dirigible/graalvm/ecmascript");
		GraalJSCodeRunner codeRunner = createCodeRunner(workingDir);

		Path codePath = Path.of("relativeImports/l12/l12.mjs");
		codeRunner.run(codePath);
	}

	private static GraalJSCodeRunner createCodeRunner(Path workingDir) {
		return new GraalJSCodeRunner.Builder(workingDir)
                .addJSPolyfill(new RequirePolyfill())
				.waitForDebugger(false)
                .build();
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
//	@Test
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
	 * Custom custom package
	 *
	 * @throws RepositoryWriteException the repository write exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ScriptingException the scripting exception
	 * @throws ContextException the context exception
	 * @throws ExtensionsException the extensions exception
	 */
//	@Test
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

//	@Test
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

//	@Test
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
