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
package org.eclipse.dirigible.database.h2;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.AbstractDatabase;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.api.wrappers.WrappedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * H2 Database adapter.
 */
public class H2Database extends AbstractDatabase {

	private static final Logger logger = LoggerFactory.getLogger(H2Database.class);

	/** The Constant TYPE. */
	public static final String NAME = "h2";

	/** The Constant TYPE. */
	public static final String TYPE = "local";

	/** The Constant DIRIGIBLE_DATABASE_H2_ROOT_FOLDER. */
	public static final String DIRIGIBLE_DATABASE_H2_ROOT_FOLDER = "DIRIGIBLE_DATABASE_H2_ROOT_FOLDER"; //$NON-NLS-1$

	/** The Constant DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT. */
	public static final String DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT = DIRIGIBLE_DATABASE_H2_ROOT_FOLDER + "_DEFAULT"; //$NON-NLS-1$

	/** The Constant DATASOURCES. */
	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<String, DataSource>());

	/**
	 * Constructor with default root folder - user.dir
	 *
	 * @throws H2DatabaseException
	 *             in case the database cannot be created
	 */
	public H2Database() throws H2DatabaseException {
		this(null);
	}

	/**
	 * Constructor with root folder parameter.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @throws H2DatabaseException
	 *             in case the database cannot be created
	 */
	public H2Database(String rootFolder) throws H2DatabaseException {
		logger.debug("Initializing the embedded H2 datasource...");

		initialize();

		logger.debug("Embedded H2 datasource initialized.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.api.IDatabase#initialize()
	 */
	@Override
	public void initialize() {
		Configuration.loadModuleConfig("/dirigible-database-h2.properties");
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
		dataSource = createDataSource(name);
		return dataSource;
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

	/**
	 * Creates the data source.
	 *
	 * @param name
	 *            the name
	 * @return the data source
	 */
	protected DataSource createDataSource(String name) {
		logger.debug("Creating an embedded H2 datasource...");

		synchronized (H2Database.class) {
			try {
				String h2Root = prepareRootFolder(name);
				
				String databaseDriver = Configuration.get("DIRIGIBLE_DATABASE_H2_DRIVER");
				String databaseUrl = Configuration.get("DIRIGIBLE_DATABASE_H2_URL");
				String databaseUsername = Configuration.get("DIRIGIBLE_DATABASE_H2_USERNAME");
				String databasePassword = Configuration.get("DIRIGIBLE_DATABASE_H2_PASSWORD");
				if ((databaseDriver != null) && (databaseUrl != null) && (databaseUsername != null) && (databasePassword != null)) {
					BasicDataSource basicDataSource = new BasicDataSource();
					basicDataSource.setDriverClassName(databaseDriver);
					basicDataSource.setUrl(databaseUrl + "/" + name);
					basicDataSource.setUsername(databaseUsername);
					basicDataSource.setPassword(databasePassword);
					basicDataSource.setDefaultAutoCommit(true);
					basicDataSource.setAccessToUnderlyingConnectionAllowed(true);
					logger.warn(String.format("Embedded H2 at: %s", h2Root));

					WrappedDataSource wrappedDataSource = new WrappedDataSource(basicDataSource);
					DATASOURCES.put(name, wrappedDataSource);
					return wrappedDataSource;
				} else {
					throw new H2DatabaseException("Invalid datasource parameters provided for H2 database");
				}
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new H2DatabaseException(e);
			}
		}
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
