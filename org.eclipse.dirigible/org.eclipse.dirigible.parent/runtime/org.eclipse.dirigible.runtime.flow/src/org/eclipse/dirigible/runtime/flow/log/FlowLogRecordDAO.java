/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.flow.log;

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

public class FlowLogRecordDAO {

	private static final Logger logger = Logger.getLogger(FlowLogRecordDAO.class);

	private static final String SQL_MAP_INSERT_FLOW_LOG = "/org/eclipse/dirigible/runtime/flow/log/sql/insert_flow_log.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_COUNT_FLOW_LOGS = "/org/eclipse/dirigible/runtime/flow/log/sql/select_count_flow_logs.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_CREATE_TABLE_FLOW_LOG = "/org/eclipse/dirigible/runtime/flow/log/sql/create_table_flow_log.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_REMOVE_OLDER_FLOW_LOGS = "/org/eclipse/dirigible/runtime/flow/log/sql/remove_older_flow_logs.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_ALL_FLOW_LOGS = "/org/eclipse/dirigible/runtime/flow/log/sql/select_all_flow_logs.sql"; //$NON-NLS-1$

	public static void insert(FlowLog flowLog) throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_INSERT_FLOW_LOG, FlowLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				int i = 0;
				pstmt.setString(++i, flowLog.getInstance());
				pstmt.setString(++i, flowLog.getFlowName());
				pstmt.setString(++i, flowLog.getFlowUUID());
				pstmt.setString(++i, flowLog.getStepName());
				pstmt.setInt(++i, flowLog.getStatus());
				pstmt.setString(++i, flowLog.getMessage());
				pstmt.setString(++i, flowLog.getContext());

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
			String sqlCount = dbUtils.readScript(connection, SQL_MAP_SELECT_COUNT_FLOW_LOGS, FlowLogRecordDAO.class);
			String sqlCreate = dbUtils.readScript(connection, SQL_MAP_CREATE_TABLE_FLOW_LOG, FlowLogRecordDAO.class);
			try {
				stmt.executeQuery(sqlCount);
			} catch (Exception e) {
				logger.error("DGB_FLOW_LOG does not exist?" + e.getMessage(), e);
				// Create Flow Log Table

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
				String sql = dbUtils.readScript(connection, SQL_MAP_REMOVE_OLDER_FLOW_LOGS, FlowLogRecordDAO.class);
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

	public static String getFlowLogRecords() throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_SELECT_ALL_FLOW_LOGS, FlowLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				List<Object> flowLogRecords = new ArrayList<Object>();

				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					JsonObject row = new JsonObject();
					row.add("instance", gson.toJsonTree(rs.getString("FLOWLOG_INSTANCE")));
					row.add("processId", gson.toJsonTree(rs.getString("FLOWLOG_FLOW_NAME")));
					row.add("processId", gson.toJsonTree(rs.getString("FLOWLOG_FLOW_UUID")));
					row.add("stepName", gson.toJsonTree(rs.getString("FLOWLOG_STEP_NAME")));
					row.add("status", gson.toJsonTree(rs.getInt("FLOWLOG_STATUS")));
					row.add("message", gson.toJsonTree(rs.getString("FLOWLOG_MESSAGE")));
					row.add("context", gson.toJsonTree(rs.getString("FLOWLOG_CONTEXT")));
					flowLogRecords.add(row);

				}

				String result = gson.toJson(flowLogRecords.toArray());

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
