/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.v8.service;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.engine.js.v8.processor.V8JavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class V8ApiSuiteTest.
 */
public class V8ApiSuiteTest extends AbstractApiSuiteTest {

	/** The repository. */
	@Inject
	private IRepository repository;

	/** The v 8 javascript engine executor. */
	private V8JavascriptEngineExecutor v8JavascriptEngineExecutor;

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest#setUp()
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.repository = getInjector().getInstance(IRepository.class);
		this.v8JavascriptEngineExecutor = getInjector().getInstance(V8JavascriptEngineExecutor.class);
	}

	/**
	 * Run suite.
	 *
	 * @throws RepositoryWriteException the repository write exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ScriptingException the scripting exception
	 * @throws ContextException the context exception
	 * @throws ExtensionsException the extensions exception
	 */
	@Test
	public void runSuite() throws RepositoryWriteException, IOException, ScriptingException, ContextException, ExtensionsException {
		super.runSuite(this.v8JavascriptEngineExecutor, repository);
	}

}
