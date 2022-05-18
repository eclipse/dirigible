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

public class WebCoreService implements IWebCoreService {
	
	private DataSource dataSource = null;
	
	private PersistenceManager<WebModel> webPersistenceManager = new PersistenceManager<WebModel>();
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

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

	@Override
	public boolean existsWeb(String location) throws WebCoreException {
		return getWeb(location) != null;
	}

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

	@Override
	public WebModel parseWeb(String path, String json) {
		WebModel result = GsonHelper.GSON.fromJson(json, WebModel.class);
		result.setLocation(path);
		setName(path, result);
		result.setHash(DigestUtils.md5Hex(json));
		return result;
	}

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

	@Override
	public String serializeWeb(WebModel webModel) {
		return GsonHelper.GSON.toJson(webModel);
	}

}
