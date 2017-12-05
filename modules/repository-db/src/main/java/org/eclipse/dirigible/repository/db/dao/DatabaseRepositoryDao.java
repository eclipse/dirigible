/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.db.dao;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryVersioningException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.db.DatabaseFile;
import org.eclipse.dirigible.repository.db.DatabaseFileVersion;
import org.eclipse.dirigible.repository.db.DatabaseFolder;
import org.eclipse.dirigible.repository.db.DatabaseObject;
import org.eclipse.dirigible.repository.db.DatabaseRepository;
import org.eclipse.dirigible.repository.db.DatabaseRepositoryException;
import org.eclipse.dirigible.repository.fs.FileSystemUtils;
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

	@Inject
	private DatabaseRepository repository;

	@Inject
	private DataSource datasource;

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
					DatabaseRepositoryUtils.saveFile(connection, path, content, isBinary, contentType);
				} finally {
					closeConnection(connection);
				}
				createVersion(path, content);
				createInfo(path);
			}
		} catch (Exception e) {
			throw new DatabaseRepositoryException(e);
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
	 * @param workspacePath
	 *            the workspace path
	 * @param content
	 *            the content
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void createVersion(String workspacePath, byte[] content) throws IOException {
		// String versionsPath = workspacePath;
		// if (directoryExists(versionsPath)) {
		// String versionsLastPath = versionsPath + File.separator + LAST;
		// byte[] bytes = DatabaseRepositoryUtils.loadFile(versionsLastPath);
		// if (bytes != null) {
		// Integer index;
		// try {
		// index = Integer.parseInt(new String(bytes, IRepository.UTF8));
		// DatabaseRepositoryUtils.saveFile(versionsPath + File.separator + (++index), content);
		// DatabaseRepositoryUtils.saveFile(versionsLastPath, index.toString().getBytes(IRepository.UTF8));
		// } catch (NumberFormatException e) {
		// logger.error(String.format("Invalid versions file: %s", versionsLastPath));
		// createInitialVersion(content, versionsPath);
		// }
		// }
		// } else {
		// createInitialVersion(content, versionsPath);
		// }
	}

	/**
	 * Creates the initial version.
	 *
	 * @param content
	 *            the content
	 * @param versionsPath
	 *            the versions path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void createInitialVersion(byte[] content, String versionsPath) throws IOException {
		// DatabaseRepositoryUtils.saveFile(versionsPath + File.separator + "1", content);
		// DatabaseRepositoryUtils.saveFile(versionsPath + File.separator + LAST, "1".getBytes(IRepository.UTF8));
	}

	/**
	 * Creates the info.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void createInfo(String workspacePath) throws IOException {
		// String infoPath = workspacePath;
		// if (fileExists(infoPath)) {
		// byte[] bytes = DatabaseRepositoryUtils.loadFile(infoPath);
		// if (bytes != null) {
		// Properties info = new Properties();
		// info.load(new ByteArrayInputStream(bytes));
		// info.setProperty(MODIFIED_BY, getUser());
		// info.setProperty(MODIFIED_AT, new Date().getTime() + "");
		//
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// info.store(out, "");
		// DatabaseRepositoryUtils.saveFile(infoPath, out.toByteArray());
		// }
		// } else {
		// createInitialInfo(infoPath);
		// }

	}

	/**
	 * Creates the initial info.
	 *
	 * @param infoPath
	 *            the info path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void createInitialInfo(String infoPath) throws IOException {
		// Properties info = new Properties();
		// info.setProperty(CREATED_BY, getUser());
		// info.setProperty(CREATED_AT, new Date().getTime() + "");
		// info.setProperty(MODIFIED_BY, getUser());
		// info.setProperty(MODIFIED_AT, new Date().getTime() + "");
		//
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// info.store(out, "");
		// DatabaseRepositoryUtils.saveFile(infoPath, out.toByteArray());
	}

	/**
	 * Removes the versions.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void removeVersions(String workspacePath) throws IOException {
		// String versionsPath = workspacePath;
		// if (DatabaseRepositoryUtils.directoryExists(versionsPath)) {
		// DatabaseRepositoryUtils.removeFile(versionsPath);
		// }
	}

	/**
	 * Removes the info.
	 *
	 * @param workspacePath
	 *            the workspace path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void removeInfo(String workspacePath) throws IOException {
		// String infoPath = workspacePath;
		// if (fileExists(infoPath)) {
		// DatabaseRepositoryUtils.removeFile(infoPath);
		// }
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
			createVersion(workspacePath, content);
			createInfo(workspacePath);
		} catch (IOException e) {
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
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.moveFile(connection, path, newPath);
			} finally {
				closeConnection(connection);
			}
			byte[] content = null;
			try {
				connection = openConnection();
				content = DatabaseRepositoryUtils.loadFile(connection, newPath);
			} finally {
				closeConnection(connection);
			}
			if (content != null) {
				createVersion(newPath, content);
				createInfo(newPath);
				removeVersions(path);
				removeInfo(path);
			}

		} catch (IOException e) {
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
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.copyFile(connection, path, newPath);
			} finally {
				closeConnection(connection);
			}
			byte[] content = null;
			try {
				connection = openConnection();
				content = DatabaseRepositoryUtils.loadFile(connection, newPath);
			} finally {
				closeConnection(connection);
			}
			if (content != null) {
				createVersion(newPath, content);
				createInfo(newPath);
			}
		} catch (IOException e) {
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
			removeVersions(path);
			removeInfo(path);
		} catch (IOException e) {
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
			removeVersions(path);
			removeInfo(path);
		} catch (IOException e) {
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
			removeVersions(newPath);
			removeInfo(newPath);
		} catch (IOException e) {
			throw new DatabaseRepositoryException(e);
		}

	}

	/**
	 * Copy folder.
	 *
	 * @param path
	 *            the path
	 * @param newPath
	 *            the new path
	 * @throws SQLException
	 */
	public void copyFolder(String path, String newPath) throws SQLException {
		try {
			Connection connection = null;
			try {
				connection = openConnection();
				DatabaseRepositoryUtils.copyFolder(connection, path, newPath);
			} finally {
				closeConnection(connection);
			}
			createInfo(newPath);
		} catch (IOException e) {
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
			// if (!fileExists(path)) {
			// // This is folder, that was not created
			// if (ContentTypeHelper.getExtension(path).isEmpty() && !path.endsWith(".")) {
			// // DatabaseRepositoryUtils.createFolder(workspacePath);
			// return null;
			// }
			// }
			if (fileExists(path)) {
				String contentType = ContentTypeHelper.getContentType(FileSystemUtils.getExtension(path));
				databaseObject = new DatabaseFile(repository, ContentTypeHelper.isBinary(contentType), contentType);
			} else {
				databaseObject = new DatabaseFolder(repository);
			}
			String name = path.substring(path.lastIndexOf(IRepository.SEPARATOR) + 1);
			databaseObject.setName(name);
			databaseObject.setPath(path);

			String infoPath = path;
			if (fileExists(infoPath)) {
				try {
					byte[] bytes = null;
					Connection connection = null;
					try {
						connection = openConnection();
						bytes = DatabaseRepositoryUtils.loadFile(connection, infoPath);
					} finally {
						closeConnection(connection);
					}
					if (bytes != null) {
						Properties info = new Properties();
						info.load(new ByteArrayInputStream(bytes));
						databaseObject.setCreatedBy(info.getProperty(CREATED_BY));
						String prop = info.getProperty(CREATED_AT);
						if (prop != null) {
							databaseObject.setCreatedAt(new Date(Long.parseLong(prop)));
						}
						databaseObject.setModifiedBy(info.getProperty(MODIFIED_BY));
						prop = info.getProperty(MODIFIED_AT);
						if (prop != null) {
							databaseObject.setModifiedAt(new Date(Long.parseLong(prop)));
						}
					}
				} catch (Throwable e) {
					throw new DatabaseRepositoryException(e);
				}
			}

		} catch (IOException e) {
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
	 */
	public List<IResourceVersion> getResourceVersionsByPath(String path) throws RepositoryVersioningException {
		List<IResourceVersion> versions = new ArrayList<IResourceVersion>();
		// String workspacePath = path;
		// String versionsPath = workspacePath;
		// DatabaseFile versionsDir = new File(versionsPath);
		// if (versionsDir.isDirectory()) {
		// File[] children = versionsDir.listFiles();
		//
		// if (children != null) {
		// for (File file : children) {
		// if (!LAST.equals(file.getName())) {
		// int version = Integer.parseInt(file.getName());
		// DatabaseResourceVersion databaseResourceVersion = new DatabaseResourceVersion(getRepository(), new
		// RepositoryPath(path),
		// version);
		// versions.add(databaseResourceVersion);
		// }
		// }
		// Collections.sort(versions);
		// }
		// }
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
		String workspacePath = path;
		return getLocalFileVersionByPath(version, workspacePath);
	}

	/**
	 * Gets the database file version by path.
	 *
	 * @param version
	 *            the version
	 * @param workspacePath
	 *            the workspace path
	 * @return the database file version by path
	 * @throws RepositoryVersioningException
	 *             the repository versioning exception
	 */
	private DatabaseFileVersion getLocalFileVersionByPath(int version, String workspacePath) throws RepositoryVersioningException {
		// String versionsPath = workspacePath;
		// String versionPath = versionsPath + File.separator + version;
		// try {
		// if (fileExists(versionPath)) {
		// byte[] bytes = DatabaseRepositoryUtils.loadFile(versionPath);
		// if (bytes != null) {
		// String ext = FilenameUtils.getExtension(workspacePath);
		// String contentType = ContentTypeHelper.getContentType(ext);
		// boolean isBinary = ContentTypeHelper.isBinary(contentType);
		// DatabaseFileVersion databaseFileVersion = new DatabaseFileVersion(getRepository(), isBinary, contentType,
		// version, bytes);
		// databaseFileVersion.setCreatedBy(DatabaseRepositoryUtils.getOwner(workspacePath));
		// databaseFileVersion.setCreatedAt(DatabaseRepositoryUtils.getModifiedAt(workspacePath));
		// return databaseFileVersion;
		// }
		// }
		// } catch (Exception e) {
		// throw new RepositoryVersioningException(e);
		// }

		return null;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	private String getUser() {
		return UserFacade.getName();
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void cleanupOldVersions() {
		// TODO Auto-generated method stub

	}

	public List<IEntity> searchName(String parameter, boolean caseInsensitive) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IEntity> searchPath(String parameter, boolean caseInsensitive) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IEntity> searchText(String parameter, boolean caseInsensitive) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean folderExists(String path) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			return DatabaseRepositoryUtils.existsFolder(connection, path);
		} finally {
			closeConnection(connection);
		}
	}

	public boolean fileExists(String path) throws SQLException {
		Connection connection = null;
		try {
			connection = openConnection();
			return DatabaseRepositoryUtils.existsFile(connection, path);
		} finally {
			closeConnection(connection);
		}
	}

}
