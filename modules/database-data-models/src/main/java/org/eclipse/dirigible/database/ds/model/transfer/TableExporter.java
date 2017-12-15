/*
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.model.transfer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableExporter {

	private static final String DATA_TYPE = "DATA_TYPE";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final String THERE_IS_NO_DATA_IN_TABLE = "There is no data in table ";
	private static final String COULD_NOT_RETRIEVE_TABLE_DATA = "Could not rettrieve table data reason: Table name is null";
	private static final String ERROR_ON_LOADING_TABLE_COLUMNS_FROM_DATABASE_FOR_TABLE = "Error on loading table columns from the Database for Table: ";

	public static final String DATA_DELIMETER = "|";

	private String tableName;
	private String tableType;
	private TableColumn[] tableColumns;

	private static final Logger logger = LoggerFactory.getLogger(TableExporter.class);

	private DataSource dataSource;

	public TableExporter(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getTableData() {
		String data = "";

		if (getTableName() == null) {
			data = COULD_NOT_RETRIEVE_TABLE_DATA;
			logger.error(COULD_NOT_RETRIEVE_TABLE_DATA);
			return data;
		}

		try {

			Connection connection = null;

			try {

				connection = dataSource.getConnection();

				List<TableColumn> availableTableColumns = new ArrayList<TableColumn>();

				ResultSet primaryKeys = getPrimaryKeys(connection, getTableName());

				while (primaryKeys.next()) {
					// pk columns
					String columnName = primaryKeys.getString(COLUMN_NAME);
					TableColumn tableColumn = new TableColumn(columnName, 0, true, true);
					availableTableColumns.add(tableColumn);
				}

				ResultSet columns = getColumns(connection, getTableName());
				while (columns.next()) {
					// columns
					String columnName = columns.getString(COLUMN_NAME);
					int columnType = columns.getInt(DATA_TYPE);

					TableColumn tableColumn = new TableColumn(columnName, columnType, false, true);
					if (!exists(availableTableColumns, tableColumn)) {
						availableTableColumns.add(tableColumn);
					}
				}

				setTableColumns(availableTableColumns.toArray(new TableColumn[] {}));
				data = getDataForTable();

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (Exception e) {
			logger.error(ERROR_ON_LOADING_TABLE_COLUMNS_FROM_DATABASE_FOR_TABLE + getTableName(), e);
		}
		return data;
	}

	private String getDataForTable() {
		String sql = null;
		String result = null;
		Statement statement = null;
		ResultSet resultSet = null;
		StringBuilder sb = new StringBuilder();
		String tableName = getTableName();
		TableColumn[] columns = getTableColumns();
		Connection connection = null;

		try {
			connection = dataSource.getConnection();

			statement = connection.createStatement();
			sql = SELECT_FROM + tableName;
			resultSet = statement.executeQuery(sql);

			while (resultSet.next()) {
				for (TableColumn column : columns) {
					sb.append(resultSet.getString(column.getName()));
					sb.append(DATA_DELIMETER);
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\n");
			}
			resultSet.close();
			statement.close();

		} catch (SQLException se) {
			logger.error(se.getMessage(), se);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		result = sb.toString();

		if (result.equalsIgnoreCase("")) {
			return THERE_IS_NO_DATA_IN_TABLE + tableName;
		} else {
			return result;
		}
	}

	private boolean exists(List<TableColumn> availableTableColumns, TableColumn tableColumn) {
		if (getTableName() == null) {
			return false;
		}
		for (TableColumn tableColumn2 : availableTableColumns) {
			TableColumn tableColumnX = tableColumn2;
			if (tableColumnX.getName().equals(tableColumn.getName())) {
				tableColumnX.setType(tableColumn.getType());
				return true;
			}
		}
		return false;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public TableColumn[] getTableColumns() {
		return tableColumns;
	}

	public void setTableColumns(TableColumn[] tableColumns) {
		this.tableColumns = tableColumns;
	}

	public static ResultSet getColumns(Connection connection, String name) throws SQLException {
		DatabaseMetaData meta = connection.getMetaData();
		if (name == null) {
			throw new SQLException("Error on getting columns of table: null");
		}
		ResultSet columns = meta.getColumns(null, null, name, null);
		if (columns.next()) {
			return meta.getColumns(null, null, name, null);
		}
		columns = meta.getColumns(null, null, name.toLowerCase(), null);
		if (columns.next()) {
			return meta.getColumns(null, null, name.toLowerCase(), null);
		}
		columns = meta.getColumns(null, null, name.toUpperCase(), null);
		return columns;
	}

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
