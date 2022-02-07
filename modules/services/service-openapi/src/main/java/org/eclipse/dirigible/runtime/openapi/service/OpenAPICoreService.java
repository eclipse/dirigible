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
package org.eclipse.dirigible.runtime.openapi.service;

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
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.runtime.openapi.api.IOpenAPICoreService;
import org.eclipse.dirigible.runtime.openapi.api.OpenAPIException;
import org.eclipse.dirigible.runtime.openapi.definition.OpenAPIDefinition;

/**
 * The Class OpenAPIsCoreService.
 */
public class OpenAPICoreService implements IOpenAPICoreService {

	private DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);

	private PersistenceManager<OpenAPIDefinition> openAPIPersistenceManager = new PersistenceManager<OpenAPIDefinition>();

	

	// OpenAPI

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.openapi.api.IOpenAPICoreService#createOpenAPI(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public OpenAPIDefinition createOpenAPI(String location, String hash) throws OpenAPIException {
		OpenAPIDefinition openAPIDefinition = new OpenAPIDefinition();
		openAPIDefinition.setLocation(location);
		openAPIDefinition.setHash(hash);
		openAPIDefinition.setCreatedBy(UserFacade.getName());
		openAPIDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				openAPIPersistenceManager.insert(connection, openAPIDefinition);
				return openAPIDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new OpenAPIException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.openapi.api.IOpenAPICoreService#getOpenAPI(java.lang.String)
	 */
	@Override
	public OpenAPIDefinition getOpenAPI(String location) throws OpenAPIException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				return openAPIPersistenceManager.find(connection, OpenAPIDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new OpenAPIException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.openapi.api.IOpenAPICoreService#removeOpenAPI(java.lang.String)
	 */
	@Override
	public void removeOpenAPI(String location) throws OpenAPIException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				openAPIPersistenceManager.delete(connection, OpenAPIDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new OpenAPIException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.openapi.api.IOpenAPICoreService#updateOpenAPI(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateOpenAPI(String location, String hash) throws OpenAPIException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				OpenAPIDefinition openAPIDefinition = getOpenAPI(location);
				openAPIDefinition.setHash(hash);
				openAPIPersistenceManager.update(connection, openAPIDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new OpenAPIException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.openapi.api.IOpenAPIsCoreService#getOpenAPIs()
	 */
	@Override
	public List<OpenAPIDefinition> getOpenAPIs() throws OpenAPIException {
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				return openAPIPersistenceManager.findAll(connection, OpenAPIDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new OpenAPIException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.runtime.openapi.api.IOpenAPICoreService#existsOpenAPI(java.lang.String)
	 */
	@Override
	public boolean existsOpenAPI(String location) throws OpenAPIException {
		return getOpenAPI(location) != null;
	}

}
