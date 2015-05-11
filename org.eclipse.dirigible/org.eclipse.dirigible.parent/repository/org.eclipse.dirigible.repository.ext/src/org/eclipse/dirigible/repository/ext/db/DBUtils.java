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

package org.eclipse.dirigible.repository.ext.db;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;

import org.eclipse.dirigible.repository.ext.db.dialect.DerbyDBSpecifier;
import org.eclipse.dirigible.repository.ext.db.dialect.HANADBSpecifier;
import org.eclipse.dirigible.repository.ext.db.dialect.IDialectSpecifier;
import org.eclipse.dirigible.repository.ext.db.dialect.PostgreSQLDBSpecifier;
import org.eclipse.dirigible.repository.ext.db.dialect.SAPDBSpecifier;
import org.eclipse.dirigible.repository.ext.db.dialect.SybaseDBSpecifier;
import org.eclipse.dirigible.repository.logging.Logger;

public class DBUtils {

	private static final String PRODUCT_DERBY = "Apache Derby"; //$NON-NLS-1$
	private static final String PRODUCT_SYBASE = "Adaptive Server Enterprise"; //$NON-NLS-1$
	private static final String PRODUCT_SAP_DB = "SAP DB"; //$NON-NLS-1$
	private static final String PRODUCT_HDB = "HDB"; //$NON-NLS-1$
	private static final String PRODUCT_POSTGRESQL = "PostgreSQL"; //$NON-NLS-1$
	
	public static final String SCRIPT_DELIMITER = ";"; //$NON-NLS-1$
	

	private static Logger logger = Logger.getLogger(DBUtils.class.getCanonicalName());

	private DataSource dataSource;

	public DBUtils(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Read whole SQL script from the class path. It can contain multiple
	 * statements separated with ';'
	 * 
	 * @param path
	 * @return the SQL script as a String
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
				String chunk = new String(buffer, 0, bytesRead,
						Charset.defaultCharset());
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
				if (bufferedInput != null)
					bufferedInput.close();
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
		StringTokenizer tokenizer = new StringTokenizer(script,
				SCRIPT_DELIMITER);

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

	public PreparedStatement getPreparedStatement(Connection connection,
			String sql) throws SQLException {
		logger.debug("entering getPreparedStatement"); //$NON-NLS-1$
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		logger.debug("exiting getPreparedStatement"); //$NON-NLS-1$
		return preparedStatement;
	}

	public Connection getConnection() throws SQLException {
		logger.debug("entering getConnection"); //$NON-NLS-1$
		Connection connection = this.dataSource.getConnection();
		logger.debug("exiting getConnection"); //$NON-NLS-1$
		return connection;
	}

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

	public static IDialectSpecifier getDialectSpecifier(String productName) {
		if (productName != null) {
			if (PRODUCT_HDB.equals(productName)) {
				return new HANADBSpecifier();
			} else if (PRODUCT_SAP_DB.equals(productName)) {
				return new SAPDBSpecifier();
			} else if (PRODUCT_SYBASE.equals(productName)) {
				return new SybaseDBSpecifier();
			} else if (PRODUCT_DERBY.equals(productName)) {
				return new DerbyDBSpecifier();
			} else if (PRODUCT_POSTGRESQL.equals(productName)) {
				return new PostgreSQLDBSpecifier();
			}
		}
		return new HANADBSpecifier();
	}

	public String specifyDataType(Connection connection, String commonType)
			throws SQLException {
		String productName = connection.getMetaData().getDatabaseProductName();
		IDialectSpecifier dialectSpecifier = getDialectSpecifier(productName);
		return dialectSpecifier.getSpecificType(commonType);
	}

	// public List<String> getListOfSchemes(DatabaseMetaData dmd,
	// String catalogName) throws SQLException {
	//
	// List<String> listOfSchemes = new ArrayList<String>();
	//
	// ResultSet rs = dmd.getSchemas(catalogName, null);
	//
	// while (rs.next()) {
	// String schemeName = rs.getString(1);
	// listOfSchemes.add(schemeName);
	// }
	// rs.close();
	//
	// return listOfSchemes;
	// }
	//
	// public List<String> getListOfTables(DatabaseMetaData dmd,
	// String catalogName, String schemeName) throws SQLException {
	//
	// List<String> listOfTables = new ArrayList<String>();
	//
	// String[] tableTypes = { "TABLE", "VIEW", "ALIAS", "SYNONYM",
	// "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "SYSTEM TABLE" };
	// ResultSet rs = dmd.getTables(catalogName, schemeName, "%", tableTypes);
	//
	// while (rs.next()) {
	// String tableName = rs.getString(3);
	// listOfTables.add(tableName);
	// }
	// rs.close();
	//
	// return listOfTables;
	// }

	/**
	 * ResultSet current row to Content transformation
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	public static byte[] dbToData(ResultSet resultSet)
			throws SQLException {
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
	public static byte[] dbToDataBinary(Connection connection, ResultSet resultSet,
			String columnName) throws SQLException, IOException {
		String productName = connection.getMetaData().getDatabaseProductName();
		IDialectSpecifier dialectSpecifier = DBUtils.getDialectSpecifier(productName);
		InputStream is = dialectSpecifier.getBinaryStream(resultSet, columnName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

}
