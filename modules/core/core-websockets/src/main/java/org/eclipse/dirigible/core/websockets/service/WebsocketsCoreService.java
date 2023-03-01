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
package org.eclipse.dirigible.core.websockets.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService;
import org.eclipse.dirigible.core.websockets.api.WebsocketsException;
import org.eclipse.dirigible.core.websockets.definition.WebsocketDefinition;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

/**
 * The Class WebsocketsCoreService.
 */
public class WebsocketsCoreService implements IWebsocketsCoreService {

	/** The data source. */
	private DataSource dataSource = null;

	/** The websockets persistence manager. */
	private PersistenceManager<WebsocketDefinition> websocketsPersistenceManager = new PersistenceManager<WebsocketDefinition>();

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

	// Websocket

	/**
	 * Creates the websocket.
	 *
	 * @param location the location
	 * @param module the module
	 * @param endpoint the endpoint
	 * @param description the description
	 * @return the websocket definition
	 * @throws WebsocketsException the websockets exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#createWebsocket(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public WebsocketDefinition createWebsocket(String location, String module, String endpoint, String description) throws WebsocketsException {
		WebsocketDefinition websocketDefinition = new WebsocketDefinition();
		websocketDefinition.setLocation(location);
		websocketDefinition.setHandler(module);
		websocketDefinition.setEndpoint(endpoint);
		websocketDefinition.setDescription(description);
		websocketDefinition.setCreatedBy(UserFacade.getName());
		websocketDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				websocketsPersistenceManager.insert(connection, websocketDefinition);
				return websocketDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebsocketsException(e);
		}
	}

	/**
	 * Gets the websocket.
	 *
	 * @param location the location
	 * @return the websocket
	 * @throws WebsocketsException the websockets exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#getWebsocket(java.lang.String)
	 */
	@Override
	public WebsocketDefinition getWebsocket(String location) throws WebsocketsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return websocketsPersistenceManager.find(connection, WebsocketDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebsocketsException(e);
		}
	}

	/**
	 * Removes the websocket.
	 *
	 * @param location the location
	 * @throws WebsocketsException the websockets exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#removeWebsocket(java.lang.String)
	 */
	@Override
	public void removeWebsocket(String location) throws WebsocketsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				websocketsPersistenceManager.delete(connection, WebsocketDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebsocketsException(e);
		}
	}

	/**
	 * Update websocket.
	 *
	 * @param location the location
	 * @param module the module
	 * @param endpoint the endpoint
	 * @param description the description
	 * @throws WebsocketsException the websockets exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#updateWebsocket(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateWebsocket(String location, String module, String endpoint, String description) throws WebsocketsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				WebsocketDefinition websocketDefinition = getWebsocket(location);
				websocketDefinition.setHandler(module);
				websocketDefinition.setEndpoint(endpoint);
				websocketDefinition.setDescription(description);
				websocketsPersistenceManager.update(connection, websocketDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebsocketsException(e);
		}
	}

	/**
	 * Gets the websockets.
	 *
	 * @return the websockets
	 * @throws WebsocketsException the websockets exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#getWebsockets()
	 */
	@Override
	public List<WebsocketDefinition> getWebsockets() throws WebsocketsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return websocketsPersistenceManager.findAll(connection, WebsocketDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebsocketsException(e);
		}
	}

	/**
	 * Gets the websocket by endpoint.
	 *
	 * @param endpoint the endpoint
	 * @return the websocket by endpoint
	 * @throws WebsocketsException the websockets exception
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#getWebsocketByEndpoint(java.lang.String)
	 */
	@Override
	public List<WebsocketDefinition> getWebsocketByEndpoint(String endpoint) throws WebsocketsException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_WEBSOCKETS")
						.where("WEBSOCKET_ENDPOINT_NAME = ?").toString();
				return websocketsPersistenceManager.query(connection, WebsocketDefinition.class, sql,
						Arrays.asList(endpoint));
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebsocketsException(e);
		}
	}

	/**
	 * Exists websocket.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws WebsocketsException the websockets exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#existsWebsocket(java.lang.String)
	 */
	@Override
	public boolean existsWebsocket(String location) throws WebsocketsException {
		return getWebsocket(location) != null;
	}

	/**
	 * Parses the websocket.
	 *
	 * @param json the json
	 * @return the websocket definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#parseWebsocket(java.lang.String)
	 */
	@Override
	public WebsocketDefinition parseWebsocket(String json) {
		return GsonHelper.fromJson(json, WebsocketDefinition.class);
	}

	/**
	 * Parses the websocket.
	 *
	 * @param json the json
	 * @return the websocket definition
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#parseWebsocket(byte[])
	 */
	@Override
	public WebsocketDefinition parseWebsocket(byte[] json) {
		return GsonHelper.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8),
				WebsocketDefinition.class);
	}

	/**
	 * Serialize websocket.
	 *
	 * @param websocketDefinition the websocket definition
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.websocket.api.IWebsocketsCoreService#serializeWebsocket(org.eclipse.dirigible.
	 * core.extensions.definition.WebsocketDefinition)
	 */
	@Override
	public String serializeWebsocket(WebsocketDefinition websocketDefinition) {
		return GsonHelper.toJson(websocketDefinition);
	}

}
