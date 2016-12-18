/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.datasource.db.dialect.DerbyDBSpecifier;
import org.eclipse.dirigible.repository.datasource.db.dialect.DialectFactory;
import org.eclipse.dirigible.repository.datasource.db.dialect.IDialectSpecifier;
import org.eclipse.dirigible.repository.logging.Logger;

public class DBUtils {

	private static Logger logger = Logger.getLogger(DBUtils.class.getCanonicalName());

	private static final String PRODUCT_DERBY = "Apache Derby"; //$NON-NLS-1$
	private static final String PRODUCT_SYBASE = "Adaptive Server Enterprise"; //$NON-NLS-1$
	private static final String PRODUCT_SAP_DB = "SAP DB"; //$NON-NLS-1$
	private static final String PRODUCT_HDB = "HDB"; //$NON-NLS-1$
	private static final String PRODUCT_POSTGRESQL = "PostgreSQL"; //$NON-NLS-1$
	private static final String PRODUCT_MYSQL = "MySQL"; //$NON-NLS-1$
	private static final String PRODUCT_MONGODB = "MongoDB"; //$NON-NLS-1$

	public static final String SCRIPT_DELIMITER = ";"; //$NON-NLS-1$

	public static final String SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$
	public static final String LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$
	public static final String GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$
	public static final String SYNONYM = "SYNONYM"; //$NON-NLS-1$
	public static final String ALIAS = "ALIAS"; //$NON-NLS-1$
	public static final String VIEW = "VIEW"; //$NON-NLS-1$
	public static final String TABLE = "TABLE"; //$NON-NLS-1$

	/**
	 * Definitions for type table
	 */
	public static final String[] TABLE_TYPES = { TABLE, VIEW, ALIAS, SYNONYM, GLOBAL_TEMPORARY, LOCAL_TEMPORARY, SYSTEM_TABLE };

	private static final String TABLE_NAME_PATTERN_ALL = "%"; //$NON-NLS-1$

	private DataSource dataSource;

	/**
	 * The constructor
	 *
	 * @param dataSource
	 */
	public DBUtils(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Read whole SQL script from the class path. It can contain multiple
	 * statements separated with ';'
	 *
	 * @param conn
	 * @param path
	 * @param clazz
	 * @return the SQL script as a String
	 * @throws IOException
	 */
	public String readScript(Connection conn, String path, Class<?> clazz) throws IOException {
		logger.debug("entering readScript"); //$NON-NLS-1$
		String sql = null;
		InputStream in = clazz.getResourceAsStream(path);
		if (in == null) {
			throw new IOException("SQL script does not exist: " + path);
		}

		BufferedInputStream bufferedInput = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(baos);
		byte[] buffer = new byte[1024];

		try {
			bufferedInput = new BufferedInputStream(in);
			int bytesRead = 0;

			while ((bytesRead = bufferedInput.read(buffer)) != -1) {
				String chunk = new String(buffer, 0, bytesRead, Charset.defaultCharset());
				writer.write(chunk);
			}

			writer.flush();

			sql = new String(baos.toByteArray(), Charset.defaultCharset());
			String productName = conn.getMetaData().getDatabaseProductName();
			IDialectSpecifier dialectSpecifier = getDialectSpecifier(productName);
			sql = dialectSpecifier.specify(sql);

		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			try {
				if (bufferedInput != null) {
					bufferedInput.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}

		logger.debug("exiting readScript"); //$NON-NLS-1$

		return sql;
	}

	/**
	 * Execute a SQL script containing multiple statements separated with ';'
	 *
	 * @param connection
	 * @param script
	 * @return
	 */
	public boolean executeUpdate(Connection connection, String script) {
		logger.debug("entering executeUpdate"); //$NON-NLS-1$
		boolean status = false;
		StringTokenizer tokenizer = new StringTokenizer(script, SCRIPT_DELIMITER);

		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();
			if ("".equals(line.trim())) { //$NON-NLS-1$
				continue;
			}
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(line);
				preparedStatement.execute();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				logger.error(line);
			} finally {
				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			status = true;
		}
		logger.debug("exiting executeUpdate"); //$NON-NLS-1$
		return status;
	}

	/**
	 * Getting a PreparedStatement
	 *
	 * @param connection
	 * @param sql
	 * @return the prepared statement
	 * @throws SQLException
	 */
	public PreparedStatement getPreparedStatement(Connection connection, String sql) throws SQLException {
		logger.debug("entering getPreparedStatement"); //$NON-NLS-1$
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		logger.debug("exiting getPreparedStatement"); //$NON-NLS-1$
		return preparedStatement;
	}

	/**
	 * Getting a Connection from the DataSource
	 *
	 * @return the connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		logger.debug("entering getConnection"); //$NON-NLS-1$
		Connection connection = this.dataSource.getConnection();
		logger.debug("exiting getConnection"); //$NON-NLS-1$
		return connection;
	}

	/**
	 * Safely closing a Connection
	 *
	 * @param connection
	 */
	public void closeConnection(Connection connection) {
		logger.debug("entering closeConnection"); //$NON-NLS-1$
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.debug("exiting closeConnection"); //$NON-NLS-1$
	}

	/**
	 * Safely closing a Statement
	 *
	 * @param statement
	 */
	public void closeStatement(Statement statement) {
		logger.debug("entering closeStatement"); //$NON-NLS-1$
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.debug("exiting closeStatement"); //$NON-NLS-1$
	}

	/**
	 * Safely closing a ResultSet
	 *
	 * @param resultSet
	 */
	public void closeResultSet(ResultSet resultSet) {
		logger.debug("entering closeResultSet"); //$NON-NLS-1$
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.debug("exiting closeResultSet"); //$NON-NLS-1$
	}

	public static IDialectSpecifier getDialectSpecifier(String productName) {
		if (productName != null) {
			IDialectSpecifier dialectSpecifier = DialectFactory.getInstance(productName);
			if (dialectSpecifier != null) {
				return dialectSpecifier;
			}
			logger.warn("No datasource dialects found! Derby dialect will be used as a fallback.");
			return new DerbyDBSpecifier(); // fallback for non-osgi env - e.g. unit tests
		}
		return DialectFactory.getInstance("Apache Derby");
	}

	public String specifyDataType(Connection connection, String commonType) throws SQLException {
		String productName = connection.getMetaData().getDatabaseProductName();
		IDialectSpecifier dialectSpecifier = getDialectSpecifier(productName);
		return dialectSpecifier.getSpecificType(commonType);
	}

	/**
	 * ResultSet current row to Content transformation
	 *
	 * @param resultSet
	 * @return the array of bytes
	 * @throws SQLException
	 */
	public static byte[] dbToData(ResultSet resultSet) throws SQLException {
		String data = resultSet.getString("DOC_CONTENT"); //$NON-NLS-1$
		return data.getBytes(Charset.defaultCharset());
	}

	/**
	 * ResultSet current row to Binary Content transformation
	 *
	 * @param repository
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static byte[] dbToDataBinary(Connection connection, ResultSet resultSet, String columnName) throws SQLException, IOException {
		String productName = connection.getMetaData().getDatabaseProductName();
		IDialectSpecifier dialectSpecifier = DBUtils.getDialectSpecifier(productName);
		InputStream is = dialectSpecifier.getBinaryStream(resultSet, columnName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	public static boolean isTableOrViewExists(Connection connection, String name) throws SQLException {

		boolean exists = false;
		ResultSet rs = connection.getMetaData().getTables(null, null, name, TABLE_TYPES);

		exists = rs.next();

		if (!exists) {
			name = name.toLowerCase(); // e.g. postgres
			rs = connection.getMetaData().getTables(null, null, name, TABLE_TYPES);
			exists = rs.next();
		}

		if (!exists) {
			name = name.toUpperCase(); // e.g. mysql
			rs = connection.getMetaData().getTables(null, null, name, TABLE_TYPES);
			exists = rs.next();
		}

		return exists;
	}

	public static ResultSet getAllTables(Connection connection) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		ResultSet tableNames = meta.getTables(null, null, TABLE_NAME_PATTERN_ALL, null);
		return tableNames;
	}

	public static ResultSet getColumns(Connection connection, String name) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		if (name == null) {
			throw new SQLException("Error on getting columns of table: null");
		}
		ResultSet columns = meta.getColumns(null, null, name, null);
		if (columns.next()) {
			return meta.getColumns(null, null, name, null);
		}
		columns = meta.getColumns(null, null, name.toLowerCase(), null);
		if (columns.next()) {
			return meta.getColumns(null, null, name.toLowerCase(), null);
		}
		columns = meta.getColumns(null, null, name.toUpperCase(), null);
		return columns;
	}

	public static ResultSet getPrimaryKeys(Connection connection, String name) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		if (name == null) {
			throw new SQLException("Error on getting primary keys of table: null");
		}
		ResultSet columns = meta.getPrimaryKeys(null, null, name);
		if (columns.next()) {
			return meta.getPrimaryKeys(null, null, name);
		}
		columns = meta.getPrimaryKeys(null, null, name.toLowerCase());
		if (columns.next()) {
			return meta.getPrimaryKeys(null, null, name.toLowerCase());
		}
		columns = meta.getPrimaryKeys(null, null, name.toUpperCase());
		return columns;
	}

}
