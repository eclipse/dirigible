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

package org.eclipse.dirigible.runtime.memory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.google.gson.Gson;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class MemoryLogRecordDAO {

	private static final Logger logger = Logger.getLogger(MemoryLogRecordDAO.class);

	private static final String DELETE_FROM_DGB_MEMORY_LOG_WHERE_ACCLOG_TIMESTAMP = "DELETE FROM DGB_MEMORY_LOG WHERE MEMLOG_TIMESTAMP < ?";
	private static final String INSERT_INTO_DGB_MEMORY_LOG = "INSERT INTO DGB_MEMORY_LOG ("
			+ "MEMLOG_FREE_MEMORY, " + "MEMLOG_TOTAL_MEMORY, " + "MEMLOG_MAX_MEMORY, "
			+ "MEMLOG_TIMESTAMP)" + "VALUES (?,?,?,?)";
	private static final String CREATE_TABLE_DGB_MEMORY_LOG = "CREATE TABLE DGB_MEMORY_LOG ("
			+ "MEMLOG_FREE_MEMORY BIGINT, " + "MEMLOG_TOTAL_MEMORY BIGINT, "
			+ "MEMLOG_MAX_MEMORY BIGINT, " + "MEMLOG_TIMESTAMP TIMESTAMP" + " )";
	private static final String SELECT_COUNT_FROM_DGB_MEMORY_LOG = "SELECT COUNT(*) FROM DGB_MEMORY_LOG";

	private static final String SELECT_ALL_DGB_MEMORY_LOG = "SELECT * FROM DGB_MEMORY_LOG";

	private static final String AVAILABLE_PROCESSORS = "availableProcessors"; //$NON-NLS-1$
	private static final String MAX_MEMORY = "maxMemory"; //$NON-NLS-1$
	private static final String TOTAL_MEMORY = "totalMemory"; //$NON-NLS-1$
	private static final String FREE_MEMORY = "freeMemory"; //$NON-NLS-1$

	public static String generateMemoryInfo() {
		Gson gson = new Gson();
		Map<String, Long> map = new HashMap<String, Long>();
		map.put(FREE_MEMORY, Runtime.getRuntime().freeMemory());
		map.put(TOTAL_MEMORY, Runtime.getRuntime().totalMemory());
		map.put(MAX_MEMORY, Runtime.getRuntime().maxMemory());
		map.put(AVAILABLE_PROCESSORS, Long.valueOf(Runtime.getRuntime().availableProcessors()));
		String content = gson.toJson(map);
		return content;
	}

	public static void insert() throws SQLException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(INSERT_INTO_DGB_MEMORY_LOG);

				int i = 0;
				pstmt.setLong(++i, Runtime.getRuntime().freeMemory());
				pstmt.setLong(++i, Runtime.getRuntime().totalMemory());
				pstmt.setLong(++i, Runtime.getRuntime().maxMemory());
				pstmt.setTimestamp(++i, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));

				pstmt.executeUpdate();

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (NamingException e) {
			throw new SQLException(e);
		}
	}

	private static void checkDB() throws NamingException, SQLException {
		DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement stmt = connection.createStatement();

			try {
				stmt.executeQuery(SELECT_COUNT_FROM_DGB_MEMORY_LOG);
			} catch (Exception e) {
				logger.error("DGB_MEMORY_LOG does not exist?" + e.getMessage(), e);
				// Create Memory Log Table
				stmt.executeUpdate(CREATE_TABLE_DGB_MEMORY_LOG);
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	public static void cleanupOlderRecords() throws SQLException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection
						.prepareStatement(DELETE_FROM_DGB_MEMORY_LOG_WHERE_ACCLOG_TIMESTAMP);

				GregorianCalendar last = new GregorianCalendar();
				last.add(Calendar.DATE, -1);
				pstmt.setTimestamp(1, new Timestamp(last.getTime().getTime()));

				pstmt.executeUpdate();

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (NamingException e) {
			throw new SQLException(e);
		}

	}

	public static String[][] getMemoryLogRecords() throws SQLException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_DGB_MEMORY_LOG);

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

				List<String[]> memoryLogRecords = new ArrayList<String[]>();
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String[] record = new String[4];
					record[0] = format.format(rs.getTimestamp("MEMLOG_TIMESTAMP"));
					record[1] = rs.getLong("MEMLOG_FREE_MEMORY") + "";
					record[2] = rs.getLong("MEMLOG_TOTAL_MEMORY") + "";
					record[3] = rs.getLong("MEMLOG_MAX_MEMORY") + "";

					memoryLogRecords.add(record);
				}

				String[][] result = new String[memoryLogRecords.size() + 1][4];

				result[0][0] = "date";
				result[0][1] = "Free";
				result[0][2] = "Total";
				result[0][3] = "Max";

				for (int i = 0; i < memoryLogRecords.size(); i++) {
					String[] record = memoryLogRecords.get(i);
					result[i + 1][0] = record[0];
					result[i + 1][1] = record[1];
					result[i + 1][2] = record[2];
					result[i + 1][3] = record[3];
				}

				return result;

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (NamingException e) {
			throw new SQLException(e);
		}
	}

}
