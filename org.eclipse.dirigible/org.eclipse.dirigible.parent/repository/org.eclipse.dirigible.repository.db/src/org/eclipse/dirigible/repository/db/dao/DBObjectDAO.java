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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.repository.db.DBBaseException;
import org.eclipse.dirigible.repository.db.DBRepository;
import org.eclipse.dirigible.repository.db.init.DBScriptsMap;
import org.eclipse.dirigible.repository.logging.Logger;

public class DBObjectDAO {

	private static Logger logger = Logger.getLogger(DBObjectDAO.class);

	private DBRepositoryDAO dbRepositoryDAO;

	DBObjectDAO(DBRepositoryDAO dbRepositoryDAO) {
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
	public DBObject getObjectByPath(String path) throws DBBaseException {
		logger.debug("entering getObjectByPath"); //$NON-NLS-1$

		checkInitialized();

		if (path == null || "".equals(path.trim())) { //$NON-NLS-1$
			return null;
		}

		DBObject dbObject = null;
		
		
		Object cached = getRepository().getCacheManager().get(path);
		if (cached != null && cached instanceof DBObject) {
			dbObject = (DBObject) cached;
		} else {
			// not cached - get from db
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try {
				connection = getRepository().getDbUtils().getConnection();
				String script = getRepository().getDbUtils().readScript(connection,
						DBScriptsMap.SCRIPT_GET_FILE_BY_PATH, this.getClass());
				preparedStatement = getRepository().getDbUtils()
						.getPreparedStatement(connection, script);
				preparedStatement.setString(1, path);
				ResultSet resultSet = null;
				try {
					resultSet = preparedStatement.executeQuery();
	
					if (resultSet.next()) {
						dbObject = DBMapper.dbToObject(getRepository(), resultSet);
					}
				} finally {
					if (resultSet != null) {
						resultSet.close();
					}
					if (preparedStatement != null) {
						preparedStatement.close();
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
			getRepository().getCacheManager().put(path, dbObject);
		}
		
		logger.debug("exiting getObjectByPath"); //$NON-NLS-1$
		return dbObject;
	}

}
