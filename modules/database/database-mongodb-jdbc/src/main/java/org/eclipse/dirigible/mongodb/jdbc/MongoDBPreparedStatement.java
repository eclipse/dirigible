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
package org.eclipse.dirigible.mongodb.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;

public class MongoDBPreparedStatement extends MongoDBStatement implements PreparedStatement {

	String pStmntString;
	SortedMap<Integer, Object> parameters = new TreeMap<Integer, Object>();
	private ResultSet rs;
	
	public MongoDBPreparedStatement(MongoDBConnection conn, String sql) {
		super(conn);
		this.pStmntString = sql;
	}

	@Override
	public boolean execute() throws SQLException {
		return super.execute(this.pStmntString);
	}
	
	@Override
	public ResultSet executeQuery() throws SQLException {
		this.rs = super.executeQuery(this.pStmntString);
		return rs;
	}

	@Override
	public int executeUpdate() throws SQLException {
		return super.executeUpdate(this.pStmntString);
	}

	@Override
	public ResultSet getResultSet(){
		return this.rs;
	}
	
	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		ParameterMetaData paramsMetadata = new MongoDBParameterMetaData(parameters);
		return paramsMetadata;
	}
	
	@Override
	public void clearParameters() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBatch() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
