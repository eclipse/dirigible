package org.eclipse.dirigible.repository.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * Utility helping in Database Repository management of the underlying Database
 */
public class DatabaseRepositoryUtils {

	private static PersistenceManager<DatabaseFileDefinition> persistenceManagerFiles = new PersistenceManager<DatabaseFileDefinition>();
	private static PersistenceManager<DatabaseFileContentDefinition> persistenceManagerFilesContent = new PersistenceManager<DatabaseFileContentDefinition>();
	private static PersistenceManager<DatabaseFileVersionDefinition> persistenceManagerFilesVersions = new PersistenceManager<DatabaseFileVersionDefinition>();

	private static final String PERCENT = "%";

	/**
	 * @param connection
	 *            the underlying connection
	 * @param path
	 *            the path of the file
	 * @param content
	 *            the content
	 * @param isBinary
	 *            whether the file is binary
	 * @param contentType
	 *            the content type
	 */
	public static void saveFile(Connection connection, String path, byte[] content, boolean isBinary, String contentType) {
		String username = UserFacade.getName();
		DatabaseFileDefinition file = persistenceManagerFiles.find(connection, DatabaseFileDefinition.class, path);
		if ((file != null) && (file.getType() != DatabaseFileDefinition.OBJECT_TYPE_FOLDER)) {
			file.setModifiedAt(System.currentTimeMillis());
			file.setModifiedBy(username);
			persistenceManagerFiles.update(connection, file, path);
		} else {
			if (file != null) {
				throw new IllegalArgumentException("Cannot save the file, because a folder with the same name already exists: " + path);
			}
			String name = extractName(path);
			file = new DatabaseFileDefinition();
			file.setPath(path);
			file.setName(name);
			file.setType(isBinary ? DatabaseFileDefinition.OBJECT_TYPE_BINARY : DatabaseFileDefinition.OBJECT_TYPE_TEXT);
			file.setCreatedAt(System.currentTimeMillis());
			file.setCreatedBy(username);
			file.setModifiedAt(file.getCreatedAt());
			file.setModifiedBy(username);
			persistenceManagerFiles.insert(connection, file);
		}

		DatabaseFileContentDefinition databaseFileContentDefinition = persistenceManagerFilesContent.find(connection,
				DatabaseFileContentDefinition.class, path);
		if (databaseFileContentDefinition != null) {
			databaseFileContentDefinition.setContent(content);
			persistenceManagerFilesContent.update(connection, databaseFileContentDefinition, path);
		} else {
			databaseFileContentDefinition = new DatabaseFileContentDefinition();
			databaseFileContentDefinition.setPath(path);
			databaseFileContentDefinition.setContent(content);
			persistenceManagerFilesContent.insert(connection, databaseFileContentDefinition);
		}

	}

	public static DatabaseFileDefinition getFile(Connection connection, String path) {
		DatabaseFileDefinition file = persistenceManagerFiles.find(connection, DatabaseFileDefinition.class, path);
		return file;
	}

	private static String extractName(String path) {
		return path.substring(path.lastIndexOf(IRepository.SEPARATOR) + 1);
	}

	public static byte[] loadFile(Connection connection, String path) {
		DatabaseFileContentDefinition databaseFileContentDefinition = persistenceManagerFilesContent.find(connection,
				DatabaseFileContentDefinition.class, path);
		if (databaseFileContentDefinition != null) {
			return databaseFileContentDefinition.getContent();
		}
		return null;
	}

	public static void moveFile(Connection connection, String path, String newPath) {
		persistenceManagerFiles.tableCheck(connection, DatabaseFileDefinition.class);
		persistenceManagerFilesContent.tableCheck(connection, DatabaseFileContentDefinition.class);
		if (existsFile(connection, newPath)) {
			throw new IllegalArgumentException("Cannot move file on an existing target: " + newPath);
		}
		if (existsFolder(connection, newPath)) {
			throw new IllegalArgumentException("Cannot move folder on an existing target: " + newPath);
		}
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES").where("FILE_PATH LIKE ?").build();
		List<DatabaseFileDefinition> databaseFileDefinitions = persistenceManagerFiles.query(connection, DatabaseFileDefinition.class, sql,
				path + PERCENT);
		for (DatabaseFileDefinition databaseFileDefinition : databaseFileDefinitions) {
			String deepPath = databaseFileDefinition.getPath().substring(path.length());
			String fullPath = new RepositoryPath(newPath + deepPath).getPath();
			if (databaseFileDefinition.getType() == DatabaseFileDefinition.OBJECT_TYPE_FOLDER) {
				createFolder(connection, fullPath);
			} else {
				byte[] content = loadFile(connection, databaseFileDefinition.getPath());
				saveFile(connection, fullPath, content, databaseFileDefinition.getType() == DatabaseFileDefinition.OBJECT_TYPE_BINARY,
						databaseFileDefinition.getContentType());
			}
		}
		removeFile(connection, path);

	}

	public static void copyFile(Connection connection, String path, String newPath) {
		persistenceManagerFiles.tableCheck(connection, DatabaseFileDefinition.class);
		if (path.endsWith(IRepository.SEPARATOR) && !(newPath.endsWith(IRepository.SEPARATOR))) {
			newPath += IRepository.SEPARATOR;
		}
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES").where("FILE_PATH LIKE ?").build();
		List<DatabaseFileDefinition> databaseFileDefinitions = persistenceManagerFiles.query(connection, DatabaseFileDefinition.class, sql,
				path + PERCENT);
		for (DatabaseFileDefinition databaseFileDefinition : databaseFileDefinitions) {
			String deepPath = databaseFileDefinition.getPath().substring(path.length());
			String fullPath = new RepositoryPath(newPath + deepPath).getPath();
			if (databaseFileDefinition.getType() == DatabaseFileDefinition.OBJECT_TYPE_FOLDER) {
				createFolder(connection, fullPath);
			} else {
				saveFile(connection, fullPath, loadFile(connection, databaseFileDefinition.getPath()),
						databaseFileDefinition.getType() == DatabaseFileDefinition.OBJECT_TYPE_BINARY, databaseFileDefinition.getContentType());
			}
		}

	}

	public static void removeFile(Connection connection, String path) {
		persistenceManagerFiles.tableCheck(connection, DatabaseFileDefinition.class);
		persistenceManagerFilesContent.tableCheck(connection, DatabaseFileContentDefinition.class);
		persistenceManagerFilesVersions.tableCheck(connection, DatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_FILES").where("FILE_PATH LIKE ?").build();
		persistenceManagerFiles.execute(connection, sql, path + PERCENT);
		sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_FILES_CONTENT").where("FILE_PATH LIKE ?").build();
		persistenceManagerFilesContent.execute(connection, sql, path + PERCENT);
		sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_FILES_VERSIONS").where("FILE_PATH LIKE ?").build();
		persistenceManagerFilesContent.execute(connection, sql, path + PERCENT);
	}

	public static void createFolder(Connection connection, String path) {
		if (!existsFolder(connection, path)) {
			String name = extractName(path);
			String username = UserFacade.getName();

			DatabaseFileDefinition folder = new DatabaseFileDefinition();
			folder.setPath(path);
			folder.setName(name);
			folder.setType(DatabaseFileDefinition.OBJECT_TYPE_FOLDER);
			folder.setCreatedAt(System.currentTimeMillis());
			folder.setCreatedBy(username);
			folder.setModifiedAt(folder.getCreatedAt());
			folder.setModifiedBy(username);

			persistenceManagerFiles.insert(connection, folder);
		}
	}

	public static void copyFolder(Connection connection, String path, String newPath) {
		copyFile(connection, path, newPath);
	}

	public static String getOwner(Connection connection, String workspacePath) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Date getModifiedAt(Connection connection, String workspacePath) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean existsFile(Connection connection, String path) {
		DatabaseFileDefinition file = persistenceManagerFiles.find(connection, DatabaseFileDefinition.class, path);
		if ((file != null) && (file.getType() != DatabaseFileDefinition.OBJECT_TYPE_FOLDER)) {
			return true;
		}
		return false;
	}

	public static boolean existsFolder(Connection connection, String path) {
		DatabaseFileDefinition folder = persistenceManagerFiles.find(connection, DatabaseFileDefinition.class, path);
		if ((folder != null) && (folder.getType() == DatabaseFileDefinition.OBJECT_TYPE_FOLDER)) {
			return true;
		}
		return false;
	}

	public static List<DatabaseFileDefinition> findChildren(Connection connection, String path) {
		persistenceManagerFiles.tableCheck(connection, DatabaseFileDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES").where("FILE_PATH LIKE ? AND FILE_PATH NOT LIKE ?")
				.build();
		String param1 = (IRepository.SEPARATOR.equals(path) ? "" : path) //$NON-NLS-1$
				+ IRepository.SEPARATOR + PERCENT;
		String param2 = (IRepository.SEPARATOR.equals(path) ? "" : path) //$NON-NLS-1$
				+ IRepository.SEPARATOR + PERCENT + IRepository.SEPARATOR + PERCENT;
		return persistenceManagerFiles.query(connection, DatabaseFileDefinition.class, sql, param1, param2);

	}

	public static List<DatabaseFileDefinition> searchName(Connection connection, String parameter, boolean caseInsensitive) throws SQLException {
		persistenceManagerFiles.tableCheck(connection, DatabaseFileDefinition.class);
		String condition = caseInsensitive ? "UPPER(FILE_NAME) LIKE ?" : "FILE_NAME LIKE ?";
		String name = caseInsensitive ? parameter.toUpperCase() : parameter;
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES").where(condition).build();

		List<DatabaseFileDefinition> results = persistenceManagerFiles.query(connection, DatabaseFileDefinition.class, sql, PERCENT + name + PERCENT);
		return results;
	}

	public static List<DatabaseFileDefinition> searchName(Connection connection, String root, String parameter, boolean caseInsensitive)
			throws SQLException {
		persistenceManagerFiles.tableCheck(connection, DatabaseFileDefinition.class);
		String condition = caseInsensitive ? "UPPER(FILE_PATH) LIKE ? AND UPPER(FILE_NAME) LIKE ?" : "FILE_PATH LIKE ? AND FILE_NAME LIKE ?";
		String path = caseInsensitive ? root.toUpperCase() : root;
		String name = caseInsensitive ? parameter.toUpperCase() : parameter;
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES").where(condition).build();

		List<DatabaseFileDefinition> results = persistenceManagerFiles.query(connection, DatabaseFileDefinition.class, sql, PERCENT + path + PERCENT,
				PERCENT + name + PERCENT);
		return results;
	}

	public static List<DatabaseFileDefinition> searchPath(Connection connection, String parameter, boolean caseInsensitive) throws SQLException {
		persistenceManagerFiles.tableCheck(connection, DatabaseFileDefinition.class);
		String condition = caseInsensitive ? "UPPER(FILE_PATH) LIKE ?" : "FILE_PATH LIKE ?";
		String name = caseInsensitive ? parameter.toUpperCase() : parameter;
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES").where(condition).build();

		List<DatabaseFileDefinition> results = persistenceManagerFiles.query(connection, DatabaseFileDefinition.class, sql, PERCENT + name + PERCENT);
		return results;
	}

	public static List<DatabaseFileVersionDefinition> findFileVersions(Connection connection, String path) {
		persistenceManagerFilesVersions.tableCheck(connection, DatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES_VERSIONS").where("FILE_PATH = ?").build();
		return persistenceManagerFilesVersions.query(connection, DatabaseFileVersionDefinition.class, sql, path);
	}

	public static void saveFileVersion(Connection connection, String path, int version, byte[] content) {
		String username = UserFacade.getName();
		String name = extractName(path);
		DatabaseFileVersionDefinition fileVersion = new DatabaseFileVersionDefinition();
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

	public static void removeFileVersions(Connection connection, String path) throws SQLException {
		persistenceManagerFilesVersions.tableCheck(connection, DatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_FILES_VERSIONS").where("FILE_PATH = ?").build();
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

	public static int getLastFileVersion(Connection connection, String path) throws SQLException {
		persistenceManagerFilesVersions.tableCheck(connection, DatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("MAX(FILE_VERSION)").from("DIRIGIBLE_FILES_VERSIONS").where("FILE_PATH = ?")
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

	public static DatabaseFileVersionDefinition getFileVersion(Connection connection, String path, int version) {
		persistenceManagerFilesVersions.tableCheck(connection, DatabaseFileVersionDefinition.class);
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES_VERSIONS")
				.where("FILE_PATH = ? AND FILE_VERSION = ?").build();
		List<DatabaseFileVersionDefinition> list = persistenceManagerFilesVersions.query(connection, DatabaseFileVersionDefinition.class, sql, path,
				version);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

}
