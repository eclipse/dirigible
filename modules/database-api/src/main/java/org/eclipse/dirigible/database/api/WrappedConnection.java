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

package org.eclipse.dirigible.database.api;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappedConnection implements Connection {

	private static final Logger logger = LoggerFactory.getLogger(WrappedConnection.class);

	private Connection originalConnection;

	private long timeAcquired;

	private WrappedDataSource dataSource;

	public WrappedConnection(Connection originalConnection, WrappedDataSource dataSource) {
		super();
		this.originalConnection = originalConnection;
		this.timeAcquired = GregorianCalendar.getInstance().getTime().getTime();
		this.dataSource = dataSource;
	}

	public long getTimeAcquired() {
		logger.trace("called - getTimeAcquired(): " + timeAcquired);
		return timeAcquired;
	}

	public long getTimeUsed() {
		long timeUsed = GregorianCalendar.getInstance().getTime().getTime() - timeAcquired;
		logger.trace("called - getTimeUsed(): " + timeUsed);
		return timeUsed;
	}

	public void clearWarnings() throws SQLException {
		logger.trace("entering - clearWarnings()");
		originalConnection.clearWarnings();
		logger.trace("exiting - clearWarnings()");
	}

	public void close() throws SQLException {
		logger.trace("entering - close()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.commit();
		}
		originalConnection.close();
		dataSource.closedConnection(this);
		logger.trace("exiting - close()");
	}

	public void commit() throws SQLException {
		logger.trace("entering - commit()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.commit();
		}
		logger.trace("exiting - commit()");
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		logger.trace("called - createArrayOf(String typeName, Object[] elements)");
		return originalConnection.createArrayOf(typeName, elements);
	}

	public Blob createBlob() throws SQLException {
		logger.trace("called - createBlob()");
		return originalConnection.createBlob();
	}

	public Clob createClob() throws SQLException {
		logger.trace("called - createClob()");
		return originalConnection.createClob();
	}

	public NClob createNClob() throws SQLException {
		logger.trace("called - createNClob()");
		return originalConnection.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		logger.trace("called - createSQLXML()");
		return originalConnection.createSQLXML();
	}

	public Statement createStatement() throws SQLException {
		logger.trace("called - createStatement()");
		return originalConnection.createStatement();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		logger.trace("called - createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
		return originalConnection.createStatement(resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		logger.trace("called - createStatement(int resultSetType, int resultSetConcurrency)");
		return originalConnection.createStatement(resultSetType, resultSetConcurrency);
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		logger.trace("called - createStatement(String typeName, Object[] attributes)");
		return originalConnection.createStruct(typeName, attributes);
	}

	public boolean getAutoCommit() throws SQLException {
		logger.trace("called - getAutoCommit()");
		return originalConnection.getAutoCommit();
	}

	public String getCatalog() throws SQLException {
		logger.trace("called - getCatalog()");
		return originalConnection.getCatalog();
	}

	public Properties getClientInfo() throws SQLException {
		logger.trace("called - getClientInfo()");
		return originalConnection.getClientInfo();
	}

	public String getClientInfo(String name) throws SQLException {
		logger.trace("called - getClientInfo(String name)");
		return originalConnection.getClientInfo(name);
	}

	public int getHoldability() throws SQLException {
		logger.trace("called - getHoldability()");
		return originalConnection.getHoldability();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		logger.trace("called - getMetaData()");
		return originalConnection.getMetaData();
	}

	public int getTransactionIsolation() throws SQLException {
		logger.trace("called - getTransactionIsolation()");
		return originalConnection.getTransactionIsolation();
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		logger.trace("called - getTypeMap()");
		return originalConnection.getTypeMap();
	}

	public SQLWarning getWarnings() throws SQLException {
		logger.trace("called - getWarnings()");
		return originalConnection.getWarnings();
	}

	public boolean isClosed() throws SQLException {
		logger.trace("called - isClosed()");
		return originalConnection.isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		logger.trace("called - isReadOnly()");
		return originalConnection.isReadOnly();
	}

	public boolean isValid(int timeout) throws SQLException {
		logger.trace("called - isValid(int timeout)");
		return originalConnection.isValid(timeout);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		logger.trace("called - isWrapperFor(Class<?> iface)");
		return originalConnection.isWrapperFor(iface);
	}

	public String nativeSQL(String sql) throws SQLException {
		logger.trace("called - nativeSQL(String sql): " + sql);
		return originalConnection.nativeSQL(sql);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		logger.trace("called - prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability): "
				+ sql);
		return originalConnection.prepareCall(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		logger.trace("called - prepareCall(String sql, int resultSetType, int resultSetConcurrency): "
				+ sql);
		return originalConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		logger.trace("called - prepareCall(String sql): " + sql);
		return originalConnection.prepareCall(sql);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability): "
				+ sql);
		return originalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int resultSetType, int resultSetConcurrency): "
				+ sql);
		return originalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		logger.trace("called - prepareStatement(String sql, int autoGeneratedKeys): " + sql);
		return originalConnection.prepareStatement(sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int[] columnIndexes): " + sql);
		return originalConnection.prepareStatement(sql, columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		logger.trace("called - prepareStatement(String sql, String[] columnNames): " + sql);
		return originalConnection.prepareStatement(sql, columnNames);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		logger.trace("called - prepareStatement(String sql): " + sql);
		return originalConnection.prepareStatement(sql);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		logger.trace("called - releaseSavepoint(Savepoint savepoint)");
		originalConnection.releaseSavepoint(savepoint);
	}

	public void rollback() throws SQLException {
		logger.trace("called - rollback()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.rollback();
		}
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		logger.trace("called - rollback(Savepoint savepoint)");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.rollback(savepoint);
		}
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		logger.trace("called - setAutoCommit(boolean autoCommit)");
		originalConnection.setAutoCommit(autoCommit);
	}

	public void setCatalog(String catalog) throws SQLException {
		logger.trace("called - setCatalog(String catalog)");
		originalConnection.setCatalog(catalog);
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		logger.trace("called - setClientInfo(Properties properties)");
		originalConnection.setClientInfo(properties);
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		logger.trace("called - setClientInfo(String name, String value)");
		originalConnection.setClientInfo(name, value);
	}

	public void setHoldability(int holdability) throws SQLException {
		logger.trace("called - setHoldability(int holdability)");
		originalConnection.setHoldability(holdability);
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		logger.trace("called - setReadOnly(boolean readOnly)");
		originalConnection.setReadOnly(readOnly);
	}

	public Savepoint setSavepoint() throws SQLException {
		logger.trace("called - setSavepoint()");
		return originalConnection.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		logger.trace("called - setSavepoint(String name)");
		return originalConnection.setSavepoint(name);
	}

	public void setTransactionIsolation(int level) throws SQLException {
		logger.trace("called - setTransactionIsolation(int level): " + level);
		originalConnection.setTransactionIsolation(level);
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		logger.trace("called - setTypeMap(Map<String, Class<?>> map)");
		originalConnection.setTypeMap(map);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		logger.trace("called - unwrap(Class<T> iface)");
		return originalConnection.unwrap(iface);
	}

	public void abort(Executor arg0) throws SQLException {
		logger.warn("called - abort()");
	}

	public int getNetworkTimeout() throws SQLException {
		logger.warn("called - getNetworkTimeout()");
		return 0;
	}

	public String getSchema() throws SQLException {
		logger.warn("called - getSchema()");
		return null;
	}

	public void setSchema(String arg0) throws SQLException {
		logger.warn("called - setSchema()");
	}
	
	public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
		logger.warn("called - setNetworkTimeout()");
	}

}
