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

import java.io.IOException;
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

import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class MemoryLogRecordDAO {

	private static final Logger logger = Logger.getLogger(MemoryLogRecordDAO.class);
	

	private static final String SQL_MAP_INSERT_MEMORY_LOG =
			"/org/eclipse/dirigible/runtime/memory/sql/insert_memory_log.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_COUNT_MEMORY_LOGS =
			"/org/eclipse/dirigible/runtime/memory/sql/select_count_memory_logs.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_CREATE_TABLE_MEMORY_LOG =
			"/org/eclipse/dirigible/runtime/memory/sql/create_table_memory_log.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_REMOVE_OLDER_MEMORY_LOGS =
			"/org/eclipse/dirigible/runtime/memory/sql/remove_older_memory_logs.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_ALL_MEMORY_LOGS =
			"/org/eclipse/dirigible/runtime/memory/sql/select_all_memory_logs.sql"; //$NON-NLS-1$
	
	private static final String AVAILABLE_PROCESSORS = "availableProcessors"; //$NON-NLS-1$
	private static final String MAX_MEMORY = "maxMemory"; //$NON-NLS-1$
	private static final String TOTAL_MEMORY = "totalMemory"; //$NON-NLS-1$
	private static final String FREE_MEMORY = "freeMemory"; //$NON-NLS-1$
	private static final String FIELD_MAX = "Max"; //$NON-NLS-1$
	private static final String FIELD_TOTAL = "Total"; //$NON-NLS-1$
	private static final String FIELD_FREE = "Free"; //$NON-NLS-1$
	private static final String FIELD_DATE = "date"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String MEMLOG_MAX_MEMORY = "MEMLOG_MAX_MEMORY"; //$NON-NLS-1$
	private static final String MEMLOG_TOTAL_MEMORY = "MEMLOG_TOTAL_MEMORY"; //$NON-NLS-1$
	private static final String MEMLOG_FREE_MEMORY = "MEMLOG_FREE_MEMORY"; //$NON-NLS-1$
	private static final String MEMLOG_TIMESTAMP = "MEMLOG_TIMESTAMP"; //$NON-NLS-1$
	private static final String DATE_FORMAT = "yyyyMMddHHmm"; //$NON-NLS-1$

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

	public static void insert() throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_INSERT_MEMORY_LOG, MemoryLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				int i = 0;
				pstmt.setLong(++i, Runtime.getRuntime().freeMemory());
				pstmt.setLong(++i, Runtime.getRuntime().totalMemory());
				pstmt.setLong(++i, Runtime.getRuntime().maxMemory());
//				pstmt.setTimestamp(++i, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));

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

	private static void checkDB() throws NamingException, SQLException, IOException {
		DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement stmt = connection.createStatement();
			DBUtils dbUtils = new DBUtils(dataSource);
			String sqlCount = 
					dbUtils.readScript(connection, SQL_MAP_SELECT_COUNT_MEMORY_LOGS, MemoryLogRecordDAO.class);
			String sqlCreate = 
					dbUtils.readScript(connection, SQL_MAP_CREATE_TABLE_MEMORY_LOG, MemoryLogRecordDAO.class);
			try {
				stmt.executeQuery(sqlCount);
			} catch (Exception e) {
				logger.error("DGB_MEMORY_LOG does not exist?" + e.getMessage(), e);
				// Create Memory Log Table
				
				stmt.executeUpdate(sqlCreate);
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	public static void cleanupOlderRecords() throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_REMOVE_OLDER_MEMORY_LOGS, MemoryLogRecordDAO.class);
				PreparedStatement pstmt = connection
						.prepareStatement(sql);

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

	public static String[][] getMemoryLogRecords() throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_SELECT_ALL_MEMORY_LOGS, MemoryLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);

				List<String[]> memoryLogRecords = new ArrayList<String[]>();
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					String[] record = new String[4];
					record[0] = format.format(rs.getTimestamp(MEMLOG_TIMESTAMP));
					record[1] = rs.getLong(MEMLOG_FREE_MEMORY) + EMPTY;
					record[2] = rs.getLong(MEMLOG_TOTAL_MEMORY) + EMPTY;
					record[3] = rs.getLong(MEMLOG_MAX_MEMORY) + EMPTY;

					memoryLogRecords.add(record);
				}

				String[][] result = new String[memoryLogRecords.size() + 1][4];

				result[0][0] = FIELD_DATE;
				result[0][1] = FIELD_FREE;
				result[0][2] = FIELD_TOTAL;
				result[0][3] = FIELD_MAX;

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
