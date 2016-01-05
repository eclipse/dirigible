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

package org.eclipse.dirigible.runtime.scripting.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.dirigible.repository.datasource.db.dialect.IDialectSpecifier;
import org.eclipse.dirigible.repository.ext.db.DBSequenceUtils;
import org.eclipse.dirigible.repository.ext.db.DBUtils;


/**
 * Utilities for Services using Database
 * 
 */
public class DbUtils {

	private DataSource dataSource;
	
	private DBSequenceUtils dbSequenceUtils;

	public DbUtils(DataSource dataSource) {
		this.dataSource = dataSource;
		this.dbSequenceUtils = new DBSequenceUtils(dataSource);
	}

	public int getNext(String sequenceName) throws SQLException {
		return this.dbSequenceUtils.getNext(sequenceName);
	}

	public int createSequence(String sequenceName, int start)
			throws SQLException {
		return this.dbSequenceUtils.createSequence(sequenceName, start);
	}

	public int dropSequence(String sequenceName) throws SQLException {
		return this.dbSequenceUtils.dropSequence(sequenceName);
	}

	public boolean existSequence(String sequenceName) throws SQLException {
		return this.dbSequenceUtils.existSequence(sequenceName);
	}
	
	public String createLimitAndOffset(String limit, String offset) throws SQLException {
		return createLimitAndOffset(
				NumberUtils.toInt(StringEscapeUtils.escapeSql(limit)), 
				NumberUtils.toInt(StringEscapeUtils.escapeSql(offset)));
	}
	
	public String createLimitAndOffset(int limit, String offset) throws SQLException {
		return createLimitAndOffset(limit, 
				NumberUtils.toInt(StringEscapeUtils.escapeSql(offset)));
	}
	
	public String createLimitAndOffset(String limit, int offset) throws SQLException {
		return createLimitAndOffset(
				NumberUtils.toInt(StringEscapeUtils.escapeSql(limit)), 
				offset);
	}
	
	public String createLimitAndOffset(int limit, int offset) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			String productName = connection.getMetaData().getDatabaseProductName();
			IDialectSpecifier dialectSpecifier = DBUtils.getDialectSpecifier(productName);
			return dialectSpecifier.createLimitAndOffset(limit, offset);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	public String createTopAndStart(int limit, int offset) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			String productName = connection.getMetaData().getDatabaseProductName();
			IDialectSpecifier dialectSpecifier = DBUtils.getDialectSpecifier(productName);
			return dialectSpecifier.createTopAndStart(limit, offset);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	public Date createDate(long value) {
		return new Date(value);
	}
	
	public Time createTime(long value) {
		return new Time(value);
	}
	
	public Timestamp createTimestamp(long value) {
		return new Timestamp(value);
	}
}
