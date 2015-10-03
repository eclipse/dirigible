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

package org.eclipse.dirigible.repository.db.init;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.ext.db.DBUtils;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Initialize the database schema of Repository Supports incremental alteration
 * of the schema
 * 
 */
public class DBRepositoryInitializer {

	private static final String MESSAGING_HUB = Messages.getString("DBRepositoryInitializer.MESSAGING_HUB"); //$NON-NLS-1$

	private static final String IT_SEEMS_DGB_SCHEMA_VERSIONS_DOESN_T_EXISTS_CHECK_WHETHER_THIS_MESSAGE_HAS_BEEN_APPEARING_MORE_THAN_ONCE = Messages.getString("DBRepositoryInitializer.IT_SEEMS_DGB_SCHEMA_VERSIONS_DOESN_T_EXISTS_CHECK_WHETHER_THIS_MESSAGE_HAS_BEEN_APPEARING_MORE_THAN_ONCE"); //$NON-NLS-1$

	private static final String EXTENSION_POINTS = Messages.getString("DBRepositoryInitializer.EXTENSION_POINTS"); //$NON-NLS-1$

	private static final String INITIALIZING_SCRIPT_VERSION_S_FROM_S_ABOUT_S = Messages.getString("DBRepositoryInitializer.INITIALIZING_SCRIPT_VERSION_S_FROM_S_ABOUT_S"); //$NON-NLS-1$

	private static final String SECURITY_FEATURES = Messages.getString("DBRepositoryInitializer.SECURITY_FEATURES"); //$NON-NLS-1$

	private static final String FILE_VERSIONS_SUPPORT = Messages.getString("DBRepositoryInitializer.FILE_VERSIONS_SUPPORT"); //$NON-NLS-1$

	private static final String FREE_TEXT_SEARCH_IN_DOCUMENTS = Messages.getString("DBRepositoryInitializer.FREE_TEXT_SEARCH_IN_DOCUMENTS"); //$NON-NLS-1$

	private static final String TEST_UPDATE = Messages.getString("DBRepositoryInitializer.TEST_UPDATE"); //$NON-NLS-1$

	private static final String INITIAL_CREATION = Messages.getString("DBRepositoryInitializer.INITIAL_CREATION"); //$NON-NLS-1$

	private static Logger logger = Logger.getLogger(DBRepositoryInitializer.class);

	private static final String TABLE_NAME_DGB_SCHEMA_VERSIONS = "DGB_SCHEMA_VERSIONS"; //$NON-NLS-1$
	private static final String TABLE_COLUMN_SCHV_VERSION = "SCHV_VERSION"; //$NON-NLS-1$

	private DBUtils dbUtils;
	private Connection connection;
	private boolean forceRecreate;

	static class ScriptDescriptor {
		int version;
		String description;
		String location;

		public ScriptDescriptor(int version, String description, String location) {
			super();
			this.version = version;
			this.description = description;
			this.location = location;
		}

	}

	/**
	 * The list with the scripts to be executed consequently
	 */
	static List<ScriptDescriptor> scriptDescriptors = new ArrayList<ScriptDescriptor>();
	static {
		scriptDescriptors.add(new ScriptDescriptor(1, INITIAL_CREATION,
				DBScriptsMap.SCRIPT_CREATE_SCHEMA_1));
		scriptDescriptors.add(new ScriptDescriptor(2, TEST_UPDATE,
				DBScriptsMap.SCRIPT_CREATE_SCHEMA_2));
		scriptDescriptors.add(new ScriptDescriptor(3,
				FREE_TEXT_SEARCH_IN_DOCUMENTS,
				DBScriptsMap.SCRIPT_CREATE_SCHEMA_3));
		scriptDescriptors.add(new ScriptDescriptor(4, FILE_VERSIONS_SUPPORT,
				DBScriptsMap.SCRIPT_CREATE_SCHEMA_4));
		scriptDescriptors.add(new ScriptDescriptor(5, SECURITY_FEATURES,
				DBScriptsMap.SCRIPT_CREATE_SCHEMA_5));
		scriptDescriptors.add(new ScriptDescriptor(6, EXTENSION_POINTS,
				DBScriptsMap.SCRIPT_CREATE_SCHEMA_6));
		scriptDescriptors.add(new ScriptDescriptor(7, MESSAGING_HUB,
				DBScriptsMap.SCRIPT_CREATE_SCHEMA_7));
	}

	public DBRepositoryInitializer(DataSource dataSource, Connection connection,
			boolean forceRecreate) {
		logger.debug("entering constructor"); //$NON-NLS-1$
		this.dbUtils = new DBUtils(dataSource);
		this.connection = connection;
		this.forceRecreate = forceRecreate;
		logger.debug("exiting constructor"); //$NON-NLS-1$
	}

	/**
	 * The entry point method
	 * 
	 * @return
	 */
	public boolean initialize() {
		boolean result = false;
		logger.debug("entering initialize"); //$NON-NLS-1$
		if (forceRecreate) {
			result = forceRecreate();
		} else {
			result = updateIncrements();
		}
		logger.debug("exiting initialize"); //$NON-NLS-1$
		return result;
	}

	/**
	 * Drop all tables and create all the schema from scratch
	 * 
	 * @return
	 */
	private boolean forceRecreate() {
		logger.warn("entering forceRecreate"); //$NON-NLS-1$
		boolean result = false;
		for (Iterator<ScriptDescriptor> iterator = scriptDescriptors.iterator(); iterator
				.hasNext();) {
			ScriptDescriptor scriptDescriptor = iterator.next();
			logger.info(String.format(
					INITIALIZING_SCRIPT_VERSION_S_FROM_S_ABOUT_S,
					scriptDescriptor.version, scriptDescriptor.location,
					scriptDescriptor.description));
			String script = null;
			try {
				script = this.dbUtils.readScript(connection, scriptDescriptor.location, this.getClass());
				result = this.dbUtils.executeUpdate(connection, script);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error(script);
			}
			if (!result) {
				break;
			}
		}

		logger.warn("exiting forceRecreate"); //$NON-NLS-1$
		return result;
	}

	/**
	 * Check the tables one by one and try to repair the schema if needed
	 * 
	 * @return
	 */
	private boolean updateIncrements() {
		logger.debug("updateIncrements"); //$NON-NLS-1$

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String script = null;
		try {
			connection = this.dbUtils.getConnection();
			script = this.dbUtils.readScript(connection,
					DBScriptsMap.SCRIPT_GET_SCHEMA_VERSION, this.getClass());
			if (versionExists()) {

				preparedStatement = this.dbUtils
						.getPreparedStatement(connection, script);
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					int version = resultSet.getInt(TABLE_COLUMN_SCHV_VERSION);

					for (Iterator<ScriptDescriptor> iterator = scriptDescriptors
							.iterator(); iterator.hasNext();) {
						ScriptDescriptor scriptDescriptor = iterator.next();
						if (scriptDescriptor.version > version) {
							logger.warn(String
									.format(INITIALIZING_SCRIPT_VERSION_S_FROM_S_ABOUT_S,
											scriptDescriptor.version,
											scriptDescriptor.location,
											scriptDescriptor.description));
							script = this.dbUtils.readScript(
									connection, scriptDescriptor.location,
									this.getClass());
							boolean result = this.dbUtils.executeUpdate(connection, script);
							if (!result) {
								break;
							}
						}
					}
				} else {
					// table version is empty
					forceRecreate();
				}
			} else {
				// table version doesn't exist
				forceRecreate();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error(script);
		} finally {
			this.dbUtils.closeStatement(preparedStatement);
			this.dbUtils.closeConnection(connection);
		}

		logger.debug("exiting updateIncrements"); //$NON-NLS-1$
		return true;
	}

	private boolean versionExists() throws SQLException {
		boolean exists = DBUtils.isTableOrViewExists(connection, TABLE_NAME_DGB_SCHEMA_VERSIONS);
		if (!exists) {
			logger.warn(IT_SEEMS_DGB_SCHEMA_VERSIONS_DOESN_T_EXISTS_CHECK_WHETHER_THIS_MESSAGE_HAS_BEEN_APPEARING_MORE_THAN_ONCE);
		}
		return exists;
	}

}
