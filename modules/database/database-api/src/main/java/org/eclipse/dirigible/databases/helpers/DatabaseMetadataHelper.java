/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.databases.helpers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.api.metadata.DatabaseMetadata;
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

	private static final Logger logger = LoggerFactory.getLogger(DatabaseMetadataHelper.class);

	static final String SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$

	static final String LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$

	static final String GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$

	static final String SYNONYM = "SYNONYM"; //$NON-NLS-1$

	static final String ALIAS = "ALIAS"; //$NON-NLS-1$

	static final String VIEW = "VIEW"; //$NON-NLS-1$

	static final String TABLE = "TABLE"; //$NON-NLS-1$

	static final String[] TABLE_TYPES = { TABLE, VIEW, ALIAS, SYNONYM, GLOBAL_TEMPORARY, LOCAL_TEMPORARY, SYSTEM_TABLE };

	private static final String PRCNT = "%"; //$NON-NLS-1$

	private static final String COLUMN_NAME = "COLUMN_NAME"; //$NON-NLS-1$

	private static final String TYPE_NAME = "TYPE_NAME"; //$NON-NLS-1$

	private static final String COLUMN_SIZE = "COLUMN_SIZE"; //$NON-NLS-1$

	private static final String EMPTY = ""; //$NON-NLS-1$

	private static final String PK = "PK"; //$NON-NLS-1$

	private static final String IS_NULLABLE = "IS_NULLABLE"; //$NON-NLS-1$

	private static final String INDEX_NAME = "INDEX_NAME"; //$NON-NLS-1$

	private static final String TYPE_INDEX = "TYPE"; //$NON-NLS-1$

	private static final String NON_UNIQUE = "NON_UNIQUE"; //$NON-NLS-1$

	private static final String INDEX_QUALIFIER = "INDEX_QUALIFIER"; //$NON-NLS-1$

	private static final String ORDINAL_POSITION = "ORDINAL_POSITION"; //$NON-NLS-1$

	private static final String ASC_OR_DESC = "ASC_OR_DESC"; //$NON-NLS-1$

	private static final String CARDINALITY = "CARDINALITY"; //$NON-NLS-1$

	private static final String PAGES_INDEX = "PAGES"; //$NON-NLS-1$

	private static final String FILTER_CONDITION = "FILTER_CONDITION"; //$NON-NLS-1$

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
	 * @param tableNameFilter
	 *            the table name filter
	 * @return the list
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static List<SchemaMetadata> listSchemas(Connection connection, String catalogName, Filter<String> schemaNameFilter,
			Filter<String> tableNameFilter) throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		List<SchemaMetadata> result = new ArrayList<SchemaMetadata>();
		ResultSet rs = null;

		ISqlDialect sqlDialect = getDialect(connection);

		try {

			if (sqlDialect.isSchemaFilterSupported()) {
				try {
					// low level filtering for schema
					rs = connection.createStatement().executeQuery(sqlDialect.getSchemaFilterScript());
				} catch (Exception e) {
					if (rs != null) {
						rs.close();
					}
					// backup in case of wrong product recognition
					rs = dmd.getSchemas(catalogName, null);
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			} else if (sqlDialect.isCatalogForSchema()) {
				rs = dmd.getCatalogs();
			} else {
				rs = dmd.getSchemas(catalogName, null);
			}
			if (rs != null) {
				while (rs.next()) {
					String schemeName = rs.getString(1); // TABLE_SCHEM or TABLE_CAT
					// higher level filtering for schema if low level is not supported
					if ((schemaNameFilter != null) && !schemaNameFilter.accepts(schemeName)) {
						continue;
					}
					result.add(new SchemaMetadata(schemeName, connection, catalogName, tableNameFilter));
				}
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return result;
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
				result.add(new TableMetadata(tableName, tableType, tableRemarks, connection, catalogName, schemeName));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return result;
	}

	/**
	 * The Interface ColumnsIteratorCallback.
	 */
	public interface ColumnsIteratorCallback {

		/**
		 * On column.
		 *
		 * @param name
		 *            the name
		 * @param type
		 *            the type
		 * @param size
		 *            the size
		 * @param isNullable
		 *            the is nullable
		 * @param isKey
		 *            the is key
		 */
		void onColumn(String name, String type, String size, String isNullable, String isKey);
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
		void onIndex(String indexName, String indexType, String columnName, String isNonUnique, String indexQualifier, String ordinalPosition,
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

		ResultSet columns = dmd.getColumns(catalogName, schemaName, tableName, null);
		if (columns == null) {
			throw new SQLException("DatabaseMetaData.getColumns returns null");
		}
		ResultSet pks = dmd.getPrimaryKeys(catalogName, schemaName, tableName);
		if (pks == null) {
			throw new SQLException("DatabaseMetaData.getPrimaryKeys returns null");
		}
		ResultSet indexes = dmd.getIndexInfo(catalogName, schemaName, tableName, false, false);
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
							columns.getString(IS_NULLABLE), pkList.contains(cname) ? PK : EMPTY);
				}
			}
			while (indexes.next()) {
				if (indicesIteratorCallback != null) {
					indicesIteratorCallback.onIndex(indexes.getString(INDEX_NAME), indexes.getString(TYPE_INDEX), indexes.getString(COLUMN_NAME),
							indexes.getString(NON_UNIQUE), indexes.getString(INDEX_QUALIFIER), indexes.getShort(ORDINAL_POSITION) + EMPTY,
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
			String json = GsonHelper.GSON.toJson(database);
			return json;
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
	
	/**
	 * Gets the product name
	 *
	 * @param dataSource
	 *            the data source
	 * @return the product name
	 * @throws SQLException
	 *             the SQL exception
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
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

}
