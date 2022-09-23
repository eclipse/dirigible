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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.AbstractDatabase;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.api.wrappers.WrappedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Custom Database.
 */
public class CustomDatabase extends AbstractDatabase {

	/** The Constant LOGGER. */
	private static final Logger logger = LoggerFactory.getLogger(CustomDatabase.class);

	/** The Constant NAME. */
	public static final String NAME = "basic";

	/** The Constant TYPE. */
	public static final String TYPE = "custom";

	/** The Constant DATASOURCES. */
	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<>());

	/**
	 * The default constructor.
	 */
	public CustomDatabase() {
		if (logger.isDebugEnabled()) {logger.debug("Initializing the custom datasources...");}

		initialize();

		if (logger.isDebugEnabled()) {logger.debug("Custom datasources initialized.");}
	}

	/**
	 * Initialize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#initialize()
	 */
	@Override
	public void initialize() {
		Configuration.loadModuleConfig("/dirigible-database-custom.properties");
		String customDataSourcesList = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES);
		if ((customDataSourcesList != null) && !"".equals(customDataSourcesList)) {
			if (logger.isTraceEnabled()) {logger.trace("Custom datasources list: " + customDataSourcesList);}
			StringTokenizer tokens = new StringTokenizer(customDataSourcesList, ",");
			while (tokens.hasMoreTokens()) {
				String name = tokens.nextToken();
				if (logger.isInfoEnabled()) {logger.info("Initializing a custom datasource with name: " + name);}
				initializeDataSource(name);
			}
		} else {
			if (logger.isTraceEnabled()) {logger.trace("No custom datasources configured");}
		}
		if (logger.isDebugEnabled()) {logger.debug(this.getClass().getCanonicalName() + " module initialized.");}
	}

	

	/**
	 * Gets the data source.
	 *
	 * @param name the name
	 * @return the data source
	 */
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
		if ((databaseDriver != null) && (databaseUrl != null) && (databaseUsername != null) && (databasePassword != null)) {
			Properties props = new Properties();
			props.setProperty("driverClassName", databaseDriver );
			props.setProperty("dataSource.user", databaseUsername );
			props.setProperty("dataSource.password", databasePassword );
			props.setProperty("jdbcUrl", databaseUrl);
			props.setProperty("leakDetectionThreshold", "10000" );
			props.setProperty("poolName", name + "HikariPool");

			Map<String, String> hikariProperties = getHikariProperties(name);
			hikariProperties.forEach(props::setProperty);

			HikariConfig config = new HikariConfig(props);
			HikariDataSource ds = new HikariDataSource(config);

			WrappedDataSource wrappedDataSource = new WrappedDataSource(ds);
			DATASOURCES.put(name, wrappedDataSource);
			return wrappedDataSource;
		}

		throw new IllegalArgumentException("Invalid configuration for the custom datasource: " + name);
	}

	/**
	 * Gets the hikari properties.
	 *
	 * @param databaseName the database name
	 * @return the hikari properties
	 */
	private Map<String, String> getHikariProperties(String databaseName) {
		Map<String, String> properties = new HashMap<>();
		String hikariDelimiter = "_HIKARI_";
		String databaseKeyPrefix = databaseName + hikariDelimiter;
		int hikariDelimiterLength = hikariDelimiter.length();
		Arrays.stream(Configuration.getKeys()).filter(key -> key.startsWith(databaseKeyPrefix))//
			.map(key -> key.substring(key.lastIndexOf(hikariDelimiter) + hikariDelimiterLength)).forEach(key -> properties.put(key, Configuration.get(databaseKeyPrefix + key)));

		return properties;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getType()
	 */
	@Override
	public String getType() {
		return TYPE;
	}

	/**
	 * Gets the data sources.
	 *
	 * @return the data sources
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#getDataSources()
	 */
	@Override
	public Map<String, DataSource> getDataSources() {
		Map<String, DataSource> datasources = new HashMap<>();
		datasources.putAll(DATASOURCES);
		return datasources;
	}

}
