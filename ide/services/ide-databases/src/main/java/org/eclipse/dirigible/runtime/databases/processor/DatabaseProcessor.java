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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.api.DatabaseModule;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.api.metadata.DatabaseArtifactTypes;
import org.eclipse.dirigible.database.databases.api.DatabasesException;
import org.eclipse.dirigible.database.databases.api.IDatabasesCoreService;
import org.eclipse.dirigible.database.databases.definition.DatabaseDefinition;
import org.eclipse.dirigible.database.databases.service.DatabasesCoreService;
import org.eclipse.dirigible.database.ds.model.DataStructureTableColumnModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintForeignKeyModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.util.DatabaseModelUtils;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableRelationModel;
import org.eclipse.dirigible.database.transfer.api.DataTransferDefinition;
import org.eclipse.dirigible.database.transfer.api.DataTransferException;
import org.eclipse.dirigible.database.transfer.api.IDataTransferCallbackHandler;
import org.eclipse.dirigible.database.transfer.manager.DataTransferManager;
import org.eclipse.dirigible.database.transfer.manager.DataTransferReverseTableProcessor;
import org.eclipse.dirigible.databases.helpers.DatabaseErrorHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseQueryHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseQueryHelper.RequestExecutionCallback;
import org.eclipse.dirigible.databases.helpers.DatabaseResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Processing the Database SQL Queries Service incoming requests.
 */
public class DatabaseProcessor {


	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseProcessor.class);

	/** The Constant CREATE_PROCEDURE. */
	private static final String CREATE_PROCEDURE = "CREATE PROCEDURE";

	/** The Constant SCRIPT_DELIMITER. */
	private static final String SCRIPT_DELIMITER = ";";
	
	/** The Constant PROCEDURE_DELIMITER. */
	private static final String PROCEDURE_DELIMITER = "--";

	/** The limited. */
	private boolean LIMITED = true;

	/** The database. */
	private IDatabase database = null;
	
	/** The databases core service. */
	private IDatabasesCoreService databasesCoreService = new DatabasesCoreService();
	
	/**
	 * Gets the database.
	 *
	 * @return the database
	 */
	protected synchronized IDatabase getDatabase() {
		if (database == null) {
			database = (IDatabase) StaticObjects.get(StaticObjects.DATABASE);
		}
		return database;
	}

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
				dataSource = getDatabase().getDataSource();
			} else {
				dataSource = getDatabase().getDataSource(name);
			}
		} else {
			dataSource = DatabaseModule.getDataSource(type, name);
		}
		return dataSource;
	}
	
	/**
	 * Describe the requested artifact in JSON.
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
	 * @param isCsv
	 *            the is csv
	 * @return the string
	 */
	public String executeQuery(String type, String name, String sql, boolean isJson, boolean isCsv) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, true, isJson, isCsv);
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
	 * @param isCsv
	 *            the is csv
	 * @return the string
	 */
	public String executeUpdate(String type, String name, String sql, boolean isJson, boolean isCsv) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, false, isJson, isCsv);
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
	 * @param isCsv
	 *            the is csv
	 * @return the string
	 */
	public String executeProcedure(String type, String name, String sql, boolean isJson, boolean isCsv) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeProcedure(dataSource, sql, isJson, isCsv);
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
	 * @param isCsv
	 *            the is csv
	 * @return the string
	 */
	public String execute(String type, String name, String sql, boolean isJson, boolean isCsv) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			return executeStatement(dataSource, sql, true, isJson, isCsv);
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
	 * @param isCsv
	 *            the is csv
	 * @return the string
	 */
	public String executeStatement(DataSource dataSource, String sql, boolean isQuery, boolean isJson, boolean isCsv) {

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
								results.add(DatabaseResultSetHelper.toJson(rs, LIMITED, true));
							} else if (isCsv) {
								results.add(DatabaseResultSetHelper.toCsv(rs, LIMITED, false));
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
	 * @param isCsv
	 *            the is csv
	 * @return the string
	 */
	private String executeProcedure(DataSource dataSource, String sql, boolean isJson, boolean isCsv) {

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
								results.add(DatabaseResultSetHelper.toJson(rs, LIMITED, true));
							} else if (isCsv) {
								results.add(DatabaseResultSetHelper.toCsv(rs, LIMITED, false));
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
	
	/**
	 * Export artifact.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param schema
	 *            the schema
	 * @param artifact
	 *            the artifact
	 * @return the string
	 */
	public String exportArtifact(String type, String name, String schema, String artifact) {
		DataSource dataSource = getDataSource(type, name);
		if (dataSource != null) {
			String sql = "SELECT * FROM \"" + schema + "\".\"" + artifact + "\"";
			return executeStatement(dataSource, sql, true, false, true);
		}
		return null;
	}
	
	/**
	 * Export schema.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param schema
	 *            the schema
	 * @return the string
	 */
	public byte[] exportSchema(String type, String name, String schema) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zipOutputStream = null;
			try {
				zipOutputStream = new ZipOutputStream(baos);
				
				DataSource dataSource = getDataSource(type, name);
				if (dataSource != null) {
					String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
					JsonElement database = GsonHelper.PARSER.parse(metadata);
					JsonArray schemes = database.getAsJsonObject().get("schemas").getAsJsonArray();
					for (int i=0; i<schemes.size(); i++) {
						JsonObject scheme = schemes.get(i).getAsJsonObject();
						if (!scheme.get("name").getAsString().equalsIgnoreCase(schema)) {
							continue;
						}
						JsonArray tables = scheme.get("tables").getAsJsonArray();
						for (int j=0; j<tables.size(); j++) {
							JsonObject table = tables.get(j).getAsJsonObject();
							String artifact = table.get("name").getAsString();
							String sql = "SELECT * FROM \"" + schema + "\".\"" + artifact + "\"";
							String tableExport = executeStatement(dataSource, sql, true, false, true);
							
							ZipEntry zipEntry = new ZipEntry(schema + "." + artifact + ".csv");
							zipOutputStream.putNextEntry(zipEntry);
							zipOutputStream.write((tableExport.getBytes() == null ? new byte[] {} : tableExport.getBytes()));
							zipOutputStream.closeEntry();
						}
					}
				}
		
			} finally {
				if (zipOutputStream != null) {
					zipOutputStream.finish();
					zipOutputStream.flush();
					zipOutputStream.close();
				}
			}

			byte[] result = baos.toByteArray();
			return result;
		} catch(IOException | SQLException e) {
			logger.error(e.getMessage());
			return e.getMessage().getBytes();
		}
	}

	/**
	 * Gets the delimiter.
	 *
	 * @param sql the sql
	 * @return the delimiter
	 */
	private String getDelimiter(String sql) {
		if (StringUtils.containsIgnoreCase(sql, CREATE_PROCEDURE)) {
			return PROCEDURE_DELIMITER;
		}
		return SCRIPT_DELIMITER;
	}
	
	/**
	 * Gets the defined databases.
	 *
	 * @return the defined databases
	 * @throws DatabasesException the databases exception
	 */
	public List<DatabaseDefinition> getDefinedDatabases() throws DatabasesException {
		return databasesCoreService.getDatabases();
	}
	
	/**
	 * Gets the defined database.
	 *
	 * @param id the id
	 * @return the defined database
	 * @throws DatabasesException the databases exception
	 */
	public DatabaseDefinition getDefinedDatabase(long id) throws DatabasesException {
		return databasesCoreService.getDatabase(id);
	}
	
	/**
	 * Creates the defined database.
	 *
	 * @param definition the definition
	 * @return the database definition
	 * @throws DatabasesException the databases exception
	 */
	public DatabaseDefinition createDefinedDatabase(DatabaseDefinition definition) throws DatabasesException {
		return databasesCoreService.createDatabase(definition);
	}
	
	/**
	 * Removes the defined database.
	 *
	 * @param id the id
	 * @throws DatabasesException the databases exception
	 */
	public void removeDefinedDatabase(long id) throws DatabasesException {
		databasesCoreService.removeDatabase(id);
	}
	
	/**
	 * Update defined database.
	 *
	 * @param definition the definition
	 * @throws DatabasesException the databases exception
	 */
	public void updateDefinedDatabase(DatabaseDefinition definition) throws DatabasesException {
		databasesCoreService.createDatabase(definition);
	}
	
	/**
	 * Transfer data.
	 *
	 * @param definition the definition
	 * @param handler the handler
	 * @throws DataTransferException the data transfer exception
	 */
	public void transferData(DataTransferDefinition definition, IDataTransferCallbackHandler handler) throws DataTransferException {
		DataTransferManager.transfer(definition, handler);
	}

	/**
	 * Export artifact metadata.
	 *
	 * @param type the type
	 * @param name the name
	 * @param schema the schema
	 * @param artifact the artifact
	 * @return the string
	 * @throws SQLException the SQL exception
	 */
	public String exportArtifactMetadata(String type, String name, String schema, String artifact) throws SQLException {
		DataSource dataSource = getDataSource(type, name);
		PersistenceTableModel model = DataTransferReverseTableProcessor.reverseTable(dataSource, schema, artifact);
		if (model != null) {
			return GsonHelper.GSON.toJson(DatabaseModelUtils.tableModelToStructure(model));
		}
		return null;
	}

	/**
	 * Export schema metadata.
	 *
	 * @param type the type
	 * @param name the name
	 * @param schema the schema
	 * @return the string
	 * @throws SQLException the SQL exception
	 */
	public String exportSchemaMetadata(String type, String name, String schema) throws SQLException {
		DataSource dataSource = getDataSource(type, name);
		List<PersistenceTableModel> models = DataTransferReverseTableProcessor.reverseTables(dataSource, schema, null);
		return GsonHelper.GSON.toJson(DatabaseModelUtils.tableModelsToSchema(schema, models));
	}

}
