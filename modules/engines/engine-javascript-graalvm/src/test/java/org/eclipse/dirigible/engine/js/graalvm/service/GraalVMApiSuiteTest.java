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
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
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

	/** The Docker Elasticsearch image. */
	private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:7.7.1";
	/** The Elasticsearch container client. */
	private static ElasticsearchContainer container;

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest#setUp()
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.repository = getInjector().getInstance(IRepository.class);
		this.graalVMJavascriptEngineExecutor = getInjector().getInstance(GraalVMJavascriptEngineExecutor.class);

		if (Boolean.parseBoolean(Configuration.get("rabbitmq.value", "false"))) {
			RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:alpine");
			rabbit.start();

			String host = rabbit.getHost();
			Integer port = rabbit.getFirstMappedPort();
			Configuration.set("DIRIGIBLE_RABBITMQ_CLIENT_URI", host + ":" + port);
		}
		if (Boolean.parseBoolean(Configuration.get("elastic.value", "false"))) {
			container = new ElasticsearchContainer(ELASTICSEARCH_IMAGE);
			container.start();

			Configuration.set("DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME", container.getHost());
			Configuration.set("DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT", container.getFirstMappedPort().toString());
		}
		if (Boolean.parseBoolean(Configuration.get("redis.value", "false"))) {
			GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
					.withExposedPorts(6379);
			redis.start();

			String host = redis.getHost();
			Integer port = redis.getFirstMappedPort();

			Configuration.set("DIRIGIBLE_REDIS_CLIENT_URI", host + ":" + port);
		}
	}

	@Override
	public void registerModules() {
		registerModulesV4();

		if (Boolean.parseBoolean(Configuration.get("spark.value", "false"))) {
			sparkRegisterModule();
		}
		if (Boolean.parseBoolean(Configuration.get("rabbitmq.value", "false"))) {
			registerModulesRabbitMQ();
		}
		if (Boolean.parseBoolean(Configuration.get("elastic.value", "false"))) {
			registerModulesElastic();
		}
		if (Boolean.parseBoolean(Configuration.get("redis.value", "false"))) {
			registerModulesRedisExt();
		}
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
