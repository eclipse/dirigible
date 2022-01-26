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
package org.eclipse.dirigible.database.managed;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.AbstractDatabase;
import org.eclipse.dirigible.database.api.IDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Managed Database
 */
public class ManagedDatabase extends AbstractDatabase {

	private static final Logger logger = LoggerFactory.getLogger(ManagedDatabase.class);

	/** The Constant NAME. */
	public static final String NAME = "managed";

	/** The Constant TYPE. */
	public static final String TYPE = "managed";

	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<String, DataSource>());

	private static final String JNDI_DEFAULT_PREFIX = "java:comp/env/jdbc/";

	/**
	 * The default constructor
	 */
	public ManagedDatabase() {
		logger.debug("Initializing the managed datasources...");

		initialize();

		logger.debug("Managed datasources initialized.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#initialize()
	 */
	@Override
	public void initialize() {
		Configuration.loadModuleConfig("/dirigible-database-managed.properties");
		String managedDataSourcesList = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_MANAGED_DATASOURCES);
		if ((managedDataSourcesList != null) && !"".equals(managedDataSourcesList)) {
			logger.trace("Managed datasources list: " + managedDataSourcesList);
			StringTokenizer tokens = new StringTokenizer(managedDataSourcesList, ",");
			while (tokens.hasMoreTokens()) {
				String name = tokens.nextToken();
				logger.info("Lookup a managed datasource with name: " + name);
				DataSource dataSource = lookupDataSource(name);
				DATASOURCES.put(name, dataSource);
			}
		} else {
			logger.warn("No managed datasources configured");
		}
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSource(java.lang.String)
	 */
	@Override
	public DataSource getDataSource(String name) {
		DataSource dataSource = DATASOURCES.get(name);
		if (dataSource != null) {
			return dataSource;
		}
		dataSource = lookupDataSource(name);
		DATASOURCES.put(name, dataSource);
		return dataSource;
	}

	/**
	 * Lookup data source.
	 *
	 * @param name
	 *            the name
	 * @return the data source
	 */
	private DataSource lookupDataSource(String name) {
		try {
			final InitialContext ctx = new InitialContext();
			if (name != null) {
				return (DataSource) ctx.lookup(JNDI_DEFAULT_PREFIX + name);
			}
			return null;
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
			throw new ManagedDatabaseException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getType()
	 */
	@Override
	public String getType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSources()
	 */
	@Override
	public Map<String, DataSource> getDataSources() {
		Map<String, DataSource> datasources = new HashMap<String, DataSource>();
		datasources.putAll(DATASOURCES);
		return datasources;
	}

}
