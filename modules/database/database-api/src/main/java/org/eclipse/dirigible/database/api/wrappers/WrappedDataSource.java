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
package org.eclipse.dirigible.database.api.wrappers;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import javax.sql.DataSource;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WrappedDataSource of the standard JDBC {@link DataSource} object with added some additional capabilities..
 */
public class WrappedDataSource implements DataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(WrappedDataSource.class);

	private static final String DATABASE_NAME_HDB = "HDB";

	private String databaseName;

	private final DataSource originalDataSource;

	/**
	 * Wrapper of the default datasource provided by the underlying platform
	 * It has some fault tolerance features, which are not available by default in the popular JDBC drivers.
	 *
	 * @param originalDataSource the original data source
	 */
	public WrappedDataSource(DataSource originalDataSource) {
		this.originalDataSource = originalDataSource;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(null, null);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		Connection connection = originalDataSource.getConnection();

		if (this.databaseName == null) {
			this.databaseName = connection.getMetaData().getDatabaseProductName();
		}

		if (databaseName.equals(DATABASE_NAME_HDB)) {
			String userName = UserFacade.getName();
			LOGGER.debug("Setting APPLICATIONUSER:{} for connection: {}", userName, connection);
			connection.setClientInfo("APPLICATIONUSER", userName);

			LOGGER.debug("Setting XS_APPLICATIONUSER:{} for connection: {}", userName, connection);
			connection.setClientInfo("XS_APPLICATIONUSER", userName);
		}

		return connection;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getLogWriter()
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return originalDataSource.getLogWriter();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getLoginTimeout()
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		return originalDataSource.getLoginTimeout();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return originalDataSource.isWrapperFor(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
	 */
	@Override
	public void setLogWriter(PrintWriter arg0) throws SQLException {
		originalDataSource.setLogWriter(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#setLoginTimeout(int)
	 */
	@Override
	public void setLoginTimeout(int arg0) throws SQLException {
		originalDataSource.setLoginTimeout(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return originalDataSource.unwrap(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getParentLogger()
	 */
	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

}
