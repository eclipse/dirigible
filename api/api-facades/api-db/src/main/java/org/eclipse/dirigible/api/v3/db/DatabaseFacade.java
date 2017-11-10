package org.eclipse.dirigible.api.v3.db;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
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
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.database.api.DatabaseModule;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.sql.DataTypeUtils;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DatabaseFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseFacade.class);

	private static IDatabase database = StaticInjector.getInjector().getInstance(IDatabase.class);;

	public static final String getDatabaseTypes() {
		return GsonHelper.GSON.toJson(DatabaseModule.getDatabaseTypes());
	}

	public static final String getDataSources(String databaseType) {
		return GsonHelper.GSON.toJson(DatabaseModule.getDataSources(databaseType));
	}

	public static final String getDataSources() {
		return GsonHelper.GSON.toJson(database.getDataSources().keySet());
	}

	public static final String getMetadata(String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return metadata;
	}

	public static final String getMetadata(String databaseType) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, null);
		if (dataSource == null) {
			String error = format("No default DataSource in the Database of Type {0} not known.", databaseType);
			throw new IllegalArgumentException(error);
		}
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return metadata;
	}

	public static final String getMetadata() throws SQLException {
		DataSource dataSource = getDataSource(null, null);
		if (dataSource == null) {
			String error = format("No default DataSource in the Default Database.");
			throw new IllegalArgumentException(error);
		}
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return metadata;
	}

	private static DataSource getDataSource(String databaseType, String datasourceName) {
		DataSource dataSource = null;
		if (databaseType == null) {
			if (datasourceName == null) {
				dataSource = database.getDataSource();
			} else {
				dataSource = database.getDataSource(datasourceName);
			}
		} else {
			dataSource = DatabaseModule.getDataSource(databaseType, datasourceName);
		}
		return dataSource;
	}

	//  ============  Query  ===========
	
	public static final String query(String sql, String parameters, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = dataSource.getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			try {
				if (parameters != null) {
					setParameters(parameters, preparedStatement);
				}
				ResultSet resultSet = preparedStatement.executeQuery();
				return DatabaseResultSetHelper.toJson(resultSet, true);
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

	public static final String query(String sql, String parameters, String databaseType) throws SQLException {
		return query(sql, parameters, databaseType, null);
	}

	public static final String query(String sql, String parameters) throws SQLException {
		return query(sql, parameters, null, null);
	}

	public static final String query(String sql) throws SQLException {
		return query(sql, null, null, null);
	}
	
	//  ===========  Update  ===========

	public static final int update(String sql, String parameters, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = dataSource.getConnection();
		try {
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

	public static final int update(String sql, String parameters, String databaseType) throws SQLException {
		return update(sql, parameters, databaseType, null);
	}

	public static final int update(String sql, String parameters) throws SQLException {
		return update(sql, parameters, null, null);
	}

	public static final int update(String sql) throws SQLException {
		return update(sql, null, null, null);
	}

	private static void setParameters(String parameters, PreparedStatement preparedStatement) throws SQLException {
		JsonElement parametersElement = GsonHelper.PARSER.parse(parameters);
		if (parametersElement instanceof JsonArray) {
			JsonArray parametersArray = (JsonArray) parametersElement;
			Iterator<JsonElement> iterator = parametersArray.iterator();
			int i = 1;
			while (iterator.hasNext()) {
				JsonElement parameterElement = iterator.next();
				if (parameterElement.isJsonPrimitive()) {
					if (parameterElement.getAsJsonPrimitive().isBoolean()) {
						preparedStatement.setBoolean(i++, parameterElement.getAsBoolean());
					} else if (parameterElement.getAsJsonPrimitive().isNumber()) {
						preparedStatement.setObject(i++, parameterElement.getAsNumber().toString());
					} else if (parameterElement.getAsJsonPrimitive().isString()) {
						preparedStatement.setString(i++, parameterElement.getAsString());
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
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type DATE");
							}
						} else if (DataTypeUtils.isTime(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Time value = new Time(valueElement.getAsJsonPrimitive().getAsLong());
								preparedStatement.setTime(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type TIME");
							}
						} else if (DataTypeUtils.isTimestamp(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Timestamp value = new Timestamp(valueElement.getAsJsonPrimitive().getAsLong());
								preparedStatement.setTimestamp(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type TIMESTAMP");
							}
						} else if (DataTypeUtils.isInteger(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Integer value = valueElement.getAsJsonPrimitive().getAsInt();
								preparedStatement.setInt(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type INTEGER");
							}
						} else if (DataTypeUtils.isTinyint(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								byte value = new Integer(valueElement.getAsJsonPrimitive().getAsInt()).byteValue();
								preparedStatement.setByte(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type TINYINT");
							}
						} else if (DataTypeUtils.isSmallint(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								short value = new Integer(valueElement.getAsJsonPrimitive().getAsInt()).shortValue();
								preparedStatement.setShort(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type SHORT");
							}
						} else if (DataTypeUtils.isBigint(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Long value = valueElement.getAsJsonPrimitive().getAsBigInteger().longValue();
								preparedStatement.setLong(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type LONG");
							}
						} else if (DataTypeUtils.isReal(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Float value = valueElement.getAsJsonPrimitive().getAsNumber().floatValue();
								preparedStatement.setFloat(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type REAL");
							}
						} else if (DataTypeUtils.isDouble(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Double value = valueElement.getAsJsonPrimitive().getAsNumber().doubleValue();
								preparedStatement.setDouble(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type DOUBLE");
							}
						} else if (DataTypeUtils.isDecimal(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Double value = valueElement.getAsJsonPrimitive().getAsNumber().doubleValue();
								preparedStatement.setDouble(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type DECIMAL");
							}
						} else if (DataTypeUtils.isBoolean(dataType)) {
							if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isNumber()) {
								Boolean value = valueElement.getAsJsonPrimitive().getAsBoolean();
								preparedStatement.setBoolean(i++, value);
							} else {
								throw new IllegalArgumentException("Wrong value of the parameter of type BOOLEAN");
							}
						} else if (DataTypeUtils.isBlob(dataType)) {
							if (valueElement.isJsonArray()) {
								byte[] bytes = BytesHelper.jsonToBytes(valueElement.getAsJsonArray().toString());
								preparedStatement.setBinaryStream(i, new ByteArrayInputStream(bytes), bytes.length);
							}
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

	public static final Connection getConnection(String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = dataSource.getConnection();
		return connection;
	}

	public static final Connection getConnection(String databaseType) throws SQLException {
		return getConnection(databaseType, null);
	}

	public static final Connection getConnection() throws SQLException {
		return getConnection(null, null);
	}
	
	//  =========  Sequence  ===========
	
	
	public static final long nextval(String sequence, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = dataSource.getConnection();
		try {
			try {
				return getNextVal(sequence, connection);
			} catch (SQLException e) {
				// assuming the sequence does not exists first time, hence create it implicitly
				logger.warn( format("Implicitly creating a Sequence [{0}] due to: [{1}]", sequence, e.getMessage()));
				createSequenceInternal(sequence, connection);
				return getNextVal(sequence, connection);
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

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

	private static void createSequenceInternal(String sequence, Connection connection) throws SQLException {
		String sql = SqlFactory.getNative(connection).create().sequence(sequence).build();
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		try {
			preparedStatement.executeUpdate();
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	public static long nextval(String sequence, String databaseType) throws SQLException {
		return nextval(sequence, databaseType, null);
	}

	public static long nextval(String sequence) throws SQLException {
		return nextval(sequence, null, null);
	}
	
	public static final void createSequence(String sequence, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = dataSource.getConnection();
		try {
			createSequenceInternal(sequence, connection);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	public static void createSequence(String sequence, String databaseType) throws SQLException {
		createSequence(sequence, databaseType, null);
	}

	public static void createSequence(String sequence) throws SQLException {
		createSequence(sequence, null, null);
	}
	
	public static final void dropSequence(String sequence, String databaseType, String datasourceName) throws SQLException {
		DataSource dataSource = getDataSource(databaseType, datasourceName);
		if (dataSource == null) {
			String error = format("DataSource {0} of Database Type {1} not known.", datasourceName, databaseType);
			throw new IllegalArgumentException(error);
		}
		Connection connection = dataSource.getConnection();
		try {
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

	public static void dropSequence(String sequence, String databaseType) throws SQLException {
		dropSequence(sequence, databaseType, null);
	}

	public static void dropSequence(String sequence) throws SQLException {
		dropSequence(sequence, null, null);
	}
	
	
	//  =========== SQL ===========
	
	
	public static SqlFactory getDefault() throws SQLException {
		return SqlFactory.getDefault();
	}

	public static SqlFactory getNative(Connection connection) throws SQLException {
		return SqlFactory.getNative(connection);
	}
	
}
