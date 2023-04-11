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
package org.eclipse.dirigible.components.data.sources.manager;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The Class DataSourcesManager.
 */
@Component
public class DataSourcesManager implements InitializingBean {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DataSourcesManager.class);
	
	/** The instance. */
	private static DataSourcesManager INSTANCE;
	
	/** The Constant DATASOURCES. */
	private static final Map<String, javax.sql.DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<>());
	
	/** The datasource service. */
	private DataSourceService datasourceService;
	
	/**
	 * Instantiates a new data sources manager.
	 *
	 * @param datasourceService the datasource service
	 */
	@Autowired
	public DataSourcesManager(DataSourceService datasourceService) {
		this.datasourceService = datasourceService;
	}
	
	/**
	 * After properties set.
	 *
	 * @throws Exception the exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		INSTANCE = this;		
	}
	
	/**
	 * Gets the.
	 *
	 * @return the dirigible O data service factory
	 */
	public static DataSourcesManager get() {
        return INSTANCE;
    }
	
	/**
	 * Gets the data source.
	 *
	 * @param name the name
	 * @return the data source
	 */
	public javax.sql.DataSource getDataSource(String name) {
		javax.sql.DataSource dataSource = DATASOURCES.get(name);
		if (dataSource != null) {
			return dataSource;
		}
		dataSource = initializeDataSource(name);
		return dataSource;
	}
	
	/**
	 * Gets the default data source.
	 *
	 * @return the default data source
	 */
	public javax.sql.DataSource getDefaultDataSource() {
		return getDataSource(getDefaultDataSourceName());
	}
	
	/**
	 * Gets the system DB.
	 *
	 * @return the system DB
	 */
	public javax.sql.DataSource getSystemDataSource() {
		return getDataSource(getSystemDataSourceName());
	}

	/**
	 * Initialize data source.
	 *
	 * @param name the name
	 * @return the javax.sql. data source
	 */
	private javax.sql.DataSource initializeDataSource(String name) {
		if (logger.isInfoEnabled()) {logger.info("Initializing a datasource with name: " + name);}
		DataSource datasource;
		datasource = getDataSourceDefinition(name);
		try {
			prepareRootFolder(name);
		} catch (IOException e) {
			logger.error("Invalid configuration for the datasource: " + name);
		}
		Properties properties = new Properties();
		properties.put("driverClassName", datasource.getDriver());
		properties.put("jdbcUrl", datasource.getUrl());
		properties.put("dataSource.url", datasource.getUrl());
		properties.put("dataSource.user", datasource.getUsername());
		properties.put("dataSource.password", datasource.getPassword());
		properties.put("dataSource.logWriter", new PrintWriter(System.out));
		
		HikariConfig config = new HikariConfig(properties);
		config.setPoolName(name);
		config.setAutoCommit(true);
		datasource.getProperties().forEach(dsp -> config.addDataSourceProperty(dsp.getName(), dsp.getValue()));
		HikariDataSource hds = new HikariDataSource(config);
		
		ManagedDataSource wrappedDataSource = new ManagedDataSource(hds);
		DATASOURCES.put(name, wrappedDataSource);
		if (logger.isInfoEnabled()) {logger.info("Initialized a datasource with name: " + name);}
		return wrappedDataSource;	
	}

	/**
	 * Gets the data source definition.
	 *
	 * @param name the name
	 * @return the data source definition
	 */
	public DataSource getDataSourceDefinition(String name) {
		try {
			return datasourceService.findByName(name);
		} catch (Exception e) {
			if (DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT.equals(name)) {
				if (logger.isErrorEnabled()) {logger.error("DataSource cannot be initialized, hence fail over database is started as a backup - " + name);}
				return new DataSource(name, name, name, null, "org.h2.Driver", "jdbc:h2:~/DefaultDBFailOver", "sa", "");
			}
			throw e;
		}
	}
	
	/**
	 * Gets the default data source name.
	 *
	 * @return the default data source name
	 */
	public String getDefaultDataSourceName() {
		return Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT, DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
	}
	
	/**
	 * Gets the system data source name.
	 *
	 * @return the system data source name
	 */
	public String getSystemDataSourceName() {
		return Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM, DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM);
	}
	
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
		String rootFolder = (DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT.equals(name)) ? DatabaseParameters.DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT
				: DatabaseParameters.DIRIGIBLE_DATABASE_H2_ROOT_FOLDER + name;
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
	
	/**
	 * Adds the data source.
	 *
	 * @param name the name
	 * @param datasource the datasource
	 */
	public void addDataSource(String name, javax.sql.DataSource datasource) {
		DATASOURCES.put(name, datasource);
	}

}
