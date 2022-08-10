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
package org.eclipse.dirigible.engine.web.service;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.engine.web.api.IWebCoreService;
import org.eclipse.dirigible.engine.web.api.WebCoreException;
import org.eclipse.dirigible.engine.web.models.WebModel;

/**
 * The Class WebCoreService.
 */
public class WebCoreService implements IWebCoreService {
	
	/** The data source. */
	private DataSource dataSource = null;
	
	/** The web persistence manager. */
	private PersistenceManager<WebModel> webPersistenceManager = new PersistenceManager<WebModel>();
	
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

	/**
	 * Creates the web.
	 *
	 * @param location the location
	 * @param name the name
	 * @param exposed the exposed
	 * @param hash the hash
	 * @return the web model
	 * @throws WebCoreException the web core exception
	 */
	@Override
	public WebModel createWeb(String location, String name, String exposed, String hash) throws WebCoreException {
		WebModel webModel = new WebModel();
		webModel.setLocation(location);
		webModel.setGuid(name);
		webModel.setExposed(exposed);
		webModel.setHash(hash);
		webModel.setCreatedBy(UserFacade.getName());
		webModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				webPersistenceManager.insert(connection, webModel);
				return webModel;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebCoreException(e);
		}
	}

	/**
	 * Gets the web.
	 *
	 * @param location the location
	 * @return the web
	 * @throws WebCoreException the web core exception
	 */
	@Override
	public WebModel getWeb(String location) throws WebCoreException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return webPersistenceManager.find(connection, WebModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebCoreException(e);
		}
	}
	
	/**
	 * Gets the web by name.
	 *
	 * @param name the name
	 * @return the web by name
	 * @throws WebCoreException the web core exception
	 */
	@Override
	public WebModel getWebByName(String name) throws WebCoreException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_WEB")
						.where("WEB_GUID = ?").toString();
				List<WebModel> webModels = webPersistenceManager.query(connection, WebModel.class, sql, Arrays.asList(name));
				if (webModels.isEmpty()) {
					return null;
				}
				if (webModels.size() > 1) {
					throw new WebCoreException(format("There are more that one Web Projects registered with the same name [{0}] at locations: [{1}] and [{2}].",
							name, webModels.get(0).getLocation(), webModels.get(1).getLocation()));
				}
				return webModels.get(0);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebCoreException(e);
		}

	}

	/**
	 * Exists web.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws WebCoreException the web core exception
	 */
	@Override
	public boolean existsWeb(String location) throws WebCoreException {
		return getWeb(location) != null;
	}

	/**
	 * Removes the web.
	 *
	 * @param location the location
	 * @throws WebCoreException the web core exception
	 */
	@Override
	public void removeWeb(String location) throws WebCoreException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				webPersistenceManager.delete(connection, WebModel.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebCoreException(e);
		}
	}

	/**
	 * Update web.
	 *
	 * @param location the location
	 * @param name the name
	 * @param exposed the exposed
	 * @param hash the hash
	 * @throws WebCoreException the web core exception
	 */
	@Override
	public void updateWeb(String location, String name, String exposed, String hash) throws WebCoreException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				WebModel webModel = getWeb(location);
				webModel.setGuid(name);
				webModel.setExposed(exposed);
				webModel.setHash(hash);
				webPersistenceManager.update(connection, webModel);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebCoreException(e);
		}
	}

	/**
	 * Gets the webs.
	 *
	 * @return the webs
	 * @throws WebCoreException the web core exception
	 */
	@Override
	public List<WebModel> getWebs() throws WebCoreException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return webPersistenceManager.findAll(connection, WebModel.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WebCoreException(e);
		}
	}

	/**
	 * Parses the web.
	 *
	 * @param path the path
	 * @param json the json
	 * @return the web model
	 */
	@Override
	public WebModel parseWeb(String path, String json) {
		WebModel result = GsonHelper.GSON.fromJson(json, WebModel.class);
		result.setLocation(path);
		setName(path, result);
		result.setHash(DigestUtils.md5Hex(json));
		return result;
	}

	/**
	 * Parses the web.
	 *
	 * @param path the path
	 * @param json the json
	 * @return the web model
	 */
	@Override
	public WebModel parseWeb(String path, byte[] json) {
		WebModel result = GsonHelper.GSON.fromJson(
				new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8),
				WebModel.class);
		result.setLocation(path);
		setName(path, result);
		result.setHash(DigestUtils.md5Hex(json));
		return result;
	}

	/**
	 * Sets the name.
	 *
	 * @param path the path
	 * @param result the result
	 */
	private void setName(String path, WebModel result) {
		String name = path.split("/")[1];
		if (result.getGuid() == null) {
			result.setGuid(name);
		} else {
			if (!result.getGuid().equals(name)) {
				throw new RuntimeException(format("The name of the project folder must the same as the 'guid' property in the 'project.json' - [{0}] and [{1}]", name, result.getGuid()));
			}
		}
	}

	/**
	 * Serialize web.
	 *
	 * @param webModel the web model
	 * @return the string
	 */
	@Override
	public String serializeWeb(WebModel webModel) {
		return GsonHelper.GSON.toJson(webModel);
	}

}
