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

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.ext.db.DBUtils;

/**
 * Utility class for transformation between JDBC related objects to DB
 * Repository objects and vice-versa
 * 
 */
public class DBMapper {

	private static final String FILE_MODIFIED_AT = "FILE_MODIFIED_AT"; //$NON-NLS-1$
	private static final String FILE_MODIFIED_BY = "FILE_MODIFIED_BY"; //$NON-NLS-1$
	private static final String FILE_CREATED_AT = "FILE_CREATED_AT"; //$NON-NLS-1$
	private static final String FILE_CREATED_BY = "FILE_CREATED_BY"; //$NON-NLS-1$
	private static final String FILE_CONTENT_TYPE = "FILE_CONTENT_TYPE"; //$NON-NLS-1$
	private static final String FILE_TYPE = "FILE_TYPE"; //$NON-NLS-1$
	private static final String FILE_PATH = "FILE_PATH"; //$NON-NLS-1$
	private static final String FILE_NAME = "FILE_NAME"; //$NON-NLS-1$
	static final int OBJECT_TYPE_FOLDER = 0;
	static final int OBJECT_TYPE_DOCUMENT = 1;
	static final int OBJECT_TYPE_BINARY = 2;

	/**
	 * ResultSet current row to DB Repository transformation
	 * 
	 * @param repository
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 * @throws DBBaseException
	 */
	static DBObject dbToObject(DBRepository repository, ResultSet resultSet)
			throws SQLException, DBBaseException {

		String name = resultSet.getString(FILE_NAME);
		String path = resultSet.getString(FILE_PATH);
		int type = resultSet.getInt(FILE_TYPE);
		String content = resultSet.getString(FILE_CONTENT_TYPE);
		String createdBy = resultSet.getString(FILE_CREATED_BY);
		Date createdAt = new Date(resultSet.getTimestamp(FILE_CREATED_AT)
				.getTime());
		String modifiedBy = resultSet.getString(FILE_MODIFIED_BY);
		Date modifiedAt = new Date(resultSet.getTimestamp(FILE_MODIFIED_AT)
				.getTime());

		DBObject dbObject = null;
		if (type == OBJECT_TYPE_FOLDER) {
			dbObject = new DBFolder(repository);
		} else if (type == OBJECT_TYPE_DOCUMENT) {
			dbObject = new DBFile(repository, false, content);
		} else if (type == OBJECT_TYPE_BINARY) {
			dbObject = new DBFile(repository, true, content);
		} else {
			throw new DBBaseException(Messages.getString("DBMapper.THE_OBJECT_IS_UNKNOWN")); //$NON-NLS-1$
		}

		dbObject.setName(name);
		dbObject.setPath(path);
		dbObject.setCreatedBy(createdBy);
		dbObject.setCreatedAt(new java.util.Date(createdAt.getTime()));
		dbObject.setModifiedBy(modifiedBy);
		dbObject.setModifiedAt(new java.util.Date(modifiedAt.getTime()));

		return dbObject;
	}

	/**
	 * ResultSet current row to Content transformation
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	static byte[] dbToData(ResultSet resultSet)
			throws SQLException {
		return DBUtils.dbToData(resultSet);
	}

	/**
	 * ResultSet current row to Binary Content transformation
	 * 
	 * @param repository
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static byte[] dbToDataBinary(Connection connection, ResultSet resultSet,
			String columnName) throws SQLException, IOException {
		return DBUtils.dbToDataBinary(connection, resultSet, columnName);
	}

	public static DBFileVersion dbToFileVersion(Connection connection, DBRepository repository,
			ResultSet resultSet) throws SQLException, DBBaseException,
			IOException {

		String path = resultSet.getString("FV_FILE_PATH"); //$NON-NLS-1$
		int version = resultSet.getInt("FV_VERSION"); //$NON-NLS-1$
		byte[] bytes = dbToDataBinary(connection, resultSet, "FV_CONTENT"); //$NON-NLS-1$
		int type = resultSet.getInt("FV_TYPE"); //$NON-NLS-1$
		String content = resultSet.getString("FV_CONTENT_TYPE"); //$NON-NLS-1$
		String createdBy = resultSet.getString("FV_CREATED_BY"); //$NON-NLS-1$
		Date createdAt = new Date(resultSet.getTimestamp("FV_CREATED_AT") //$NON-NLS-1$
				.getTime());

		DBFileVersion dbFileVersion = new DBFileVersion(repository,
				(type == OBJECT_TYPE_BINARY), content, version, bytes);

		dbFileVersion.setPath(path);
		dbFileVersion.setCreatedBy(createdBy);
		dbFileVersion.setCreatedAt(createdAt);

		return dbFileVersion;
	}

}
