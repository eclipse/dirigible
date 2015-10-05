/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.db.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.GregorianCalendar;

import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.db.init.DBScriptsMap;
import org.eclipse.dirigible.repository.logging.Logger;

public class DBFileDAO extends DBObjectDAO {

	private static final String MAX_SIZE_OF_BINARY_RESOURCE_IS = Messages.getString("DBFileDAO.MAX_SIZE_OF_BINARY_RESOURCE_IS"); //$NON-NLS-1$

	private static final String SINGLE_RESOURCES_BIGGER_THAN_4K_NOT_SUPPORTED = Messages
			.getString("DBFileDAO.SINGLE_RESOURCES_BIGGER_THAN_4K_NOT_SUPPORTED"); //$NON-NLS-1$

	private static final String BIN_CONTENT = "BIN_CONTENT"; //$NON-NLS-1$

	private static Logger logger = Logger.getLogger(DBFileDAO.class);

	private static int DOC_CHUNK_SIZE = 2000;

	private static int BIN_MAX_SIZE = 2097152;

	DBFileDAO(DBRepositoryDAO dbRepositoryDAO) {
		super(dbRepositoryDAO);
	}

	/**
	 * Return the database file object
	 *
	 * @param path
	 * @return
	 * @throws DBBaseException
	 */
	public DBFile getFileByPath(String path) throws DBBaseException {
		logger.debug("entering getFileByPath"); //$NON-NLS-1$

		checkInitialized();

		DBFile dbFile = null;
		DBObject dbObject = getObjectByPath(path);
		if (dbObject instanceof DBFile) {
			dbFile = (DBFile) dbObject;
		}
		logger.debug("exiting getFileByPath"); //$NON-NLS-1$
		return dbFile;
	}

	/**
	 * Create the database file or folder object in the target database schema,
	 * located at the given path, content type, etc.
	 *
	 * @param name
	 * @param path
	 * @param contentType
	 * @param createdBy
	 * @param modifiedBy
	 * @param type
	 * @throws DBBaseException
	 */
	void insertFile(String name, String path, String contentType, String createdBy, String modifiedBy, int type) throws DBBaseException {
		insertFile(name, path, contentType, createdBy, modifiedBy, type, false);
	}

	/**
	 * Create the database file or folder object in the target database schema,
	 * located at the given path, content type, etc.
	 *
	 * @param name
	 * @param path
	 * @param contentType
	 * @param createdBy
	 * @param modifiedBy
	 * @param type
	 * @param override
	 * @throws DBBaseException
	 */
	void insertFile(String name, String path, String contentType, String createdBy, String modifiedBy, int type, boolean override)
			throws DBBaseException {
		logger.debug("entering insertFile"); //$NON-NLS-1$

		checkInitialized();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_INSERT_FILE, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, path);
			preparedStatement.setInt(3, type);
			preparedStatement.setString(4, contentType);
			preparedStatement.setString(5, createdBy);
			preparedStatement.setTimestamp(6, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));
			preparedStatement.setString(7, modifiedBy);
			preparedStatement.setTimestamp(8, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			if (override) {
				getRepository().getDbUtils().closeStatement(preparedStatement);
				getRepository().getDbUtils().closeConnection(connection);
				updateFile(name, path, contentType, createdBy, modifiedBy, type);
			} else {
				throw new DBBaseException(e);
			}
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
		logger.debug("exiting insertFile"); //$NON-NLS-1$
	}

	/**
	 * Create the database file or folder object in the target database schema,
	 * located at the given path, content type, etc.
	 *
	 * @param name
	 * @param path
	 * @param contentType
	 * @param createdBy
	 * @param modifiedBy
	 * @param type
	 * @throws DBBaseException
	 */
	void updateFile(String name, String path, String contentType, String createdBy, String modifiedBy, int type) throws DBBaseException {
		logger.debug("entering updateFile"); //$NON-NLS-1$

		checkInitialized();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_UPDATE_FILE, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setInt(1, type);
			preparedStatement.setString(2, contentType);
			preparedStatement.setString(3, createdBy);
			preparedStatement.setTimestamp(4, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));
			preparedStatement.setString(5, modifiedBy);
			preparedStatement.setTimestamp(6, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));
			preparedStatement.setString(7, name);
			preparedStatement.setString(8, path);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
		logger.debug("exiting updateFile"); //$NON-NLS-1$
	}

	/**
	 * Cascading - delete the content of the text file located at the provided
	 * path
	 *
	 * @param path
	 * @throws DBBaseException
	 */
	void removeDocsCascade(String path) throws DBBaseException {

		checkInitialized();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_REMOVE_DOCS_CASCADE, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, path + DBRepository.PATH_DELIMITER + "%"); //$NON-NLS-1$
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
	}

	/**
	 * Create the file (text or binary) at the given path
	 *
	 * @param path
	 * @param bytes
	 * @param isBinary
	 * @param contentType
	 * @return
	 * @throws DBBaseException
	 */
	public DBFile createFile(String path, byte[] bytes, boolean isBinary, String contentType) throws DBBaseException {
		return createFile(path, bytes, isBinary, contentType, false);
	}

	/**
	 * Create the file (text or binary) at the given path
	 *
	 * @param path
	 * @param bytes
	 * @param isBinary
	 * @param contentType
	 * @param override
	 * @return
	 * @throws DBBaseException
	 */
	public DBFile createFile(String path, byte[] bytes, boolean isBinary, String contentType, boolean override) throws DBBaseException {

		checkInitialized();

		if ((path == null) || "".equals(path.trim())) { //$NON-NLS-1$
			return null;
		}

		String createdBy = getRepository().getUser();
		String modifiedBy = getRepository().getUser();

		DBFile resource = getFileByPath(path);
		if ((override) || (resource == null)) {
			String name = path.substring(path.lastIndexOf(DBRepository.PATH_DELIMITER) + 1);
			String collectionPath = path.substring(0, path.lastIndexOf(DBRepository.PATH_DELIMITER));
			DBFolder parent = getDbRepositoryDAO().getDbFolderDAO().createFolder(collectionPath);
			if (parent != null) {
				insertFile(name, path, contentType, createdBy, modifiedBy, isBinary ? DBMapper.OBJECT_TYPE_BINARY : DBMapper.OBJECT_TYPE_DOCUMENT,
						override);
				resource = getFileByPath(path);
				removeDocument(resource);
				removeBinary(resource);
				if (isBinary) {
					insertBinary(resource, bytes);
				} else {
					insertDocument(resource, bytes);
				}
			}

		}

		return resource;
	}

	/**
	 * Create chunks of content for a given text file
	 *
	 * @param resource
	 * @param bytes
	 * @throws DBBaseException
	 */
	private void insertDocument(DBFile resource, byte[] bytes) throws DBBaseException {

		checkInitialized();

		if (bytes == null) {
			bytes = new byte[] {};
		}

		if (bytes.length > DOC_CHUNK_SIZE) {
			byte[][] chunks = divideArray(bytes, DOC_CHUNK_SIZE);
			for (int i = 0; i < (chunks.length - 1); i++) {
				insertDocumentSingle(resource, chunks[i], i);
			}
			insertDocumentSingle(resource, chunks[chunks.length - 1], chunks.length - 1);
		} else {
			insertDocumentSingle(resource, bytes, 0);
		}

	}

	/**
	 * Utility method for splitting byte array by chunks
	 *
	 * @param source
	 * @param chuncksize
	 * @return
	 */
	private byte[][] divideArray(byte[] source, int chuncksize) {

		int chunksCount = (int) Math.ceil(source.length / (double) chuncksize);
		byte[][] ret = new byte[chunksCount][chuncksize];

		int start = 0;

		for (int i = 0; i < (ret.length - 1); i++) {
			ret[i] = Arrays.copyOfRange(source, start, start + chuncksize);
			start += chuncksize;
		}
		int lastChunkLength = source.length - ((ret.length - 1) * chuncksize);
		byte[] lastChunk = new byte[lastChunkLength];
		lastChunk = Arrays.copyOfRange(source, start, start + lastChunkLength);
		ret[ret.length - 1] = lastChunk;

		return ret;
	}

	/**
	 * Create single chunk of content for a given text file
	 *
	 * @param resource
	 * @param bytes
	 * @param order
	 * @throws DBBaseException
	 */
	private void insertDocumentSingle(DBFile resource, byte[] bytes, int order) throws DBBaseException {

		checkInitialized();

		if (bytes.length > DOC_CHUNK_SIZE) {
			// TODO refactor API - methods to throws typed exceptions
			throw new RuntimeException(SINGLE_RESOURCES_BIGGER_THAN_4K_NOT_SUPPORTED);
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_INSERT_DOCUMENT, this.getClass());

			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, resource.getPath());
			preparedStatement.setString(2, new String(bytes, Charset.defaultCharset()));
			preparedStatement.setInt(3, order);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}

	}

	/**
	 * Create the document (content chunks)
	 *
	 * @param resource
	 * @param bytes
	 * @throws DBBaseException
	 */
	void setDocument(DBFile resource, byte[] bytes) throws DBBaseException {

		checkInitialized();

		removeDocument(resource);
		insertDocument(resource, bytes);

		setModified(resource);
	}

	void setModified(DBFile resource) {

		checkInitialized();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_SET_MODIFIED, this.getClass());

			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, getRepository().getUser());
			preparedStatement.setTimestamp(2, new Timestamp(GregorianCalendar.getInstance().getTime().getTime()));
			preparedStatement.setString(3, resource.getPath());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}

		getRepository().getCacheManager().clear(resource.getPath());
	}

	/**
	 * Retrieve the document content - combine the chunks
	 *
	 * @param resource
	 * @return
	 * @throws DBBaseException
	 */
	byte[] getDocument(DBFile resource) throws DBBaseException {

		checkInitialized();

		if (resource == null) {
			return null;
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_GET_DOCUMENT, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, resource.getPath());
			ResultSet resultSet = preparedStatement.executeQuery();
			byte[] bytes = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (resultSet.next()) {
				bytes = DBMapper.dbToData(resultSet);
				baos.write(bytes);
			}
			return baos.toByteArray();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
	}

	/**
	 * Delete database file by given path
	 *
	 * @param path
	 * @throws DBBaseException
	 */
	void removeFileByPath(String path) throws DBBaseException {

		checkInitialized();

		removeDocument(getFileByPath(path));
		removeBinary(getFileByPath(path));

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_REMOVE_FILE_BY_PATH, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, path);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}

		getRepository().getCacheManager().clear(path);
	}

	/**
	 * Delete the content (all the chunks) for a given file
	 *
	 * @param resource
	 * @throws DBBaseException
	 */
	private void removeDocument(DBFile resource) throws DBBaseException {

		checkInitialized();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_REMOVE_DOCUMENT, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, resource.getPath());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
	}

	/**
	 * Retrieve the binary content of a file
	 *
	 * @param resource
	 * @return
	 * @throws DBBaseException
	 */
	public byte[] getBinary(DBFile resource) throws DBBaseException {

		checkInitialized();

		if (resource == null) {
			return null;
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_GET_BINARY, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, resource.getPath());
			ResultSet resultSet = preparedStatement.executeQuery();
			byte[] bytes = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (resultSet.next()) {
				bytes = DBMapper.dbToDataBinary(connection, resultSet, BIN_CONTENT);
				baos.write(bytes);
			}
			return baos.toByteArray();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}
	}

	/**
	 * Create the binary content of for a given file, as before this clean the
	 * content if any
	 *
	 * @param resource
	 * @param bytes
	 * @param contentType
	 * @throws DBBaseException
	 */
	public void setBinary(DBFile resource, byte[] bytes, String contentType) throws DBBaseException {

		checkInitialized();

		removeBinary(resource);
		insertBinary(resource, bytes);

		setModified(resource);
	}

	/**
	 * Create the binary content of for a given file
	 *
	 * @param resource
	 * @param bytes
	 * @throws DBBaseException
	 */
	private void insertBinary(DBFile resource, byte[] bytes) throws DBBaseException {

		checkInitialized();

		if (bytes == null) {
			bytes = new byte[] {};
		}

		if (bytes.length > BIN_MAX_SIZE) {
			throw new DBBaseException(MAX_SIZE_OF_BINARY_RESOURCE_IS + BIN_MAX_SIZE);
		} else {

			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try {
				connection = getRepository().getDbUtils().getConnection();
				String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_INSERT_BINARY, this.getClass());
				preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
				preparedStatement.setString(1, resource.getPath());
				preparedStatement.setBinaryStream(2, new ByteArrayInputStream(bytes), bytes.length);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				throw new DBBaseException(e);
			} catch (IOException e) {
				throw new DBBaseException(e);
			} finally {
				getRepository().getDbUtils().closeStatement(preparedStatement);
				getRepository().getDbUtils().closeConnection(connection);
			}
		}

	}

	/**
	 * Delete the binary content of for a given file
	 *
	 * @param resource
	 * @throws DBBaseException
	 */
	private void removeBinary(DBFile resource) throws DBBaseException {

		checkInitialized();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_REMOVE_BINARY, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, resource.getPath());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBBaseException(e);
		} catch (IOException e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}

	}

	public void renameFileByPath(String path, String newPath) throws DBBaseException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getRepository().getDbUtils().getConnection();

			renameDocuments(connection, path, newPath);
			renameBinaries(connection, path, newPath);

			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_GET_FILES_BY_PATH_CASCADE, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, path + "%");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String oldFilePath = resultSet.getString("FILE_PATH");
				String newFilePath = oldFilePath.replace(path, newPath);
				String newName = new RepositoryPath(newFilePath).getLastSegment();
				try {
					script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_RENAME_FILE, this.getClass());
					preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
					preparedStatement.setString(1, newFilePath);
					preparedStatement.setString(2, newName);
					preparedStatement.setString(3, oldFilePath);
					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					throw new DBBaseException(e);
				} finally {
					getRepository().getDbUtils().closeStatement(preparedStatement);
				}
			}
		} catch (Exception e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
			getRepository().getDbUtils().closeConnection(connection);
		}

		getRepository().getCacheManager().clear();

	}

	private void renameDocuments(Connection connection, String path, String newPath) throws DBBaseException {

		PreparedStatement preparedStatement = null;
		try {
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_GET_DOCUMENTS_BY_PATH_CASCADE, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, path + "%");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String oldFilePath = resultSet.getString("DOC_FILE_PATH");
				String newFilePath = oldFilePath.replace(path, newPath);
				try {
					script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_RENAME_DOCUMENT, this.getClass());
					preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
					preparedStatement.setString(1, newFilePath);
					preparedStatement.setString(2, oldFilePath);
					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					throw new DBBaseException(e);
				} finally {
					getRepository().getDbUtils().closeStatement(preparedStatement);
				}
			}
		} catch (Exception e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
		}

		getRepository().getCacheManager().clear();

	}

	private void renameBinaries(Connection connection, String path, String newPath) throws DBBaseException {

		PreparedStatement preparedStatement = null;
		try {
			String script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_GET_BINARIES_BY_PATH_CASCADE, this.getClass());
			preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
			preparedStatement.setString(1, path + "%");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String oldFilePath = resultSet.getString("BIN_FILE_PATH");
				String newFilePath = oldFilePath.replace(path, newPath);
				try {
					script = getRepository().getDbUtils().readScript(connection, DBScriptsMap.SCRIPT_RENAME_BINARY, this.getClass());
					preparedStatement = getRepository().getDbUtils().getPreparedStatement(connection, script);
					preparedStatement.setString(1, newFilePath);
					preparedStatement.setString(2, oldFilePath);
					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					throw new DBBaseException(e);
				} finally {
					getRepository().getDbUtils().closeStatement(preparedStatement);
				}
			}
		} catch (Exception e) {
			throw new DBBaseException(e);
		} finally {
			getRepository().getDbUtils().closeStatement(preparedStatement);
		}

		getRepository().getCacheManager().clear();

	}

}
