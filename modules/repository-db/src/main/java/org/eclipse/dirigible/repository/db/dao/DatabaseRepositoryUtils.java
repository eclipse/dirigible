package org.eclipse.dirigible.repository.db.dao;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IRepository;

/**
 * Utility helping in Database Repository management of the underlying Database
 */
public class DatabaseRepositoryUtils {

	private static PersistenceManager<DatabaseFileDefinition> persistenceManagerFiles = new PersistenceManager<DatabaseFileDefinition>();
	private static PersistenceManager<DatabaseFileContentDefinition> persistenceManagerFilesContent = new PersistenceManager<DatabaseFileContentDefinition>();

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
			persistenceManagerFiles.update(connection, file, path);
			file.setModifiedAt(System.currentTimeMillis());
			file.setModifiedBy(username);
		} else {
			if (file != null) {
				throw new IllegalArgumentException("Folder with the same name already exists: " + path);
			}
			String name = path.substring(path.lastIndexOf(IRepository.SEPARATOR) + 1);
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

	public static byte[] loadFile(Connection connection, String path) {
		DatabaseFileContentDefinition databaseFileContentDefinition = persistenceManagerFilesContent.find(connection,
				DatabaseFileContentDefinition.class, path);
		if (databaseFileContentDefinition != null) {
			return databaseFileContentDefinition.getContent();
		}
		return null;
	}

	public static void moveFile(Connection connection, String path, String newPath) {
		// TODO Auto-generated method stub

	}

	public static void copyFile(Connection connection, String path, String newPath) {
		// TODO Auto-generated method stub

	}

	public static void removeFile(Connection connection, String path) {
		persistenceManagerFiles.delete(connection, DatabaseFileDefinition.class, path);
	}

	public static void createFolder(Connection connection, String path) {
		if (!existsFolder(connection, path)) {
			String name = path.substring(path.lastIndexOf(IRepository.SEPARATOR) + 1);
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
		// TODO Auto-generated method stub

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
		String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_FILES").where("FILE_PATH LIKE ? AND FILE_PATH NOT LIKE ?")
				.build();
		String param1 = (IRepository.SEPARATOR.equals(path) ? "" : path) //$NON-NLS-1$
				+ IRepository.SEPARATOR + PERCENT;
		String param2 = (IRepository.SEPARATOR.equals(path) ? "" : path) //$NON-NLS-1$
				+ IRepository.SEPARATOR + PERCENT + IRepository.SEPARATOR + PERCENT;
		return persistenceManagerFiles.query(connection, DatabaseFileDefinition.class, sql, param1, param2);

	}

}
