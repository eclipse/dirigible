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
package org.eclipse.dirigible.cms.db.dao;

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
import org.eclipse.dirigible.cms.db.CmsDatabaseCollection;
import org.eclipse.dirigible.cms.db.CmsDatabaseEntity;
import org.eclipse.dirigible.cms.db.CmsDatabaseFile;
import org.eclipse.dirigible.cms.db.CmsDatabaseFileVersion;
import org.eclipse.dirigible.cms.db.CmsDatabaseFolder;
import org.eclipse.dirigible.cms.db.CmsDatabaseObject;
import org.eclipse.dirigible.cms.db.CmsDatabaseRepository;
import org.eclipse.dirigible.cms.db.CmsDatabaseRepositoryException;
import org.eclipse.dirigible.cms.db.CmsDatabaseResource;
import org.eclipse.dirigible.cms.db.CmsDatabaseResourceVersion;
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
public class CmisDatabaseRepositoryDao {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(CmisDatabaseRepositoryDao.class);

	/** The Constant LAST. */
	private static final String LAST = "last";

	/** The Constant MODIFIED_AT. */
	private static final String MODIFIED_AT = "modifiedAt";

	/** The Constant MODIFIED_BY. */
	private static final String MODIFIED_BY = "modifiedBy";

	/** The Constant CREATED_AT. */
	private static final String CREATED_AT = "createdAt";

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "createdBy";

	/** The Constant OBJECT_TYPE_FOLDER. */
	static final int OBJECT_TYPE_FOLDER = 0;

	/** The Constant OBJECT_TYPE_DOCUMENT. */
	static final int OBJECT_TYPE_DOCUMENT = 1;

	/** The Constant OBJECT_TYPE_BINARY. */
	static final int OBJECT_TYPE_BINARY = 2;

	/** The repository. */
	private CmsDatabaseRepository repository = (CmsDatabaseRepository) StaticObjects.get(StaticObjects.CMS_DATABASE_REPOSITORY);

	/** The datasource. */
	private DataSource datasource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);

	/**
	 * Instantiates a new database repository dao.
	 */
	public CmisDatabaseRepositoryDao() {

	}

	/**
	 * Instantiates a new database repository dao.
	 *
	 * @param repository
	 *            the provided repository
	 * @param datasource
	 *            the provided datasource
	 */
	public CmisDatabaseRepositoryDao(CmsDatabaseRepository repository, DataSource datasource) {
		this.repository = repository;
		this.datasource = datasource;
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public CmsDatabaseRepository getRepository() {
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
	 * @throws CmsDatabaseRepositoryException
	 *             the database repository exception
	 */
	public void createFile(String path, byte[] content, boolean isBinary, String contentType) throws CmsDatabaseRepositoryException {
		createFile(path, content, isBinary, contentType, true);
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
	 * @throws CmsDatabaseRepositoryException
	 *             the database repository exception
	 */
	public void createFile(String path, byte[] content, boolean isBinary, String contentType, boolean override) throws CmsDatabaseRepositoryException {
		try {
			if (!fileExists(path) || override) {
				Connection connection = null;
				try {
					connection = openConnection();
					ensureFoldersCreated(path);
					CmisDatabaseRepositoryUtils.saveFile(connection, path, content, isBinary, contentType);
				} finally {
					closeConnection(connection);
				}
				createVersion(path, content);
			}
		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Ensure folders created.
	 *
	 * @param path the path
	 * @throws SQLException the SQL exception
	 */
	private void ensureFoldersCreated(String path) throws SQLException {
		RepositoryPath fullPath = new RepositoryPath(path).getParentPath();
		StringBuilder buff = new StringBuilder();
		for (String segment : fullPath.getSegments()) {
			buff.append(IRepository.SEPARATOR).append(segment);
			createFolder(buff.toString());
		}
	}

	/**
	 * Close connection.
	 *
	 * @param connection the connection
	 * @throws SQLException the SQL exception
	 */
	private void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * Open connection.
	 *
	 * @return the connection
	 * @throws SQLException the SQL exception
	 */
	private Connection openConnection() throws SQLException {
		return datasource.getConnection();
	}

	/**
	 * Creates the version.
	 *
	 * @param path            the workspace path
	 * @param content            the content
	 * @throws SQLException             on sql error
	 */
	private void createVersion(String path, byte[] content) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			int version = CmisDatabaseRepositoryUtils.getLastFileVersion(connection, path);
			CmisDatabaseRepositoryUtils.saveFileVersion(connection, path, ++version, content);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Removes the versions.
	 *
	 * @param path the path
	 * @throws SQLException             sql error
	 */
	private void removeVersions(String path) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			CmisDatabaseRepositoryUtils.removeFileVersions(connection, path);
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
	 * @param databaseFile            the database file
	 * @param content            the content
	 * @throws SQLException the SQL exception
	 */
	public void setFileContent(CmsDatabaseFile databaseFile, byte[] content) throws SQLException {
		try {
			String workspacePath = databaseFile.getPath();
			Connection connection = null;
			try {
				connection = openConnection();
				CmisDatabaseRepositoryUtils.saveFile(connection, databaseFile.getPath(), content, databaseFile.isBinary(), databaseFile.getContentType());
			} finally {
				closeConnection(connection);
			}
			createVersion(workspacePath, content);
		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Gets the file content.
	 *
	 * @param databaseFile
	 *            the database file
	 * @return the file content
	 */
	public byte[] getFileContent(CmsDatabaseFile databaseFile) {
		try {
			String workspacePath = databaseFile.getPath();
			Connection connection = null;
			try {
				connection = openConnection();
				return CmisDatabaseRepositoryUtils.loadFile(connection, workspacePath);
			} finally {
				closeConnection(connection);
			}
		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Rename file.
	 *
	 * @param path            the path
	 * @param newPath            the new path
	 * @throws SQLException the SQL exception
	 */
	public void renameFile(String path, String newPath) throws SQLException {
		try {
			Connection connection = null;
			byte[] content = null;
			try {
				connection = openConnection();
				CmisDatabaseRepositoryUtils.moveFile(connection, path, newPath);
				content = CmisDatabaseRepositoryUtils.loadFile(connection, newPath);
			} finally {
				closeConnection(connection);
			}
			if (content != null) {
				createVersion(newPath, content);
				removeVersions(path);
			}

		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Copy file.
	 *
	 * @param path            the path
	 * @param newPath            the new path
	 * @throws SQLException the SQL exception
	 */
	public void copyFile(String path, String newPath) throws SQLException {
		try {
			byte[] content = null;
			Connection connection = null;
			try {
				connection = openConnection();
				CmisDatabaseRepositoryUtils.copyFile(connection, path, newPath);
				content = CmisDatabaseRepositoryUtils.loadFile(connection, newPath);
			} finally {
				closeConnection(connection);
			}
			if (content != null) {
				createVersion(newPath, content);
			}
		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Removes the file by path.
	 *
	 * @param path            the path
	 * @throws SQLException the SQL exception
	 */
	public void removeFileByPath(String path) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				CmisDatabaseRepositoryUtils.removeFile(connection, path);
			} finally {
				closeConnection(connection);
			}
			removeVersions(path);
		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Removes the folder by path.
	 *
	 * @param path            the path
	 * @throws SQLException the SQL exception
	 */
	public void removeFolderByPath(String path) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				CmisDatabaseRepositoryUtils.removeFile(connection, path);
			} finally {
				closeConnection(connection);
			}
			removeVersions(path);
		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Creates the folder.
	 *
	 * @param path            the normalize path
	 * @throws SQLException the SQL exception
	 */
	public void createFolder(String path) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				CmisDatabaseRepositoryUtils.createFolder(connection, path);
			} finally {
				closeConnection(connection);
			}
		} catch (RepositoryWriteException e) {
			throw new CmsDatabaseRepositoryException(e);
		}
	}

	/**
	 * Rename folder.
	 *
	 * @param path            the path
	 * @param newPath            the new path
	 * @throws SQLException the SQL exception
	 */
	public void renameFolder(String path, String newPath) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				CmisDatabaseRepositoryUtils.moveFile(connection, path, newPath);
			} finally {
				closeConnection(connection);
			}
			removeVersions(newPath);
		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}

	}

	/**
	 * Copy folder.
	 *
	 * @param path
	 *            the source path
	 * @param newPath
	 *            the target path
	 * @throws CmsDatabaseRepositoryException
	 *             in case of error
	 */
	public void copyFolder(String path, String newPath) throws CmsDatabaseRepositoryException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				CmisDatabaseRepositoryUtils.copyFolder(connection, path, newPath);
			} finally {
				closeConnection(connection);
			}
		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}

	}

	/**
	 * Gets the object by path.
	 *
	 * @param path            the path
	 * @return the object by path
	 * @throws SQLException the SQL exception
	 */
	public CmsDatabaseObject getObjectByPath(String path) throws SQLException {
		CmsDatabaseObject databaseObject = null;
		try {
			if (fileExists(path)) {
				String contentType = ContentTypeHelper.getContentType(FilenameUtils.getExtension(path));
				databaseObject = new CmsDatabaseFile(repository, ContentTypeHelper.isBinary(contentType), contentType);
			} else {
				databaseObject = new CmsDatabaseFolder(repository);
			}
			String name = path.substring(path.lastIndexOf(IRepository.SEPARATOR) + 1);
			databaseObject.setName(name);
			databaseObject.setPath(path);

			if (fileExists(path)) {
				try {
					Connection connection = null;
					try {
						connection = openConnection();
						CmisDatabaseFileDefinition fileDefinition = CmisDatabaseRepositoryUtils.getFile(connection, path);
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
					throw new CmsDatabaseRepositoryException(e);
				}
			}

		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
		return databaseObject;

	}

	/**
	 * Gets the children by folder.
	 *
	 * @param path            the path
	 * @return the children by folder
	 * @throws SQLException the SQL exception
	 */
	public List<CmsDatabaseObject> getChildrenByFolder(String path) throws SQLException {
		List<CmsDatabaseObject> databaseObjects = new ArrayList<CmsDatabaseObject>();
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				List<CmisDatabaseFileDefinition> definitions = CmisDatabaseRepositoryUtils.findChildren(connection, path);
				for (CmisDatabaseFileDefinition definition : definitions) {
					databaseObjects.add(getObjectByPath(definition.getPath()));
				}
			} finally {
				closeConnection(connection);
			}

		} catch (Exception e) {
			throw new CmsDatabaseRepositoryException(e);
		}
		return databaseObjects;
	}

	/**
	 * Gets the resource versions by path.
	 *
	 * @param path            the path
	 * @return the resource versions by path
	 * @throws RepositoryVersioningException             in case of error
	 */
	public List<IResourceVersion> getResourceVersionsByPath(String path) throws RepositoryVersioningException {
		List<IResourceVersion> versions = new ArrayList<IResourceVersion>();
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				List<CmisDatabaseFileVersionDefinition> definitions = CmisDatabaseRepositoryUtils.findFileVersions(connection, path);
				for (CmisDatabaseFileVersionDefinition definition : definitions) {
					CmsDatabaseResourceVersion databaseResourceVersion = new CmsDatabaseResourceVersion(getRepository(), new RepositoryPath(path),
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
	public CmsDatabaseFileVersion getFileVersionByPath(String path, int version) throws RepositoryVersioningException {
		try {
			return getLocalFileVersionByPath(version, path);
		} catch (SQLException e) {
			throw new RepositoryVersioningException(e);
		}
	}

	/**
	 * Gets the database file version by path.
	 *
	 * @param version            the version
	 * @param path            the workspace path
	 * @return the database file version by path
	 * @throws RepositoryVersioningException             the repository versioning exception
	 * @throws SQLException the SQL exception
	 */
	private CmsDatabaseFileVersion getLocalFileVersionByPath(int version, String path) throws RepositoryVersioningException, SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			CmisDatabaseFileVersionDefinition versionDefinition = CmisDatabaseRepositoryUtils.getFileVersion(connection, path, version);
			CmisDatabaseFileDefinition fileDefinition = CmisDatabaseRepositoryUtils.getFile(connection, path);
			CmsDatabaseFileVersion fileVersion = new CmsDatabaseFileVersion(repository,
					fileDefinition.getType() == CmisDatabaseFileDefinition.OBJECT_TYPE_BINARY, fileDefinition.getContentType(), version,
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
	 * Discposing resources if needed.
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/**
	 * Clean-up versions older than the given period.
	 */
	public void cleanupOldVersions() {
		// TODO Auto-generated method stub

	}

	/**
	 * Search by name of the resource.
	 *
	 * @param parameter            the search term
	 * @param caseInsensitive            whether to be case sensitive
	 * @return list of entities
	 * @throws RepositorySearchException             in case of an error
	 */
	public List<IEntity> searchName(String parameter, boolean caseInsensitive) throws RepositorySearchException {
		return searchName(null, parameter, caseInsensitive);
	}

	/**
	 * Search by name of the resource.
	 *
	 * @param root            the relative root
	 * @param parameter            the search term
	 * @param caseInsensitive            whether to be case sensitive
	 * @return list of entities
	 */
	public List<IEntity> searchName(String root, String parameter, boolean caseInsensitive) {
		List<IEntity> results = new ArrayList<IEntity>();
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				List<CmisDatabaseFileDefinition> databaseFileDefinitions = null;
				if (root != null) {
					databaseFileDefinitions = CmisDatabaseRepositoryUtils.searchName(connection, root, parameter, caseInsensitive);
				} else {
					databaseFileDefinitions = CmisDatabaseRepositoryUtils.searchName(connection, parameter, caseInsensitive);
				}

				for (CmisDatabaseFileDefinition databaseFileDefinition : databaseFileDefinitions) {
					CmsDatabaseEntity databaseEntity = null;
					if (databaseFileDefinition.getType() == CmisDatabaseFileDefinition.OBJECT_TYPE_FOLDER) {
						databaseEntity = new CmsDatabaseCollection(repository, new RepositoryPath(databaseFileDefinition.getPath()));
					} else {
						databaseEntity = new CmsDatabaseResource(repository, new RepositoryPath(databaseFileDefinition.getPath()));
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
	 * Search by the full path of a resource.
	 *
	 * @param parameter            the search term
	 * @param caseInsensitive            whether to be case sensitive
	 * @return list of entities
	 */
	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) {
		List<IEntity> results = new ArrayList<IEntity>();
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				List<CmisDatabaseFileDefinition> databaseFileDefinitions = CmisDatabaseRepositoryUtils.searchPath(connection, parameter, caseInsensitive);
				for (CmisDatabaseFileDefinition databaseFileDefinition : databaseFileDefinitions) {
					CmsDatabaseEntity databaseEntity = null;
					if (databaseFileDefinition.getType() == CmisDatabaseFileDefinition.OBJECT_TYPE_FOLDER) {
						databaseEntity = new CmsDatabaseCollection(repository, new RepositoryPath(databaseFileDefinition.getPath()));
					} else {
						databaseEntity = new CmsDatabaseResource(repository, new RepositoryPath(databaseFileDefinition.getPath()));
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
	 * Whether the folder exists.
	 *
	 * @param path            the path
	 * @return true if exists and false otherwise
	 * @throws SQLException             in case of an error
	 */
	public boolean folderExists(String path) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			return CmisDatabaseRepositoryUtils.existsFolder(connection, path);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * Whether the file exists.
	 *
	 * @param path            the path
	 * @return true if exists and false otherwise
	 * @throws SQLException             in case of an error
	 */
	public boolean fileExists(String path) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			return CmisDatabaseRepositoryUtils.existsFile(connection, path);
		} finally {
			closeConnection(connection);
		}
	}

	/**
	 * List all the resources paths.
	 *
	 * @return the list
	 * @throws SQLException             in case of an error
	 */
	public List<String> getAllResourcePaths() throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			return CmisDatabaseRepositoryUtils.getAllResourcePaths(connection);
		} finally {
			closeConnection(connection);
		}
	}

}
