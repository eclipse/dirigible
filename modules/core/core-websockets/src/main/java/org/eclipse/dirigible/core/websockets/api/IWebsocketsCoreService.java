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
package org.eclipse.dirigible.core.websockets.api;

import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.websockets.definition.WebsocketDefinition;

/**
 * The Interface IWebsocketsCoreService.
 */
public interface IWebsocketsCoreService extends ICoreService {

	/** The Constant FILE_EXTENSION_EXTENSION. */
	public static final String FILE_EXTENSION_WEBSOCKET = ".websocket";


	
	// Websockets

	/**
	 * Creates the websocket.
	 *
	 * @param location
	 *            the location
	 * @param module
	 *            the module
	 * @param endpoint
	 *            the endpoint
	 * @param description
	 *            the description
	 * @return the websocket definition
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	public WebsocketDefinition createWebsocket(String location, String module, String endpoint, String description) throws WebsocketsException;

	/**
	 * Gets the websocket.
	 *
	 * @param location
	 *            the location
	 * @return the extension
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	public WebsocketDefinition getWebsocket(String location) throws WebsocketsException;

	/**
	 * Exists websocket.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	public boolean existsWebsocket(String location) throws WebsocketsException;

	/**
	 * Removes the websocket.
	 *
	 * @param location
	 *            the location
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	public void removeWebsocket(String location) throws WebsocketsException;

	/**
	 * Update websocket.
	 *
	 * @param location
	 *            the location
	 * @param module
	 *            the module
	 * @param endpoint
	 *            the endpoint
	 * @param description
	 *            the description
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	public void updateWebsocket(String location, String module, String endpoint, String description) throws WebsocketsException;

	/**
	 * Gets the websockets.
	 *
	 * @return the websockets
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	public List<WebsocketDefinition> getWebsockets() throws WebsocketsException;

	/**
	 * Gets the websocket by endpoint.
	 *
	 * @param endpoint
	 *            the endpoint
	 * @return the websocket by endpoint
	 * @throws WebsocketsException
	 *             the websockets exception
	 */
	public List<WebsocketDefinition> getWebsocketByEndpoint(String endpoint) throws WebsocketsException;

	/**
	 * Parses the websocket.
	 *
	 * @param json
	 *            the json
	 * @return the websocket definition
	 */
	public WebsocketDefinition parseWebsocket(String json);

	/**
	 * Parses the websocket.
	 *
	 * @param json
	 *            the json
	 * @return the websocket definition
	 */
	public WebsocketDefinition parseWebsocket(byte[] json);

	/**
	 * Serialize websocket.
	 *
	 * @param websocketDefinition
	 *            the websocket definition
	 * @return the string
	 */
	public String serializeWebsocket(WebsocketDefinition websocketDefinition);

}
