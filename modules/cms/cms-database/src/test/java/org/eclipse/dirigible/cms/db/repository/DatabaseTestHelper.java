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
package org.eclipse.dirigible.cms.db.repository;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

public class DatabaseTestHelper {

	/**
	 * Creates the data source.
	 *
	 * @param name
	 *            the name
	 * @return the data source
	 * @throws Exception
	 *             the exception
	 */
	public static DataSource createDataSource(String name) throws Exception {
		try {
			Properties databaseProperties = new Properties();
			InputStream in = DatabaseTestHelper.class.getResourceAsStream("/database.properties");
			if (in != null) {
				try {
					databaseProperties.load(in);
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}
			String database = System.getProperty("database");
			if (database == null) {
				database = "derby";
			}

			if ("derby".equals(database)) {
				DataSource embeddedDataSource = new EmbeddedDataSource();
				String derbyRoot = prepareRootFolder(name);
				((EmbeddedDataSource) embeddedDataSource).setDatabaseName(derbyRoot);
				((EmbeddedDataSource) embeddedDataSource).setCreateDatabase("create");
				((EmbeddedDataSource) embeddedDataSource).setConnectionAttributes("name=" + name);
				return embeddedDataSource;
			}
			BasicDataSource basicDataSource = new BasicDataSource();
			String databaseDriver = databaseProperties.getProperty(database + ".driver");
			basicDataSource.setDriverClassName(databaseDriver);
			String databaseUrl = databaseProperties.getProperty(database + ".url");
			basicDataSource.setUrl(databaseUrl);
			String databaseUsername = databaseProperties.getProperty(database + ".username");
			basicDataSource.setUsername(databaseUsername);
			String databasePassword = databaseProperties.getProperty(database + ".password");
			basicDataSource.setPassword(databasePassword);
			String defaultQueryTimeout = databaseProperties.getProperty(database + ".timeout");
			int databaseTimeout = (defaultQueryTimeout != null) ? Integer.parseInt(defaultQueryTimeout) : 60;
			basicDataSource.setDefaultQueryTimeout(databaseTimeout);
			basicDataSource.setDefaultAutoCommit(true);
			basicDataSource.setAccessToUnderlyingConnectionAllowed(true);

			return basicDataSource;

		} catch (IOException e) {
			throw new Exception(e);
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
	private static String prepareRootFolder(String name) throws IOException {
		File rootFile = new File(name);
		File parentFile = rootFile.getCanonicalFile().getParentFile();
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				throw new IOException(format("Creation of the root folder [{0}] of the embedded Derby database failed.", name));
			}
		}
		return name;
	}

}
