package org.eclipse.dirigible.database.ds.model.transfer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Table Metadata Helper
 *
 */
public class TableMetadataHelper {
	
	private static final String DATA_TYPE = "DATA_TYPE";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	
	/**
	 * Returns the columns result set
	 * 
	 * @param connection the connection
	 * @param name the table name
	 * @return the result set with the columns metadata
	 * @throws SQLException in case of an error
	 */
	public static List<TableColumn> getColumns(Connection connection, String name) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		if (name == null) {
			throw new SQLException("Error on getting columns of table: null");
		}
		ResultSet columns = meta.getColumns(null, null, name, null);
		if (columns.next()) {
			return populateColumns(meta.getColumns(null, null, name, null));
		}
		columns = meta.getColumns(null, null, name.toLowerCase(), null);
		if (columns.next()) {
			return populateColumns(meta.getColumns(null, null, name.toLowerCase(), null));
		}
		columns = meta.getColumns(null, null, name.toUpperCase(), null);
		return populateColumns(columns);
	}
	
	private static List<TableColumn> populateColumns(ResultSet columns) throws SQLException {
		
		List<TableColumn> availableTableColumns = new ArrayList<TableColumn>();
		
		while (columns.next()) {
			// columns
			String columnName = columns.getString(COLUMN_NAME);
			int columnType = columns.getInt(DATA_TYPE);

			TableColumn tableColumn = new TableColumn(columnName, columnType, false, true);
			availableTableColumns.add(tableColumn);
		}
		
		return availableTableColumns;
	}

	/**
	 * Returns the primary keys result set
	 * 
	 * @param connection the connection
	 * @param name the table name
	 * @return the result set with the primary keys metadata
	 * @throws SQLException in case of an error
	 */
	public static ResultSet getPrimaryKeys(Connection connection, String name) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		if (name == null) {
			throw new SQLException("Error on getting primary keys of table: null");
		}
		ResultSet columns = meta.getPrimaryKeys(null, null, name);
		if (columns.next()) {
			return meta.getPrimaryKeys(null, null, name);
		}
		columns = meta.getPrimaryKeys(null, null, name.toLowerCase());
		if (columns.next()) {
			return meta.getPrimaryKeys(null, null, name.toLowerCase());
		}
		columns = meta.getPrimaryKeys(null, null, name.toUpperCase());
		return columns;
	}

}
