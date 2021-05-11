/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.service;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.engine.js.graalvm.processor.GraalVMJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * The Class GraalVMApiSuiteTest.
 */
public class GraalVMApiSuiteTest extends AbstractApiSuiteTest {

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
		setUpRedis();
		registerModulesExt();
	}

	private void setUpRedis(){
		GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
				.withExposedPorts(6379);
		redis.start();

		String host = redis.getHost();
		Integer port = redis.getFirstMappedPort();

		Configuration.set("DIRIGIBLE_REDIS_CLIENT_URI", host + ":" + port);
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
		super.runSuite(this.graalVMJavascriptEngineExecutor, repository);
	}

}
