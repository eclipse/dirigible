package org.eclipse.dirigible.runtime.databases.helpers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.Squle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DatabaseMetadataHelper {
	
	public static final String SYSTEM_TABLE = "SYSTEM TABLE"; //$NON-NLS-1$
	public static final String LOCAL_TEMPORARY = "LOCAL TEMPORARY"; //$NON-NLS-1$
	public static final String GLOBAL_TEMPORARY = "GLOBAL TEMPORARY"; //$NON-NLS-1$
	public static final String SYNONYM = "SYNONYM"; //$NON-NLS-1$
	public static final String ALIAS = "ALIAS"; //$NON-NLS-1$
	public static final String VIEW = "VIEW"; //$NON-NLS-1$
	public static final String TABLE = "TABLE"; //$NON-NLS-1$

	public static final String[] TABLE_TYPES = { TABLE, VIEW, ALIAS, SYNONYM, GLOBAL_TEMPORARY, LOCAL_TEMPORARY, SYSTEM_TABLE };

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
	
	public interface Filter<T> {
		boolean accepts(T t);
	}
	
	private static ISquleDialect getDialect(Connection connection) {
		return Squle.deriveDialect(connection);
	}
	
	public static List<String> listSchemaNames(Connection connection, String catalogName, Filter<String> schemaNameFilter)
			throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		List<String> listOfSchemes = new ArrayList<String>();
		ResultSet rs = null;

		ISquleDialect squleDialect = getDialect(connection);

		try {

			if (squleDialect.isSchemaFilterSupported()) {
				try {
					// low level filtering for schema
					rs = connection.createStatement().executeQuery(squleDialect.getSchemaFilterScript());
				} catch (Exception e) {
					// backup in case of wrong product recognition
					rs = dmd.getSchemas(catalogName, null);
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			} else if (squleDialect.isCatalogForSchema()) {
				rs = dmd.getCatalogs();
			} else {
				rs = dmd.getSchemas(catalogName, null);
			}
			if (rs != null) {
				while (rs.next()) {
					String schemeName = rs.getString(1);
					// higher level filtering for schema if low level is not supported
					if ((schemaNameFilter != null) && !schemaNameFilter.accepts(schemeName)) {
						continue;
					}
					listOfSchemes.add(schemeName);
				}
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return listOfSchemes;
	}
	
	public static List<String> listTableNames(Connection connection, String catalogName, String schemeName, Filter<String> tableNameFilter)
			throws SQLException {

		DatabaseMetaData dmd = connection.getMetaData();

		ISquleDialect squleDialect = getDialect(connection);

		List<String> listOfTables = new ArrayList<String>();

		ResultSet rs = null;
		if (squleDialect.isCatalogForSchema()) {
			rs = dmd.getTables(schemeName, null, PRCNT, TABLE_TYPES);
		} else {
			rs = dmd.getTables(catalogName, schemeName, PRCNT, TABLE_TYPES);
		}
		try {
			while (rs.next()) {
				String tableName = rs.getString(3);
				if ((tableNameFilter != null) && !tableNameFilter.accepts(tableName)) {
					continue;
				}
				listOfTables.add(tableName);
			}
		} finally {
			rs.close();
		}

		return listOfTables;
	}

	public interface ColumnsIteratorCallback {
		void onColumn(String name, String type, String size, String isNullable, String isKey);
	}

	public interface IndicesIteratorCallback {
		void onIndex(String indexName, String indexType, String columnName, String isNonUnique, String indexQualifier, String ordinalPosition,
				String sortOrder, String cardinality, String pagesIndex, String filterCondition);
	}

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
	
	public static String getAsJson(Connection connection, String catalogName, Filter<String> schemaNameFilter, Filter<String> tableNameFilter) throws SQLException {
		List<String> shemaNames = DatabaseMetadataHelper.listSchemaNames(connection, catalogName, schemaNameFilter);
		JsonObject root = new JsonObject();
		for (String schemaName : shemaNames) {
			JsonObject schema = new JsonObject();
			root.add(schemaName, schema);
			List<String> tableNames = DatabaseMetadataHelper.listTableNames(connection, catalogName, schemaName, tableNameFilter);
			for (String tableName : tableNames) {
				JsonObject table = new JsonObject();
				schema.add(tableName, table);
				JsonArray columns = new JsonArray();
				table.add("columns", columns);
				JsonArray indices = new JsonArray();
				table.add("indices", indices);
				DatabaseMetadataHelper.iterateTableDefinition(connection, catalogName, schemaName, tableName, new ColumnsIteratorCallback() {
					@Override
					public void onColumn(String columnName, String columnType, String columnSize, String isNullable, String isKey) {
						JsonObject column = new JsonObject();
						columns.add(column);
						column.addProperty("columnName", columnName);
						column.addProperty("columnType", columnType);
						column.addProperty("columnSize", columnSize);
						column.addProperty("isNullable", isNullable);
						column.addProperty("isKey", isKey);
					}
				}, new IndicesIteratorCallback() {
					@Override
					public void onIndex(String indexName, String indexType, String columnName, String isNonUnique, String indexQualifier,
							String ordinalPosition, String sortOrder, String cardinality, String pagesIndex, String filterCondition) {
						JsonObject index = new JsonObject();
						indices.add(index);
						index.addProperty("indexName", indexName);
						index.addProperty("indexType", indexType);
						index.addProperty("columnName", columnName);
						index.addProperty("isNonUnique", isNonUnique);
						index.addProperty("indexQualifier", indexQualifier);
						index.addProperty("ordinalPosition", ordinalPosition);
						index.addProperty("sortOrder", sortOrder);
						index.addProperty("cardinality", cardinality);
						index.addProperty("pagesIndex", pagesIndex);
						index.addProperty("filterCondition", filterCondition);
					}
				});
			}
		}
		return root.toString();
	}

}
