package org.eclipse.dirigible.runtime.databases.helpers;

import java.sql.Connection;
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

	public enum Config {
		ShowTableContentScript, SchemaFilterScript;
	}

	private DatabaseQueryHelper() {

	}

	public interface Filter<T> {
		boolean accepts(T t);
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
