/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.job.log;

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

public class JobLogRecordDAO {

	private static final Logger logger = Logger.getLogger(JobLogRecordDAO.class);

	private static final String SQL_MAP_INSERT_JOB_LOG = "/org/eclipse/dirigible/runtime/job/log/sql/insert_job_log.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_COUNT_JOB_LOGS = "/org/eclipse/dirigible/runtime/job/log/sql/select_count_job_logs.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_CREATE_TABLE_JOB_LOG = "/org/eclipse/dirigible/runtime/job/log/sql/create_table_job_log.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_REMOVE_OLDER_JOB_LOGS = "/org/eclipse/dirigible/runtime/job/log/sql/remove_older_job_logs.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_ALL_JOB_LOGS = "/org/eclipse/dirigible/runtime/job/log/sql/select_all_job_logs.sql"; //$NON-NLS-1$

	public static void insert(JobLog jobLog) throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_INSERT_JOB_LOG, JobLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				int i = 0;
				pstmt.setString(++i, jobLog.getInstance());
				pstmt.setString(++i, jobLog.getJobName());
				pstmt.setString(++i, jobLog.getJobUUID());
				pstmt.setInt(++i, jobLog.getStatus());
				pstmt.setString(++i, jobLog.getMessage());
				pstmt.setString(++i, jobLog.getContext());

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
			String sqlCount = dbUtils.readScript(connection, SQL_MAP_SELECT_COUNT_JOB_LOGS, JobLogRecordDAO.class);
			String sqlCreate = dbUtils.readScript(connection, SQL_MAP_CREATE_TABLE_JOB_LOG, JobLogRecordDAO.class);
			try {
				stmt.executeQuery(sqlCount);
			} catch (Exception e) {
				logger.error("DGB_JOB_LOG does not exist?" + e.getMessage(), e);
				// Create Job Log Table

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
				String sql = dbUtils.readScript(connection, SQL_MAP_REMOVE_OLDER_JOB_LOGS, JobLogRecordDAO.class);
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

	public static String getJobLogRecords() throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_SELECT_ALL_JOB_LOGS, JobLogRecordDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				List<Object> jobLogRecords = new ArrayList<Object>();

				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					JsonObject row = new JsonObject();
					row.add("instance", gson.toJsonTree(rs.getString("JOBLOG_INSTANCE")));
					row.add("processId", gson.toJsonTree(rs.getString("JOBLOG_JOB_NAME")));
					row.add("processId", gson.toJsonTree(rs.getString("JOBLOG_JOB_UUID")));
					row.add("status", gson.toJsonTree(rs.getInt("JOBLOG_STATUS")));
					row.add("message", gson.toJsonTree(rs.getString("JOBLOG_MESSAGE")));
					row.add("context", gson.toJsonTree(rs.getString("JOBLOG_CONTEXT")));
					jobLogRecords.add(row);

				}

				String result = gson.toJson(jobLogRecords.toArray());

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
