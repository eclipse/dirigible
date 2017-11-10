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

public class V8ApiSuiteTest extends AbstractApiSuiteTest {

	@Inject
	private IRepository repository;

	private V8JavascriptEngineExecutor v8JavascriptEngineExecutor;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.repository = getInjector().getInstance(IRepository.class);
		this.v8JavascriptEngineExecutor = getInjector().getInstance(V8JavascriptEngineExecutor.class);
	}

	@Test
	public void runSuite() throws RepositoryWriteException, IOException, ScriptingException, ContextException, ExtensionsException {
		super.runSuite(this.v8JavascriptEngineExecutor, repository);
	}

}
