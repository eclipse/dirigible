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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	public static String getMemoryLogRecords() throws SQLException, IOException {
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

				List<List<List<Object>>> memoryLogRecords = new ArrayList<List<List<Object>>>();
				
				List<List<Object>> memlogFree = new ArrayList<List<Object>>();
				List<List<Object>> memlogTotal = new ArrayList<List<Object>>();
				List<List<Object>> memlogMax = new ArrayList<List<Object>>();
				
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					
//					Long date = new Long(format.format(rs.getTimestamp(MEMLOG_TIMESTAMP)));
					Date date = rs.getTimestamp(MEMLOG_TIMESTAMP);
					List<Object> pair = new ArrayList<Object>();
					pair.add(date);
					pair.add(new Long(rs.getLong(MEMLOG_FREE_MEMORY)/1048576));
					memlogFree.add(pair);
					
					pair = new ArrayList<Object>();
					pair.add(date);
					pair.add(new Long(rs.getLong(MEMLOG_TOTAL_MEMORY)/1048576));
					memlogTotal.add(pair);
					
					pair = new ArrayList<Object>();
					pair.add(date);
					pair.add(new Long(rs.getLong(MEMLOG_MAX_MEMORY)/1048576));
					memlogMax.add(pair);
					
				}
				
				memoryLogRecords.add(memlogFree);
				memoryLogRecords.add(memlogTotal);
				memoryLogRecords.add(memlogMax);
				

				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
				String result = gson.toJson(memoryLogRecords.toArray());
				
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
