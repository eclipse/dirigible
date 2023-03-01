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
package org.eclipse.dirigible.engine.wiki.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.engine.wiki.api.IWikiCoreService;
import org.eclipse.dirigible.engine.wiki.api.WikiException;
import org.eclipse.dirigible.engine.wiki.definition.WikiDefinition;

/**
 * The Class WikiCoreService.
 */
public class WikiCoreService implements IWikiCoreService {

	/** The data source. */
	private DataSource dataSource = null;

	/** The wiki persistence manager. */
	private PersistenceManager<WikiDefinition> wikiPersistenceManager = new PersistenceManager<WikiDefinition>();

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

	// Wiki

	/**
	 * Creates the wiki.
	 *
	 * @param location the location
	 * @param hash the hash
	 * @return the wiki definition
	 * @throws WikiException the wiki exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.wiki.api.IWikiCoreService#createWiki(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public WikiDefinition createWiki(String location, String hash) throws WikiException {
		WikiDefinition wikiDefinition = new WikiDefinition();
		wikiDefinition.setLocation(location);
		wikiDefinition.setHash(hash);
		wikiDefinition.setCreatedBy(UserFacade.getName());
		wikiDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				wikiPersistenceManager.insert(connection, wikiDefinition);
				return wikiDefinition;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WikiException(e);
		}
	}

	/**
	 * Gets the wiki.
	 *
	 * @param location the location
	 * @return the wiki
	 * @throws WikiException the wiki exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.wiki.api.IWikiCoreService#getWiki(java.lang.String)
	 */
	@Override
	public WikiDefinition getWiki(String location) throws WikiException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return wikiPersistenceManager.find(connection, WikiDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WikiException(e);
		}
	}

	/**
	 * Removes the wiki.
	 *
	 * @param location the location
	 * @throws WikiException the wiki exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.wiki.api.IWikiCoreService#removeWiki(java.lang.String)
	 */
	@Override
	public void removeWiki(String location) throws WikiException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				wikiPersistenceManager.delete(connection, WikiDefinition.class, location);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WikiException(e);
		}
	}

	/**
	 * Update wiki.
	 *
	 * @param location the location
	 * @param hash the hash
	 * @throws WikiException the wiki exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.wiki.api.IWikiCoreService#updateWiki(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateWiki(String location, String hash) throws WikiException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				WikiDefinition wikiDefinition = getWiki(location);
				wikiDefinition.setHash(hash);
				wikiPersistenceManager.update(connection, wikiDefinition);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WikiException(e);
		}
	}

	/**
	 * Gets the wikis.
	 *
	 * @return the wikis
	 * @throws WikiException the wiki exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.wiki.api.IWikisCoreService#getWikis()
	 */
	@Override
	public List<WikiDefinition> getWikis() throws WikiException {
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				return wikiPersistenceManager.findAll(connection, WikiDefinition.class);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new WikiException(e);
		}
	}

	/**
	 * Exists wiki.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws WikiException the wiki exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.wiki.api.IWikiCoreService#existsWiki(java.lang.String)
	 */
	@Override
	public boolean existsWiki(String location) throws WikiException {
		return getWiki(location) != null;
	}

}
