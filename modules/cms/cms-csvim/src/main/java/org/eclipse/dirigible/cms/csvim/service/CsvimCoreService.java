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

	/** The data source. */
	private DataSource dataSource = null;

	/** The csvim persistence manager. */
	private PersistenceManager<CsvimDefinition> csvimPersistenceManager = new PersistenceManager<CsvimDefinition>();

	/** The csv persistence manager. */
	private PersistenceManager<CsvDefinition> csvPersistenceManager = new PersistenceManager<CsvDefinition>();
	
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

	// CSVIM

	/**
	 * Creates the csvim.
	 *
	 * @param location the location
	 * @param hash the hash
	 * @return the csvim definition
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Gets the csvim.
	 *
	 * @param location the location
	 * @return the csvim
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Removes the csvim.
	 *
	 * @param location the location
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Update csvim.
	 *
	 * @param location the location
	 * @param hash the hash
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Gets the csvims.
	 *
	 * @return the csvims
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Exists csvim.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Parses the csvim.
	 *
	 * @param json the json
	 * @return the csvim definition
	 */
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
		definition.setCsvFileDefinitions(GsonHelper.fromJson(json, csvListType));
		definition.setHash(DigestUtils.md5Hex(json.getBytes()));
		return definition;
	}

	/**
	 * Parses the csvim.
	 *
	 * @param json the json
	 * @return the csvim definition
	 */
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
		definition.setCsvFileDefinitions(GsonHelper
				.fromJson(new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8), csvListType));
		definition.setHash(DigestUtils.md5Hex(json));
		return definition;
	}

	/**
	 * Serialize csvim.
	 *
	 * @param csvimDefinition the csvim definition
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService#serializeCsvim(org.
	 * eclipse.dirigible. core.extensions.definition.CsvimDefinition)
	 */
	@Override
	public String serializeCsvim(CsvimDefinition csvimDefinition) {
		return GsonHelper.toJson(csvimDefinition.getCsvFileDefinitions());
	}

	// CSV

	/**
	 * Creates the csv.
	 *
	 * @param location the location
	 * @param hash the hash
	 * @return the csv definition
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Gets the csv.
	 *
	 * @param location the location
	 * @return the csv
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Removes the csv.
	 *
	 * @param location the location
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Update csv.
	 *
	 * @param location the location
	 * @param hash the hash
	 * @param imported the imported
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Gets the csvs.
	 *
	 * @return the csvs
	 * @throws CsvimException the csvim exception
	 */
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

	/**
	 * Exists csv.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws CsvimException the csvim exception
	 */
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
