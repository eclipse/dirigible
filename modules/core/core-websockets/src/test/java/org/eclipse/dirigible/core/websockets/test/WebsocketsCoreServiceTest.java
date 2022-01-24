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
package org.eclipse.dirigible.core.websockets.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService;
import org.eclipse.dirigible.core.websockets.api.WebsocketsException;
import org.eclipse.dirigible.core.websockets.definition.WebsocketDefinition;
import org.eclipse.dirigible.core.websockets.service.WebsocketsCoreService;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class WebsocketsCoreServiceTest.
 */
public class WebsocketsCoreServiceTest extends AbstractDirigibleTest {

	private IWebsocketsCoreService websocketsCoreService;

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.websocketsCoreService = new WebsocketsCoreService();
	}

	/**
	 * Creates the websocket test.
	 *
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	@Test
	public void createWebsocketTest() throws WebsocketsException {
		websocketsCoreService.removeWebsocket("/test_ws1");
		websocketsCoreService.createWebsocket("/test_ws1", "test_ws1", "test_endpoint1", "Test");
		List<WebsocketDefinition> list = websocketsCoreService.getWebsocketByEndpoint("test_endpoint1");
		assertEquals(1, list.size());
		WebsocketDefinition extensionDefinition = list.get(0);
		assertEquals("test_ws1", extensionDefinition.getHandler());
		assertEquals("test_endpoint1", extensionDefinition.getEndpoint());

		websocketsCoreService.removeWebsocket("test_ws1");
	}

	/**
	 * Gets the websocket test.
	 *
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	@Test
	public void getWebsocketTest() throws WebsocketsException {
		websocketsCoreService.removeWebsocket("test_ws1");
		websocketsCoreService.createWebsocket("/test_ws1", "test_ws1", "test_endpoint1", "Test WS");
		WebsocketDefinition websocketDefinition = websocketsCoreService.getWebsocket("/test_ws1");
		assertEquals("test_ws1", websocketDefinition.getHandler());
		assertEquals("test_endpoint1", websocketDefinition.getEndpoint());
		assertEquals("Test WS", websocketDefinition.getDescription());

		websocketsCoreService.removeWebsocket("test_ws1");

	}

	/**
	 * Gets the websockets test.
	 *
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	@Test
	public void getWebsocketsTest() throws WebsocketsException {
		websocketsCoreService.removeWebsocket("/test_ws1");
		websocketsCoreService.createWebsocket("/test_ws1", "test_ws1", "test_endpoint1", "Test WS 1");
		websocketsCoreService.removeWebsocket("/test_ws11");
		websocketsCoreService.createWebsocket("/test_ws11", "test_ws11", "test_endpoint11", "Test WS 11");
		websocketsCoreService.removeWebsocket("/test_ws2");
		websocketsCoreService.createWebsocket("/test_ws2", "test_ws2", "test_endpoint2", "Test WS 2");
		websocketsCoreService.removeWebsocket("/test_ws22");
		websocketsCoreService.createWebsocket("/test_ws22", "test_ws22", "test_endpoint22", "Test WS 22");

		List<WebsocketDefinition> list = websocketsCoreService.getWebsocketByEndpoint("test_endpoint1");
		assertEquals(1, list.size());
		list = websocketsCoreService.getWebsocketByEndpoint("test_endpoint2");
		assertEquals(1, list.size());
		WebsocketDefinition websocketDefinition = list.get(0);
		assertEquals("test_endpoint2", websocketDefinition.getEndpoint());
		assertEquals("Test WS 2", websocketDefinition.getDescription());

		websocketsCoreService.removeWebsocket("/test_ws1");
		websocketsCoreService.removeWebsocket("/test_ws11");
		websocketsCoreService.removeWebsocket("/test_ws2");
		websocketsCoreService.removeWebsocket("/test_ws22");

	}

	/**
	 * Update websocket test.
	 *
	 * @throws WebsocketsException
	 *             the extensions exception
	 */
	@Test
	public void updatetExtensionTest() throws WebsocketsException {
		websocketsCoreService.removeWebsocket("/test_ws1");
		websocketsCoreService.createWebsocket("/test_ws1", "test_ws1", "test_endpoint1", "Test WS");
		WebsocketDefinition websocketDefinition = websocketsCoreService.getWebsocket("/test_ws1");

		assertEquals("test_ws1", websocketDefinition.getHandler());
		assertEquals("test_endpoint1", websocketDefinition.getEndpoint());
		assertEquals("Test WS", websocketDefinition.getDescription());
		websocketsCoreService.updateWebsocket("/test_ws1", "test_ws1", "test_endpoint1", "Test WS 2");
		websocketDefinition = websocketsCoreService.getWebsocket("/test_ws1");
		assertEquals("test_ws1", websocketDefinition.getHandler());
		assertEquals("Test WS 2", websocketDefinition.getDescription());

		websocketsCoreService.removeWebsocket("/test_ws1");
	}

	/**
	 * Removes the websocket test.
	 *
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	@Test
	public void removeWebsocketTest() throws WebsocketsException {
		websocketsCoreService.removeWebsocket("/test_ws1");
		websocketsCoreService.createWebsocket("/test_ws1", "test_ws1", "test_endpoint1", "Test WS");
		WebsocketDefinition websocketDefinition = websocketsCoreService.getWebsocket("/test_ws1");
		websocketsCoreService.removeWebsocket("/test_ws1");
		websocketDefinition = websocketsCoreService.getWebsocket("/test_ws1");
		assertNull(websocketDefinition);

	}

	/**
	 * Parses the websocket test.
	 *
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	@Test
	public void parseWebsocketTest() throws WebsocketsException {
		WebsocketDefinition websocketDefinition = new WebsocketDefinition();
		websocketDefinition.setLocation("/test_ws1");
		websocketDefinition.setHandler("test_ws1");
		websocketDefinition.setEndpoint("test_endpoint1");
		websocketDefinition.setDescription("Test");
		websocketDefinition.setCreatedAt(new Timestamp(new Date().getTime()));
		websocketDefinition.setCreatedBy("test_user");
		String json = websocketsCoreService.serializeWebsocket(websocketDefinition);
		System.out.println(json);
		WebsocketDefinition websocketDefinition2 = websocketsCoreService.parseWebsocket(json);
		assertEquals(websocketDefinition.getLocation(), websocketDefinition2.getLocation());
	}

}
