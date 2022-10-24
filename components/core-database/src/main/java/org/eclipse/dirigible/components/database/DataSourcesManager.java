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
package org.eclipse.dirigible.components.database;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.api.wrappers.WrappedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourcesManager {

	/** The Constant LOGGER. */
	private static final Logger logger = LoggerFactory.getLogger(DataSourcesManager.class);

	// Custom DataSources

	/** The Constant DATASOURCES. */
	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<>());

	public void initialize() {
		String customDataSourcesList = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES);
		if ((customDataSourcesList != null) && !"".equals(customDataSourcesList)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Custom datasources list: " + customDataSourcesList);
			}
			StringTokenizer tokens = new StringTokenizer(customDataSourcesList, ",");
			while (tokens.hasMoreTokens()) {
				String name = tokens.nextToken();
				if (logger.isInfoEnabled()) {
					logger.info("Initializing a custom datasource with name: " + name);
				}
				initializeDataSource(name);
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("No custom datasources configured");
			}
			createLocalDataSource(getDefaultDataSourceName());
		}
		if (logger.isDebugEnabled()) {
			logger.debug(this.getClass().getCanonicalName() + " module initialized.");
		}
	}

	/**
	 * Gets the data source.
	 *
	 * @param name the name
	 * @return the data source
	 */
	public DataSource getDataSource(String name) {
		DataSource dataSource = DATASOURCES.get(name);
		if (dataSource != null) {
			return dataSource;
		}
		dataSource = initializeDataSource(name);
		return dataSource;
	}
	
	public DataSource getDefaultDataSource() {
		DataSource dataSource = DATASOURCES.get(getDefaultDataSourceName());
		if (dataSource != null) {
			return dataSource;
		}
		dataSource = initializeDataSource(getDefaultDataSourceName());
		if (dataSource == null) {
			dataSource = createLocalDataSource(getDefaultDataSourceName());
		}
		return dataSource;
	}

	/**
	 * Initialize data source.
	 *
	 * @param name the name
	 * @return the data source
	 */
	private DataSource initializeDataSource(String name) {
		String databaseDriver = Configuration.get(name + "_DRIVER");
		String databaseUrl = Configuration.get(name + "_URL");
		String databaseUsername = Configuration.get(name + "_USERNAME");
		String databasePassword = Configuration.get(name + "_PASSWORD");
		if ((databaseDriver != null) && (databaseUrl != null) && (databaseUsername != null)
				&& (databasePassword != null)) {
			Properties props = new Properties();
			props.setProperty("driverClassName", databaseDriver);
			props.setProperty("dataSource.user", databaseUsername);
			props.setProperty("dataSource.password", databasePassword);
			props.setProperty("jdbcUrl", databaseUrl);
			props.setProperty("leakDetectionThreshold", "10000");
			props.setProperty("poolName", name + "HikariPool");

			Map<String, String> hikariProperties = getHikariProperties(name);
			hikariProperties.forEach(props::setProperty);

			HikariConfig config = new HikariConfig(props);
			HikariDataSource ds = new HikariDataSource(config);

			WrappedDataSource wrappedDataSource = new WrappedDataSource(ds);
			DATASOURCES.put(name, wrappedDataSource);
			return wrappedDataSource;
		}

		logger.error("Invalid configuration for the custom datasource: " + name);
		return null;
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
				.map(key -> key.substring(key.lastIndexOf(hikariDelimiter) + hikariDelimiterLength))
				.forEach(key -> properties.put(key, Configuration.get(databaseKeyPrefix + key)));

		return properties;
	}
	
	/**
	 * Creates the data source.
	 *
	 * @param name
	 *            the name
	 * @return the data source
	 */
	protected DataSource createLocalDataSource(String name) {
		if (logger.isDebugEnabled()) {logger.debug("Creating an embedded H2 datasource...");}
		synchronized (DataSourcesManager.class) {
			try {
				Configuration.loadModuleConfig("/dirigible-database-h2.properties");
				String h2Root = prepareRootFolder(name);
				String databaseUrl = Configuration.get("DIRIGIBLE_DATABASE_H2_URL");
				String databaseUsername = Configuration.get("DIRIGIBLE_DATABASE_H2_USERNAME");
				String databasePassword = Configuration.get("DIRIGIBLE_DATABASE_H2_PASSWORD");
				
				String databaseTimeout = Configuration.get("DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT", "180000");
				int timeout = 180000;
				try {
					timeout = Integer.parseInt(databaseTimeout);
				} catch (NumberFormatException e) {
					timeout = 180000;
				}
				
				if ((databaseUrl != null) && (databaseUsername != null) && (databasePassword != null)) {
					HikariConfig config = new HikariConfig();

					config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
					config.setPoolName("H2DBHikariPool");
					config.setConnectionTestQuery("VALUES 1");
					config.addDataSourceProperty("URL", databaseUrl + "/" + name);
					config.addDataSourceProperty("user", databaseUsername);
					config.addDataSourceProperty("password", databasePassword);
					config.setMinimumIdle(5);
					config.setMaximumPoolSize(50);
					config.setConnectionTimeout(timeout);
					config.setLeakDetectionThreshold(timeout);
					config.setIdleTimeout(600000);
					config.setMaxLifetime(1800000);
					config.setAutoCommit(true);

					HikariDataSource ds = new HikariDataSource(config);

					if (logger.isWarnEnabled()) {logger.warn("Embedded H2 at: {}", h2Root);}

					DATASOURCES.put(name, ds);
					return ds;
				} else {
					throw new RuntimeException("Invalid datasource parameters provided for H2 database");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/** The Constant DIRIGIBLE_DATABASE_H2_ROOT_FOLDER. */
	public static final String DIRIGIBLE_DATABASE_H2_ROOT_FOLDER = "DIRIGIBLE_DATABASE_H2_ROOT_FOLDER"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT. */
	public static final String DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT = DIRIGIBLE_DATABASE_H2_ROOT_FOLDER + "_DEFAULT"; //$NON-NLS-1$

	/**
	 * Prepare root folder.
	 *
	 * @param name
	 *            the name
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String prepareRootFolder(String name) throws IOException {
		// TODO validate name parameter
		// TODO get by name from Configuration

		String rootFolder = (IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT.equals(name)) ? DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT
				: DIRIGIBLE_DATABASE_H2_ROOT_FOLDER + name;
		String h2Root = Configuration.get(rootFolder, name);
		File rootFile = new File(h2Root);
		File parentFile = rootFile.getCanonicalFile().getParentFile();
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				throw new IOException(format("Creation of the root folder [{0}] of the embedded H2 database failed.", h2Root));
			}
		}
		return h2Root;
	}
	
	public String getDefaultDataSourceName() {
		return Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT, IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
	}

}
