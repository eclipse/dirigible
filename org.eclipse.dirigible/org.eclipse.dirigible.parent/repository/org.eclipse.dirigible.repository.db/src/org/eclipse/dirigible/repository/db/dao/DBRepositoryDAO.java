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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.db.DBResourceVersion;
import org.eclipse.dirigible.repository.db.init.DBRepositoryInitializer;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Utility class for direct database manipulation via JDBC
 * Base tables:
 * DGB_FILES - the files and folder registry DGB_DOCUMENTS - the content of text
 * files separated by chunks DGB_BINARIES - the content of binary files
 * DGB_SCHEMA_VERSIONS - the version of the current repository schema
 */
public class DBRepositoryDAO {

	private static Logger logger = Logger.getLogger(DBRepositoryDAO.class);

	private static final String DATA_ACCESS_OBJECT_FOR_REPOSITORY_NOT_INITIALIZED = Messages
			.getString("DBRepositoryDAO.DATA_ACCESS_OBJECT_FOR_REPOSITORY_NOT_INITIALIZED"); //$NON-NLS-1$

	private DBRepository repository;
	private boolean initialized = false;
	private DBObjectDAO dbObjectDAO;
	private DBFolderDAO dbFolderDAO;
	private DBFileDAO dbFileDAO;
	private DBSearchDAO dbSearchDAO;
	private DBFileVersionDAO dbFileVersionDAO;

	public DBRepositoryDAO(DBRepository repository) {
		logger.debug("entering constructor"); //$NON-NLS-1$

		this.repository = repository;
		this.dbObjectDAO = new DBObjectDAO(this);
		this.dbFolderDAO = new DBFolderDAO(this);
		this.dbFileDAO = new DBFileDAO(this);
		this.dbSearchDAO = new DBSearchDAO(this);
		this.dbFileVersionDAO = new DBFileVersionDAO(this);

		logger.debug("exiting constructor"); //$NON-NLS-1$
	}

	/**
	 * Getter for the Repository object
	 *
	 * @return the DB Repository
	 */
	DBRepository getRepository() {
		logger.debug("entering getRepository"); //$NON-NLS-1$
		DBRepository dbRepository = repository;
		logger.debug("exiting getRepository"); //$NON-NLS-1$
		return dbRepository;
	}

	/**
	 * Getter for the DBObjectDAO object
	 *
	 * @return DB Object DAO
	 */
	public DBObjectDAO getDbObjectDAO() {
		return dbObjectDAO;
	}

	/**
	 * Getter for the DBFolderDAO object
	 *
	 * @return DB Folder DAO
	 */
	public DBFolderDAO getDbFolderDAO() {
		return dbFolderDAO;
	}

	/**
	 * Getter for the DBFileDAO object
	 *
	 * @return DB File DAO
	 */
	public DBFileDAO getDbFileDAO() {
		return dbFileDAO;
	}

	/**
	 * Getter for the DBSearchDAO object
	 *
	 * @return DB Search DAO
	 */
	public DBSearchDAO getDbSearchDAO() {
		return dbSearchDAO;
	}

	/**
	 * Initialize the database schema
	 *
	 * @param forceRecreate
	 *            recreate or hard reset if needed
	 * @return whether the initialization was successful
	 * @throws SQLException
	 *             SQL Exception
	 */
	public boolean initialize(boolean forceRecreate) throws SQLException {
		logger.debug("entering initialize"); //$NON-NLS-1$
		synchronized (DBRepositoryDAO.class) {
			Connection connection = null;
			try {
				connection = getRepository().getDbUtils().getConnection();
				initialized = initialize(connection, forceRecreate);
			} finally {
				getRepository().getDbUtils().closeConnection(connection);
			}
		}
		logger.debug("exiting initialize"); //$NON-NLS-1$
		return initialized;
	}

	/**
	 * The subprocess of the above
	 *
	 * @param connection
	 *            the connection
	 * @param forceRecreate
	 *            whether to force recreation
	 * @return whether the initialization was successful
	 */
	private boolean initialize(Connection connection, boolean forceRecreate) {
		logger.debug("entering initialize with connection"); //$NON-NLS-1$
		DBRepositoryInitializer dbRepositoryInitializer = new DBRepositoryInitializer(repository.getDataSource(), connection, forceRecreate);
		boolean result = dbRepositoryInitializer.initialize();
		logger.debug("exiting initialize with connection"); //$NON-NLS-1$
		return result;
	}

	/**
	 * Check whether the database schema has already been initialized
	 *
	 * @throws DBBaseException
	 *             DB Exception
	 */
	void checkInitialized() throws DBBaseException {
		if (!initialized) {
			throw new DBBaseException(DATA_ACCESS_OBJECT_FOR_REPOSITORY_NOT_INITIALIZED);
		}
	}

	/**
	 * Query the database and retrieve the database object based on the provided
	 * path
	 *
	 * @param path
	 *            the path
	 * @return DB Object
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public DBObject getObjectByPath(String path) throws DBBaseException {
		return this.dbObjectDAO.getObjectByPath(path);
	}

	/**
	 * Return the database folder object
	 *
	 * @param path
	 *            the path
	 * @return DB Folder
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public DBFolder getFolderByPath(String path) throws DBBaseException {
		return this.dbFolderDAO.getFolderByPath(path);
	}

	/**
	 * Return the database file object
	 *
	 * @param path
	 *            the path
	 * @return DB File
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public DBFile getFileByPath(String path) throws DBBaseException {
		return this.dbFileDAO.getFileByPath(path);
	}

	/**
	 * Create the database folder object in the target database schema, located
	 * at the given path - cascading
	 *
	 * @param path
	 *            the path
	 * @return DB Folder
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public DBFolder createFolder(String path) throws DBBaseException {
		return this.dbFolderDAO.createFolder(path);
	}

	/**
	 * Delete the database folder object based on the given path
	 *
	 * @param path
	 *            the path
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public void removeFolderByPath(String path) throws DBBaseException {
		this.dbFolderDAO.removeFolderByPath(path);
	}

	/**
	 * Create the file (text or binary) at the given path
	 *
	 * @param path
	 *            the path
	 * @param bytes
	 *            the content
	 * @param isBinary
	 *            whether it is a binary content
	 * @param contentType
	 *            the content type
	 * @return DB File
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public DBFile createFile(String path, byte[] bytes, boolean isBinary, String contentType) throws DBBaseException {
		return createFile(path, bytes, isBinary, contentType, false);
	}

	/**
	 * Create the file (text or binary) at the given path
	 *
	 * @param path
	 *            the path
	 * @param bytes
	 *            the content
	 * @param isBinary
	 *            whether it is a binary content
	 * @param contentType
	 *            the content type
	 * @param override
	 *            whether to overrider the content
	 * @return DB File
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public DBFile createFile(String path, byte[] bytes, boolean isBinary, String contentType, boolean override) throws DBBaseException {
		DBFile dbFile = this.dbFileDAO.createFile(path, bytes, isBinary, contentType, override);
		this.dbFileVersionDAO.createFileVersion(path, bytes, isBinary, contentType);
		return dbFile;
	}

	/**
	 * Create the document (content chunks)
	 *
	 * @param resource
	 *            the file
	 * @param bytes
	 *            the content
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public void setDocument(DBFile resource, byte[] bytes) throws DBBaseException {
		this.dbFileDAO.setDocument(resource, bytes);
		this.dbFileVersionDAO.createFileVersion(resource.getPath(), bytes, resource.isBinary(), resource.getContentType());
	}

	/**
	 * Retrieve the document content - combine the chunks
	 *
	 * @param resource
	 *            the file
	 * @return the content
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public byte[] getDocument(DBFile resource) throws DBBaseException {
		return this.dbFileDAO.getDocument(resource);
	}

	/**
	 * Delete database file by given path
	 *
	 * @param path
	 *            the path
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public void removeFileByPath(String path) throws DBBaseException {
		this.dbFileDAO.removeFileByPath(path);
		this.dbFileVersionDAO.removeAllFileVersions(path);
	}

	public void cleanupOldVersions() throws DBBaseException {
		Calendar calendar = new GregorianCalendar();
		calendar.roll(Calendar.MONTH, false);
		this.dbFileVersionDAO.removeAllFileVersionsBeforeDate(calendar.getTime());
	}

	public void dispose() {
	}

	/**
	 * Iterate the sub-folders and files of a given folder
	 *
	 * @param path
	 *            the path
	 * @return a list of DB Objects
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public List<DBObject> getChildrenByFolder(String path) throws DBBaseException {
		return this.dbFolderDAO.getChildrenByFolder(path);
	}

	/**
	 * Retrieve the binary content of a file
	 *
	 * @param resource
	 *            the file
	 * @return the content
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public byte[] getBinary(DBFile resource) throws DBBaseException {
		return this.dbFileDAO.getBinary(resource);
	}

	/**
	 * Create the binary content of for a given file, as before this clean the
	 * content if any
	 *
	 * @param resource
	 *            the file
	 * @param bytes
	 *            the content
	 * @param contentType
	 *            the content type
	 * @throws DBBaseException
	 *             DB Exception
	 */
	public void setBinary(DBFile resource, byte[] bytes, String contentType) throws DBBaseException {
		this.dbFileDAO.setBinary(resource, bytes, contentType);
	}

	/**
	 * Search for files and folders containing the parameter in their name
	 * (means %parameter)
	 *
	 * @param parameter
	 *            the search parameter
	 * @param caseInsensitive
	 *            whether the search is case sensitive
	 * @return list of entities matching the search
	 */
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) {
		return this.dbSearchDAO.searchName(parameter, caseInsensitive);
	}

	/**
	 * Search for files and folders containing the parameter in their name
	 * (means %parameter) under specified root folder (means *root)
	 *
	 * @param root
	 *            the root
	 * @param parameter
	 *            the search parameter
	 * @param caseInsensitive
	 *            whether the search is case sensitive
	 * @return list of entities matching the search
	 */
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) {
		return this.dbSearchDAO.searchName(root, parameter, caseInsensitive);
	}

	/**
	 * Search for files and folders containing the parameter in their name
	 * (means %parameter%)
	 *
	 * @param parameter
	 *            the search parameter
	 * @param caseInsensitive
	 *            whether the search is case sensitive
	 * @return list of entities matching the search
	 */
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) {
		return this.dbSearchDAO.searchPath(parameter, caseInsensitive);
	}

	/**
	 * Search for files containing the parameter in their content
	 *
	 * @param parameter
	 *            the search parameter
	 * @param caseInsensitive
	 *            whether the search is case sensitive
	 * @return list of entities matching the search
	 */
	public List<IEntity> searchText(String parameter, boolean caseInsensitive) {
		return this.dbSearchDAO.searchInPathAndText(parameter, caseInsensitive);
	}

	public DBFileVersion getFileVersionByPath(String path, int version) {
		return this.dbFileVersionDAO.getFileVersionByPath(path, version);
	}

	public List<DBFileVersion> getFileVersionsByPath(String path) {
		return this.dbFileVersionDAO.getFileVersionsByPath(path);
	}

	public List<IResourceVersion> getResourceVersionsByPath(String path) {
		List<IResourceVersion> resultList = new ArrayList<IResourceVersion>();
		List<DBFileVersion> fileVersions = getFileVersionsByPath(path);
		for (DBFileVersion dbFileVersion : fileVersions) {
			resultList.add(new DBResourceVersion(getRepository(), new RepositoryPath(path), dbFileVersion.getVersion()));
		}
		return resultList;
	}

	public void renameFolder(String path, String newPath) {
		this.dbFolderDAO.renameFolderByPath(path, newPath);
	}

	public void renameFile(String path, String newPath) {
		this.dbFileDAO.renameFileByPath(path, newPath);
	}
}
