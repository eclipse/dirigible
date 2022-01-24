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

import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Wrapped Connection of the standard JDBC {@link Connection} object with added some additional capabilities.
 */
public class WrappedConnection implements Connection {

	private static final Logger logger = LoggerFactory.getLogger(WrappedConnection.class);

	private Connection originalConnection;

	private WrappedDataSource dataSource;

	private long timeAcquired;

	private String operationalInfo;

	/**
	 * Instantiates a new wrapped connection.
	 *
	 * @param originalConnection
	 *            the original connection
	 * @param dataSource
	 *            the data source
	 */
	public WrappedConnection(Connection originalConnection, WrappedDataSource dataSource) {
		super();
		this.originalConnection = originalConnection;
		this.timeAcquired = GregorianCalendar.getInstance().getTime().getTime();
		this.dataSource = dataSource;
		ThreadContextFacade.addCloseable(this);
	}

	/**
	 * Gets the time acquired.
	 *
	 * @return the time acquired
	 */
	public long getTimeAcquired() {
		logger.trace("called - getTimeAcquired(): " + timeAcquired);
		return timeAcquired;
	}

	/**
	 * Gets the time used.
	 *
	 * @return the time used
	 */
	public long getTimeUsed() {
		long timeUsed = GregorianCalendar.getInstance().getTime().getTime() - timeAcquired;
		logger.trace("called - getTimeUsed(): " + timeUsed);
		return timeUsed;
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#clearWarnings()
	 */
	@Override
	public void clearWarnings() throws SQLException {
		logger.trace("entering - clearWarnings()");
		originalConnection.clearWarnings();
		logger.trace("exiting - clearWarnings()");
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#close()
	 */
	@Override
	public void close() throws SQLException {
		logger.trace("entering - close()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.commit();
		}
		originalConnection.close();
		dataSource.closedConnection(this);
		ThreadContextFacade.removeCloseable(this);
		logger.trace("exiting - close()");
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#commit()
	 */
	@Override
	public void commit() throws SQLException {
		logger.trace("entering - commit()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.commit();
		}
		logger.trace("exiting - commit()");
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		logger.trace("called - createArrayOf(String typeName, Object[] elements)");
		return originalConnection.createArrayOf(typeName, elements);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createBlob()
	 */
	@Override
	public Blob createBlob() throws SQLException {
		logger.trace("called - createBlob()");
		return originalConnection.createBlob();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createClob()
	 */
	@Override
	public Clob createClob() throws SQLException {
		logger.trace("called - createClob()");
		return originalConnection.createClob();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createNClob()
	 */
	@Override
	public NClob createNClob() throws SQLException {
		logger.trace("called - createNClob()");
		return originalConnection.createNClob();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createSQLXML()
	 */
	@Override
	public SQLXML createSQLXML() throws SQLException {
		logger.trace("called - createSQLXML()");
		return originalConnection.createSQLXML();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createStatement()
	 */
	@Override
	public Statement createStatement() throws SQLException {
		logger.warn("called unsecured [createStatement()]");
		return originalConnection.createStatement();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		logger.warn("called unsecured [createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)]");
		return originalConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		logger.warn("called unsecured [createStatement(int resultSetType, int resultSetConcurrency)]");
		return originalConnection.createStatement(resultSetType, resultSetConcurrency);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		logger.trace("called - createStatement(String typeName, Object[] attributes)");
		return originalConnection.createStruct(typeName, attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getAutoCommit()
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		logger.trace("called - getAutoCommit()");
		return originalConnection.getAutoCommit();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getCatalog()
	 */
	@Override
	public String getCatalog() throws SQLException {
		logger.trace("called - getCatalog()");
		return originalConnection.getCatalog();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getClientInfo()
	 */
	@Override
	public Properties getClientInfo() throws SQLException {
		logger.trace("called - getClientInfo()");
		return originalConnection.getClientInfo();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getClientInfo(java.lang.String)
	 */
	@Override
	public String getClientInfo(String name) throws SQLException {
		logger.trace("called - getClientInfo(String name)");
		return originalConnection.getClientInfo(name);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getHoldability()
	 */
	@Override
	public int getHoldability() throws SQLException {
		logger.trace("called - getHoldability()");
		return originalConnection.getHoldability();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getMetaData()
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		logger.trace("called - getMetaData()");
		return originalConnection.getMetaData();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	@Override
	public int getTransactionIsolation() throws SQLException {
		logger.trace("called - getTransactionIsolation()");
		return originalConnection.getTransactionIsolation();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getTypeMap()
	 */
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		logger.trace("called - getTypeMap()");
		return originalConnection.getTypeMap();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getWarnings()
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		logger.trace("called - getWarnings()");
		return originalConnection.getWarnings();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#isClosed()
	 */
	@Override
	public boolean isClosed() throws SQLException {
		logger.trace("called - isClosed()");
		return originalConnection.isClosed();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() throws SQLException {
		logger.trace("called - isReadOnly()");
		return originalConnection.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#isValid(int)
	 */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		logger.trace("called - isValid(int timeout)");
		return originalConnection.isValid(timeout);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		logger.trace("called - isWrapperFor(Class<?> iface)");
		return originalConnection.isWrapperFor(iface);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	@Override
	public String nativeSQL(String sql) throws SQLException {
		logger.trace("called - nativeSQL(String sql): " + sql);
		return originalConnection.nativeSQL(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		logger.trace("called - prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability): " + sql);
		return originalConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		logger.trace("called - prepareCall(String sql, int resultSetType, int resultSetConcurrency): " + sql);
		return originalConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		logger.trace("called - prepareCall(String sql): " + sql);
		return originalConnection.prepareCall(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability): " + sql);
		return originalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int resultSetType, int resultSetConcurrency): " + sql);
		return originalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int autoGeneratedKeys): " + sql);
		return originalConnection.prepareStatement(sql, autoGeneratedKeys);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int[] columnIndexes): " + sql);
		return originalConnection.prepareStatement(sql, columnIndexes);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		logger.trace("called - prepareStatement(String sql, String[] columnNames): " + sql);
		return originalConnection.prepareStatement(sql, columnNames);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		logger.trace("called - prepareStatement(String sql): " + sql);
		return originalConnection.prepareStatement(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		logger.trace("called - releaseSavepoint(Savepoint savepoint)");
		originalConnection.releaseSavepoint(savepoint);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#rollback()
	 */
	@Override
	public void rollback() throws SQLException {
		logger.trace("called - rollback()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.rollback();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		logger.trace("called - rollback(Savepoint savepoint)");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.rollback(savepoint);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		logger.trace("called - setAutoCommit(boolean autoCommit)");
		originalConnection.setAutoCommit(autoCommit);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	@Override
	public void setCatalog(String catalog) throws SQLException {
		logger.trace("called - setCatalog(String catalog)");
		originalConnection.setCatalog(catalog);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setClientInfo(java.util.Properties)
	 */
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		logger.trace("called - setClientInfo(Properties properties)");
		originalConnection.setClientInfo(properties);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		logger.trace("called - setClientInfo(String name, String value)");
		originalConnection.setClientInfo(name, value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setHoldability(int)
	 */
	@Override
	public void setHoldability(int holdability) throws SQLException {
		logger.trace("called - setHoldability(int holdability)");
		originalConnection.setHoldability(holdability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		logger.trace("called - setReadOnly(boolean readOnly)");
		originalConnection.setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setSavepoint()
	 */
	@Override
	public Savepoint setSavepoint() throws SQLException {
		logger.trace("called - setSavepoint()");
		return originalConnection.setSavepoint();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		logger.trace("called - setSavepoint(String name)");
		return originalConnection.setSavepoint(name);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		logger.trace("called - setTransactionIsolation(int level): " + level);
		originalConnection.setTransactionIsolation(level);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		logger.trace("called - setTypeMap(Map<String, Class<?>> map)");
		originalConnection.setTypeMap(map);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		logger.trace("called - unwrap(Class<T> iface)");
		return originalConnection.unwrap(iface);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#abort(java.util.concurrent.Executor)
	 */
	@Override
	public void abort(Executor arg0) throws SQLException {
		logger.warn("called - abort()");
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getNetworkTimeout()
	 */
	@Override
	public int getNetworkTimeout() throws SQLException {
		logger.warn("called - getNetworkTimeout()");
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#getSchema()
	 */
	@Override
	public String getSchema() throws SQLException {
		logger.warn("called - getSchema()");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setSchema(java.lang.String)
	 */
	@Override
	public void setSchema(String arg0) throws SQLException {
		logger.warn("called - setSchema()");
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.Connection#setNetworkTimeout(java.util.concurrent.Executor, int)
	 */
	@Override
	public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
		logger.warn("called - setNetworkTimeout()");
	}

	/**
	 * Gets the operational info.
	 *
	 * @return the operational info
	 */
	public String getOperationalInfo() {
		return operationalInfo;
	}

	/**
	 * Sets the operational info.
	 *
	 * @param operationalInfo
	 *            the new operational info
	 */
	public void setOperationalInfo(String operationalInfo) {
		this.operationalInfo = operationalInfo;
	}

}
