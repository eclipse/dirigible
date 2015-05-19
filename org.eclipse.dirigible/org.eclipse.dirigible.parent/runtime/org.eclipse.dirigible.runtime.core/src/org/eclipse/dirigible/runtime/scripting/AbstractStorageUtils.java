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

package org.eclipse.dirigible.runtime.scripting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.logging.Logger;

public abstract class AbstractStorageUtils implements IStorage {
	
	private static final String MAX_STORAGE_FILE_SIZE_MESSAGE = "The maximum allowed storage file size is %d MB";

	public static final int MAX_STORAGE_FILE_SIZE_IN_MEGA_BYTES = 2;

	public static final int MAX_STORAGE_FILE_SIZE_IN_BYTES = MAX_STORAGE_FILE_SIZE_IN_MEGA_BYTES * 1024 * 1024;

	public static final String TOO_BIG_DATA_MESSAGE = String.format(MAX_STORAGE_FILE_SIZE_MESSAGE,
			MAX_STORAGE_FILE_SIZE_IN_MEGA_BYTES);

	private static final String TABLE_DOES_NOT_EXIST_S = "Table does not exist: %s";

	private static final Logger logger = Logger.getLogger(AbstractStorageUtils.class);
	
	protected DataSource dataSource;

	public AbstractStorageUtils(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	protected void checkDB(String checkDBQuery, String createTableQuery) throws NamingException,
			SQLException {
		DataSource dataSource = this.dataSource;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			Statement stmt = connection.createStatement();
			try {
				stmt.executeQuery(checkDBQuery);
			} catch (Exception e) {
				logger.warn(String.format(TABLE_DOES_NOT_EXIST_S, e.getMessage()));
				// Create Table
				stmt.executeUpdate(createTableQuery);
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	protected boolean exists(String path, String existsQuery, String checkDBQuery, String createTableQuery) throws SQLException {
		try {
			checkDB(checkDBQuery, createTableQuery);

			DataSource dataSource = this.dataSource;
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(existsQuery);
				pstmt.setString(1, path);

				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					return true;
				}

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (NamingException e) {
			throw new SQLException(e);
		}
		return false;
	}

	protected void clear(String clearQuery, String checkDBQuery, String createTableQuery) throws SQLException {
		try {
			checkDB(checkDBQuery, createTableQuery);

			DataSource dataSource = this.dataSource;
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				Statement stmt = connection.createStatement();
				stmt.executeUpdate(clearQuery);
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (NamingException e) {
			throw new SQLException(e);
		}
	}

	protected void delete(String path, String deleteQuery, String checkDBQuery, String createTableQuery) throws SQLException {
		try {
			checkDB(checkDBQuery, createTableQuery);

			DataSource dataSource = this.dataSource;
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(deleteQuery);
				pstmt.setString(1, path);
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

}
