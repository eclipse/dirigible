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

package org.eclipse.dirigible.runtime.metrics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class AccessLogLocationsDAO {

	private static final String DELETE_FROM_DGB_ACCESS_LOG_LOCATIONS_WHERE_ACCLOGLOCATION = "DELETE FROM DGB_ACCESS_LOG_LOCATIONS WHERE ACCLOGLOC_LOCATION = ?";
	private static final String DELETE_FROM_DGB_ACCESS_LOG_LOCATIONS_ALL = "DELETE FROM DGB_ACCESS_LOG_LOCATIONS";

	private static final String SELECT_ALL_DGB_ACCESS_LOG_LOCATIONS = "SELECT * FROM DGB_ACCESS_LOG_LOCATIONS";

	private static final String INSERT_INTO_DGB_ACCESS_LOG_LOCATIONS = "INSERT INTO DGB_ACCESS_LOG_LOCATIONS ("
			+ "ACCLOGLOC_LOCATION) " + "VALUES (?)";

	private static final String CREATE_TABLE_DGB_ACCESS_LOG_LOCATIONS = "CREATE TABLE DGB_ACCESS_LOG_LOCATIONS ("
			+ " ACCLOGLOC_LOCATION VARCHAR(256))";

	private static final String SELECT_COUNT_FROM_DGB_ACCESS_LOG_LOCATIONS = "SELECT COUNT(*) FROM DGB_ACCESS_LOG_LOCATIONS";

	private static final Logger logger = Logger.getLogger(AccessLogLocationsDAO.class);

	public static void refreshLocations() throws SQLException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection
						.prepareStatement(SELECT_ALL_DGB_ACCESS_LOG_LOCATIONS);

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

	public static void insertLocation(String location) throws SQLException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection
						.prepareStatement(INSERT_INTO_DGB_ACCESS_LOG_LOCATIONS);

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

	public static void deleteLocation(String location) throws SQLException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection
						.prepareStatement(DELETE_FROM_DGB_ACCESS_LOG_LOCATIONS_WHERE_ACCLOGLOCATION);

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

	public static void deleteAllLocations() throws SQLException {
		try {
			checkDB();

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection
						.prepareStatement(DELETE_FROM_DGB_ACCESS_LOG_LOCATIONS_ALL);

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

	private static void checkDB() throws NamingException, SQLException {
		DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement stmt = connection.createStatement();

			try {
				stmt.executeQuery(SELECT_COUNT_FROM_DGB_ACCESS_LOG_LOCATIONS);
			} catch (Exception e) {
				logger.error("DGB_ACCESS_LOG does not exist?" + e.getMessage(), e);
				// Create Access Log Table
				stmt.executeUpdate(CREATE_TABLE_DGB_ACCESS_LOG_LOCATIONS);
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
