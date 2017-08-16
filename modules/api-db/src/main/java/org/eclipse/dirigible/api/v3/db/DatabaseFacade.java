package org.eclipse.dirigible.api.v3.db;

import static java.text.MessageFormat.format;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.database.api.DatabaseModule;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseResultSetHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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
		return GsonHelper.GSON.toJson(database.getDataSources());
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

	public static final String query(String databaseType, String datasourceName, String sql, String parameters) throws SQLException {
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

	public static final String query(String databaseType, String sql, String parameters) throws SQLException {
		return query(databaseType, null, sql, parameters);
	}

	public static final String query(String sql, String parameters) throws SQLException {
		return query(null, null, sql, parameters);
	}

	public static final String query(String sql) throws SQLException {
		return query(null, null, sql, null);
	}

	public static final int update(String databaseType, String datasourceName, String sql, String parameters) throws SQLException {
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

	public static final int update(String databaseType, String sql, String parameters) throws SQLException {
		return update(databaseType, null, sql, parameters);
	}

	public static final int update(String sql, String parameters) throws SQLException {
		return update(null, null, sql, parameters);
	}

	public static final int update(String sql) throws SQLException {
		return update(null, null, sql, null);
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
				} else {
					throw new IllegalArgumentException("Parameters must contain primitives only");
				}
			}
		} else {
			throw new IllegalArgumentException("Parameters must be provided as a JSON array, e.g. [1, 'John', 9876]");
		}
	}

}
