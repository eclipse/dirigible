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
package org.eclipse.dirigible.database.ds.model.transfer;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.DataStructuresUtils;
import org.eclipse.dirigible.commons.api.helpers.DateTimeUtils;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TableImporter.
 */
public class TableImporter {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TableImporter.class);

	/** The Constant BATCH_SIZE. */
	private static final int BATCH_SIZE = 500;

	/** The content. */
	private byte[] content;
	
	/** The table name. */
	private String tableName;
	
	/** The data source. */
	private DataSource dataSource;

	/**
	 * Instantiates a new table importer.
	 *
	 * @param dataSource the data source
	 * @param content the content
	 * @param tableName the table name
	 */
	public TableImporter(DataSource dataSource, byte[] content, String tableName) {
		this.content = content;
		this.tableName = DataStructuresUtils.getCaseSensitiveTableName(tableName);
		this.dataSource = dataSource;
	}

	/**
	 * Insert.
	 *
	 * @throws Exception the exception
	 */
	public void insert() throws Exception {
		Connection connection = null;
		try {
			connection = getConnection();

			List<String[]> records = TableDataReader.readRecords(new ByteArrayInputStream(content));

			insertRecords(connection, records, tableName);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Insert records.
	 *
	 * @param connection the connection
	 * @param records the records
	 * @param tableName the table name
	 * @throws SQLException the SQL exception
	 * @throws ParseException the parse exception
	 */
	private void insertRecords(Connection connection, List<String[]> records, String tableName) throws SQLException, ParseException {
		if (logger.isDebugEnabled()) {logger.debug("Start importing data for the table: {} ...", tableName);}
		InsertBuilder insertBuilder = new InsertBuilder(SqlFactory.deriveDialect(connection));
	    insertBuilder.into(tableName);
	    
	    List<TableColumn> availableTableColumns = TableMetadataHelper.getColumns(connection, tableName);
	    for (int i = 0; i < availableTableColumns.size(); i++) {
	    	String columnName = availableTableColumns.get(i).getName();
			insertBuilder.column("\"" + columnName + "\"").value("?");
		}
	        
		PreparedStatement insertStatement = connection
				.prepareStatement(insertBuilder.build());

		int recordsInBatch = 0;
		int rn = 0;
		for (String[] record : records) {
			rn++;
			if (record.length > availableTableColumns.size()) {
				if (logger.isErrorEnabled()) {logger.error("Columns count in the provided data record is bigger than the available columns number in the target table: {}. Skipped record number: {}", tableName, rn);}
			}
			for (int i = 0; i < record.length; i++) {
				TableColumn tableColumn = availableTableColumns.get(i);
				switch (tableColumn.getType()) {
					case Types.INTEGER:
						insertStatement.setInt(i + 1, Integer.parseInt(record[i]));
						break;
					case Types.BIGINT:
						insertStatement.setLong(i + 1, Long.parseLong(record[i]));
						break;
					case Types.SMALLINT:
						insertStatement.setShort(i + 1, Short.parseShort(record[i]));
						break;
					case Types.TINYINT:
						insertStatement.setByte(i + 1, Byte.parseByte(record[i]));
						break;
					case Types.BOOLEAN:
					case Types.BIT:
						insertStatement.setBoolean(i + 1, Boolean.parseBoolean(record[i]));
						break;
					case Types.DOUBLE:
						insertStatement.setDouble(i + 1, Double.parseDouble(record[i]));
						break;
					case Types.FLOAT:
					case Types.REAL:
						insertStatement.setFloat(i + 1, Float.parseFloat(record[i]));
						break;
					case Types.DECIMAL:
						insertStatement.setBigDecimal(i + 1, new BigDecimal(record[i]));
						break;
					case Types.DATE:
						insertStatement.setDate(i + 1, DateTimeUtils.parseDate(record[i]));
						break;
					case Types.TIME:
						insertStatement.setTime(i + 1,DateTimeUtils.parseTime(record[i]));
						break;
					case Types.TIMESTAMP:
						insertStatement.setTimestamp(i + 1, DateTimeUtils.parseDateTime(record[i]));
						break;
					default:
						insertStatement.setString(i + 1, record[i]);
						break;
				}
				
			}
			insertStatement.addBatch();
			recordsInBatch++;

			if (recordsInBatch == BATCH_SIZE) {
				insertStatement.executeBatch();
				recordsInBatch = 0;
			}
		}
		if (recordsInBatch != 0) {
			insertStatement.executeBatch();
		}
		if (logger.isDebugEnabled()) {logger.debug("Done importing data for the table: {}, records: {}", tableName, rn);}
	}

	/**
	 * Close connection.
	 *
	 * @param con the con
	 * @throws SQLException the SQL exception
	 */
	private void closeConnection(Connection con) throws SQLException {
		if (con != null) {
			con.close();
		}
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws Exception the exception
	 */
	private Connection getConnection() throws Exception {
		Connection con = dataSource.getConnection();
		return con;
	}

}
