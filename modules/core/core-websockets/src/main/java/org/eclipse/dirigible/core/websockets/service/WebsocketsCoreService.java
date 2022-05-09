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

	private DataSource dataSource = null;

	private PersistenceManager<WebsocketDefinition> websocketsPersistenceManager = new PersistenceManager<WebsocketDefinition>();

	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

	// Websocket

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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#existsWebsocket(java.lang.String)
	 */
	@Override
	public boolean existsWebsocket(String location) throws WebsocketsException {
		return getWebsocket(location) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#parseWebsocket(java.lang.String)
	 */
	@Override
	public WebsocketDefinition parseWebsocket(String json) {
		return GsonHelper.GSON.fromJson(json, WebsocketDefinition.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.websockets.api.IWebsocketsCoreService#parseWebsocket(byte[])
	 */
	@Override
	public WebsocketDefinition parseWebsocket(byte[] json) {
		return GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8),
				WebsocketDefinition.class);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.core.websocket.api.IWebsocketsCoreService#serializeWebsocket(org.eclipse.dirigible.
	 * core.extensions.definition.WebsocketDefinition)
	 */
	@Override
	public String serializeWebsocket(WebsocketDefinition websocketDefinition) {
		return GsonHelper.GSON.toJson(websocketDefinition);
	}

}
