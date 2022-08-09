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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * Utility helping in Database Repository management of the underlying Database.
 */
public class CmisDatabaseRepositoryUtils {

	/** The persistence manager files. */
	private static PersistenceManager<CmisDatabaseFileDefinition> persistenceManagerFiles = new PersistenceManager<CmisDatabaseFileDefinition>();
	
	/** The persistence manager files content. */
	private static PersistenceManager<CmisDatabaseFileContentDefinition> persistenceManagerFilesContent = new PersistenceManager<CmisDatabaseFileContentDefinition>();
	
	/** The persistence manager files versions. */
	private static PersistenceManager<CmisDatabaseFileVersionDefinition> persistenceManagerFilesVersions = new PersistenceManager<CmisDatabaseFileVersionDefinition>();

	/** The Constant PERCENT. */
	private static final String PERCENT = "%";

	/**
	 * Save file.
	 *
	 * @param connection            the underlying connection
	 * @param path            the path of the file
	 * @param content            the content
	 * @param isBinary            whether the file is binary
	 * @param contentType            the content type
	 */
	public static void saveFile(Connection connection, String path, byte[] content, boolean isBinary, String contentType) {
		String username = UserFacade.getName();
		CmisDatabaseFileDefinition file = persistenceManagerFiles.find(connection, CmisDatabaseFileDefinition.class, path);
		if ((file != null) && (file.getType() != CmisDatabaseFileDefinition.OBJECT_TYPE_FOLDER)) {
			file.setModifiedAt(System.currentTimeMillis());
			file.setModifiedBy(username);
			persistenceManagerFiles.update(connection, file);
		} else {
			if (file != null) {
				throw new IllegalArgumentException("Cannot save the file, because a folder with the same name already exists: " + path);
			}
			String name = extractName(path);
			file = new CmisDatabaseFileDefinition();
			file.setPath(path);
			file.setName(name);
			file.setType(isBinary ? CmisDatabaseFileDefinition.OBJECT_TYPE_BINARY : CmisDatabaseFileDefinition.OBJECT_TYPE_TEXT);
			file.setCreatedAt(System.currentTimeMillis());
			file.setCreatedBy(username);
			file.setModifiedAt(file.getCreatedAt());
			file.setModifiedBy(username);
			persistenceManagerFiles.insert(connection, file);
		}

		CmisDatabaseFileContentDefinition databaseFileContentDefinition = persistenceManagerFilesContent.find(connection,
				CmisDatabaseFileContentDefinition.class, path);
		if (databaseFileContentDefinition != null) {
			databaseFileContentDefinition.setContent(content);
			persistenceManagerFilesContent.update(connection, databaseFileContentDefinition);
		} else {
			databaseFileContentDefinition = new CmisDatabaseFileContentDefinition();
			databaseFileContentDefinition.setPath(path);
			databaseFileContentDefinition.setContent(content);
			persistenceManagerFilesContent.insert(connection, databaseFileContentDefinition);
		}

	}

	/**
	 * Gets the file.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @return file definition
	 */
	public static CmisDatabaseFileDefinition getFile(Connection connection, String path) {
		CmisDatabaseFileDefinition file = persistenceManagerFiles.find(connection, CmisDatabaseFileDefinition.class, path);
		return file;
	}

	/**
	 * Extract name.
	 *
	 * @param path the path
	 * @return the string
	 */
	private static String extractName(String path) {
		return path.substring(path.lastIndexOf(IRepository.SEPARATOR) + 1);
	}

	/**
	 * Loads a file.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @return the content as byte array
	 */
	public static byte[] loadFile(Connection connection, String path) {
		CmisDatabaseFileContentDefinition databaseFileContentDefinition = persistenceManagerFilesContent.find(connection,
				CmisDatabaseFileContentDefinition.class, path);
		if (databaseFileContentDefinition != null) {
			return databaseFileContentDefinition.getContent();
		}
		return null;
	}

	/**
	 * Moves the file.
	 *
	 * @param connection            the connection
	 * @param path            the old path
	 * @param newPath            the new path
	 */
	public static void moveFile(Connection connection, String path, String newPath) {
		persistenceManagerFiles.tableCheck(connection, CmisDatabaseFileDefinition.class);
		persistenceManagerFilesContent.tableCheck(connection, CmisDatabaseFileContentDefinition.class);
		if (existsFile(connection, newPath)) {
			throw new IllegalArgumentException("Cannot move file on an existing target: " + newPath);
		}
		if (existsFolder(connection, newPath)) {
			throw new IllegalArgumentException("Cannot move folder on an existing target: " + newPath);
		}
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_CMS_FILES").where("FILE_PATH LIKE ?").build();
		List<CmisDatabaseFileDefinition> databaseFileDefinitions = persistenceManagerFiles.query(connection, CmisDatabaseFileDefinition.class, sql,
				path + PERCENT);
		for (CmisDatabaseFileDefinition databaseFileDefinition : databaseFileDefinitions) {
			String deepPath = databaseFileDefinition.getPath().substring(path.length());
			String fullPath = new RepositoryPath(newPath + deepPath).getPath();
			if (databaseFileDefinition.getType() == CmisDatabaseFileDefinition.OBJECT_TYPE_FOLDER) {
				createFolder(connection, fullPath);
			} else {
				byte[] content = loadFile(connection, databaseFileDefinition.getPath());
				saveFile(connection, fullPath, content, databaseFileDefinition.getType() == CmisDatabaseFileDefinition.OBJECT_TYPE_BINARY,
						databaseFileDefinition.getContentType());
			}
		}
		removeFile(connection, path);

	}

	/**
	 * Copy a file.
	 *
	 * @param connection            the connection
	 * @param path            the old path
	 * @param newPath            the new path
	 */
	public static void copyFile(Connection connection, String path, String newPath) {
		persistenceManagerFiles.tableCheck(connection, CmisDatabaseFileDefinition.class);
		if (path.endsWith(IRepository.SEPARATOR) && !(newPath.endsWith(IRepository.SEPARATOR))) {
			newPath += IRepository.SEPARATOR;
		}
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_CMS_FILES").where("FILE_PATH LIKE ?").build();
		List<CmisDatabaseFileDefinition> databaseFileDefinitions = persistenceManagerFiles.query(connection, CmisDatabaseFileDefinition.class, sql,
				path + PERCENT);
		for (CmisDatabaseFileDefinition databaseFileDefinition : databaseFileDefinitions) {
			String deepPath = databaseFileDefinition.getPath().substring(path.length());
			String fullPath = new RepositoryPath(newPath + deepPath).getPath();
			if (databaseFileDefinition.getType() == CmisDatabaseFileDefinition.OBJECT_TYPE_FOLDER) {
				createFolder(connection, fullPath);
			} else {
				saveFile(connection, fullPath, loadFile(connection, databaseFileDefinition.getPath()),
						databaseFileDefinition.getType() == CmisDatabaseFileDefinition.OBJECT_TYPE_BINARY, databaseFileDefinition.getContentType());
			}
		}

	}

	/**
	 * Removes a file.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 */
	public static void removeFile(Connection connection, String path) {
		persistenceManagerFiles.tableCheck(connection, CmisDatabaseFileDefinition.class);
		persistenceManagerFilesContent.tableCheck(connection, CmisDatabaseFileContentDefinition.class);
		persistenceManagerFilesVersions.tableCheck(connection, CmisDatabaseFileVersionDefinition.class);
		
		String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_CMS_FILES").where("FILE_PATH = ?").build();
		persistenceManagerFiles.execute(connection, sql, path);
		sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_CMS_FILES").where("FILE_PATH LIKE ?").build();
		persistenceManagerFiles.execute(connection, sql, path + IRepositoryStructure.SEPARATOR + PERCENT);
		
		sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_CMS_FILES_CONTENT").where("FILE_PATH = ?").build();
		persistenceManagerFilesContent.execute(connection, sql, path);
		sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_CMS_FILES_CONTENT").where("FILE_PATH LIKE ?").build();
		persistenceManagerFilesContent.execute(connection, sql, path + IRepositoryStructure.SEPARATOR + PERCENT);
		
		sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_CMS_FILES_VERSIONS").where("FILE_PATH = ?").build();
		persistenceManagerFilesContent.execute(connection, sql, path);
		sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_CMS_FILES_VERSIONS").where("FILE_PATH LIKE ?").build();
		persistenceManagerFilesContent.execute(connection, sql, path + IRepositoryStructure.SEPARATOR + PERCENT);
	}

	/**
	 * Creates a folder.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 */
	public static void createFolder(Connection connection, String path) {
		if (!existsFolder(connection, path)) {
			String name = extractName(path);
			String username = UserFacade.getName();

			CmisDatabaseFileDefinition folder = new CmisDatabaseFileDefinition();
			folder.setPath(path);
			folder.setName(name);
			folder.setType(CmisDatabaseFileDefinition.OBJECT_TYPE_FOLDER);
			folder.setCreatedAt(System.currentTimeMillis());
			folder.setCreatedBy(username);
			folder.setModifiedAt(folder.getCreatedAt());
			folder.setModifiedBy(username);

			persistenceManagerFiles.insert(connection, folder);
		}
	}

	/**
	 * Copy a folder.
	 *
	 * @param connection            the connection
	 * @param path            the old path
	 * @param newPath            the new path
	 */
	public static void copyFolder(Connection connection, String path, String newPath) {
		copyFile(connection, path, newPath);
	}

	/**
	 * Gets the owner.
	 *
	 * @param connection            the connection
	 * @param workspacePath            the path
	 * @return the owner name
	 */
	public static String getOwner(Connection connection, String workspacePath) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the modified at date.
	 *
	 * @param connection            the connection
	 * @param workspacePath            the path
	 * @return the modification date
	 */
	public static Date getModifiedAt(Connection connection, String workspacePath) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Whether the file exists.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @return true if exists and false otherwise
	 */
	public static boolean existsFile(Connection connection, String path) {
		CmisDatabaseFileDefinition file = persistenceManagerFiles.find(connection, CmisDatabaseFileDefinition.class, path);
		if ((file != null) && (file.getType() != CmisDatabaseFileDefinition.OBJECT_TYPE_FOLDER)) {
			return true;
		}
		return false;
	}

	/**
	 * Whether the folder exists.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @return true if exists and false otherwise
	 */
	public static boolean existsFolder(Connection connection, String path) {
		CmisDatabaseFileDefinition folder = persistenceManagerFiles.find(connection, CmisDatabaseFileDefinition.class, path);
		if ((folder != null) && (folder.getType() == CmisDatabaseFileDefinition.OBJECT_TYPE_FOLDER)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the folders' sub-files and sub-folders.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @return the list of definitions
	 */
	public static List<CmisDatabaseFileDefinition> findChildren(Connection connection, String path) {
		persistenceManagerFiles.tableCheck(connection, CmisDatabaseFileDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_CMS_FILES").where("FILE_PATH LIKE ? AND FILE_PATH NOT LIKE ?")
				.build();
		String param1 = (IRepository.SEPARATOR.equals(path) ? "" : path) //$NON-NLS-1$
				+ IRepository.SEPARATOR + PERCENT;
		String param2 = (IRepository.SEPARATOR.equals(path) ? "" : path) //$NON-NLS-1$
				+ IRepository.SEPARATOR + PERCENT + IRepository.SEPARATOR + PERCENT;
		return persistenceManagerFiles.query(connection, CmisDatabaseFileDefinition.class, sql, param1, param2);

	}

	/**
	 * Search in name attribute.
	 *
	 * @param connection            the connection
	 * @param parameter            the search term
	 * @param caseInsensitive            whether it is case sensitive
	 * @return a list of definitions
	 * @throws SQLException             in case of errors
	 */
	public static List<CmisDatabaseFileDefinition> searchName(Connection connection, String parameter, boolean caseInsensitive) throws SQLException {
		persistenceManagerFiles.tableCheck(connection, CmisDatabaseFileDefinition.class);
		String condition = caseInsensitive ? "UPPER(FILE_NAME) LIKE ?" : "FILE_NAME LIKE ?";
		String name = caseInsensitive ? parameter.toUpperCase() : parameter;
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_CMS_FILES").where(condition).build();

		List<CmisDatabaseFileDefinition> results = persistenceManagerFiles.query(connection, CmisDatabaseFileDefinition.class, sql, PERCENT + name + PERCENT);
		return results;
	}

	/**
	 * Search in name attribute.
	 *
	 * @param connection            the connection
	 * @param root            the relative root
	 * @param parameter            the search term
	 * @param caseInsensitive            whether it is case sensitive
	 * @return a list of definitions
	 * @throws SQLException             in case of errors
	 */
	public static List<CmisDatabaseFileDefinition> searchName(Connection connection, String root, String parameter, boolean caseInsensitive)
			throws SQLException {
		persistenceManagerFiles.tableCheck(connection, CmisDatabaseFileDefinition.class);
		String condition = caseInsensitive ? "UPPER(FILE_PATH) LIKE ? AND UPPER(FILE_NAME) LIKE ?" : "FILE_PATH LIKE ? AND FILE_NAME LIKE ?";
		String path = caseInsensitive ? root.toUpperCase() : root;
		String name = caseInsensitive ? parameter.toUpperCase() : parameter;
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_CMS_FILES").where(condition).build();

		List<CmisDatabaseFileDefinition> results = persistenceManagerFiles.query(connection, CmisDatabaseFileDefinition.class, sql, PERCENT + path + PERCENT,
				PERCENT + name + PERCENT);
		return results;
	}

	/**
	 * Search in path attribute.
	 *
	 * @param connection            the connection
	 * @param parameter            the search term
	 * @param caseInsensitive            whether it is case sensitive
	 * @return a list of definitions
	 * @throws SQLException             in case of errors
	 */
	public static List<CmisDatabaseFileDefinition> searchPath(Connection connection, String parameter, boolean caseInsensitive) throws SQLException {
		persistenceManagerFiles.tableCheck(connection, CmisDatabaseFileDefinition.class);
		String condition = caseInsensitive ? "UPPER(FILE_PATH) LIKE ?" : "FILE_PATH LIKE ?";
		String name = caseInsensitive ? parameter.toUpperCase() : parameter;
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_CMS_FILES").where(condition).build();

		List<CmisDatabaseFileDefinition> results = persistenceManagerFiles.query(connection, CmisDatabaseFileDefinition.class, sql, PERCENT + name + PERCENT);
		return results;
	}

	/**
	 * Returns the file versions.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @return the list with versions
	 */
	public static List<CmisDatabaseFileVersionDefinition> findFileVersions(Connection connection, String path) {
		persistenceManagerFilesVersions.tableCheck(connection, CmisDatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_CMS_FILES_VERSIONS").where("FILE_PATH = ?").build();
		return persistenceManagerFilesVersions.query(connection, CmisDatabaseFileVersionDefinition.class, sql, path);
	}

	/**
	 * Stores a version.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @param version            the version
	 * @param content            the content
	 */
	public static void saveFileVersion(Connection connection, String path, int version, byte[] content) {
		String username = UserFacade.getName();
		String name = extractName(path);
		CmisDatabaseFileVersionDefinition fileVersion = new CmisDatabaseFileVersionDefinition();
		fileVersion.setPath(path);
		fileVersion.setName(name);
		fileVersion.setVersion(version);
		fileVersion.setContent(content);
		fileVersion.setCreatedAt(System.currentTimeMillis());
		fileVersion.setCreatedBy(username);
		fileVersion.setModifiedAt(fileVersion.getCreatedAt());
		fileVersion.setModifiedBy(username);
		persistenceManagerFilesVersions.insert(connection, fileVersion);

	}

	/**
	 * Removes all the file versions.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @throws SQLException             in case of an error
	 */
	public static void removeFileVersions(Connection connection, String path) throws SQLException {
		persistenceManagerFilesVersions.tableCheck(connection, CmisDatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_CMS_FILES_VERSIONS").where("FILE_PATH = ?").build();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, path);
			statement.executeUpdate();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	/**
	 * Returns the last version.
	 *
	 * @param connection            the connections
	 * @param path            the path
	 * @return the last version
	 * @throws SQLException             in case of an error
	 */
	public static int getLastFileVersion(Connection connection, String path) throws SQLException {
		persistenceManagerFilesVersions.tableCheck(connection, CmisDatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("MAX(FILE_VERSION)").from("DIRIGIBLE_CMS_FILES_VERSIONS").where("FILE_PATH = ?")
				.build();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, path);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	/**
	 * Gets a specific version.
	 *
	 * @param connection            the connection
	 * @param path            the path
	 * @param version            the version number
	 * @return the version definition
	 */
	public static CmisDatabaseFileVersionDefinition getFileVersion(Connection connection, String path, int version) {
		persistenceManagerFilesVersions.tableCheck(connection, CmisDatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_CMS_FILES_VERSIONS")
				.where("FILE_PATH = ? AND FILE_VERSION = ?").build();
		List<CmisDatabaseFileVersionDefinition> list = persistenceManagerFilesVersions.query(connection, CmisDatabaseFileVersionDefinition.class, sql, path,
				version);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * Returns all the resources' oaths.
	 *
	 * @param connection            the connection
	 * @return the list of paths
	 * @throws SQLException             in case of an error
	 */
	public static List<String> getAllResourcePaths(Connection connection) throws SQLException {
		List<String> results = new ArrayList<String>();
		persistenceManagerFiles.tableCheck(connection, CmisDatabaseFileDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("FILE_PATH").from("DIRIGIBLE_CMS_FILES").where("FILE_TYPE <> 0").order("FILE_PATH")
				.build();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				results.add(rs.getString(1));
			}
			return results;
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

}
