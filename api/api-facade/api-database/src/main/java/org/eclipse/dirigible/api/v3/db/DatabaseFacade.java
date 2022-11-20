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
package org.eclipse.dirigible.api.v3.db;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.api.DatabaseModule;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.dynamic.DynamicDatabase;
import org.eclipse.dirigible.database.persistence.processors.identity.PersistenceNextValueIdentityProcessor;
import org.eclipse.dirigible.database.sql.DataTypeUtils;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The Class DatabaseFacade.
 */
public class DatabaseFacade implements IScriptingFacade {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseFacade.class);

	/** The database. */
	private static IDatabase database = null;
	
	/**
	 * Gets the database.
	 *
	 * @return the database
	 */
	protected static synchronized IDatabase getDatabase() {
		if (database == null) {
			database = (IDatabase) StaticObjects.get(StaticObjects.DATABASE);
		}
		return database;
	}

	/**
	 * Gets the database types.
	 *
	 * @return the database types
	 */
	public static final String getDatabaseTypes() {
		return GsonHelper.toJson(DatabaseModule.getDatabaseTypes());
	}

	/**
	 * Gets the data sources.
	 *
	 * @param databaseType the database type
	 * @return the data sources
	 */
	public static final String getDataSources(String databaseType) {
		return GsonHelper.toJson(DatabaseModule.getDataSources(databaseType));
	}

	/**
	 * Gets the data sources.
	 *
	 * @return the data sources
	 */
	public static final String getDataSources() {
		return GsonHelper.toJson(getDatabase().getDataSources().keySet());
	}

	/**
	 * Gets the default data source.
	 *
	 * @return the default data source
	 */
	public static final DataSource getDefaultDataSource() {
		return getDatabase().getDataSource();
	}

	/**
	 * Create a data source.
	 *
	 * @param name the name of the datasource
	 * @param driver the driver of the datasource
	 * @param url the url of the datasource
	 * @param username the username of the datasource
	 * @param password the password of the datasource
	 * @param properties the properties of the datasource
	 * @throws IOException in case of an error
	 */
	public static final void createDataSource(String name, String driver, String url, String username, String password, String properties) throws IOException {
		DynamicDatabase.createDataSource(name, driver, url, username, password, properties);
	}

	/**
	 * Gets the metadata.
	 *
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @return the metadata
	 * @throws SQLException the SQL exception
	 */
	public static final String getMetadata(String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return metadata;
	}

	/**
	 * Gets the metadata.
	 *
	 * @param databaseType the database type
	 * @return the metadata
	 * @throws SQLException the SQL exception
	 */
	public static final String getMetadata(String databaseType) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, null);
		if (dataSource == null) {
			String error = format("No default DataSource in the Database of Type {0} not known.", databaseType);
			throw new IllegalArgumentException(error);
		}
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return metadata;
	}

	/**
	 * Gets the metadata.
	 *
	 * @return the metadata
	 * @throws SQLException the SQL exception
	 */
	public static final String getMetadata() throws SQLException {
		DataSource dataSource = getDataSource(null, null);
		if (dataSource == null) {
			String error = format("No default DataSource in the Default Database.");
			throw new IllegalArgumentException(error);
		}
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return metadata;
	}

	/**
	 * Gets the product name of the database.
	 *
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @return the product name
	 * @throws SQLException the SQL exception
	 */
	public static final String getProductName(String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		String productName = DatabaseMetadataHelper.getProductName(dataSource);
		return productName;
	}

	/**
	 * Gets the product name of the database.
	 *
	 * @param databaseType the database type
	 * @return the product name
	 * @throws SQLException the SQL exception
	 */
	public static final String getProductName(String databaseType) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, null);
		if (dataSource == null) {
			String error = format("No default DataSource in the Database of Type {0} not known.", databaseType);
			throw new IllegalArgumentException(error);
		}
		String productName = DatabaseMetadataHelper.getProductName(dataSource);
		return productName;
	}

	/**
	 * Gets the product name of the database.
	 *
	 * @return the product name
	 * @throws SQLException the SQL exception
	 */
	public static final String getProductName() throws SQLException {
		DataSource dataSource = getDataSource(null, null);
		if (dataSource == null) {
			String error = format("No default DataSource in the Default Database.");
			throw new IllegalArgumentException(error);
		}
		String productName = DatabaseMetadataHelper.getProductName(dataSource);
		return productName;
	}

	/**
	 * Gets the data source.
	 *
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @return the data source
	 */
	private static DataSource getDataSource(String databaseType, String datasourceName) {
		DataSource dataSource = null;
		if (databaseType == null) {
			if (datasourceName == null) {
				dataSource = getDatabase().getDataSource();
			} else {
				dataSource = getDatabase().getDataSource(datasourceName);
			}
		} else {
			dataSource = DatabaseModule.getDataSource(databaseType, datasourceName);
		}
		return dataSource;
	}

	//  ============  Query  ===========

	/**
	 * Executes SQL query.
	 *
	 * @param sql the sql
	 * @param parameters the parameters
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @return the result of the query as JSON
	 * @throws SQLException the SQL exception
	 */
	public static final String query(String sql, String parameters, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			try {
				if (parameters != null) {
					setParameters(parameters, preparedStatement);
				}
				ResultSet resultSet = preparedStatement.executeQuery();
				return DatabaseResultSetHelper.toJson(resultSet, false, false);
			} finally {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Executes SQL query.
	 *
	 * @param sql the sql
	 * @param parameters the parameters
	 * @param databaseType the database type
	 * @return the result of the query as JSON
	 * @throws SQLException the SQL exception
	 */
	public static final String query(String sql, String parameters, String databaseType) throws SQLException {
		return query(sql, parameters, databaseType, null);
	}

	/**
	 * Executes SQL query.
	 *
	 * @param sql the sql
	 * @param parameters the parameters
	 * @return the result of the query as JSON
	 * @throws SQLException the SQL exception
	 */
	public static final String query(String sql, String parameters) throws SQLException {
		return query(sql, parameters, null, null);
	}

	/**
	 * Executes SQL query.
	 *
	 * @param sql the sql
	 * @return the result of the query as JSON
	 * @throws SQLException the SQL exception
	 */
	public static final String query(String sql) throws SQLException {
		return query(sql, null, null, null);
	}

	//  ===========  Update  ===========

	/**
	 * Executes SQL update.
	 *
	 * @param sql the sql
	 * @param parameters the parameters
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @return the number of the rows that has been changed
	 * @throws SQLException the SQL exception
	 */
	public static final int update(String sql, String parameters, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			try {
				if (parameters != null) {
					setParameters(parameters, preparedStatement);
				}
				return preparedStatement.executeUpdate();
			} finally {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Executes SQL update.
	 *
	 * @param sql the sql
	 * @param parameters the parameters
	 * @param databaseType the database type
	 * @return the number of the rows that has been changed
	 * @throws SQLException the SQL exception
	 */
	public static final int update(String sql, String parameters, String databaseType) throws SQLException {
		return update(sql, parameters, databaseType, null);
	}

	/**
	 * Executes SQL update.
	 *
	 * @param sql the sql
	 * @param parameters the parameters
	 * @return the number of the rows that has been changed
	 * @throws SQLException the SQL exception
	 */
	public static final int update(String sql, String parameters) throws SQLException {
		return update(sql, parameters, null, null);
	}

	/**
	 * Executes SQL update.
	 *
	 * @param sql the sql
	 * @return the number of the rows that has been changed
	 * @throws SQLException the SQL exception
	 */
	public static final int update(String sql) throws SQLException {
		return update(sql, null, null, null);
	}

	/**
	 * Sets the parameters.
	 *
	 * @param parameters the parameters
	 * @param preparedStatement the prepared statement
	 * @throws SQLException the SQL exception
	 */
	private static void setParameters(String parameters, PreparedStatement preparedStatement) throws SQLException {
		JsonElement parametersElement = GsonHelper.parseJson(parameters);
		if (parametersElement instanceof JsonArray) {
			JsonArray parametersArray = (JsonArray) parametersElement;
			Iterator<JsonElement> iterator = parametersArray.iterator();
			int i = 1;
			while (iterator.hasNext()) {
				JsonElement parameterElement = iterator.next();
				if (parameterElement.isJsonPrimitive()) {
					if (parameterElement.getAsJsonPrimitive().isBoolean()) {
						preparedStatement.setBoolean(i++, parameterElement.getAsBoolean());
					} else if (parameterElement.getAsJsonPrimitive().isString()) {
						preparedStatement.setString(i++, parameterElement.getAsString());
					} else if (parameterElement.getAsJsonPrimitive().isNumber()) {
						boolean isNumberParameterSet = false;
						int numberIndex = i++;
						try {
							preparedStatement.setInt(numberIndex, parameterElement.getAsInt());
							isNumberParameterSet = true;
						} catch (SQLException | ClassCastException e) {
							// Do nothing
						}

						if (!isNumberParameterSet) {
							try {
								preparedStatement.setShort(numberIndex, parameterElement.getAsShort());
								isNumberParameterSet = true;
							} catch (SQLException | ClassCastException e) {
								// Do nothing
							}
						}
						if (!isNumberParameterSet) {
							try {
								preparedStatement.setLong(numberIndex, parameterElement.getAsLong());
								isNumberParameterSet = true;
							} catch (SQLException | ClassCastException e) {
								// Do nothing
							}
						}
						if (!isNumberParameterSet) {
							try {
								preparedStatement.setBigDecimal(numberIndex, parameterElement.getAsBigDecimal());
								isNumberParameterSet = true;
							} catch (SQLException | ClassCastException e) {
								// Do nothing
							}
						}
						if (!isNumberParameterSet) {
							preparedStatement.setObject(numberIndex, parameterElement.getAsNumber().toString());
						}
					} else {
						throw new IllegalArgumentException("Parameter type unkown");
					}
				} else if (parameterElement.isJsonObject()) {
					JsonObject jsonObject = parameterElement.getAsJsonObject();
					JsonElement typeElement = jsonObject.get("type");
					JsonElement valueElement = jsonObject.get("value");

					if (typeElement.isJsonPrimitive() && typeElement.getAsJsonPrimitive().isString()) {
						String dataType = typeElement.getAsJsonPrimitive().getAsString();

						if(valueElement.isJsonNull()){
							Integer sqlType = DataTypeUtils.getSqlTypeByDataType(dataType);
							preparedStatement.setNull(i++, sqlType);
						} else if (DataTypeUtils.isVarchar(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								String value = valueElement.getAsJsonPrimitive().getAsString();
								preparedStatement.setString(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type VARCHAR");
							}
						} else if (DataTypeUtils.isNvarchar(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								String value = valueElement.getAsJsonPrimitive().getAsString();
								preparedStatement.setString(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type VARCHAR");
							}
						} else if (DataTypeUtils.isChar(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								String value = valueElement.getAsJsonPrimitive().getAsString();
								preparedStatement.setString(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type CHAR");
							}
						} else if (DataTypeUtils.isDate(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Date value = new Date(valueElement.getAsJsonPrimitive().getAsLong());
								preparedStatement.setDate(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Date value = null;
								try {
									value = new Date(Long.parseLong(valueElement.getAsJsonPrimitive().getAsString()));
								} catch (NumberFormatException e) {
									// assume date string in ISO format e.g. 2018-05-22T21:00:00.000Z
									value = new Date(javax.xml.bind.DatatypeConverter.parseDateTime(valueElement.getAsJsonPrimitive().getAsString()).getTime().getTime());
								}
								preparedStatement.setDate(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type DATE");
							}
						} else if (DataTypeUtils.isTime(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Time value = new Time(valueElement.getAsJsonPrimitive().getAsLong());
								preparedStatement.setTime(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Time value = null;
								try {
									value = new Time(Long.parseLong(valueElement.getAsJsonPrimitive().getAsString()));
								} catch (NumberFormatException e) {
									// assume XSDTime
									value = new Time(javax.xml.bind.DatatypeConverter.parseTime(valueElement.getAsJsonPrimitive().getAsString()).getTime().getTime());
								}
								preparedStatement.setTime(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type TIME");
							}
						} else if (DataTypeUtils.isTimestamp(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Timestamp value = new Timestamp(valueElement.getAsJsonPrimitive().getAsLong());
								preparedStatement.setTimestamp(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Timestamp value = null;
								try {
									value = new Timestamp(Long.parseLong(valueElement.getAsJsonPrimitive().getAsString()));
								} catch (NumberFormatException e) {
									// assume date string in ISO format e.g. 2018-05-22T21:00:00.000Z
									value = new Timestamp(javax.xml.bind.DatatypeConverter.parseDateTime(valueElement.getAsJsonPrimitive().getAsString()).getTime().getTime());
								}
								preparedStatement.setTimestamp(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type TIMESTAMP");
							}
						} else if (DataTypeUtils.isInteger(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Integer value = valueElement.getAsJsonPrimitive().getAsInt();
								preparedStatement.setInt(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Integer value = Integer.parseInt(valueElement.getAsJsonPrimitive().getAsString());
								preparedStatement.setInt(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type INTEGER");
							}
						} else if (DataTypeUtils.isTinyint(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								byte value = (byte) valueElement.getAsJsonPrimitive().getAsInt();
								preparedStatement.setByte(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								byte value = (byte) Integer.parseInt(valueElement.getAsJsonPrimitive().getAsString());
								preparedStatement.setByte(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type TINYINT");
							}
						} else if (DataTypeUtils.isSmallint(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								short value = (short) valueElement.getAsJsonPrimitive().getAsInt();
								preparedStatement.setShort(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								short value = (short) Integer.parseInt(valueElement.getAsJsonPrimitive().getAsString());
								preparedStatement.setShort(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type SHORT");
							}
						} else if (DataTypeUtils.isBigint(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Long value = valueElement.getAsJsonPrimitive().getAsBigInteger().longValue();
								preparedStatement.setLong(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Long value = Long.parseLong(valueElement.getAsJsonPrimitive().getAsString());
								preparedStatement.setLong(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type LONG");
							}
						} else if (DataTypeUtils.isReal(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Float value = valueElement.getAsJsonPrimitive().getAsNumber().floatValue();
								preparedStatement.setFloat(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Float value = Float.parseFloat(valueElement.getAsJsonPrimitive().getAsString());
								preparedStatement.setFloat(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type REAL");
							}
						} else if (DataTypeUtils.isDouble(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Double value = valueElement.getAsJsonPrimitive().getAsNumber().doubleValue();
								preparedStatement.setDouble(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Double value = Double.parseDouble(valueElement.getAsJsonPrimitive().getAsString());
								preparedStatement.setDouble(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type DOUBLE");
							}
						} else if (DataTypeUtils.isDecimal(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Double value = valueElement.getAsJsonPrimitive().getAsNumber().doubleValue();
								preparedStatement.setDouble(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Double value = Double.parseDouble(valueElement.getAsJsonPrimitive().getAsString());
								preparedStatement.setDouble(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type DECIMAL");
							}
						} else if (DataTypeUtils.isBoolean(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Boolean value = valueElement.getAsJsonPrimitive().getAsBoolean();
								preparedStatement.setBoolean(i++, value);
							} else if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
								Boolean value = Boolean.parseBoolean(valueElement.getAsJsonPrimitive().getAsString());
								preparedStatement.setBoolean(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type BOOLEAN");
							}
						} else if (DataTypeUtils.isBlob(dataType)) {
							if (valueElement.isJsonArray()) {
								byte[] bytes = BytesHelper.jsonToBytes(valueElement.getAsJsonArray().toString());
								preparedStatement.setBinaryStream(i, new ByteArrayInputStream(bytes), bytes.length);
							}
						} else {
							throw new IllegalArgumentException("Parameter 'type'["+dataType+"] must be a string representing a valid database type name");
						}
					} else {
						throw new IllegalArgumentException("Parameter 'type' must be a string representing the database type name");
					}
				} else {
					throw new IllegalArgumentException("Parameters must contain primitives and objects only");
				}
			}
		} else {
			throw new IllegalArgumentException("Parameters must be provided as a JSON array, e.g. [1, 'John', 9876]");
		}
	}

	/**
	 * Gets the connection.
	 *
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @return the connection
	 * @throws SQLException the SQL exception
	 */
	public static final Connection getConnection(String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = dataSource.getConnection();

		return connection;
	}

	/**
	 * Gets the connection.
	 *
	 * @param databaseType the database type
	 * @return the connection
	 * @throws SQLException the SQL exception
	 */
	public static final Connection getConnection(String databaseType) throws SQLException {
		return getConnection(databaseType, null);
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws SQLException the SQL exception
	 */
	public static final Connection getConnection() throws SQLException {
		return getConnection(null, null);
	}

	//  =========  Sequence  ===========

	/**
	 * Nextval.
	 *
	 * @param sequence the sequence
	 * @return the long
	 * @throws SQLException the SQL exception
	 */
	public static long nextval(String sequence) throws SQLException {
		return nextval(sequence, null, null, null);
	}

	/**
	 * Nextval.
	 *
	 * @param sequence the sequence
	 * @param databaseType the database type
	 * @return the long
	 * @throws SQLException the SQL exception
	 */
	public static long nextval(String sequence, String databaseType) throws SQLException {
		return nextval(sequence, databaseType, null, null);
	}

	/**
	 * Nextval.
	 *
	 * @param sequence the sequence
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @return the nextval
	 * @throws SQLException the SQL exception
	 */
	public static final long nextval(String sequence, String databaseType, String datasourceName, String tableName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			try {
				return getNextVal(sequence, connection);
			} catch (SQLException e) {
				// assuming the sequence does not exists first time, hence create it implicitly
				if (logger.isWarnEnabled()) {logger.warn( format("Implicitly creating a Sequence [{0}] due to: [{1}]", sequence, e.getMessage()));}
				createSequenceInternal(sequence, null, connection, tableName);
				return getNextVal(sequence, connection);
			} catch (IllegalStateException e) {
				// assuming the sequence objects are not supported by the underlying database
				PersistenceNextValueIdentityProcessor persistenceNextValueIdentityProcessor =
						new PersistenceNextValueIdentityProcessor(null);
				long id = persistenceNextValueIdentityProcessor.nextval(connection, sequence);
				return  id;
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Gets the next val.
	 *
	 * @param sequence the sequence
	 * @param connection the connection
	 * @return the next val
	 * @throws SQLException the SQL exception
	 */
	private static long getNextVal(String sequence, Connection connection) throws SQLException {
		String sql = SqlFactory.getNative(connection).nextval(sequence).build();
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		try {
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getLong(1);
			}
			throw new SQLException("ResultSet is empty while getting next value of the Sequence: " + sequence);
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	/**
	 * Creates the sequence internal.
	 *
	 * @param sequence the sequence
	 * @param connection the connection
	 * @throws SQLException the SQL exception
	 */
	private static void createSequenceInternal(String sequence, Integer sequenceStart, Connection connection, String tableName) throws SQLException {
		if (sequenceStart == null && tableName != null) {
			String countSql = SqlFactory.getNative(connection).select().column("count(*)").from(tableName).build();
			PreparedStatement countPreparedStatement = null;
			try {
				countPreparedStatement = connection.prepareStatement(countSql);
				ResultSet rs = countPreparedStatement.executeQuery();
				if (rs.next()) {
					sequenceStart = rs.getInt(1);
					sequenceStart ++;
				}
			} catch(SQLException e) {
				// Do nothing
			} finally {
				if (countPreparedStatement != null) {
					countPreparedStatement.close();
				}
			}
		}

		String sql = SqlFactory.getNative(connection).create().sequence(sequence).start(sequenceStart).build();
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		try {
			preparedStatement.executeUpdate();
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	/**
	 * Creates the sequence.
	 *
	 * @param sequence the sequence
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @throws SQLException the SQL exception
	 */
	public static final void createSequence(String sequence, Integer start, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			createSequenceInternal(sequence, start, connection, null);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Creates the sequence.
	 *
	 * @param sequence the sequence
	 * @param databaseType the database type
	 * @throws SQLException the SQL exception
	 */
	public static void createSequence(String sequence, Integer start, String databaseType) throws SQLException {
		createSequence(sequence, start, databaseType, null);
	}

	/**
	 * Creates the sequence.
	 *
	 * @param sequence the sequence
	 * @throws SQLException the SQL exception
	 */
	public static void createSequence(String sequence, Integer start) throws SQLException {
		createSequence(sequence, null, null);
	}

	/**
	 * Creates the sequence.
	 *
	 * @param sequence the sequence
	 * @throws SQLException the SQL exception
	 */
	public static void createSequence(String sequence) throws SQLException {
		createSequence(sequence, null, null);
	}

	/**
	 * Drop sequence.
	 *
	 * @param sequence the sequence
	 * @param databaseType the database type
	 * @param datasourceName the datasource name
	 * @throws SQLException the SQL exception
	 */
	public static final void dropSequence(String sequence, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			String sql = SqlFactory.getNative(connection).drop().sequence(sequence).build();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			try {
				preparedStatement.executeUpdate();
			} finally {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Drop sequence.
	 *
	 * @param sequence the sequence
	 * @param databaseType the database type
	 * @throws SQLException the SQL exception
	 */
	public static void dropSequence(String sequence, String databaseType) throws SQLException {
		dropSequence(sequence, databaseType, null);
	}

	/**
	 * Drop sequence.
	 *
	 * @param sequence the sequence
	 * @throws SQLException the SQL exception
	 */
	public static void dropSequence(String sequence) throws SQLException {
		dropSequence(sequence, null, null);
	}


	//  =========== SQL ===========


	/**
	 * Gets the default SQL factory.
	 *
	 * @return the default SQL factory
	 * @throws SQLException the SQL exception
	 */
	public static SqlFactory getDefault() throws SQLException {
		return SqlFactory.getDefault();
	}

	/**
	 * Gets a native SQL factory.
	 *
	 * @param connection the connection
	 * @return a native SQL factory
	 * @throws SQLException the SQL exception
	 */
	public static SqlFactory getNative(Connection connection) throws SQLException {
		return SqlFactory.getNative(connection);
	}

}
