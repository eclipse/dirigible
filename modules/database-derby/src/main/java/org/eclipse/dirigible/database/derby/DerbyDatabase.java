/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.derby;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.api.wrappers.WrappedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The File System based Local Repository implementation of {@link IRepository}
 */
public class DerbyDatabase implements IDatabase {

	private static final Logger logger = LoggerFactory.getLogger(DerbyDatabase.class);

	public static final String TYPE = "derby";
	public static final String DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER = "DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER"; //$NON-NLS-1$
	public static final String DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER_DEFAULT = DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER + "_DEFAULT"; //$NON-NLS-1$

	private static final Map<String, DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<String, DataSource>());

	/**
	 * Constructor with default root folder - user.dir
	 *
	 * @throws DerbyDatabaseException
	 *             in case the database cannot be created
	 */
	public DerbyDatabase() throws DerbyDatabaseException {
		this(null);
	}

	/**
	 * Constructor with root folder parameter
	 *
	 * @param rootFolder
	 *            the root folder
	 * @throws DerbyDatabaseException
	 *             in case the database cannot be created
	 */
	public DerbyDatabase(String rootFolder) throws DerbyDatabaseException {
		logger.debug("Initializing the embedded Derby datasource...");

		initialize();

		logger.debug("Embedded Derby datasource initialized.");
	}

	@Override
	public void initialize() {
		Configuration.load("/dirigible-database-derby.properties");
		logger.debug(this.getClass().getCanonicalName() + " module initialized.");
	}

	@Override
	public DataSource getDataSource() {
		return getDataSource(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
	}

	@Override
	public DataSource getDataSource(String name) {
		DataSource dataSource = DATASOURCES.get(name);
		if (dataSource != null) {
			return dataSource;
		}
		if (DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT.equals(name) || DIRIGIBLE_DATABASE_DATASOURCE_TEST.equals(name)) {
			dataSource = createDataSource(name);
			return dataSource;
		}
		return null;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	protected DataSource createDataSource(String name) {
		logger.debug("Creating an embedded Derby datasource...");

		synchronized (DerbyDatabase.class) {
			try {
				DataSource dataSource = new EmbeddedDataSource();
				String derbyRoot = prepareRootFolder(name);
				((EmbeddedDataSource) dataSource).setDatabaseName(derbyRoot);
				((EmbeddedDataSource) dataSource).setCreateDatabase("create");
				logger.warn(String.format("Embedded Derby at: %s", derbyRoot));

				WrappedDataSource wrappedDataSource = new WrappedDataSource(dataSource);
				DATASOURCES.put(name, wrappedDataSource);
				return wrappedDataSource;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new DerbyDatabaseException(e);
			}
		}
	}

	private String prepareRootFolder(String name) throws IOException {
		// TODO validate name parameter
		// TODO get by name form Configuration

		String rootFolder = (IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT.equals(name)) ? DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER_DEFAULT
				: DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER + name;
		String derbyRoot = Configuration.get(rootFolder, name);
		File rootFile = new File(derbyRoot);
		File parentFile = rootFile.getCanonicalFile().getParentFile();
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				throw new IOException(format("Creation of the root folder [{0}] of the embedded Derby database failed.", derbyRoot));
			}
		}
		return derbyRoot;
	}

	@Override
	public Map<String, DataSource> getDataSources() {
		Map<String, DataSource> datasources = new HashMap<String, DataSource>();
		datasources.putAll(DATASOURCES);
		return datasources;
	}

}
