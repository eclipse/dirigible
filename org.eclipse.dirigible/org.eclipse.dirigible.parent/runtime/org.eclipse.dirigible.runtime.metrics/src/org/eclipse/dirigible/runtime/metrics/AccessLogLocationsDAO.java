/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.metrics;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class AccessLogLocationsDAO {

	private static final Logger logger = Logger.getLogger(AccessLogLocationsDAO.class);

	private static final String SQL_MAP_REMOVE_LOG_LOCATION = "/org/eclipse/dirigible/runtime/metrics/sql/remove_log_location.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_REMOVE_ALL_LOG_LOCATIONS = "/org/eclipse/dirigible/runtime/metrics/sql/remove_all_log_locations.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_ALL_LOG_LOCATIONS = "/org/eclipse/dirigible/runtime/metrics/sql/select_all_log_locations.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_INSERT_LOG_LOCATION = "/org/eclipse/dirigible/runtime/metrics/sql/insert_log_location.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_CREATE_TABLE_LOG_LOCATIONS = "/org/eclipse/dirigible/runtime/metrics/sql/create_table_log_locations.sql"; //$NON-NLS-1$
	private static final String SQL_MAP_SELECT_COUNT_LOG_LOCATIONS = "/org/eclipse/dirigible/runtime/metrics/sql/select_count_log_locations.sql"; //$NON-NLS-1$

	public static void refreshLocations() throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_SELECT_ALL_LOG_LOCATIONS, AccessLogLocationsDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				ResultSet rs = pstmt.executeQuery();

				AccessLogLocationsSynchronizer.getAccessLogLocations().clear();
				while (rs.next()) {
					String location = rs.getString(1);
					AccessLogLocationsSynchronizer.getAccessLogLocations().add(location);
				}

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (NamingException e) {
			throw new SQLException(e);
		}
	}

	public static void insertLocation(String location) throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_INSERT_LOG_LOCATION, AccessLogLocationsDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				pstmt.setString(1, location);

				pstmt.executeUpdate();

				AccessLogLocationsSynchronizer.getAccessLogLocations().add(location);

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (NamingException e) {
			throw new SQLException(e);
		}
	}

	public static void deleteLocation(String location) throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_REMOVE_LOG_LOCATION, AccessLogLocationsDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				pstmt.setString(1, location);

				pstmt.executeUpdate();

				AccessLogLocationsSynchronizer.getAccessLogLocations().remove(location);

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (NamingException e) {
			throw new SQLException(e);
		}
	}

	public static void deleteAllLocations() throws SQLException, IOException {
		try {
			checkDB();

			DataSource dataSource = DataSourceFacade.getInstance().getDataSource(null);
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DBUtils dbUtils = new DBUtils(dataSource);
				String sql = dbUtils.readScript(connection, SQL_MAP_REMOVE_ALL_LOG_LOCATIONS, AccessLogLocationsDAO.class);
				PreparedStatement pstmt = connection.prepareStatement(sql);

				pstmt.executeUpdate();

				AccessLogLocationsSynchronizer.getAccessLogLocations().clear();

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
			String sqlCount = dbUtils.readScript(connection, SQL_MAP_SELECT_COUNT_LOG_LOCATIONS, AccessLogLocationsDAO.class);

			try {
				stmt.executeQuery(sqlCount);
			} catch (Exception e) {
				logger.warn("DGB_ACCESS_LOG does not exist?" + e.getMessage());
				// Create Access Log Table
				String sqlCreate = dbUtils.readScript(connection, SQL_MAP_CREATE_TABLE_LOG_LOCATIONS, AccessLogLocationsDAO.class);
				stmt.executeUpdate(sqlCreate);
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
