/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.databases.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.eclipse.dirigible.database.api.DatabaseModule;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.databases.helpers.DatabaseQueryHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseQueryHelper.RequestExecutionCallback;
import org.eclipse.dirigible.databases.helpers.DatabaseResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Database SQL Queries Service incoming requests
 */
public class DatabaseProcessor {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseProcessor.class);

	private static final String SCRIPT_DELIMITER = ";";
	private boolean LIMITED = true;

	@Inject
	private IDatabase database;

	public boolean existsDatabase(String type, String name) {
		DataSource dataSource = getDataSource(type, name);
		return dataSource != null;
	}

	public List<String> getDatabaseTypes() {
		return DatabaseModule.getDatabaseTypes();
	}

	public Set<String> getDataSources(String type) {
		return DatabaseModule.getDataSources(type);
	}

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

	public String executeQuery(String type, String name, String sql, boolean isJson) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, true, isJson);
		}
		return null;
	}

	public String executeUpdate(String type, String name, String sql, boolean isJson) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, false, isJson);
		}
		return null;
	}

	public String execute(String type, String name, String sql, boolean isJson) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, true, isJson);
		}
		return null;
	}

	public String executeStatement(DataSource dataSource, String sql, boolean isQuery, boolean isJson) {

		if ((sql == null) || (sql.length() == 0)) {
			return "";
		}

		List<String> results = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();

		StringTokenizer tokenizer = new StringTokenizer(sql, SCRIPT_DELIMITER);
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
			return String.join("\n", errors);
		}
		return String.join("\n", results);
	}

}
