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
package org.eclipse.dirigible.database.ds.model.transfer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.DataStructuresUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TableExporter.
 */
public class TableExporter {

	/** The Constant SELECT_FROM. */
	private static final String SELECT_FROM = "SELECT * FROM ";
	
	/** The Constant THERE_IS_NO_DATA_IN_TABLE. */
	private static final String THERE_IS_NO_DATA_IN_TABLE = "There is no data in table ";
	
	/** The Constant COULD_NOT_RETRIEVE_TABLE_DATA. */
	private static final String COULD_NOT_RETRIEVE_TABLE_DATA = "Could not retrieve table data reason: Table name is null";
	
	/** The Constant ERROR_ON_LOADING_TABLE_COLUMNS_FROM_DATABASE_FOR_TABLE. */
	private static final String ERROR_ON_LOADING_TABLE_COLUMNS_FROM_DATABASE_FOR_TABLE = "Error on loading table columns from the Database for Table: ";

	/** The Constant DATA_DELIMETER. */
	public static final String DATA_DELIMETER = "|";

	/** The table name. */
	private String tableName;
	
	/** The table type. */
	private String tableType;
	
	/** The table columns. */
	private TableColumn[] tableColumns;

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TableExporter.class);

	/** The data source. */
	private DataSource dataSource;

	/**
	 * Instantiates a new table exporter.
	 *
	 * @param dataSource the data source
	 */
	public TableExporter(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Gets the table data.
	 *
	 * @return the table data
	 */
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
				List<TableColumn> availableTableColumns = TableMetadataHelper.getColumns(connection, getTableName());
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

	/**
	 * Gets the data for table.
	 *
	 * @return the data for table
	 */
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

	/**
	 * Exists.
	 *
	 * @param availableTableColumns the available table columns
	 * @param tableColumn the table column
	 * @return true, if successful
	 */
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

	/**
	 * Gets the table name.
	 *
	 * @return the table name
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Sets the table name.
	 *
	 * @param tableName the new table name
	 */
	public void setTableName(String tableName) {
		this.tableName = DataStructuresUtils.getCaseSensitiveTableName(tableName);
	}

	/**
	 * Gets the table type.
	 *
	 * @return the table type
	 */
	public String getTableType() {
		return tableType;
	}

	/**
	 * Sets the table type.
	 *
	 * @param tableType the new table type
	 */
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	/**
	 * Gets the table columns.
	 *
	 * @return the table columns
	 */
	public TableColumn[] getTableColumns() {
		return tableColumns;
	}

	/**
	 * Sets the table columns.
	 *
	 * @param tableColumns the new table columns
	 */
	public void setTableColumns(TableColumn[] tableColumns) {
		this.tableColumns = tableColumns;
	}

}
