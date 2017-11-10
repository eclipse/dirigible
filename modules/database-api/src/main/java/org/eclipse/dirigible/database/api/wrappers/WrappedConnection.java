/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappedConnection implements Connection {

	private static final Logger logger = LoggerFactory.getLogger(WrappedConnection.class);

	private Connection originalConnection;

	private WrappedDataSource dataSource;

	private long timeAcquired;

	private String operationalInfo;

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

	@Override
	public void clearWarnings() throws SQLException {
		logger.trace("entering - clearWarnings()");
		originalConnection.clearWarnings();
		logger.trace("exiting - clearWarnings()");
	}

	@Override
	public void close() throws SQLException {
		logger.trace("entering - close()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.commit();
		}
		originalConnection.close();
		dataSource.closedConnection(this);
		logger.trace("exiting - close()");
	}

	@Override
	public void commit() throws SQLException {
		logger.trace("entering - commit()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.commit();
		}
		logger.trace("exiting - commit()");
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		logger.trace("called - createArrayOf(String typeName, Object[] elements)");
		return originalConnection.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException {
		logger.trace("called - createBlob()");
		return originalConnection.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		logger.trace("called - createClob()");
		return originalConnection.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		logger.trace("called - createNClob()");
		return originalConnection.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		logger.trace("called - createSQLXML()");
		return originalConnection.createSQLXML();
	}

	@Override
	public Statement createStatement() throws SQLException {
		logger.trace("called - createStatement()");
		return originalConnection.createStatement();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		logger.trace("called - createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
		return originalConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		logger.trace("called - createStatement(int resultSetType, int resultSetConcurrency)");
		return originalConnection.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		logger.trace("called - createStatement(String typeName, Object[] attributes)");
		return originalConnection.createStruct(typeName, attributes);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		logger.trace("called - getAutoCommit()");
		return originalConnection.getAutoCommit();
	}

	@Override
	public String getCatalog() throws SQLException {
		logger.trace("called - getCatalog()");
		return originalConnection.getCatalog();
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		logger.trace("called - getClientInfo()");
		return originalConnection.getClientInfo();
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		logger.trace("called - getClientInfo(String name)");
		return originalConnection.getClientInfo(name);
	}

	@Override
	public int getHoldability() throws SQLException {
		logger.trace("called - getHoldability()");
		return originalConnection.getHoldability();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		logger.trace("called - getMetaData()");
		return originalConnection.getMetaData();
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		logger.trace("called - getTransactionIsolation()");
		return originalConnection.getTransactionIsolation();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		logger.trace("called - getTypeMap()");
		return originalConnection.getTypeMap();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		logger.trace("called - getWarnings()");
		return originalConnection.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException {
		logger.trace("called - isClosed()");
		return originalConnection.isClosed();
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		logger.trace("called - isReadOnly()");
		return originalConnection.isReadOnly();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		logger.trace("called - isValid(int timeout)");
		return originalConnection.isValid(timeout);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		logger.trace("called - isWrapperFor(Class<?> iface)");
		return originalConnection.isWrapperFor(iface);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		logger.trace("called - nativeSQL(String sql): " + sql);
		return originalConnection.nativeSQL(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		logger.trace("called - prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability): " + sql);
		return originalConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		logger.trace("called - prepareCall(String sql, int resultSetType, int resultSetConcurrency): " + sql);
		return originalConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		logger.trace("called - prepareCall(String sql): " + sql);
		return originalConnection.prepareCall(sql);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability): " + sql);
		return originalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int resultSetType, int resultSetConcurrency): " + sql);
		return originalConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int autoGeneratedKeys): " + sql);
		return originalConnection.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		logger.trace("called - prepareStatement(String sql, int[] columnIndexes): " + sql);
		return originalConnection.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		logger.trace("called - prepareStatement(String sql, String[] columnNames): " + sql);
		return originalConnection.prepareStatement(sql, columnNames);
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		logger.trace("called - prepareStatement(String sql): " + sql);
		return originalConnection.prepareStatement(sql);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		logger.trace("called - releaseSavepoint(Savepoint savepoint)");
		originalConnection.releaseSavepoint(savepoint);
	}

	@Override
	public void rollback() throws SQLException {
		logger.trace("called - rollback()");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.rollback();
		}
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		logger.trace("called - rollback(Savepoint savepoint)");
		if (!originalConnection.getAutoCommit()) {
			originalConnection.rollback(savepoint);
		}
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		logger.trace("called - setAutoCommit(boolean autoCommit)");
		originalConnection.setAutoCommit(autoCommit);
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		logger.trace("called - setCatalog(String catalog)");
		originalConnection.setCatalog(catalog);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		logger.trace("called - setClientInfo(Properties properties)");
		originalConnection.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		logger.trace("called - setClientInfo(String name, String value)");
		originalConnection.setClientInfo(name, value);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		logger.trace("called - setHoldability(int holdability)");
		originalConnection.setHoldability(holdability);
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		logger.trace("called - setReadOnly(boolean readOnly)");
		originalConnection.setReadOnly(readOnly);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		logger.trace("called - setSavepoint()");
		return originalConnection.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		logger.trace("called - setSavepoint(String name)");
		return originalConnection.setSavepoint(name);
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		logger.trace("called - setTransactionIsolation(int level): " + level);
		originalConnection.setTransactionIsolation(level);
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		logger.trace("called - setTypeMap(Map<String, Class<?>> map)");
		originalConnection.setTypeMap(map);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		logger.trace("called - unwrap(Class<T> iface)");
		return originalConnection.unwrap(iface);
	}

	@Override
	public void abort(Executor arg0) throws SQLException {
		logger.warn("called - abort()");
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		logger.warn("called - getNetworkTimeout()");
		return 0;
	}

	@Override
	public String getSchema() throws SQLException {
		logger.warn("called - getSchema()");
		return null;
	}

	@Override
	public void setSchema(String arg0) throws SQLException {
		logger.warn("called - setSchema()");
	}

	@Override
	public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
		logger.warn("called - setNetworkTimeout()");
	}

	public String getOperationalInfo() {
		return operationalInfo;
	}

	public void setOperationalInfo(String operationalInfo) {
		this.operationalInfo = operationalInfo;
	}

}
