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
package org.eclipse.dirigible.runtime.databases.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.api.DatabaseModule;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.api.metadata.DatabaseArtifactTypes;
import org.eclipse.dirigible.databases.helpers.DatabaseErrorHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseQueryHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseQueryHelper.RequestExecutionCallback;
import org.eclipse.dirigible.databases.helpers.DatabaseResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Database SQL Queries Service incoming requests.
 */
public class DatabaseProcessor {


	private static final Logger logger = LoggerFactory.getLogger(DatabaseProcessor.class);

	private static final String CREATE_PROCEDURE = "CREATE PROCEDURE";

	private static final String SCRIPT_DELIMITER = ";";
	private static final String PROCEDURE_DELIMITER = "--";

	private boolean LIMITED = true;

	private IDatabase database = (IDatabase) StaticObjects.get(StaticObjects.DATABASE);

	/**
	 * Exists database.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	public boolean existsDatabase(String type, String name) {
		DataSource dataSource = getDataSource(type, name);
		return dataSource != null;
	}

	/**
	 * Gets the database types.
	 *
	 * @return the database types
	 */
	public List<String> getDatabaseTypes() {
		return DatabaseModule.getDatabaseTypes();
	}

	/**
	 * Gets the data sources.
	 *
	 * @param type
	 *            the type
	 * @return the data sources
	 */
	public Set<String> getDataSources(String type) {
		return DatabaseModule.getDataSources(type);
	}

	/**
	 * Gets the data source.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @return the data source
	 */
	public DataSource getDataSource(String type, String name) {
		DataSource dataSource = null;
		if (type == null) {
			if (name == null) {
				dataSource = database.getDataSource();
			} else {
				dataSource = database.getDataSource(name);
			}
		} else {
			dataSource = DatabaseModule.getDataSource(type, name);
		}
		return dataSource;
	}
	
	/**
	 * Describe the requested artifact in JSON
	 * 
	 * @param dataSource the requested datasource
	 * @param schema the requested schema
	 * @param artifact the requested artifact
	 * @param kind the type of the artifact
	 * @return the JSON representation
	 * @throws SQLException in case of an error
	 */
	public String describeArtifact(DataSource dataSource, String schema, String artifact, String kind) throws SQLException {
		
		String metadata = null;
				
		if (artifact != null && !artifact.trim().isEmpty()) {
			DatabaseArtifactTypes type = DatabaseArtifactTypes.TABLE;
			try {
				type = DatabaseArtifactTypes.valueOf(kind);
			} catch (Exception e) {
				logger.warn("Kind is unknown", e);
			}
			switch (type) {
				case PROCEDURE: metadata = DatabaseMetadataHelper.getProcedureMetadataAsJson(dataSource, schema, artifact); break;
				case FUNCTION: metadata = DatabaseMetadataHelper.getFunctionMetadataAsJson(dataSource, schema, artifact); break;
				default: metadata = DatabaseMetadataHelper.getTableMetadataAsJson(dataSource, schema, artifact); // TABLE, VIEW
			}
		}
		return metadata;
		
	}

	/**
	 * Execute query.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param sql
	 *            the sql
	 * @param isJson
	 *            the is json
	 * @return the string
	 */
	public String executeQuery(String type, String name, String sql, boolean isJson) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, true, isJson);
		}
		return null;
	}

	/**
	 * Execute update.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param sql
	 *            the sql
	 * @param isJson
	 *            the is json
	 * @return the string
	 */
	public String executeUpdate(String type, String name, String sql, boolean isJson) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, false, isJson);
		}
		return null;
	}

	/**
	 * Execute update.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param sql
	 *            the sql
	 * @param isJson
	 *            the is json
	 * @return the string
	 */
	public String executeProcedure(String type, String name, String sql, boolean isJson) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeProcedure(dataSource, sql, isJson);
		}
		return null;
	}

	/**
	 * Execute.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param sql
	 *            the sql
	 * @param isJson
	 *            the is json
	 * @return the string
	 */
	public String execute(String type, String name, String sql, boolean isJson) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, true, isJson);
		}
		return null;
	}

	/**
	 * Execute statement.
	 *
	 * @param dataSource
	 *            the data source
	 * @param sql
	 *            the sql
	 * @param isQuery
	 *            the is query
	 * @param isJson
	 *            the is json
	 * @return the string
	 */
	public String executeStatement(DataSource dataSource, String sql, boolean isQuery, boolean isJson) {

		if ((sql == null) || (sql.length() == 0)) {
			return "";
		}

		List<String> results = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();

		StringTokenizer tokenizer = new StringTokenizer(sql, getDelimiter(sql));
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();
			if ("".equals(line.trim())) {
				continue;
			}

			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DatabaseQueryHelper.executeSingleStatement(connection, line, isQuery, new RequestExecutionCallback() {
					@Override
					public void updateDone(int recordsCount) {
						results.add(recordsCount + "");
					}

					@Override
					public void queryDone(ResultSet rs) {
						try {
							if (isJson) {
								results.add(DatabaseResultSetHelper.toJson(rs, LIMITED));
							} else {
								results.add(DatabaseResultSetHelper.print(rs, LIMITED));
							}
						} catch (SQLException e) {
							logger.warn(e.getMessage(), e);
							errors.add(e.getMessage());
						}
					}

					@Override
					public void error(Throwable t) {
						logger.warn(t.getMessage(), t);
						errors.add(t.getMessage());
					}
				});
			} catch (SQLException e) {
				logger.warn(e.getMessage(), e);
				errors.add(e.getMessage());
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						logger.warn(e.getMessage(), e);
					}
				}
			}
		}

		if (!errors.isEmpty()) {
			if (isJson) {
				return DatabaseErrorHelper.toJson(String.join("\n", errors));
			}
			return DatabaseErrorHelper.print(String.join("\n", errors));
		}

		return String.join("\n", results);
	}

	/**
	 * Execute procedure.
	 *
	 * @param dataSource
	 *            the data source
	 * @param sql
	 *            the sql
	 * @param isJson
	 *            the is json
	 * @return the string
	 */
	private String executeProcedure(DataSource dataSource, String sql, boolean isJson) {

		if ((sql == null) || (sql.length() == 0)) {
			return "";
		}

		List<String> results = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();

		StringTokenizer tokenizer = new StringTokenizer(sql, getDelimiter(sql));
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken();
			if ("".equals(line.trim())) {
				continue;
			}

			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DatabaseQueryHelper.executeSingleProcedure(connection, line, new RequestExecutionCallback() {

					@Override
					public void updateDone(int recordsCount) {
						results.add(recordsCount + "");
					}

					@Override
					public void queryDone(ResultSet rs) {
						try {
							if (isJson) {
								results.add(DatabaseResultSetHelper.toJson(rs, LIMITED));
							} else {
								results.add(DatabaseResultSetHelper.print(rs, LIMITED));
							}
						} catch (SQLException e) {
							logger.warn(e.getMessage(), e);
							errors.add(e.getMessage());
						}
					}

					@Override
					public void error(Throwable t) {
						logger.warn(t.getMessage(), t);
						errors.add(t.getMessage());
					}
				});
			} catch (SQLException e) {
				logger.warn(e.getMessage(), e);
				errors.add(e.getMessage());
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						logger.warn(e.getMessage(), e);
					}
				}
			}
		}

		if (!errors.isEmpty()) {
			if (isJson) {
				return DatabaseErrorHelper.toJson(String.join("\n", errors));
			}
			return DatabaseErrorHelper.print(String.join("\n", errors));
		}

		if (isJson) {
			return GsonHelper.GSON.toJson(results);
		}
		return String.join("\n", results);
	}

	private String getDelimiter(String sql) {
		if (StringUtils.containsIgnoreCase(sql, CREATE_PROCEDURE)) {
			return PROCEDURE_DELIMITER;
		}
		return SCRIPT_DELIMITER;
	}

}
