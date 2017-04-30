/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db.transfer;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

public class DBTableImporter {

	private static final String DOT = "."; //$NON-NLS-1$

	private static final String CLOSE = ")"; //$NON-NLS-1$

	private static final String VALUES = " VALUES ("; //$NON-NLS-1$

	private static final String INSERT_INTO = "INSERT INTO "; //$NON-NLS-1$

	private static final String Q = "?"; //$NON-NLS-1$

	private static final String COMMA = ","; //$NON-NLS-1$

	private static final int BATCH_SIZE = 500;

	private byte[] csvFileContent;
	private String tableName;
	private DataSource dataSource;

	public DBTableImporter(DataSource dataSource, byte[] csvFileContent, String fileName) {
		this.csvFileContent = csvFileContent;
		this.tableName = getFileNameWithoutExtension(fileName);
		this.dataSource = dataSource;
	}

	public void insert() throws Exception {
		Connection con = null;
		try {
			con = getConnection();

			List<String[]> records = DBTableDataReader.readRecords(new ByteArrayInputStream(csvFileContent));

			insertRecords(con, records, tableName);
		} finally {
			closeConnection(con);
		}
	}

	private void insertRecords(Connection con, List<String[]> records, String tableName) throws SQLException {
		int columnsCount = records.get(0).length;
		PreparedStatement insertStat = con.prepareStatement(INSERT_INTO + tableName + VALUES + generateQM(columnsCount) + CLOSE);

		int recordsInBatch = 0;

		for (String[] record : records) {
			for (int i = 0; i < record.length; i++) {
				insertStat.setString(i + 1, record[i]);
			}
			insertStat.addBatch();
			recordsInBatch++;

			if (recordsInBatch == BATCH_SIZE) {
				insertStat.executeBatch();
				recordsInBatch = 0;
			}
		}
		if (recordsInBatch != 0) {
			insertStat.executeBatch();
		}
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

	private String getFileNameWithoutExtension(String csvFileName) {
		return csvFileName.substring(0, csvFileName.lastIndexOf(DOT));
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
