/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.databases.helpers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.api.metadata.DatabaseMetadata;
import org.eclipse.dirigible.database.api.metadata.FunctionMetadata;
import org.eclipse.dirigible.database.api.metadata.ProcedureMetadata;
import org.eclipse.dirigible.database.api.metadata.SchemaMetadata;
import org.eclipse.dirigible.database.api.metadata.TableMetadata;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Database Metadata Helper.
 */
public class DatabaseMetadataHelper {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseMetadataHelper.class);

	/** The Constant SYSTEM_TABLE. */
	static final String SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$

	/** The Constant LOCAL_TEMPORARY. */
	static final String LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$

	/** The Constant GLOBAL_TEMPORARY. */
	static final String GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$

	/** The Constant SYNONYM. */
	static final String SYNONYM = "SYNONYM"; //$NON-NLS-1$

	/** The Constant ALIAS. */
	static final String ALIAS = "ALIAS"; //$NON-NLS-1$

	/** The Constant VIEW. */
	static final String VIEW = "VIEW"; //$NON-NLS-1$

	/** The Constant TABLE. */
	static final String TABLE = "TABLE"; //$NON-NLS-1$

	/** The Constant TABLE_TYPES. */
	static final String[] TABLE_TYPES = { TABLE, VIEW, ALIAS, SYNONYM, GLOBAL_TEMPORARY, LOCAL_TEMPORARY, SYSTEM_TABLE };

	/** The Constant PRCNT. */
	private static final String PRCNT = "%"; //$NON-NLS-1$

	/** The Constant COLUMN_NAME. */
	private static final String COLUMN_NAME = "COLUMN_NAME"; //$NON-NLS-1$
	
	/** The Constant COLUMN_TYPE. */
	private static final String COLUMN_TYPE = "COLUMN_TYPE"; //$NON-NLS-1$

	/** The Constant TYPE_NAME. */
	private static final String TYPE_NAME = "TYPE_NAME"; //$NON-NLS-1$

	/** The Constant COLUMN_SIZE. */
	private static final String COLUMN_SIZE = "COLUMN_SIZE"; //$NON-NLS-1$

	/** The Constant EMPTY. */
	private static final String EMPTY = ""; //$NON-NLS-1$

	/** The Constant PK. */
	private static final String PK = "PK"; //$NON-NLS-1$

	/** The Constant IS_NULLABLE. */
	private static final String IS_NULLABLE = "IS_NULLABLE"; //$NON-NLS-1$

	/** The Constant INDEX_NAME. */
	private static final String INDEX_NAME = "INDEX_NAME"; //$NON-NLS-1$

	/** The Constant TYPE_INDEX. */
	private static final String TYPE_INDEX = "TYPE"; //$NON-NLS-1$

	/** The Constant NON_UNIQUE. */
	private static final String NON_UNIQUE = "NON_UNIQUE"; //$NON-NLS-1$

	/** The Constant INDEX_QUALIFIER. */
	private static final String INDEX_QUALIFIER = "INDEX_QUALIFIER"; //$NON-NLS-1$

	/** The Constant ORDINAL_POSITION. */
	private static final String ORDINAL_POSITION = "ORDINAL_POSITION"; //$NON-NLS-1$

	/** The Constant ASC_OR_DESC. */
	private static final String ASC_OR_DESC = "ASC_OR_DESC"; //$NON-NLS-1$

	/** The Constant CARDINALITY. */
	private static final String CARDINALITY = "CARDINALITY"; //$NON-NLS-1$

	/** The Constant PAGES_INDEX. */
	private static final String PAGES_INDEX = "PAGES"; //$NON-NLS-1$

	/** The Constant FILTER_CONDITION. */
	private static final String FILTER_CONDITION = "FILTER_CONDITION"; //$NON-NLS-1$
	
	/** The Constant PRECISION. */
	private static final String PRECISION = "PRECISION"; //$NON-NLS-1$
	
	/** The Constant LENGTH. */
	private static final String LENGTH = "LENGTH"; //$NON-NLS-1$
	
	/** The Constant SCALE. */
	private static final String SCALE = "SCALE"; //$NON-NLS-1$
	
	/** The Constant RADIX. */
	private static final String RADIX = "RADIX"; //$NON-NLS-1$
	
	/** The Constant NULLABLE. */
	private static final String NULLABLE = "NULLABLE"; //$NON-NLS-1$
	
	/** The Constant REMARKS. */
	private static final String REMARKS = "REMARKS"; //$NON-NLS-1$
	
	/** The Constant DECIMAL_DIGITS. */
	public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS"; //$NON-NLS-1$
	
	
	
	

	/**
	 * The Interface Filter.
	 *
	 * @param <T>
	 *            the generic type
	 */
	public interface Filter<T> {

		/**
		 * Accepts.
		 *
		 * @param t
		 *            the t
		 * @return true, if successful
		 */
		boolean accepts(T t);
	}

	/**
	 * Gets the dialect.
	 *
	 * @param connection
	 *            the connection
	 * @return the dialect
	 */
	private static ISqlDialect getDialect(Connection connection) {
		return SqlFactory.deriveDialect(connection);
	}

	/**
	 * List schemas.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemaNameFilter
	 *            the schema name filter
	 * @param nameFilter
	 *            the name filter
	 * @return the list
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static List<SchemaMetadata> listSchemas(Connection connection, String catalogName, Filter<String> schemaNameFilter,
			Filter<String> nameFilter) throws SQLException {
		
		ISqlDialect sqlDialect = getDialect(connection);

		List<SchemaMetadata> result = new ArrayList<SchemaMetadata>();
		if (sqlDialect.isSchemaFilterSupported()) {
			try {
				// low level filtering for schema
				try (Statement stmt = connection.createStatement()) {
					try (ResultSet rs = stmt.executeQuery(sqlDialect.getSchemaFilterScript())) {
						filterSchemas(connection, catalogName, schemaNameFilter, nameFilter, result, rs);
					}
				}
			} catch (Exception e) {
				DatabaseMetaData dmd = connection.getMetaData();
				// backup in case of wrong product recognition
				try (ResultSet rs = dmd.getSchemas(catalogName, null)) {
					filterSchemas(connection, catalogName, schemaNameFilter, nameFilter, result, rs);
				}
			}
		} else if (sqlDialect.isCatalogForSchema()) {
			DatabaseMetaData dmd = connection.getMetaData();
			try (ResultSet rs = dmd.getCatalogs()) {
				filterSchemas(connection, catalogName, schemaNameFilter, nameFilter, result, rs);
			}
		} else {
			DatabaseMetaData dmd = connection.getMetaData();
			try (ResultSet rs = dmd.getSchemas(catalogName, null)) {
				filterSchemas(connection, catalogName, schemaNameFilter, nameFilter, result, rs);
			}
		}

		return result;
	}

	private static void filterSchemas(Connection connection, String catalogName, Filter<String> schemaNameFilter,
			Filter<String> nameFilter, List<SchemaMetadata> result, ResultSet rs) throws SQLException {
		if (rs != null) {
			while (rs.next()) {
				String schemeName = rs.getString(1); // TABLE_SCHEM or TABLE_CAT
				// higher level filtering for schema if low level is not supported
				if ((schemaNameFilter != null) && !schemaNameFilter.accepts(schemeName)) {
					continue;
				}
				result.add(new SchemaMetadata(schemeName, connection, catalogName, nameFilter));
			}
		}
	}

	/**
	 * List tables.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemeName
	 *            the scheme name
	 * @param tableNameFilter
	 *            the table name filter
	 * @return the list
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static List<TableMetadata> listTables(Connection connection, String catalogName, String schemeName, Filter<String> tableNameFilter)
			throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ISqlDialect sqlDialect = getDialect(connection);

		List<TableMetadata> result = new ArrayList<TableMetadata>();

		ResultSet rs = null;
		try {
			if (sqlDialect.isCatalogForSchema()) {
				rs = dmd.getTables(schemeName, null, PRCNT, TABLE_TYPES);
			} else {
				rs = dmd.getTables(catalogName, schemeName, PRCNT, TABLE_TYPES);
			}

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				String tableType = rs.getString("TABLE_TYPE");
				String tableRemarks = rs.getString("REMARKS");
				if ((tableNameFilter != null) && !tableNameFilter.accepts(tableName)) {
					continue;
				}
				result.add(new TableMetadata(tableName, tableType, tableRemarks, connection, catalogName, schemeName, false));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return result;
	}
	
	/**
	 * List procedures.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemeName
	 *            the scheme name
	 * @param procedureNameFilter
	 *            the procedure name filter
	 * @return the list
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static List<ProcedureMetadata> listProcedures(Connection connection, String catalogName, String schemeName, Filter<String> procedureNameFilter)
			throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ISqlDialect sqlDialect = getDialect(connection);

		List<ProcedureMetadata> result = new ArrayList<ProcedureMetadata>();

		ResultSet rs = null;
		try {
			if (sqlDialect.isCatalogForSchema()) {
				rs = dmd.getProcedures(schemeName, null, PRCNT);
			} else {
				rs = dmd.getProcedures(catalogName, schemeName, PRCNT);
			}

			while (rs.next()) {
				String procedureName = rs.getString("PROCEDURE_NAME");
				String procedureType = rs.getString("PROCEDURE_TYPE");
				String procedureRemarks = rs.getString("REMARKS");
				if ((procedureNameFilter != null) && !procedureNameFilter.accepts(procedureName)) {
					continue;
				}
				result.add(new ProcedureMetadata(procedureName, procedureType, procedureRemarks, connection, catalogName, schemeName, false));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return result;
	}
	
	/**
	 * List functions.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemeName
	 *            the scheme name
	 * @param functionNameFilter
	 *            the function name filter
	 * @return the list
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static List<FunctionMetadata> listFunctions(Connection connection, String catalogName, String schemeName, Filter<String> functionNameFilter)
			throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ISqlDialect sqlDialect = getDialect(connection);

		List<FunctionMetadata> result = new ArrayList<FunctionMetadata>();

		ResultSet rs = null;
		try {
			if (sqlDialect.isCatalogForSchema()) {
				rs = dmd.getFunctions(schemeName, null, PRCNT);
			} else {
				rs = dmd.getFunctions(catalogName, schemeName, PRCNT);
			}

			while (rs.next()) {
				String functionName = rs.getString("FUNCTION_NAME");
				String functionType = rs.getString("FUNCTION_TYPE");
				String functionRemarks = rs.getString("REMARKS");
				if ((functionNameFilter != null) && !functionNameFilter.accepts(functionName)) {
					continue;
				}
				result.add(new FunctionMetadata(functionName, functionType, functionRemarks, connection, catalogName, schemeName, false));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return result;
	}
	
	
	/**
	 * Describe table.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemeName
	 *            the scheme name
	 * @param tableName
	 *            the table name
	 * @return the TableMetadata
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static TableMetadata describeTable(Connection connection, String catalogName, String schemeName, String tableName)
			throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ISqlDialect sqlDialect = getDialect(connection);

		ResultSet rs = null;
		try {
			if (sqlDialect.isCatalogForSchema()) {
				rs = dmd.getTables(schemeName, null, normalizeTableName(tableName), TABLE_TYPES);
			} else {
				rs = dmd.getTables(catalogName, schemeName, normalizeTableName(tableName), TABLE_TYPES);
			}

			if (rs.next()) {
				String tableType = rs.getString("TABLE_TYPE");
				String tableRemarks = rs.getString("REMARKS");
				return new TableMetadata(tableName, tableType, tableRemarks, connection, catalogName, schemeName, true);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return null;
	}
	
	/**
	 * Describe procedure.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemeName
	 *            the scheme name
	 * @param procedureName
	 *            the procedure name
	 * @return the ProcedureMetadata
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static ProcedureMetadata describeProcedure(Connection connection, String catalogName, String schemeName, String procedureName)
			throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ISqlDialect sqlDialect = getDialect(connection);

		ResultSet rs = null;
		try {
			if (sqlDialect.isCatalogForSchema()) {
				rs = dmd.getProcedures(schemeName, null, normalizeTableName(procedureName));
			} else {
				rs = dmd.getProcedures(catalogName, schemeName, normalizeTableName(procedureName));
			}

			if (rs.next()) {
				String procedureType = rs.getString("PROCEDURE_TYPE");
				String procedureRemarks = rs.getString("REMARKS");
				return new ProcedureMetadata(procedureName, procedureType, procedureRemarks, connection, catalogName, schemeName, true);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return null;
	}
	
	/**
	 * Describe function.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemeName
	 *            the scheme name
	 * @param functionName
	 *            the function name
	 * @return the FunctionMetadata
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static FunctionMetadata describeFunction(Connection connection, String catalogName, String schemeName, String functionName)
			throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ISqlDialect sqlDialect = getDialect(connection);

		ResultSet rs = null;
		try {
			if (sqlDialect.isCatalogForSchema()) {
				rs = dmd.getFunctions(schemeName, null, normalizeTableName(functionName));
			} else {
				rs = dmd.getFunctions(catalogName, schemeName, normalizeTableName(functionName));
			}

			if (rs.next()) {
				String functionType = rs.getString("FUNCTION_TYPE");
				String functionRemarks = rs.getString("REMARKS");
				return new FunctionMetadata(functionName, functionType, functionRemarks, connection, catalogName, schemeName, true);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return null;
	}

	/**
	 * The Interface ColumnsIteratorCallback.
	 */
	public interface ColumnsIteratorCallback {

		/**
		 * On column.
		 *
		 * @param name            the name
		 * @param type            the type
		 * @param size            the size
		 * @param isNullable            the is nullable
		 * @param isKey            the is key
		 * @param scale the scale
		 */
		void onColumn(String name, String type, String size, boolean isNullable, boolean isKey, int scale);
	}
	
	/**
	 * The Interface ProceduresColumnsIteratorCallback.
	 */
	public interface ProcedureColumnsIteratorCallback {

		/**
		 * Procedure column callback.
		 *
		 * @param name name
		 * @param kind kind
		 * @param type type
		 * @param precision precision
		 * @param length length
		 * @param scale scale
		 * @param radix radix
		 * @param nullable nullable
		 * @param remarks remarks
		 */
		void onProcedureColumn(String name, int kind, String type, int precision, int length, int scale, int radix, boolean nullable, String remarks);
	}
	
	/**
	 * The Interface FunctionColumnsIteratorCallback.
	 */
	public interface FunctionColumnsIteratorCallback {

		/**
		 * Function column callback.
		 *
		 * @param name name
		 * @param kind kind
		 * @param type type
		 * @param precision precision
		 * @param length length
		 * @param scale scale
		 * @param radix radix
		 * @param nullable nullable
		 * @param remarks remarks
		 */
		void onFunctionColumn(String name, int kind, String type, int precision, int length, int scale, int radix, boolean nullable, String remarks);
	}

	/**
	 * The Interface IndicesIteratorCallback.
	 */
	public interface IndicesIteratorCallback {

		/**
		 * On index.
		 *
		 * @param indexName
		 *            the index name
		 * @param indexType
		 *            the index type
		 * @param columnName
		 *            the column name
		 * @param isNonUnique
		 *            the is non unique
		 * @param indexQualifier
		 *            the index qualifier
		 * @param ordinalPosition
		 *            the ordinal position
		 * @param sortOrder
		 *            the sort order
		 * @param cardinality
		 *            the cardinality
		 * @param pagesIndex
		 *            the pages index
		 * @param filterCondition
		 *            the filter condition
		 */
		void onIndex(String indexName, String indexType, String columnName, boolean isNonUnique, String indexQualifier, String ordinalPosition,
				String sortOrder, String cardinality, String pagesIndex, String filterCondition);
	}

	/**
	 * Iterate table definition.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemaName
	 *            the schema name
	 * @param tableName
	 *            the table name
	 * @param columnsIteratorCallback
	 *            the columns iterator callback
	 * @param indicesIteratorCallback
	 *            the indices iterator callback
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static void iterateTableDefinition(Connection connection, String catalogName, String schemaName, String tableName,
			ColumnsIteratorCallback columnsIteratorCallback, IndicesIteratorCallback indicesIteratorCallback) throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ResultSet columns = dmd.getColumns(catalogName, schemaName, normalizeTableName(tableName), null);
		if (columns == null) {
			throw new SQLException("DatabaseMetaData.getColumns returns null");
		}
		ResultSet pks = dmd.getPrimaryKeys(catalogName, schemaName, normalizeTableName(tableName));
		if (pks == null) {
			throw new SQLException("DatabaseMetaData.getPrimaryKeys returns null");
		}
		ResultSet indexes = dmd.getIndexInfo(catalogName, schemaName, normalizeTableName(tableName), false, false);
		if (indexes == null) {
			throw new SQLException("DatabaseMetaData.getIndexInfo returns null");
		}

		try {

			List<String> pkList = new ArrayList<String>();
			while (pks.next()) {
				String pkName = pks.getString(COLUMN_NAME);
				pkList.add(pkName);
			}

			while (columns.next()) {
				if (columnsIteratorCallback != null) {
					String cname = columns.getString(COLUMN_NAME);
					columnsIteratorCallback.onColumn(cname, columns.getString(TYPE_NAME), columns.getInt(COLUMN_SIZE) + EMPTY,
							columns.getBoolean(IS_NULLABLE), pkList.contains(cname), columns.getInt(DECIMAL_DIGITS));
				}
			}
			while (indexes.next()) {
				if (indicesIteratorCallback != null) {
					indicesIteratorCallback.onIndex(indexes.getString(INDEX_NAME), indexes.getString(TYPE_INDEX), indexes.getString(COLUMN_NAME),
							indexes.getBoolean(NON_UNIQUE), indexes.getString(INDEX_QUALIFIER), indexes.getShort(ORDINAL_POSITION) + EMPTY,
							indexes.getString(ASC_OR_DESC), indexes.getInt(CARDINALITY) + EMPTY, indexes.getInt(PAGES_INDEX) + EMPTY,
							indexes.getString(FILTER_CONDITION));
				}
			}
		} finally {
			columns.close();
			indexes.close();
			pks.close();
		}
	}
	
	/**
	 * Iterate procedure definition.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemaName
	 *            the schema name
	 * @param procedureName
	 *            the procedure name
	 * @param procedureColumnsIteratorCallback
	 *            the procedure columns iterator callback
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static void iterateProcedureDefinition(Connection connection, String catalogName, String schemaName, String procedureName,
			ProcedureColumnsIteratorCallback procedureColumnsIteratorCallback) throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ResultSet columns = dmd.getProcedureColumns(catalogName, schemaName, normalizeTableName(procedureName), null);
		if (columns == null) {
			throw new SQLException("DatabaseMetaData.getProcedureColumns returns null");
		}

		try {

			
			while (columns.next()) {
				if (procedureColumnsIteratorCallback != null) {
					String cname = columns.getString(COLUMN_NAME);
					procedureColumnsIteratorCallback.onProcedureColumn(cname, columns.getInt(COLUMN_TYPE), columns.getString(TYPE_NAME), columns.getInt(PRECISION),
							columns.getInt(LENGTH), columns.getInt(SCALE), columns.getInt(RADIX), columns.getBoolean(NULLABLE), columns.getString(REMARKS));
				}
			}

		} finally {
			columns.close();
		}
	}
	
	/**
	 * Iterate function definition.
	 *
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemaName
	 *            the schema name
	 * @param functionName
	 *            the function name
	 * @param functionColumnsIteratorCallback
	 *            the function columns iterator callback
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static void iterateFunctionDefinition(Connection connection, String catalogName, String schemaName, String functionName,
			FunctionColumnsIteratorCallback functionColumnsIteratorCallback) throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ResultSet columns = dmd.getFunctionColumns(catalogName, schemaName, normalizeTableName(functionName), null);
		if (columns == null) {
			throw new SQLException("DatabaseMetaData.getFunctionColumns returns null");
		}

		try {

			
			while (columns.next()) {
				if (functionColumnsIteratorCallback != null) {
					String cname = columns.getString(COLUMN_NAME);
					functionColumnsIteratorCallback.onFunctionColumn(cname, columns.getInt(COLUMN_TYPE), columns.getString(TYPE_NAME), columns.getInt(PRECISION),
							columns.getInt(LENGTH), columns.getInt(SCALE), columns.getInt(RADIX), columns.getBoolean(NULLABLE), columns.getString(REMARKS));
				}
			}

		} finally {
			columns.close();
		}
	}

	/**
	 * Gets the metadata as json.
	 *
	 * @param dataSource
	 *            the data source
	 * @return the metadata as json
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String getMetadataAsJson(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetadata database = new DatabaseMetadata(connection, null, null, null);
			String json = GsonHelper.toJson(database);
			return json;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					if (logger.isWarnEnabled()) {logger.warn(e.getMessage(), e);}
				}
			}
		}
	}
	
	/**
	 * Gets the metadata as json.
	 *
	 * @param dataSource
	 *            the data source
	 * @param schema
	 * 			  the schema name
	 * @param table
	 * 			  the table name
	 * @return the metadata as json
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String getTableMetadataAsJson(DataSource dataSource, String schema, String table) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			TableMetadata tableMetadata = describeTable(connection, null, schema, table);
			String json = GsonHelper.toJson(tableMetadata);
			return json;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					if (logger.isWarnEnabled()) {logger.warn(e.getMessage(), e);}
				}
			}
		}
	}
	
	/**
	 * Gets the metadata as json.
	 *
	 * @param dataSource
	 *            the data source
	 * @param schema
	 * 			  the schema name
	 * @param procedure
	 * 			  the procedure name
	 * @return the metadata as json
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String getProcedureMetadataAsJson(DataSource dataSource, String schema, String procedure) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			ProcedureMetadata procedureMetadata = describeProcedure(connection, null, schema, procedure);
			String json = GsonHelper.toJson(procedureMetadata);
			return json;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					if (logger.isWarnEnabled()) {logger.warn(e.getMessage(), e);}
				}
			}
		}
	}
	
	/**
	 * Gets the metadata as json.
	 *
	 * @param dataSource
	 *            the data source
	 * @param schema
	 * 			  the schema name
	 * @param function
	 * 			  the function name
	 * @return the metadata as json
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String getFunctionMetadataAsJson(DataSource dataSource, String schema, String function) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			FunctionMetadata functionMetadata = describeFunction(connection, null, schema, function);
			String json = GsonHelper.toJson(functionMetadata);
			return json;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					if (logger.isWarnEnabled()) {logger.warn(e.getMessage(), e);}
				}
			}
		}
	}
	
	/**
	 * Gets the product name.
	 *
	 * @param dataSource            the data source
	 * @return the product name
	 * @throws SQLException             the SQL exception
	 */
	public static String getProductName(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetadata database = new DatabaseMetadata(connection, null, null, null);
			return database.getDatabaseProductName();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					if (logger.isWarnEnabled()) {logger.warn(e.getMessage(), e);}
				}
			}
		}
	}
	
	/**
	 * Makes necessary formatting if needed.
	 *
	 * @param table the table name
	 * @return the formatted table name
	 */
	public static String normalizeTableName(String table) {
		if (table != null && table.startsWith("\"") && table.endsWith("\"")) {
			table = table.substring(1, table.length()-1);
		}
		return table;
	}

}
