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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WrappedDataSource of the standard JDBC {@link DataSource} object with added some additional capabilities..
 */
public class WrappedDataSource implements DataSource {

	private static final Logger logger = LoggerFactory.getLogger(WrappedDataSource.class);

	private static final String DATABASE_NAME_HDB = "HDB";

	private String databaseName;

	private DataSource originalDataSource;

	private static final Collection<WrappedConnection> connections = Collections.synchronizedCollection(new ArrayList<WrappedConnection>());

	private static int MAX_CONNECTIONS_COUNT = 8;

	// default timeout before kill the victim connection
	private static long WAIT_TIMEOUT = 500;

	private static int WAIT_COUNT = 5;

	private static boolean AUTO_COMMIT_ENABLED = false;

	{
		initAutoCommitEnabled();
		initMaxConnectionsCount();
		initWaitTimeout();
		initWaitCount();
	}

	/**
	 * Wrapper of the default datasource provided by the underlying platform
	 * It has some fault tolerance features, which are not available by default in the popular JDBC drivers.
	 *
	 * @param originalDataSource the original data source
	 */
	public WrappedDataSource(DataSource originalDataSource) {
		super();
		this.originalDataSource = originalDataSource;
	}

	/**
	 * Inits the auto commit enabled.
	 */
	protected void initAutoCommitEnabled() {
		String param = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT);
		if (param != null) {
			AUTO_COMMIT_ENABLED = Boolean.parseBoolean(param);
		}
	}

	/**
	 * Inits the max connections count.
	 */
	protected void initMaxConnectionsCount() {
		String param = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT);
		if (param != null) {
			MAX_CONNECTIONS_COUNT = Integer.parseInt(param);
		}
	}

	/**
	 * Inits the wait timeout.
	 */
	protected void initWaitTimeout() {
		String param = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DEFAULT_WAIT_TIMEOUT);
		if (param != null) {
			WAIT_TIMEOUT = Long.parseLong(param);
		}
	}

	/**
	 * Inits the wait count.
	 */
	protected void initWaitCount() {
		String param = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DEFAULT_WAIT_COUNT);
		if (param != null) {
			WAIT_COUNT = Integer.parseInt(param);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		logger.trace("entering - getConnection()");

		return getConnection(null, null);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		logger.trace("entering - getConnection(String username, String password)");
		checkConnections();
		Connection connection = username == null && password == null ?
				originalDataSource.getConnection() : originalDataSource.getConnection(username, password);

		WrappedConnection wrappedConnection = new WrappedConnection(connection, this);

		if (this.databaseName == null) {
			this.databaseName = wrappedConnection.getMetaData().getDatabaseProductName();
		}

		if (databaseName.equals(DATABASE_NAME_HDB)) {
			wrappedConnection.setClientInfo("APPLICATIONUSER", UserFacade.getName());
		}

		addConnection(wrappedConnection);
		wrappedConnection.setAutoCommit(AUTO_COMMIT_ENABLED);
		logger.trace("Connection acquired: " + wrappedConnection.hashCode() + " count: " + connections.size());
		logger.trace("exiting - getConnection(String username, String password)");
		return wrappedConnection;
	}

	/**
	 * Check connections.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void checkConnections() throws SQLException {
		for (int i = 0; i < WAIT_COUNT; i++) {
			if (connections.size() == MAX_CONNECTIONS_COUNT) {
				try {
					synchronized (this) {
						while (getOldestConnection().getTimeUsed() < (WAIT_TIMEOUT * WAIT_COUNT)) {
							// wait some time and re-check
							this.wait(WAIT_TIMEOUT);
						}
					}
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			} else {
				return;
			}
		}
		forceRelaseConnection();
	}

	/**
	 * Force relase connection.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void forceRelaseConnection() throws SQLException {
		logger.trace("entering - forceRelaseConnection()");
		WrappedConnection oldestConnection = getOldestConnection();
		if (oldestConnection != null) {
			logger.error("Potential connection leak; victim connection is: " + oldestConnection.hashCode() + ", used (ms): "
					+ oldestConnection.getTimeUsed());
			logger.error(oldestConnection.getOperationalInfo());
			oldestConnection.close();
		}
		logger.trace("exiting - forceRelaseConnection()");
	}

	/**
	 * Gets the oldest connection.
	 *
	 * @return the oldest connection
	 */
	protected WrappedConnection getOldestConnection() {
		WrappedConnection oldestConnection = null;
		synchronized (connections) {
			for (WrappedConnection connection : connections) {
				if (oldestConnection == null) {
					oldestConnection = connection;
				}
				if (oldestConnection.getTimeAcquired() < connection.getTimeAcquired()) {
					oldestConnection = connection;
				}
			}
		}
		return oldestConnection;
	}

	/**
	 * Adds the connection.
	 *
	 * @param connection the connection
	 */
	private void addConnection(WrappedConnection connection) {
		logger.trace("entering - addConnection()");
		String operationalInfo = getOperationalInfo();
		connection.setOperationalInfo(operationalInfo);
		connections.add(connection);
		logger.trace("exiting - addConnection()");
	}

	/**
	 * Gets the operational info.
	 *
	 * @return the operational info
	 */
	private String getOperationalInfo() {
		StringBuilder buff = new StringBuilder();
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			buff.append(element.toString()).append(System.getProperty("line.separator"));
		}
		String operationalInfo = buff.toString();
		return operationalInfo;
	}

	/**
	 * Removes the connection.
	 *
	 * @param connection the connection
	 */
	private void removeConnection(WrappedConnection connection) {
		logger.trace("entering - removeConnection()");
		connections.remove(connection);
		logger.trace("exiting - removeConnection()");
	}

	/**
	 * Closed connection.
	 *
	 * @param wrappedConnection the wrapped connection
	 */
	public void closedConnection(WrappedConnection wrappedConnection) {
		logger.trace("entering - closeConnection()");
		removeConnection(wrappedConnection);
		logger.trace("Connection released: " + wrappedConnection.hashCode() + " count: " + connections.size() + " time used: "
				+ wrappedConnection.getTimeUsed() + "ms");
		logger.trace("exiting - closeConnection()");
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getLogWriter()
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		logger.debug("called - getLogWriter()");
		return originalDataSource.getLogWriter();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getLoginTimeout()
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		logger.debug("called - getLoginTimeout()");
		return originalDataSource.getLoginTimeout();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		logger.debug("called - isWrapperFor(Class<?> arg0)");
		return originalDataSource.isWrapperFor(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
	 */
	@Override
	public void setLogWriter(PrintWriter arg0) throws SQLException {
		logger.debug("called - setLogWriter(PrintWriter arg0)");
		originalDataSource.setLogWriter(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#setLoginTimeout(int)
	 */
	@Override
	public void setLoginTimeout(int arg0) throws SQLException {
		logger.debug("called - setLoginTimeout(int arg0)");
		originalDataSource.setLoginTimeout(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		logger.debug("called - unwrap(Class<T> arg0)");
		return originalDataSource.unwrap(arg0);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getParentLogger()
	 */
	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		logger.debug("called - getParentLogger()");
		throw new SQLFeatureNotSupportedException();
	}

}
