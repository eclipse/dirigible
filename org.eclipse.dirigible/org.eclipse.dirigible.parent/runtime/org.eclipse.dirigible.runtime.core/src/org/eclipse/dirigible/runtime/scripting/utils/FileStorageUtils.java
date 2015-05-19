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

package org.eclipse.dirigible.runtime.scripting.utils;

import java.io.ByteArrayInputStream;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.AbstractStorageUtils;
import org.eclipse.dirigible.runtime.scripting.EStorageException;

public class FileStorageUtils extends AbstractStorageUtils {

	private static final Logger logger = Logger.getLogger(FileStorageUtils.class);

	private static final String DGB_FILE_STORAGE = "DGB_FILE_STORAGE";
	private static final String FILE_STORAGE_PATH = "FILE_STORAGE_PATH";
	private static final String FILE_STORAGE_DATA = "FILE_STORAGE_DATA";
	private static final String FILE_STORAGE_CONTENT_TYPE = "FILE_STORAGE_CONTENT_TYPE";
	private static final String FILE_STORAGE_TIMESTAMP = "FILE_STORAGE_TIMESTAMP";

	private static final String INSERT_INTO_DGB_FILE_STORAGE = "INSERT INTO " + DGB_FILE_STORAGE
			+ " (" + FILE_STORAGE_PATH + ", " + FILE_STORAGE_DATA + ", "
			+ FILE_STORAGE_CONTENT_TYPE + ", " + FILE_STORAGE_TIMESTAMP + ")" + "VALUES (?,?,?,?)";

	private static final String UPDATE_DGB_FILE_STORAGE = "UPDATE " + DGB_FILE_STORAGE + " SET "
			+ FILE_STORAGE_PATH + " = ?, " + FILE_STORAGE_DATA + " = ?, "
			+ FILE_STORAGE_CONTENT_TYPE + " = ?, " + FILE_STORAGE_TIMESTAMP + " = ?";

	private static final String DELETE_DGB_FILE_STORAGE = "DELETE FROM " + DGB_FILE_STORAGE;

	private static final String DELETE_DGB_STORAGE_PATH = "DELETE FROM " + DGB_FILE_STORAGE
			+ " WHERE " + FILE_STORAGE_PATH + " = ?";

	private static final String CREATE_TABLE_DGB_FILE_STORAGE = "CREATE TABLE " + DGB_FILE_STORAGE
			+ " (" + FILE_STORAGE_PATH + " VARCHAR(2048) PRIMARY KEY, " + FILE_STORAGE_DATA
			+ " BLOB, " + FILE_STORAGE_CONTENT_TYPE + " VARCHAR(50), " + FILE_STORAGE_TIMESTAMP
			+ " TIMESTAMP" + " )";

	private static final String SELECT_COUNT_FROM_DGB_FILE_STORAGE = "SELECT COUNT(*) FROM "
			+ DGB_FILE_STORAGE;

	private static final String SELECT_DGB_FILE_STORAGE = "SELECT * FROM " + DGB_FILE_STORAGE
			+ " WHERE " + FILE_STORAGE_PATH + " = ?";

	private static final String SELECT_DGB_FILE_STORAGE_EXISTS = "SELECT " + FILE_STORAGE_PATH
			+ " FROM " + DGB_FILE_STORAGE + " WHERE " + FILE_STORAGE_PATH + " = ?";

	public FileStorageUtils(DataSource dataSource) {
		super(dataSource);
	}

	private void checkDB() throws NamingException, SQLException {
		super.checkDB(SELECT_COUNT_FROM_DGB_FILE_STORAGE, CREATE_TABLE_DGB_FILE_STORAGE);
	}

	@Override
	public boolean exists(String path) throws EStorageException {
		try {
			return super.exists(path, SELECT_DGB_FILE_STORAGE_EXISTS,
					SELECT_COUNT_FROM_DGB_FILE_STORAGE, CREATE_TABLE_DGB_FILE_STORAGE);
		} catch (SQLException e) {
			throw new EStorageException(e);
		}
	}

	@Override
	public void clear() throws EStorageException {
		try {
			super.clear(DELETE_DGB_FILE_STORAGE, SELECT_COUNT_FROM_DGB_FILE_STORAGE,
					CREATE_TABLE_DGB_FILE_STORAGE);
		} catch (Exception e) {
			throw new EStorageException(e);
		}
	}

	@Override
	public void delete(String path) throws EStorageException {
		try {
			super.delete(path, DELETE_DGB_STORAGE_PATH, SELECT_COUNT_FROM_DGB_FILE_STORAGE,
					CREATE_TABLE_DGB_FILE_STORAGE);
		} catch (Exception e) {
			throw new EStorageException(e);
		}
	}

	@Override
	public void put(String path, byte[] data, String contentType) throws EStorageException {
		checkMaxSize(data);
		try {
			checkDB();

			if (exists(path)) {
				update(path, data, contentType);
			} else {
				insert(path, data, contentType);
			}

		} catch (Exception e) {
			throw new EStorageException(e);
		}
	}
	
	@Override
	public void put(String path, byte[] data) throws EStorageException {
		put(path, data, ContentTypeHelper.DEFAULT_CONTENT_TYPE);
	}

	private byte[] checkMaxSize(byte[] data) {
		if (data.length > MAX_STORAGE_FILE_SIZE_IN_BYTES) {
			logger.warn(TOO_BIG_DATA_MESSAGE);
			throw new InvalidParameterException(TOO_BIG_DATA_MESSAGE);
		}
		return data;
	}

	private void insert(String path, byte[] data, String contentType) throws SQLException {
		DataSource dataSource = this.dataSource;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(INSERT_INTO_DGB_FILE_STORAGE);

			int i = 0;
			pstmt.setString(++i, path);
			pstmt.setBinaryStream(++i, new ByteArrayInputStream(data), data.length);
			pstmt.setString(++i, contentType);
			pstmt.setTimestamp(++i, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));

			pstmt.executeUpdate();

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	private void update(String path, byte[] data, String contentType) throws SQLException {
		DataSource dataSource = this.dataSource;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(UPDATE_DGB_FILE_STORAGE);

			int i = 0;
			pstmt.setString(++i, path);
			pstmt.setBinaryStream(++i, new ByteArrayInputStream(data), data.length);
			pstmt.setString(++i, contentType);
			pstmt.setTimestamp(++i, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));

			pstmt.executeUpdate();

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	@Override
	public byte[] get(String path) throws EStorageException {
		return getFile(path).data;
	}

	// Retrieve photo data from the cache
	public FileStorageFile getFile(String path) throws EStorageException {
		try {
			checkDB();

			DataSource dataSource = this.dataSource;
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_DGB_FILE_STORAGE);
				pstmt.setString(1, path);

				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					byte[] data = DBUtils.dbToDataBinary(connection, rs, FILE_STORAGE_DATA);
					String contentType = rs.getString(FILE_STORAGE_CONTENT_TYPE);
					return new FileStorageFile(data, contentType);
				}

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (Exception e) {
			throw new EStorageException(e);
		}
		return null;
	}

	public class FileStorageFile {

		public byte[] data;
		public String contentType;

		public FileStorageFile(byte[] data, String contentType) {
			this.data = data;
			this.contentType = contentType;
		}
	}

}