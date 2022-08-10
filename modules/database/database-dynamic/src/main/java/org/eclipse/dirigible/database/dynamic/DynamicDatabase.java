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
package org.eclipse.dirigible.database.dynamic;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.api.AbstractDatabase;
import org.eclipse.dirigible.database.api.wrappers.WrappedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The Dynamic Database.
 */
public class DynamicDatabase extends AbstractDatabase {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DynamicDatabase.class);

	/** The Constant NAME. */
	public static final String NAME = "basic";

	/** The Constant TYPE. */
	public static final String TYPE = "dynamic";

	/** The Constant DATASOURCES. */
	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<String, DataSource>());

	/**
	 * The default constructor.
	 */
	public DynamicDatabase() {
		logger.debug("Initializing the dynamic datasources...");

		initialize();

		logger.debug("Dynamic datasources initialized.");
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
		//Configuration.load("/dirigible-database-dynamic.properties");
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
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
		throw new IllegalArgumentException("Dynamic datasource has not been created: " + name);
	}

	/**
	 * Initialize a data source.
	 *
	 * @param name            the name
	 * @param databaseDriver the database driver
	 * @param databaseUrl the database url
	 * @param databaseUsername the database username
	 * @param databasePassword the database password
	 * @param databaseConnectionProperties the database connection properties
	 * @return the data source
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static DataSource createDataSource(String name, String databaseDriver, String databaseUrl, String databaseUsername, String databasePassword, String databaseConnectionProperties) throws IOException {
		DataSource dataSource = DATASOURCES.get(name);
		if (dataSource != null) {
			logger.warn(String.format("Dynamic datasource with name [%s] already exists.", name));
			return dataSource;
		}
		if ((databaseDriver != null) && (databaseUrl != null) && (databaseUsername != null) && (databasePassword != null)) {
			
			Properties props = new Properties();
			props.setProperty("driverClassName", databaseDriver);
			props.setProperty("dataSource.user", databaseUsername);
			props.setProperty("dataSource.password", databasePassword);
			props.setProperty("jdbcUrl", databaseUrl);
			props.setProperty("leakDetectionThreshold", "10000" );
			props.setProperty("poolName", name + "HikariPool");

			if (databaseConnectionProperties != null
					&& !"".equals(databaseConnectionProperties.trim())) {
				Properties definedProps = new Properties();
				definedProps.load(new StringReader(databaseConnectionProperties));
				Map<String, String> hikariProperties = getHikariProperties(definedProps);
				hikariProperties.forEach(props::setProperty);
			}

			HikariConfig config = new HikariConfig(props);
			HikariDataSource ds = new HikariDataSource(config);

			WrappedDataSource wrappedDataSource = new WrappedDataSource(ds);
			DATASOURCES.put(name, wrappedDataSource);
			return wrappedDataSource;
		}
		throw new IllegalArgumentException("Invalid configuration for the dynamic datasource: " + name);
	}
	
	/**
	 * Gets the hikari properties.
	 *
	 * @param definedProps the defined props
	 * @return the hikari properties
	 */
	private static Map<String, String> getHikariProperties(Properties definedProps) {
		Map<String, String> properties = new HashMap<>();
		String hikariDelimiter = "HIKARI_";
		int hikariDelimiterLength = hikariDelimiter.length();
		String[] array = new String[definedProps.keySet().size()];
		definedProps.keySet().toArray(array);
		Arrays.stream(array).filter(key -> key.startsWith(hikariDelimiter))
			.map(key -> key.substring(key.lastIndexOf(hikariDelimiter) + hikariDelimiterLength))
			.forEach(key -> properties.put(key, (String) definedProps.get(hikariDelimiter + key)));

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
		Map<String, DataSource> datasources = new HashMap<String, DataSource>();
		datasources.putAll(DATASOURCES);
		return datasources;
	}

}
