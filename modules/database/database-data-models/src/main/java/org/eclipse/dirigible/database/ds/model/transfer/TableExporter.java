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

public class TableExporter {

	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final String THERE_IS_NO_DATA_IN_TABLE = "There is no data in table ";
	private static final String COULD_NOT_RETRIEVE_TABLE_DATA = "Could not retrieve table data reason: Table name is null";
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
		this.tableName = DataStructuresUtils.getCaseSensitiveTableName(tableName);
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

}
