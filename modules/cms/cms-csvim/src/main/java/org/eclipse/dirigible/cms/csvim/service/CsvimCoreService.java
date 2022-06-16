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
package org.eclipse.dirigible.cms.csvim.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.cms.csvim.api.CsvimException;
import org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService;
import org.eclipse.dirigible.cms.csvim.definition.CsvDefinition;
import org.eclipse.dirigible.cms.csvim.definition.CsvFileDefinition;
import org.eclipse.dirigible.cms.csvim.definition.CsvimDefinition;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.persistence.PersistenceManager;

import com.google.gson.reflect.TypeToken;

/**
 * The Class ExtensionsCoreService.
 */
public class CsvimCoreService implements ICsvimCoreService {

	private DataSource dataSource = null;

	private PersistenceManager<CsvimDefinition> csvimPersistenceManager = new PersistenceManager<CsvimDefinition>();

	private PersistenceManager<CsvDefinition> csvPersistenceManager = new PersistenceManager<CsvDefinition>();
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

	// CSVIM

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#createCsvim(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public CsvimDefinition createCsvim(String location, String hash) throws CsvimException {
		CsvimDefinition csvimDefinition = new CsvimDefinition();
		csvimDefinition.setLocation(location);
		csvimDefinition.setHash(hash);
		csvimDefinition.setCreatedBy(UserFacade.getName());
		csvimDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				csvimPersistenceManager.insert(connection, csvimDefinition);
				return csvimDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#getCsvim(java.lang.
	 * String)
	 */
	@Override
	public CsvimDefinition getCsvim(String location) throws CsvimException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return csvimPersistenceManager.find(connection, CsvimDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#removeCsvim(java.lang.
	 * String)
	 */
	@Override
	public void removeCsvim(String location) throws CsvimException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				csvimPersistenceManager.delete(connection, CsvimDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#updateCsvim(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public void updateCsvim(String location, String hash) throws CsvimException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				CsvimDefinition csvimDefinition = getCsvim(location);
				csvimDefinition.setHash(hash);
				csvimPersistenceManager.update(connection, csvimDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#getCsvims()
	 */
	@Override
	public List<CsvimDefinition> getCsvims() throws CsvimException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return csvimPersistenceManager.findAll(connection, CsvimDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#existsCsvim(java.lang.
	 * String)
	 */
	@Override
	public boolean existsCsvim(String location) throws CsvimException {
		return getCsvim(location) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#parseCsvim(java.lang.
	 * String)
	 */
	@Override
	public CsvimDefinition parseCsvim(String json) {
		CsvimDefinition definition = new CsvimDefinition();
		Type csvListType = new TypeToken<ArrayList<CsvFileDefinition>>() {
		}.getType();
		definition.setCsvFileDefinitions(GsonHelper.GSON.fromJson(json, csvListType));
		definition.setHash(DigestUtils.md5Hex(json.getBytes()));
		return definition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#parseCsvim(byte[])
	 */
	@Override
	public CsvimDefinition parseCsvim(byte[] json) {
		CsvimDefinition definition = new CsvimDefinition();
		Type csvListType = new TypeToken<ArrayList<CsvFileDefinition>>() {
		}.getType();
		definition.setCsvFileDefinitions(GsonHelper.GSON
				.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), csvListType));
		definition.setHash(DigestUtils.md5Hex(json));
		return definition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#serializeCsvim(org.
	 * eclipse.dirigible. core.extensions.definition.CsvimDefinition)
	 */
	@Override
	public String serializeCsvim(CsvimDefinition csvimDefinition) {
		return GsonHelper.GSON.toJson(csvimDefinition.getCsvFileDefinitions());
	}

	// CSV

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#createCsv(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public CsvDefinition createCsv(String location, String hash) throws CsvimException {
		CsvDefinition csvDefinition = new CsvDefinition();
		csvDefinition.setLocation(location);
		csvDefinition.setHash(hash);
		csvDefinition.setCreatedBy(UserFacade.getName());
		csvDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				csvPersistenceManager.insert(connection, csvDefinition);
				return csvDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#getCsv(java.lang.
	 * String)
	 */
	@Override
	public CsvDefinition getCsv(String location) throws CsvimException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return csvPersistenceManager.find(connection, CsvDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#removeCsv(java.lang.
	 * String)
	 */
	@Override
	public void removeCsv(String location) throws CsvimException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				csvPersistenceManager.delete(connection, CsvDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#updateCsv(java.lang.
	 * String, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public void updateCsv(String location, String hash, Boolean imported) throws CsvimException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				CsvDefinition csvDefinition = getCsv(location);
				if (hash != null) {
					csvDefinition.setHash(hash);
				}
				csvDefinition.setImported(imported);
				csvPersistenceManager.update(connection, csvDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#getCsvs()
	 */
	@Override
	public List<CsvDefinition> getCsvs() throws CsvimException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return csvPersistenceManager.findAll(connection, CsvDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new CsvimException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#existsCsv(java.lang.
	 * String)
	 */
	@Override
	public boolean existsCsv(String location) throws CsvimException {
		return getCsv(location) != null;
	}

}
