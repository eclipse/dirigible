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

package org.eclipse.dirigible.repository.db.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.db.init.DBScriptsMap;
import org.eclipse.dirigible.repository.logging.Logger;

public class DBFileVersionDAO {

	private static Logger logger = Logger.getLogger(DBFileVersionDAO.class);

	private DBRepositoryDAO dbRepositoryDAO;

	DBFileVersionDAO(DBRepositoryDAO dbRepositoryDAO) {
		this.dbRepositoryDAO = dbRepositoryDAO;
	}

	/**
	 * Getter for DBRepositoryDAO object
	 * 
	 * @return
	 */
	public DBRepositoryDAO getDbRepositoryDAO() {
		return dbRepositoryDAO;
	}

	/**
	 * Getter for the Repository instance
	 * 
	 * @return
	 */
	protected DBRepository getRepository() {
		return this.dbRepositoryDAO.getRepository();
	}

	/**
	 * Check whether the database schema is initialized
	 * 
	 * @return
	 */
	protected void checkInitialized() {
		this.dbRepositoryDAO.checkInitialized();
	}

	/**
	 * Query the database and retrieve the database object based on the provided
	 * path
	 * 
	 * @param path
	 * @return
	 * @throws DBBaseException
	 */
	public DBFileVersion getFileVersionByPath(String path, int version)
			throws DBBaseException {
		logger.debug("entering getFileVersionByPath"); //$NON-NLS-1$

		checkInitialized();

		if (path == null || "".equals(path.trim())) { //$NON-NLS-1$
			return null;
		}

		DBFileVersion dbFileVersion = null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection,
					DBScriptsMap.SCRIPT_GET_FILE_VERSION_BY_PATH,
					this.getClass());
			preparedStatement = getRepository().getDbUtils()
					.getPreparedStatement(connection, script);
			preparedStatement.setString(1, path);
			preparedStatement.setInt(2, version);
			ResultSet resultSet = null;
			try {
				resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					dbFileVersion = DBMapper.dbToFileVersion(connection, getRepository(),
							resultSet);
				}
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}

		} catch (Exception e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
		logger.debug("exiting getFileVersionByPath"); //$NON-NLS-1$
		return dbFileVersion;
	}

	private int insertFileVersion(String path, byte[] bytes,
			String contentType, String createdBy, int type)
			throws DBBaseException {
		logger.debug("entering insertFileVersion"); //$NON-NLS-1$

		checkInitialized();

		int version = getNextVersion(path);
		if (bytes == null) {
			bytes = new byte[] {};
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection,
					DBScriptsMap.SCRIPT_INSERT_FILE_VERSION, this.getClass());
			preparedStatement = getRepository().getDbUtils()
					.getPreparedStatement(connection, script);

			int i = 0;
			preparedStatement.setString(++i, path);
			preparedStatement.setInt(++i, version);
			preparedStatement.setBinaryStream(++i, new ByteArrayInputStream(
					bytes), bytes.length);
			preparedStatement.setInt(++i, type);
			preparedStatement.setString(++i, contentType);
			preparedStatement.setString(++i, createdBy);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
		logger.debug("exiting insertFileVersion"); //$NON-NLS-1$
		return version;
	}

	private int getNextVersion(String path) {
		logger.debug("entering getNextVersion"); //$NON-NLS-1$

		checkInitialized();

		if (path == null || "".equals(path.trim())) { //$NON-NLS-1$
			return 0;
		}

		int version = 0;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection,
					DBScriptsMap.SCRIPT_GET_NEXT_FILE_VERSION_BY_PATH,
					this.getClass());
			preparedStatement = getRepository().getDbUtils()
					.getPreparedStatement(connection, script);
			preparedStatement.setString(1, path);
			ResultSet resultSet = null;
			try {
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					version = resultSet.getInt(1);
				}
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}

		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
		logger.debug("exiting getNextVersion"); //$NON-NLS-1$
		return (version + 1);
	}

	/**
	 * Create the file version (text or binary) at the given path
	 * 
	 * @param path
	 * @param bytes
	 * @param isBinary
	 * @param contentType
	 * @return
	 * @throws DBBaseException
	 */
	public DBFileVersion createFileVersion(String path, byte[] bytes,
			boolean isBinary, String contentType) throws DBBaseException {

		checkInitialized();

		if (path == null || "".equals(path.trim())) { //$NON-NLS-1$
			return null;
		}

		if (isBinary) {
			return null;
		}

		String createdBy = getRepository().getUser();

		DBFileVersion fileVersion = null;
		String collectionPath = path.substring(0,
				path.lastIndexOf(DBRepository.PATH_DELIMITER));
		DBFolder parent = getDbRepositoryDAO().getDbFolderDAO().createFolder(
				collectionPath);
		if (parent != null) {
			int version = insertFileVersion(path, bytes, contentType,
					createdBy, isBinary ? DBMapper.OBJECT_TYPE_BINARY
							: DBMapper.OBJECT_TYPE_DOCUMENT);
			fileVersion = getFileVersionByPath(path, version);
		}
		return fileVersion;
	}

	public List<DBFileVersion> getFileVersionsByPath(String path) {
		logger.debug("entering getFileVersionsByPath"); //$NON-NLS-1$

		checkInitialized();

		if (path == null || "".equals(path.trim())) { //$NON-NLS-1$
			return null;
		}

		List<DBFileVersion> dbFileVersions = new ArrayList<DBFileVersion>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection,
					DBScriptsMap.SCRIPT_GET_FILE_VERSIONS_BY_PATH,
					this.getClass());
			preparedStatement = getRepository().getDbUtils()
					.getPreparedStatement(connection, script);
			preparedStatement.setString(1, path);
			ResultSet resultSet = null;
			try {
				resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					DBFileVersion dbFileVersion = DBMapper.dbToFileVersion(
							connection, getRepository(), resultSet);
					dbFileVersions.add(dbFileVersion);
				}
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}

		} catch (Exception e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
		logger.debug("exiting getFileVersionsByPath"); //$NON-NLS-1$
		return dbFileVersions;
	}

	public void removeAllFileVersions(String path) throws DBBaseException {
		logger.debug("entering removeAllFileVersions"); //$NON-NLS-1$

		checkInitialized();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection,
					DBScriptsMap.SCRIPT_REMOVE_ALL_FILE_VERSIONS,
					this.getClass());
			preparedStatement = getRepository().getDbUtils()
					.getPreparedStatement(connection, script);

			int i = 0;
			preparedStatement.setString(++i, path);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
		logger.debug("exiting removeAllFileVersions"); //$NON-NLS-1$
	}
	
	public void removeAllFileVersionsBeforeDate(Date date) throws DBBaseException {
		logger.debug("entering removeAllFileVersionsBeforeDate"); //$NON-NLS-1$

		checkInitialized();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection,
					DBScriptsMap.SCRIPT_REMOVE_ALL_FILE_VERSIONS_BEFORE_DATE,
					this.getClass());
			preparedStatement = getRepository().getDbUtils()
					.getPreparedStatement(connection, script);

			int i = 0;
			preparedStatement.setDate(++i, new java.sql.Date(date.getTime()));
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
		logger.debug("exiting removeAllFileVersionsBeforeDate"); //$NON-NLS-1$
	}
	
	

}
