package org.eclipse.dirigible.runtime.ide.databases.processor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class for common DataSource operations.
 * An instance represents a single DataSource.
 */
@SuppressWarnings("javadoc")
public class DatabaseQueryHelper {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseQueryHelper.class);

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

	static final String DEFAULT_DATASOURCE_NAME = "Default";

	public enum Config {
		ShowTableContentScript, SchemaFilterScript;
	}

	private DatabaseQueryHelper() {

	}

	public interface Filter<T> {
		boolean accepts(T t);
	}

	public interface ColumnsIteratorCallback {
		void onColumn(String name, String type, String size, String isNullable, String isKey);
	}

	public interface IndicesIteratorCallback {
		void onIndex(String indexName, String indexType, String columnName, String isNonUnique, String indexQualifier, String ordinalPosition,
				String sortOrder, String cardinality, String pagesIndex, String filterCondition);
	}

	public static void iterateTableDefinition(Connection conn, String tableName, String catalogName, String schemaName,
			ColumnsIteratorCallback columnsIteratorCallback, IndicesIteratorCallback indicesIteratorCallback) throws SQLException {

		DatabaseMetaData dmd = conn.getMetaData();

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

	public interface RequestExecutionCallback {
		void updateDone(int recordsCount);

		void queryDone(ResultSet rs);

		void error(Throwable t);
	}

	/**
	 * Executes a single SQL statement. The callbacks are on queryDone in case of query or updateDone in case of update,
	 * and on error. The method does not iterate on the result set and its pointer is in its initial position.
	 * It is up to the callback to do something with it.
	 *
	 * @param connection
	 *            the connection
	 * @param sql
	 *            the SQL expression
	 * @param isQuery
	 *            whether it is a query or update
	 * @param callback
	 *            the callback
	 */
	public static void executeSingleStatement(Connection connection, String sql, boolean isQuery, RequestExecutionCallback callback) {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			if (isQuery) {
				resultSet = preparedStatement.executeQuery();
				callback.queryDone(resultSet);
			} else {
				preparedStatement.executeUpdate();
				callback.updateDone(preparedStatement.getUpdateCount());
			}
		} catch (Exception e) {
			callback.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}

	/**
	 * Callback interface for the {@link DatabaseQueryHelper#executeQueryStatement(Connection, String, ResultSetIteratorCallback)}
	 * method.
	 */
	public interface ResultSetIteratorCallback {
		void onQueryDone(Connection conn, List<NavigableMap<String, Object>> table);

		void onRowConstruction(Connection conn, NavigableMap<String, Object> row);

		void onError(Connection conn, Throwable t);
	}

	/**
	 * Unlike executeSingleStatement(String, boolean, RequestIteratorCallback), this method
	 * iterates on the ResultSet and produces a table data structure in the form of a list of ordered key-value tuples.
	 * Schematically it looks like this:
	 *
	 * <PRE>
	 * [[{column 1:value A}, {column 2: value B}, {column 3: value C}],
	 *  [{column 1:value D}, {column 2: value E}, {column 3: value F}],
	 *  [{column 1:value G}, {column 2: value I}, {column 3: value H}]]
	 * </PRE>
	 *
	 * The callbacks are on completing the whole data structure, on error, and on each row construction.
	 *
	 * @param connection
	 *            the connection
	 * @param sql
	 *            the SQL expression
	 * @param callback
	 *            the callback
	 */
	public static void executeQueryStatement(Connection connection, String sql, ResultSetIteratorCallback callback) {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			int columnsCount = resultSet.getMetaData().getColumnCount();
			List<NavigableMap<String, Object>> table = new ArrayList<NavigableMap<String, Object>>();
			NavigableMap<String, Object> row = new TreeMap<String, Object>();
			for (int i = 1; i <= columnsCount; i++) {
				row.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
				callback.onRowConstruction(connection, row);
				table.add(row);
			}
			callback.onQueryDone(connection, table);
		} catch (Exception e) {
			callback.onError(connection, e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}

}
