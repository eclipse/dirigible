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
package org.eclipse.dirigible.database.ds.model.transfer;

import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Table Metadata Helper.
 */
public class TableMetadataHelper {

    /** The Constant DATA_TYPE. */
    private static final String DATA_TYPE = "DATA_TYPE";
    
    /** The Constant COLUMN_NAME. */
    private static final String COLUMN_NAME = "COLUMN_NAME";

    /**
     * Returns the columns result set.
     *
     * @param connection the connection
     * @param tableName  the table name
     * @return the result set with the columns metadata
     * @throws SQLException in case of an error
     */
    public static List<TableColumn> getColumns(Connection connection, String tableName) throws SQLException {
        return getColumns(connection, tableName, null);
    }

    /**
     * Gets the columns.
     *
     * @param connection the connection
     * @param tableName the table name
     * @param schemaName the schema name
     * @return the columns
     * @throws SQLException the SQL exception
     */
    public static List<TableColumn> getColumns(Connection connection, String tableName, String schemaName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        if (tableName == null) {
            throw new SQLException("Error on getting columns of table: null");
        }
        ResultSet columns = meta.getColumns(null, schemaName, DatabaseMetadataHelper.normalizeTableName(tableName), null);
        if (columns.next()) {
            return populateColumns(meta.getColumns(null, schemaName, DatabaseMetadataHelper.normalizeTableName(tableName), null));
        }
        columns = meta.getColumns(null, schemaName, tableName.toLowerCase(), null);
        if (columns.next()) {
            return populateColumns(meta.getColumns(null, schemaName, DatabaseMetadataHelper.normalizeTableName(tableName).toLowerCase(), null));
        }
        columns = meta.getColumns(null, schemaName, DatabaseMetadataHelper.normalizeTableName(tableName).toUpperCase(), null);
        return populateColumns(columns);
    }

    /**
     * Populate columns.
     *
     * @param columns the columns
     * @return the list
     * @throws SQLException the SQL exception
     */
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
     * Returns the primary keys result set.
     *
     * @param connection the connection
     * @param name       the table name
     * @return the result set with the primary keys metadata
     * @throws SQLException in case of an error
     */
    public static ResultSet getPrimaryKeys(Connection connection, String name) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        if (name == null) {
            throw new SQLException("Error on getting primary keys of table: null");
        }
        ResultSet columns = meta.getPrimaryKeys(null, null, DatabaseMetadataHelper.normalizeTableName(name));
        if (columns.next()) {
            return meta.getPrimaryKeys(null, null, DatabaseMetadataHelper.normalizeTableName(name));
        }
        columns = meta.getPrimaryKeys(null, null, DatabaseMetadataHelper.normalizeTableName(name).toLowerCase());
        if (columns.next()) {
            return meta.getPrimaryKeys(null, null, DatabaseMetadataHelper.normalizeTableName(name).toLowerCase());
        }
        columns = meta.getPrimaryKeys(null, null, DatabaseMetadataHelper.normalizeTableName(name).toUpperCase());
        return columns;
    }

}
