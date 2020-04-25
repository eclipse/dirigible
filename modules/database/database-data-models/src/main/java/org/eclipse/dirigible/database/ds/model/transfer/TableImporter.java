/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.ds.model.transfer;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableImporter {
	
	private static final Logger logger = LoggerFactory.getLogger(TableImporter.class);

	private static final String CLOSE = ")"; //$NON-NLS-1$

	private static final String VALUES = " VALUES ("; //$NON-NLS-1$

	private static final String INSERT_INTO = "INSERT INTO "; //$NON-NLS-1$

	private static final String Q = "?"; //$NON-NLS-1$

	private static final String COMMA = ","; //$NON-NLS-1$

	private static final int BATCH_SIZE = 500;

	private byte[] content;
	private String tableName;
	private DataSource dataSource;

	public TableImporter(DataSource dataSource, byte[] content, String tableName) {
		this.content = content;
		this.tableName = tableName;
		this.dataSource = dataSource;
	}

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

	private void insertRecords(Connection connection, List<String[]> records, String tableName) throws SQLException, ParseException {
		logger.debug("Start importing data for the table: {} ...", tableName);
		int columnsCount = records.get(0).length;
		PreparedStatement insertStatement = connection
				.prepareStatement(INSERT_INTO + tableName + VALUES + generateQM(columnsCount) + CLOSE);

		int recordsInBatch = 0;

		List<TableColumn> availableTableColumns = TableMetadataHelper.getColumns(connection, tableName);
		
		for (TableColumn tableColumn : availableTableColumns) {
			logger.debug("    {}: {}", tableColumn.getName(), tableColumn.getType());
		}
		
		int rn = 0;
		for (String[] record : records) {
			rn++;
			if (record.length > availableTableColumns.size()) {
				logger.error("Columns count in the provided data record is bigger than the available columns number in the target table: {}. Skipped record number: {}", tableName, rn);
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
						insertStatement.setDate(i + 1, new Date(DateFormat.getInstance().parse(record[i]).getTime()));
						break;
					case Types.TIME:
						insertStatement.setTime(i + 1, new Time(DateFormat.getInstance().parse(record[i]).getTime()));
						break;
					case Types.TIMESTAMP:
						insertStatement.setTimestamp(i + 1, new Timestamp(DateFormat.getInstance().parse(record[i]).getTime()));
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
		logger.debug("Done importing data for the table: {}, records: {}", tableName, rn);
	}

	private String generateQM(int number) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < number; i++) {
			result.append(Q);

			if ((i + 1) < number) {
				result.append(COMMA);
			}
		}
		return result.toString();
	}

	private void closeConnection(Connection con) throws SQLException {
		if (con != null) {
			con.close();
		}
	}

	private Connection getConnection() throws Exception {
		Connection con = dataSource.getConnection();
		return con;
	}

}
