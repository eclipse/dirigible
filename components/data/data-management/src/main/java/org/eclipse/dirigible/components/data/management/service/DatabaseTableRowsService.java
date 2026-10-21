/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.management.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.database.ParameterizedStatement;
import org.eclipse.dirigible.components.database.helpers.DatabaseResultSetHelper;
import org.eclipse.dirigible.components.database.params.ParametersSetter;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Reads and edits the rows of one table, as the Database perspective's result view does.
 *
 * <p>
 * Every identifier - the schema, the table and each column - is quoted with the escape symbol of
 * the database's dialect, an embedded escape symbol doubled, so a name can never end its quoted
 * identifier early. Every value is a bound parameter, set the way the database API sets one, so a
 * JSON null is a SQL {@code NULL} and a string reaches a typed column converted.
 */
@Service
public class DatabaseTableRowsService {

    private final DataSourcesManager dataSourcesManager;

    /**
     * Instantiates a new database table rows service.
     *
     * @param dataSourcesManager the data sources manager
     */
    public DatabaseTableRowsService(DataSourcesManager dataSourcesManager) {
        this.dataSourcesManager = dataSourcesManager;
    }

    /**
     * Reads every row of the table.
     *
     * @param datasource the data source name
     * @param schema the schema name
     * @param table the table name
     * @return the rows, as a JSON array of objects keyed by column
     * @throws Exception when the data source is unknown, or the rows cannot be read or written out
     */
    public String findAll(String datasource, String schema, String table) throws Exception {
        try (Connection connection = getDataSource(datasource).getConnection()) {
            char escape = escapeSymbol(connection);
            String sql = "SELECT * FROM " + qualifiedTable(escape, schema, table);
            try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                DatabaseResultSetHelper.toJson(resultSet, false, false, output);
                return output.toString(StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * Inserts a row.
     *
     * @param datasource the data source name
     * @param schema the schema name
     * @param table the table name
     * @param row the column values, keyed by column
     * @throws SQLException when the data source is unknown or the statement fails
     * @throws IllegalArgumentException when the row holds no column
     */
    public void insert(String datasource, String schema, String table, JsonObject row) throws SQLException {
        if (row.isEmpty()) {
            throw new IllegalArgumentException("The row holds no column value");
        }
        try (Connection connection = getDataSource(datasource).getConnection()) {
            char escape = escapeSymbol(connection);
            List<String> columns = new ArrayList<>();
            List<String> placeholders = new ArrayList<>();
            JsonArray parameters = new JsonArray();
            for (Map.Entry<String, JsonElement> column : row.entrySet()) {
                columns.add(quote(escape, column.getKey()));
                placeholders.add("?");
                parameters.add(column.getValue());
            }
            String sql = "INSERT INTO " + qualifiedTable(escape, schema, table) + " (" + String.join(", ", columns) + ") VALUES ("
                    + String.join(", ", placeholders) + ")";
            executeUpdate(connection, sql, parameters);
        }
    }

    /**
     * Updates a row: the columns outside the primary key are set, the key columns select the row.
     *
     * @param datasource the data source name
     * @param schema the schema name
     * @param table the table name
     * @param row the column values, keyed by column, the key columns among them
     * @param primaryKey the primary key columns
     * @throws SQLException when the data source is unknown or the statement fails
     * @throws IllegalArgumentException when the row sets no column, or holds none of the key columns
     */
    public void update(String datasource, String schema, String table, JsonObject row, Set<String> primaryKey) throws SQLException {
        try (Connection connection = getDataSource(datasource).getConnection()) {
            char escape = escapeSymbol(connection);
            List<String> assignments = new ArrayList<>();
            List<String> conditions = new ArrayList<>();
            JsonArray values = new JsonArray();
            JsonArray keyValues = new JsonArray();
            for (Map.Entry<String, JsonElement> column : row.entrySet()) {
                String predicate = quote(escape, column.getKey()) + " = ?";
                if (primaryKey.contains(column.getKey())) {
                    conditions.add(predicate);
                    keyValues.add(column.getValue());
                } else {
                    assignments.add(predicate);
                    values.add(column.getValue());
                }
            }
            if (assignments.isEmpty()) {
                throw new IllegalArgumentException("The row sets no column outside the primary key");
            }
            if (conditions.isEmpty()) {
                throw new IllegalArgumentException("The row holds none of the primary key columns " + primaryKey);
            }
            values.addAll(keyValues);
            String sql = "UPDATE " + qualifiedTable(escape, schema, table) + " SET " + String.join(", ", assignments) + " WHERE "
                    + String.join(" AND ", conditions);
            executeUpdate(connection, sql, values);
        }
    }

    /**
     * Deletes the row the primary key columns select.
     *
     * @param datasource the data source name
     * @param schema the schema name
     * @param table the table name
     * @param row the column values, keyed by column, the key columns among them
     * @param primaryKey the primary key columns
     * @throws SQLException when the data source is unknown or the statement fails
     * @throws IllegalArgumentException when no key column is given, or the row lacks the value of one
     */
    public void delete(String datasource, String schema, String table, JsonObject row, Set<String> primaryKey) throws SQLException {
        if (primaryKey.isEmpty()) {
            throw new IllegalArgumentException("No primary key column is given");
        }
        try (Connection connection = getDataSource(datasource).getConnection()) {
            char escape = escapeSymbol(connection);
            List<String> conditions = new ArrayList<>();
            JsonArray keyValues = new JsonArray();
            for (String column : primaryKey) {
                if (!row.has(column)) {
                    throw new IllegalArgumentException("The row holds no value for the primary key column [" + column + "]");
                }
                conditions.add(quote(escape, column) + " = ?");
                keyValues.add(row.get(column));
            }
            String sql = "DELETE FROM " + qualifiedTable(escape, schema, table) + " WHERE " + String.join(" AND ", conditions);
            executeUpdate(connection, sql, keyValues);
        }
    }

    private DataSource getDataSource(String datasource) {
        DataSource dataSource = dataSourcesManager.getDataSource(datasource);
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source [" + datasource + "] is not known");
        }
        return dataSource;
    }

    private static void executeUpdate(Connection connection, String sql, JsonArray parameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ParametersSetter.setIndexedParameters(parameters, new ParameterizedStatement(statement));
            statement.executeUpdate();
        }
    }

    private static char escapeSymbol(Connection connection) {
        return SqlFactory.deriveDialect(connection)
                         .getEscapeSymbol();
    }

    private static String qualifiedTable(char escape, String schema, String table) {
        return quote(escape, schema) + "." + quote(escape, table);
    }

    /**
     * Quotes an identifier with the dialect's escape symbol, doubling an embedded one.
     */
    private static String quote(char escape, String identifier) {
        String symbol = String.valueOf(escape);
        return symbol + identifier.replace(symbol, symbol + symbol) + symbol;
    }
}
