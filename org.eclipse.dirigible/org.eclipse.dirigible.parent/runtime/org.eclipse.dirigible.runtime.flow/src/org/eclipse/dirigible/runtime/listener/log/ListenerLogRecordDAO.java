/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener.log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ListenerLogRecordDAO {

	private static final Logger logger = Logger.getLogger(ListenerLogRecordDAO.class);

	private static final String SQL_MAP_INSERT_LISTENER_LOG = "/org/eclipse/dirigible/runtime/listener/log/sql/insert_listener_log.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_COUNT_LISTENER_LOGS = "/org/eclipse/dirigible/runtime/listener/log/sql/select_count_listener_logs.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_CREATE_TABLE_LISTENER_LOG = "/org/eclipse/dirigible/runtime/listener/log/sql/create_table_listener_log.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_REMOVE_OLDER_LISTENER_LOGS = "/org/eclipse/dirigible/runtime/listener/log/sql/remove_older_listener_logs.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_ALL_LISTENER_LOGS = "/org/eclipse/dirigible/runtime/listener/log/sql/select_all_listener_logs.sql"; //$NON-NLS-1$

	public static void insert(ListenerLog listenerLog) throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_INSERT_LISTENER_LOG, ListenerLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				int i = 0;
				pstmt.setString(++i, listenerLog.getInstance());
				pstmt.setString(++i, listenerLog.getListenerName());
				pstmt.setString(++i, listenerLog.getListenerUUID());
				pstmt.setInt(++i, listenerLog.getStatus());
				pstmt.setString(++i, listenerLog.getMessage());
				pstmt.setString(++i, listenerLog.getContext());

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
		DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement stmt = connection.createStatement();
			DBUtils dbUtils = new DBUtils(dataSource);
			String sqlCount = dbUtils.readScript(connection, SQL_MAP_SELECT_COUNT_LISTENER_LOGS, ListenerLogRecordDAO.class);
			String sqlCreate = dbUtils.readScript(connection, SQL_MAP_CREATE_TABLE_LISTENER_LOG, ListenerLogRecordDAO.class);
			try {
				stmt.executeQuery(sqlCount);
			} catch (Exception e) {
				logger.error("DGB_LISTENER_LOG does not exist?" + e.getMessage(), e);
				// Create Listener Log Table

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

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_REMOVE_OLDER_LISTENER_LOGS, ListenerLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

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

	public static String getListenerLogRecords() throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_SELECT_ALL_LISTENER_LOGS, ListenerLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				List<Object> listenerLogRecords = new ArrayList<Object>();

				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					JsonObject row = new JsonObject();
					row.add("instance", gson.toJsonTree(rs.getString("LISTENERLOG_INSTANCE")));
					row.add("processId", gson.toJsonTree(rs.getString("LISTENERLOG_LISTENER_NAME")));
					row.add("processId", gson.toJsonTree(rs.getString("LISTENERLOG_LISTENER_UUID")));
					row.add("status", gson.toJsonTree(rs.getInt("LISTENERLOG_STATUS")));
					row.add("message", gson.toJsonTree(rs.getString("LISTENERLOG_MESSAGE")));
					row.add("context", gson.toJsonTree(rs.getString("LISTENERLOG_CONTEXT")));
					listenerLogRecords.add(row);

				}

				String result = gson.toJson(listenerLogRecords.toArray());

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
