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
package org.eclipse.dirigible.database.defined;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.AbstractDatabase;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.api.wrappers.WrappedDataSource;
import org.eclipse.dirigible.database.databases.api.DatabasesException;
import org.eclipse.dirigible.database.databases.api.IDatabasesCoreService;
import org.eclipse.dirigible.database.databases.definition.DatabaseDefinition;
import org.eclipse.dirigible.database.databases.service.DatabasesCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The Defined Database
 */
public class DefinedDatabase extends AbstractDatabase {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefinedDatabase.class);

	/** The Constant NAME. */
	public static final String NAME = "basic";

	/** The Constant TYPE. */
	public static final String TYPE = "defined";

	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<>());
	
	private IDatabasesCoreService databasesCoreService = new DatabasesCoreService();

	/**
	 * The default constructor
	 */
	public DefinedDatabase() {
		LOGGER.debug("Initializing the defined datasources...");

		initialize();

		LOGGER.debug("Defined datasources initialized.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#initialize()
	 */
	@Override
	public void initialize() {
		Configuration.loadModuleConfig("/dirigible-database-defined.properties");
		
//		try {
//			List<DatabaseDefinition> databases = databasesCoreService.getDatabases();
//			for (DatabaseDefinition database : databases) {
//				LOGGER.info("Initializing a defined datasource with name: " + database.getName());
//				initializeDataSource(database.getName());
//			}
//		} catch (DatabasesException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
		
		LOGGER.debug(this.getClass().getCanonicalName() + " module initialized.");
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
		
		try {
			DatabaseDefinition database = databasesCoreService.getDatabaseByName(name);
			
			if ((database != null) 
					&& (database.getDriver() != null) 
					&& (database.getUrl() != null) 
					&& (database.getUsername() != null) 
					&& (database.getPassword() != null)) {
				Properties props = new Properties();
				props.setProperty("driverClassName", database.getDriver() );
				props.setProperty("dataSource.user", database.getUsername() );
				props.setProperty("dataSource.password", database.getPassword() );
				props.setProperty("jdbcUrl", database.getUrl());
				props.setProperty("leakDetectionThreshold", "10000" );
				props.setProperty("poolName", name + "HikariPool");

				if (database.getParameters() != null
						&& !"".equals(database.getParameters().trim())) {
					Properties definedProps = new Properties();
					definedProps.load(new StringReader(database.getParameters()));
					Map<String, String> hikariProperties = getHikariProperties(definedProps);
					hikariProperties.forEach(props::setProperty);
				}

				HikariConfig config = new HikariConfig(props);
				HikariDataSource ds = new HikariDataSource(config);

				WrappedDataSource wrappedDataSource = new WrappedDataSource(ds);
				DATASOURCES.put(name, wrappedDataSource);
				return wrappedDataSource;
			}
			
		} catch (DatabasesException | IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		throw new IllegalArgumentException("Invalid configuration for the defined datasource: " + name);
	}

	private Map<String, String> getHikariProperties(Properties definedProps) {
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
		Map<String, DataSource> datasources = new HashMap<>();
		try {
			List<DatabaseDefinition> databases = databasesCoreService.getDatabases();
			for (DatabaseDefinition database : databases) {
				LOGGER.info("Initializing a defined datasource with name: " + database.getName());
				initializeDataSource(database.getName());
			}
		} catch (DatabasesException e) {
			LOGGER.error(e.getMessage(), e);
		}
		datasources.putAll(DATASOURCES);
		return datasources;
	}

}
