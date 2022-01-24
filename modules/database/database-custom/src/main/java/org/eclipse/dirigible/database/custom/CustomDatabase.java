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
package org.eclipse.dirigible.database.custom;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.AbstractDatabase;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.api.wrappers.WrappedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Custom Database
 */
public class CustomDatabase extends AbstractDatabase {

	private static final Logger logger = LoggerFactory.getLogger(CustomDatabase.class);

	/** The Constant NAME. */
	public static final String NAME = "basic";

	/** The Constant TYPE. */
	public static final String TYPE = "custom";

	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<String, DataSource>());

	/**
	 * The default constructor
	 */
	public CustomDatabase() {
		logger.debug("Initializing the custom datasources...");

		initialize();

		logger.debug("Custom datasources initialized.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#initialize()
	 */
	@Override
	public void initialize() {
		Configuration.loadModuleConfig("/dirigible-database-custom.properties");
		String customDataSourcesList = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES);
		if ((customDataSourcesList != null) && !"".equals(customDataSourcesList)) {
			logger.trace("Custom datasources list: " + customDataSourcesList);
			StringTokenizer tokens = new StringTokenizer(customDataSourcesList, ",");
			while (tokens.hasMoreTokens()) {
				String name = tokens.nextToken();
				logger.info("Initializing a custom datasource with name: " + name);
				initializeDataSource(name);
			}
		} else {
			logger.trace("No custom datasources configured");
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
		dataSource = initializeDataSource(name);
		return dataSource;
	}

	/**
	 * Initialize data source.
	 *
	 * @param name
	 *            the name
	 * @return the data source
	 */
	private DataSource initializeDataSource(String name) {
		String databaseDriver = Configuration.get(name + "_DRIVER");
		String databaseUrl = Configuration.get(name + "_URL");
		String databaseUsername = Configuration.get(name + "_USERNAME");
		String databasePassword = Configuration.get(name + "_PASSWORD");
		int databaseTimeout = Integer.parseInt(Configuration.get(name + "_TIMEOUT", "60"));
		String databaseConnectionProperties = Configuration.get(name + "_CONNECTION_PROPERTIES");
		if ((databaseDriver != null) && (databaseUrl != null) && (databaseUsername != null) && (databasePassword != null)) {
			BasicDataSource basicDataSource = new BasicDataSource();
			basicDataSource.setDriverClassName(databaseDriver);
			basicDataSource.setUrl(databaseUrl);
			basicDataSource.setUsername(databaseUsername);
			basicDataSource.setPassword(databasePassword);
			basicDataSource.setDefaultAutoCommit(true);
			basicDataSource.setAccessToUnderlyingConnectionAllowed(true);
			basicDataSource.setDefaultQueryTimeout(databaseTimeout);
			if (databaseConnectionProperties != null && !databaseConnectionProperties.isEmpty()) {
				basicDataSource.setConnectionProperties(databaseConnectionProperties);
			}
			WrappedDataSource wrappedDataSource = new WrappedDataSource(basicDataSource);
			DATASOURCES.put(name, wrappedDataSource);
			return wrappedDataSource;
		}
		throw new IllegalArgumentException("Invalid configuration for the custom datasource: " + name);
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
