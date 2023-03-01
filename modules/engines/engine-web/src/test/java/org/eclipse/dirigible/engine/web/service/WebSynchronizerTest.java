/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.web.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.engine.web.api.IWebCoreService;
import org.eclipse.dirigible.engine.web.api.WebCoreException;
import org.eclipse.dirigible.engine.web.models.WebModel;
import org.eclipse.dirigible.engine.web.processor.WebEngineProcessor;
import org.eclipse.dirigible.engine.web.processor.WebExposureManager;
import org.eclipse.dirigible.engine.web.synchronizer.WebSynchronizer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class WebSynchronizerTest.
 */
public class WebSynchronizerTest extends AbstractDirigibleTest {

	/** The web core service. */
	private IWebCoreService webCoreService;

	/** The web synchronizer. */
	private WebSynchronizer webSynchronizer;
	
	/** The web engine processor. */
	private WebEngineProcessor webEngineProcessor;

	/** The repository. */
	private IRepository repository;

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.webCoreService = new WebCoreService();
		this.webSynchronizer = new WebSynchronizer();
		this.webEngineProcessor = new WebEngineProcessor();
		
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Full web test.
	 *
	 * @throws WebCoreException
	 *             the web core exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void fullWebTest() throws WebCoreException, IOException, InterruptedException {
		
		byte[] content = webEngineProcessor.getResourceContent("/control/ui/index.html");
		assertNotNull(content);
		content = webEngineProcessor.getResourceContent("/control/js/index.js");
		assertNotNull(content);
		
		createWebTest();

		Thread.sleep(5000);
		
		content = webEngineProcessor.getResourceContent("/control/ui/index.html");
		assertNotNull(content);
		content = webEngineProcessor.getResourceContent("/control/js/index.js");
		assertNull(content);

		repository.removeResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/project.json");

		webSynchronizer.synchronize();

		WebModel webModel = webCoreService.getWebByName("custom");
		assertNull(webModel);
		assertFalse(WebExposureManager.existExposableProject("custom"));

		Thread.sleep(5000);
	}

	/**
	 * Creates the web test.
	 *
	 * @throws WebCoreException
	 *             the web core exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void createWebTest() throws WebCoreException, IOException {
		webSynchronizer.registerPredeliveredProject("/control/project.json");

		WebModel webModelCustom = new WebModel();
		webModelCustom.setLocation("/custom/project.json");
		webModelCustom.setGuid("custom");
		webModelCustom.setExposed("js,css");
		webModelCustom.setCreatedAt(new Timestamp(new Date().getTime()));
		webModelCustom.setCreatedBy("test_user");

		String json = webCoreService.serializeWeb(webModelCustom);
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/project.json", json.getBytes());

		webSynchronizer.synchronize();

		WebModel webModel = webCoreService.getWeb("/control/project.json");
		assertNotNull(webModel);
		webModel = webCoreService.getWeb("/custom/project.json");
		assertNotNull(webModel);
	}

}
