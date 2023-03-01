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
package org.eclipse.dirigible.core.websockets.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService;
import org.eclipse.dirigible.core.websockets.api.WebsocketsException;
import org.eclipse.dirigible.core.websockets.definition.WebsocketDefinition;
import org.eclipse.dirigible.core.websockets.service.WebsocketsCoreService;
import org.eclipse.dirigible.core.websockets.synchronizer.WebsocketsSynchronizer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class ExtensionsSynchronizerTest.
 */
public class WebsocketsSynchronizerTest extends AbstractDirigibleTest {

	/** The websockets core service. */
	private IWebsocketsCoreService websocketsCoreService;

	/** The websockets synchronizer. */
	private WebsocketsSynchronizer websocketsSynchronizer;

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
		this.websocketsCoreService = new WebsocketsCoreService();
		this.websocketsSynchronizer = new WebsocketsSynchronizer();
		this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
	}

	/**
	 * Creates the websocket test.
	 *
	 * @throws WebsocketsException
	 *             the websockets exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void createWebsocketTest() throws WebsocketsException, IOException {
		websocketsSynchronizer.registerPredeliveredWebsocket("/control/control.websocket");

		WebsocketDefinition websocketDefinitionCustom = new WebsocketDefinition();
		websocketDefinitionCustom.setLocation("/custom/custom.websocket");
		websocketDefinitionCustom.setEndpoint("/mycustomwebsocket");
		websocketDefinitionCustom.setHandler("control/handler.js");
		websocketDefinitionCustom.setDescription("Test");
		websocketDefinitionCustom.setCreatedAt(new Timestamp(new Date().getTime()));
		websocketDefinitionCustom.setCreatedBy("test_user");

		String json = websocketsCoreService.serializeWebsocket(websocketDefinitionCustom);
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.websocket", json.getBytes());

		websocketsSynchronizer.synchronize();

		WebsocketDefinition websocketDefinition = websocketsCoreService.getWebsocket("/control/control.websocket");
		assertNotNull(websocketDefinition);
		websocketDefinition = websocketsCoreService.getWebsocket("/custom/custom.websocket");
		assertNotNull(websocketDefinition);

	}

	/**
	 * Cleanup websocket test.
	 *
	 * @throws WebsocketsException
	 *             the extensions exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void cleanupWebsocketTest() throws WebsocketsException, IOException {
		createWebsocketTest();

		repository.removeResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/custom/custom.websocket");

		websocketsSynchronizer.synchronize();

		WebsocketDefinition websocketDefinition = websocketsCoreService.getWebsocket("/custom/custom.websocket");
		assertNull(websocketDefinition);

	}

}
