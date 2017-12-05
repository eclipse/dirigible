/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.custom;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Custom Database
 */
public class CustomDatabase implements IDatabase {

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
		Configuration.load("/dirigible-database-custom.properties");
		String customDataSourcesList = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES);
		if ((customDataSourcesList != null) && !"".equals(customDataSourcesList)) {
			logger.trace("Custom datasources list: " + customDataSourcesList);
			StringTokenizer tokens = new StringTokenizer(customDataSourcesList, ",");
			while (tokens.hasMoreTokens()) {
				String name = tokens.nextToken();
				logger.info("Initializing a custom datasource with name: " + name);
				DataSource dataSource = initializeDataSource(name);
				DATASOURCES.put(name, dataSource);
			}
		} else {
			logger.trace("No custom datasources configured");
		}
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {
		return getDataSource(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
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
		DATASOURCES.put(name, dataSource);
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
		if ((databaseDriver != null) && (databaseUrl != null) && (databaseUsername != null) && (databasePassword != null)) {
			BasicDataSource basicDataSource = new BasicDataSource();
			basicDataSource.setDriverClassName(databaseDriver);
			basicDataSource.setUrl(databaseUrl);
			basicDataSource.setUsername(databaseUsername);
			basicDataSource.setPassword(databasePassword);
			basicDataSource.setDefaultAutoCommit(true);
			return basicDataSource;
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
