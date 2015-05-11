/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.logging.Logger;

public class WrappedDataSource implements DataSource {
	
	private static final Logger logger = Logger.getLogger(WrappedDataSource.class);
	
	private static final int MAX_CONNECTIONS_COUNT = 8;
	
	private DataSource originalDataSource;
	
	private static final Collection<WrappedConnection> connections = Collections.synchronizedCollection(new ArrayList<WrappedConnection>());

	// default timeout before kill the victim connection
	private static final long WAIT_TIMEOUT = 500;
	private static final int WAIT_COUNT = 5;

	public WrappedDataSource(DataSource originalDataSource) {
		super();
		this.originalDataSource = originalDataSource;
	}

	public Connection getConnection() throws SQLException {
		logger.debug("entring - getConnection()");
		checkConnections();
		WrappedConnection wrappedConnection = new WrappedConnection(originalDataSource.getConnection(), this);
		addConnection(wrappedConnection);
		wrappedConnection.setAutoCommit(false);
		logger.debug("Connection acquired: " + wrappedConnection.hashCode() + " count: " + connections.size());
		logger.debug("exiting - getConnection()");
		return wrappedConnection;
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		logger.debug("entring - getConnection(String username, String password)");
		checkConnections();
		WrappedConnection wrappedConnection = new WrappedConnection(originalDataSource.getConnection(username, password), this);
		addConnection(wrappedConnection);
		wrappedConnection.setAutoCommit(false);
		logger.debug("Connection acquired: " + wrappedConnection.hashCode() + " count: " + connections.size());
		logger.debug("exiting - getConnection(String username, String password)");
		return wrappedConnection;
	}

	private void checkConnections() throws SQLException {
		for (int i=0;i<WAIT_COUNT;i++) {
			if (connections.size() == MAX_CONNECTIONS_COUNT) {
				try {
					synchronized(this) {
						// wait some time and re-check  
						this.wait(WAIT_TIMEOUT);
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
	
	private void forceRelaseConnection() throws SQLException {
		logger.debug("entring - forceRelaseConnection()");
		WrappedConnection oldestConnection = null;
		for (WrappedConnection connection : connections) {
			if (oldestConnection == null) {
				oldestConnection = connection;
			}
			if (oldestConnection.getTimeAcquired() < connection.getTimeAcquired()) {
				oldestConnection = connection;
			}
		}
		if (oldestConnection != null) {
			logger.error("Potential connection leak; victim connection is: " + oldestConnection.hashCode() + ", used (ms): " + oldestConnection.getTimeUsed());
			oldestConnection.close();
		}
		logger.debug("exiting - forceRelaseConnection()");
	}

	private void addConnection(WrappedConnection connection) {
		logger.debug("entring - addConnection()");
		connections.add(connection);
		logger.debug("exiting - addConnection()");
	}
	
	private void removeConnection(WrappedConnection connection) {
		logger.debug("entring - removeConnection()");
		connections.remove(connection);
		logger.debug("exiting - removeConnection()");
	}
	
	public void closedConnection(WrappedConnection wrappedConnection) {
		logger.debug("entring - closeConnection()");
		removeConnection(wrappedConnection);
		logger.debug("Connection released: " + wrappedConnection.hashCode() + " count: " + connections.size() 
				+ " time used: " + wrappedConnection.getTimeUsed() + "ms");
		logger.debug("exiting - closeConnection()");
	}

	public PrintWriter getLogWriter() throws SQLException {
		logger.debug("called - getLogWriter()");
		return originalDataSource.getLogWriter();
	}

	public int getLoginTimeout() throws SQLException {
		logger.debug("called - getLoginTimeout()");
		return originalDataSource.getLoginTimeout();
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		logger.debug("called - isWrapperFor(Class<?> arg0)");
		return originalDataSource.isWrapperFor(arg0);
	}

	public void setLogWriter(PrintWriter arg0) throws SQLException {
		logger.debug("called - setLogWriter(PrintWriter arg0)");
		originalDataSource.setLogWriter(arg0);
	}

	public void setLoginTimeout(int arg0) throws SQLException {
		logger.debug("called - setLoginTimeout(int arg0)");
		originalDataSource.setLoginTimeout(arg0);
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		logger.debug("called - unwrap(Class<T> arg0)");
		return originalDataSource.unwrap(arg0);
	}
	
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		logger.debug("called - getParentLogger()");
		throw new SQLFeatureNotSupportedException();
	}

}
