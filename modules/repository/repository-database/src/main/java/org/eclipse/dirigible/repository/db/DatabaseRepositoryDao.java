/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.repository.db;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositorySearchException;
import org.eclipse.dirigible.repository.api.RepositoryVersioningException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Database Repository DAO.
 */
public class DatabaseRepositoryDao {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseRepositoryDao.class);

	private static final String LAST = "last";

	private static final String MODIFIED_AT = "modifiedAt";

	private static final String MODIFIED_BY = "modifiedBy";

	private static final String CREATED_AT = "createdAt";

	private static final String CREATED_BY = "createdBy";

	static final int OBJECT_TYPE_FOLDER = 0;

	static final int OBJECT_TYPE_DOCUMENT = 1;

	static final int OBJECT_TYPE_BINARY = 2;

	private DatabaseRepository repository = (DatabaseRepository) StaticObjects.get(StaticObjects.DATABASE_REPOSITORY);

	private DataSource datasource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);

	/**
	 * Instantiates a new database repository dao.
	 */
	public DatabaseRepositoryDao() {

	}

	/**
	 * Instantiates a new database repository dao.
	 *
	 * @param repository
	 *            the provided repository
	 * @param datasource
	 *            the provided datasource
	 */
	public DatabaseRepositoryDao(DatabaseRepository repository, DataSource datasource) {
		this.repository = repository;
		this.datasource = datasource;
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public DatabaseRepository getRepository() {
		return this.repository;
	}

	/**
	 * Creates the file.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param isBinary
	 *            the is binary
	 * @param contentType
	 *            the content type
	 * @throws DatabaseRepositoryException
	 *             the database repository exception
	 */
	public void createFile(String path, byte[] content, boolean isBinary, String contentType) throws DatabaseRepositoryException {
		createFile(path, content, isBinary, contentType, true);
		((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
	}

	/**
	 * Creates the file.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param isBinary
	 *            the is binary
	 * @param contentType
	 *            the content type
	 * @param override
	 *            to override if exist
	 * @throws DatabaseRepositoryException
	 *             the database repository exception
	 */
	public void createFile(String path, byte[] content, boolean isBinary, String contentType, boolean override) throws DatabaseRepositoryException {
		try {
			if (!fileExists(path) || override) {
				Connection connection = null;
				try {
					connection = openConnection();
					ensureFoldersCreated(path);
					DatabaseRepositoryUtils.saveFile(connection, path, content, isBinary, contentType);
				} finally {
					closeConnection(connection);
				}
				((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
				createVersion(path, content);
			}
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
	}

	private void ensureFoldersCreated(String path) throws SQLException {
		RepositoryPath fullPath = new RepositoryPath(path).getParentPath();
		StringBuilder buff = new StringBuilder();
		for (String segment : fullPath.getSegments()) {
			buff.append(IRepository.SEPARATOR).append(segment);
			createFolder(buff.toString());
		}
	}

	private void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	private Connection openConnection() throws SQLException {
		return datasource.getConnection();
	}

	/**
	 * Creates the version.
	 *
	 * @param path
	 *            the workspace path
	 * @param content
	 *            the content
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws SQLException
	 *             on sql error
	 */
	private void createVersion(String path, byte[] content) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			int version = DatabaseRepositoryUtils.getLastFileVersion(connection, path);
			DatabaseRepositoryUtils.saveFileVersion(connection, path, ++version, content);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Removes the versions.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws SQLException
	 *             sql error
	 */
	private void removeVersions(String path) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			DatabaseRepositoryUtils.removeFileVersions(connection, path);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Check initialized.
	 */
	public void checkInitialized() {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the file content.
	 *
	 * @param databaseFile
	 *            the database file
	 * @param content
	 *            the content
	 * @throws SQLException
	 */
	public void setFileContent(DatabaseFile databaseFile, byte[] content) throws SQLException {
		try {
			String workspacePath = databaseFile.getPath();
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.saveFile(connection, databaseFile.getPath(), content, databaseFile.isBinary(), databaseFile.getContentType());
			} finally {
				closeConnection(connection);
			}
			((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
			createVersion(workspacePath, content);
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
	}

	/**
	 * Gets the file content.
	 *
	 * @param databaseFile
	 *            the database file
	 * @return the file content
	 */
	public byte[] getFileContent(DatabaseFile databaseFile) {
		try {
			String workspacePath = databaseFile.getPath();
			Connection connection = null;
			try {
				connection = openConnection();
				return DatabaseRepositoryUtils.loadFile(connection, workspacePath);
			} finally {
				closeConnection(connection);
			}
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
	}

	/**
	 * Rename file.
	 *
	 * @param path
	 *            the path
	 * @param newPath
	 *            the new path
	 * @throws SQLException
	 */
	public void renameFile(String path, String newPath) throws SQLException {
		try {
			Connection connection = null;
			byte[] content = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.moveFile(connection, path, newPath);
				content = DatabaseRepositoryUtils.loadFile(connection, newPath);
			} finally {
				closeConnection(connection);
			}
			((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
			if (content != null) {
				createVersion(newPath, content);
				removeVersions(path);
			}

		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
	}

	/**
	 * Copy file.
	 *
	 * @param path
	 *            the path
	 * @param newPath
	 *            the new path
	 * @throws SQLException
	 */
	public void copyFile(String path, String newPath) throws SQLException {
		try {
			byte[] content = null;
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.copyFile(connection, path, newPath);
				content = DatabaseRepositoryUtils.loadFile(connection, newPath);
			} finally {
				closeConnection(connection);
			}
			((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
			if (content != null) {
				createVersion(newPath, content);
			}
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
	}

	/**
	 * Removes the file by path.
	 *
	 * @param path
	 *            the path
	 * @throws SQLException
	 */
	public void removeFileByPath(String path) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.removeFile(connection, path);
			} finally {
				closeConnection(connection);
			}
			((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
			removeVersions(path);
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
	}

	/**
	 * Removes the folder by path.
	 *
	 * @param path
	 *            the path
	 * @throws SQLException
	 */
	public void removeFolderByPath(String path) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.removeFile(connection, path);
			} finally {
				closeConnection(connection);
			}
			((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
			removeVersions(path);
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
	}

	/**
	 * Creates the folder.
	 *
	 * @param path
	 *            the normalize path
	 * @throws SQLException
	 */
	public void createFolder(String path) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.createFolder(connection, path);
			} finally {
				closeConnection(connection);
			}
			((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
		} catch (RepositoryWriteException e) {
			throw new DatabaseRepositoryException(e);
		}
	}

	/**
	 * Rename folder.
	 *
	 * @param path
	 *            the path
	 * @param newPath
	 *            the new path
	 * @throws SQLException
	 */
	public void renameFolder(String path, String newPath) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.moveFile(connection, path, newPath);
			} finally {
				closeConnection(connection);
			}
			((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
			removeVersions(newPath);
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}

	}

	/**
	 * Copy folder.
	 *
	 * @param path
	 *            the source path
	 * @param newPath
	 *            the target path
	 * @throws DatabaseRepositoryException
	 *             in case of error
	 */
	public void copyFolder(String path, String newPath) throws DatabaseRepositoryException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.copyFolder(connection, path, newPath);
			} finally {
				closeConnection(connection);
			}
			((DatabaseRepository) getRepository()).setLastModified(System.currentTimeMillis());
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}

	}

	/**
	 * Gets the object by path.
	 *
	 * @param path
	 *            the path
	 * @return the object by path
	 * @throws SQLException
	 */
	public DatabaseObject getObjectByPath(String path) throws SQLException {
		DatabaseObject databaseObject = null;
		try {
			if (fileExists(path)) {
				String contentType = ContentTypeHelper.getContentType(FilenameUtils.getExtension(path));
				databaseObject = new DatabaseFile(repository, ContentTypeHelper.isBinary(contentType), contentType);
			} else {
				databaseObject = new DatabaseFolder(repository);
			}
			String name = path.substring(path.lastIndexOf(IRepository.SEPARATOR) + 1);
			databaseObject.setName(name);
			databaseObject.setPath(path);

			if (fileExists(path)) {
				try {
					Connection connection = null;
					try {
						connection = openConnection();
						DatabaseFileDefinition fileDefinition = DatabaseRepositoryUtils.getFile(connection, path);
						databaseObject.setCreatedBy(fileDefinition.getCreatedBy());
						long prop = fileDefinition.getCreatedAt();
						if (prop != 0) {
							databaseObject.setCreatedAt(new Date(prop));
						}
						databaseObject.setModifiedBy(fileDefinition.getModifiedBy());
						prop = fileDefinition.getModifiedAt();
						if (prop != 0) {
							databaseObject.setModifiedAt(new Date(prop));
						}
					} finally {
						closeConnection(connection);
					}
				} catch (Throwable e) {
					throw new DatabaseRepositoryException(e);
				}
			}

		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
		return databaseObject;

	}

	/**
	 * Gets the children by folder.
	 *
	 * @param path
	 *            the path
	 * @return the children by folder
	 * @throws SQLException
	 */
	public List<DatabaseObject> getChildrenByFolder(String path) throws SQLException {
		List<DatabaseObject> databaseObjects = new ArrayList<DatabaseObject>();
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				List<DatabaseFileDefinition> definitions = DatabaseRepositoryUtils.findChildren(connection, path);
				for (DatabaseFileDefinition definition : definitions) {
					databaseObjects.add(getObjectByPath(definition.getPath()));
				}
			} finally {
				closeConnection(connection);
			}

		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
		}
		return databaseObjects;
	}

	/**
	 * Gets the resource versions by path.
	 *
	 * @param path
	 *            the path
	 * @return the resource versions by path
	 * @throws RepositoryVersioningException
	 *             the repository versioning exception
	 * @throws RepositoryVersioningException
	 *             in case of error
	 */
	public List<IResourceVersion> getResourceVersionsByPath(String path) throws RepositoryVersioningException {
		List<IResourceVersion> versions = new ArrayList<IResourceVersion>();
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				List<DatabaseFileVersionDefinition> definitions = DatabaseRepositoryUtils.findFileVersions(connection, path);
				for (DatabaseFileVersionDefinition definition : definitions) {
					DatabaseResourceVersion databaseResourceVersion = new DatabaseResourceVersion(getRepository(), new RepositoryPath(path),
							definition.getVersion());
					versions.add(databaseResourceVersion);
				}
			} finally {
				closeConnection(connection);
			}
		} catch (Exception e) {
			throw new RepositoryVersioningException(e);
		}
		Collections.sort(versions);
		return versions;
	}

	/**
	 * Gets the file version by path.
	 *
	 * @param path
	 *            the path
	 * @param version
	 *            the version
	 * @return the file version by path
	 * @throws RepositoryVersioningException
	 *             the repository versioning exception
	 */
	public DatabaseFileVersion getFileVersionByPath(String path, int version) throws RepositoryVersioningException {
		try {
			return getLocalFileVersionByPath(version, path);
		} catch (SQLException e) {
			throw new RepositoryVersioningException(e);
		}
	}

	/**
	 * Gets the database file version by path.
	 *
	 * @param version
	 *            the version
	 * @param path
	 *            the workspace path
	 * @return the database file version by path
	 * @throws RepositoryVersioningException
	 *             the repository versioning exception
	 * @throws SQLException
	 */
	private DatabaseFileVersion getLocalFileVersionByPath(int version, String path) throws RepositoryVersioningException, SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			DatabaseFileVersionDefinition versionDefinition = DatabaseRepositoryUtils.getFileVersion(connection, path, version);
			DatabaseFileDefinition fileDefinition = DatabaseRepositoryUtils.getFile(connection, path);
			DatabaseFileVersion fileVersion = new DatabaseFileVersion(repository,
					fileDefinition.getType() == DatabaseFileDefinition.OBJECT_TYPE_BINARY, fileDefinition.getContentType(), version,
					versionDefinition.getContent());
			fileVersion.setName(versionDefinition.getName());
			fileVersion.setPath(versionDefinition.getPath());
			fileVersion.setCreatedAt(new Date(versionDefinition.getCreatedAt()));
			fileVersion.setCreatedBy(versionDefinition.getCreatedBy());
			fileVersion.setModifiedAt(new Date(versionDefinition.getModifiedAt()));
			fileVersion.setModifiedBy(versionDefinition.getModifiedBy());
			return fileVersion;
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	private String getUser() {
		return UserFacade.getName();
	}

	/**
	 * Discposing resources if needed
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/**
	 * Clean-up versions older than the given period
	 */
	public void cleanupOldVersions() {
		// TODO Auto-generated method stub

	}

	/**
	 * Search by name of the resource
	 *
	 * @param parameter
	 *            the search term
	 * @param caseInsensitive
	 *            whether to be case sensitive
	 * @return list of entities
	 * @throws RepositorySearchException
	 *             in case of an error
	 */
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return searchName(null, parameter, caseInsensitive);
	}

	/**
	 * Search by name of the resource
	 *
	 * @param root
	 *            the relative root
	 * @param parameter
	 *            the search term
	 * @param caseInsensitive
	 *            whether to be case sensitive
	 * @return list of entities
	 */
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) {
		List<IEntity> results = new ArrayList<IEntity>();
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				List<DatabaseFileDefinition> databaseFileDefinitions = null;
				if (root != null) {
					databaseFileDefinitions = DatabaseRepositoryUtils.searchName(connection, root, parameter, caseInsensitive);
				} else {
					databaseFileDefinitions = DatabaseRepositoryUtils.searchName(connection, parameter, caseInsensitive);
				}

				for (DatabaseFileDefinition databaseFileDefinition : databaseFileDefinitions) {
					DatabaseEntity databaseEntity = null;
					if (databaseFileDefinition.getType() == DatabaseFileDefinition.OBJECT_TYPE_FOLDER) {
						databaseEntity = new DatabaseCollection(repository, new RepositoryPath(databaseFileDefinition.getPath()));
					} else {
						databaseEntity = new DatabaseResource(repository, new RepositoryPath(databaseFileDefinition.getPath()));
					}
					results.add(databaseEntity);
				}
				return results;
			} finally {
				closeConnection(connection);
			}
		} catch (SQLException e) {
			throw new RepositorySearchException(e);
		}
	}

	/**
	 * Search by the full path of a resource
	 *
	 * @param parameter
	 *            the search term
	 * @param caseInsensitive
	 *            whether to be case sensitive
	 * @return list of entities
	 */
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) {
		List<IEntity> results = new ArrayList<IEntity>();
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				List<DatabaseFileDefinition> databaseFileDefinitions = DatabaseRepositoryUtils.searchPath(connection, parameter, caseInsensitive);
				for (DatabaseFileDefinition databaseFileDefinition : databaseFileDefinitions) {
					DatabaseEntity databaseEntity = null;
					if (databaseFileDefinition.getType() == DatabaseFileDefinition.OBJECT_TYPE_FOLDER) {
						databaseEntity = new DatabaseCollection(repository, new RepositoryPath(databaseFileDefinition.getPath()));
					} else {
						databaseEntity = new DatabaseResource(repository, new RepositoryPath(databaseFileDefinition.getPath()));
					}
					results.add(databaseEntity);
				}
				return results;
			} finally {
				closeConnection(connection);
			}
		} catch (SQLException e) {
			throw new RepositorySearchException(e);
		}
	}

	/**
	 * Whether the folder exists
	 *
	 * @param path
	 *            the path
	 * @return true if exists and false otherwise
	 * @throws SQLException
	 *             in case of an error
	 */
	public boolean folderExists(String path) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			return DatabaseRepositoryUtils.existsFolder(connection, path);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Whether the file exists
	 * 
	 * @param path
	 *            the path
	 * @return true if exists and false otherwise
	 * @throws SQLException
	 *             in case of an error
	 */
	public boolean fileExists(String path) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			return DatabaseRepositoryUtils.existsFile(connection, path);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * List all the resources paths
	 * 
	 * @return the list
	 * @throws SQLException
	 *             in case of an error
	 */
	public List<String> getAllResourcePaths() throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			return DatabaseRepositoryUtils.getAllResourcePaths(connection);
		} finally {
			closeConnection(connection);
		}
	}

}
