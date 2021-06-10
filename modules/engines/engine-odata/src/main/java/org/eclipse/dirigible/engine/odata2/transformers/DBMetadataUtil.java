/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import com.google.common.base.CaseFormat;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableRelationModel;
import org.eclipse.dirigible.engine.odata2.definition.ODataProperty;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Singleton
public class DBMetadataUtil {

    public static final String DIRIGIBLE_GENERATE_PRETTY_NAMES = "DIRIGIBLE_GENERATE_PRETTY_NAMES";

    private static final boolean IS_CASE_SENSETIVE = Boolean.parseBoolean(Configuration.get(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE));

    @Inject
    private DataSource dataSource;

    public static final String JDBC_COLUMN_PROPERTY = "COLUMN_NAME";
    public static final String JDBC_COLUMN_TYPE = "TYPE_NAME";
    public static final String JDBC_FK_TABLE_NAME_PROPERTY = "FKTABLE_NAME";
    public static final String JDBC_FK_NAME_PROPERTY = "FK_NAME";
    public static final String JDBC_PK_NAME_PROPERTY = "PK_NAME";
    public static final String JDBC_PK_TABLE_NAME_PROPERTY = "PKTABLE_NAME";
    public static final String JDBC_FK_COLUMN_NAME_PROPERTY = "FKCOLUMN_NAME";
    public static final String JDBC_PK_COLUMN_NAME_PROPERTY = "PKCOLUMN_NAME";
    public static final Map<String, String> sqlToOdataEdmColumnTypes = new HashMap<>();

    static {
        sqlToOdataEdmColumnTypes.put("TIME", "Edm.Time");
        sqlToOdataEdmColumnTypes.put("DATE", "Edm.DateTime");
        sqlToOdataEdmColumnTypes.put("SECONDDATE", "Edm.DateTime");
        sqlToOdataEdmColumnTypes.put("TIMESTAMP", "Edm.DateTime");
        sqlToOdataEdmColumnTypes.put("TINYINT", "Edm.Byte");
        sqlToOdataEdmColumnTypes.put("SMALLINT", "Edm.Int16");
        sqlToOdataEdmColumnTypes.put("INTEGER", "Edm.Int32");
        sqlToOdataEdmColumnTypes.put("INT4", "Edm.Int32");
        sqlToOdataEdmColumnTypes.put("BIGINT", "Edm.Int64");
        sqlToOdataEdmColumnTypes.put("SMALLDECIMAL", "Edm.Decimal");
        sqlToOdataEdmColumnTypes.put("DECIMAL", "Edm.Decimal");
        sqlToOdataEdmColumnTypes.put("REAL", "Edm.Single");
        sqlToOdataEdmColumnTypes.put("FLOAT", "Edm.Single");
        sqlToOdataEdmColumnTypes.put("DOUBLE", "Edm.Double");
        sqlToOdataEdmColumnTypes.put("VARCHAR", "Edm.String");
        sqlToOdataEdmColumnTypes.put("NVARCHAR", "Edm.String");
        sqlToOdataEdmColumnTypes.put("CHAR", "Edm.String");
        sqlToOdataEdmColumnTypes.put("NCHAR", "Edm.String");
        sqlToOdataEdmColumnTypes.put("BINARY", "Edm.Binary");
        sqlToOdataEdmColumnTypes.put("VARBINARY", "Edm.Binary");
        sqlToOdataEdmColumnTypes.put("BOOLEAN", "Edm.Boolean");
        sqlToOdataEdmColumnTypes.put("BYTE", "Edm.Byte");
        sqlToOdataEdmColumnTypes.put("BIT", "Edm.Byte");
        sqlToOdataEdmColumnTypes.put("BLOB", "Edm.String");
    }

    public PersistenceTableModel getTableMetadata(String tableName) throws SQLException {
        PersistenceTableModel tableMetadata = new PersistenceTableModel(tableName, new ArrayList<>(), new ArrayList<>());
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetadata = connection.getMetaData();
            addFields(databaseMetadata, connection, tableMetadata);
            addPrimaryKeys(databaseMetadata, connection, tableMetadata);
            addForeignKeys(databaseMetadata, connection, tableMetadata);
            addTableType(databaseMetadata, connection, tableMetadata);
        } catch (SQLException e) {
            throw e;
        }

        convertSqlTypesToOdataEdmTypes(tableMetadata.getColumns());
        return tableMetadata;
    }

    private void addForeignKeys(DatabaseMetaData databaseMetadata, Connection connection, PersistenceTableModel tableMetadata) throws SQLException {
        ResultSet foreignKeys = databaseMetadata.getImportedKeys(connection.getCatalog(), null, normalizeTableName(tableMetadata.getTableName()));
        if (!foreignKeys.isBeforeFirst() && !IS_CASE_SENSETIVE) {
            // Fallback for PostgreSQL
            foreignKeys = databaseMetadata.getImportedKeys(connection.getCatalog(), null, normalizeTableName(tableMetadata.getTableName().toLowerCase()));
        }
        while (foreignKeys.next()) {
            PersistenceTableRelationModel relationMetadata = new PersistenceTableRelationModel(foreignKeys.getString(JDBC_FK_TABLE_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_PK_TABLE_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_FK_COLUMN_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_PK_COLUMN_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_FK_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_PK_NAME_PROPERTY)
            );
            tableMetadata.getRelations().add(relationMetadata);
        }
    }

    private void convertSqlTypesToOdataEdmTypes(List<PersistenceTableColumnModel> columnsMetadata) {
        columnsMetadata.forEach(column -> column.setType(sqlToOdataEdmColumnTypes.get(column.getType().toUpperCase())));
    }

    private void addPrimaryKeys(DatabaseMetaData databaseMetadata, Connection connection, PersistenceTableModel tableMetadata) throws SQLException {
        ResultSet primaryKeys = databaseMetadata.getPrimaryKeys(connection.getCatalog(), null, normalizeTableName(tableMetadata.getTableName()));
        if (!primaryKeys.isBeforeFirst() && !IS_CASE_SENSETIVE) {
            // Fallback for PostgreSQL
            primaryKeys = databaseMetadata.getPrimaryKeys(connection.getCatalog(), null, normalizeTableName(tableMetadata.getTableName().toLowerCase()));
        }
        while (primaryKeys.next()) {
            setColumnPrimaryKey(primaryKeys.getString(JDBC_COLUMN_PROPERTY), tableMetadata);
        }
    }

    private void setColumnPrimaryKey(String columnName, PersistenceTableModel tableModel) {
        tableModel.getColumns().forEach(column -> {
            if (column.getName().equals(columnName)) {
                column.setPrimaryKey(true);
            }
        });
    }

    private void addFields(DatabaseMetaData databaseMetadata, Connection connection, PersistenceTableModel tableMetadata) throws SQLException {
        ResultSet columns = databaseMetadata.getColumns(connection.getCatalog(), null, normalizeTableName(tableMetadata.getTableName()), null);
        if (!columns.isBeforeFirst() && !IS_CASE_SENSETIVE) {
            // Fallback for PostgreSQL
            columns = databaseMetadata.getColumns(connection.getCatalog(), null, normalizeTableName(tableMetadata.getTableName().toLowerCase()), null);
        }
        while (columns.next()) {
            tableMetadata.getColumns().add(
                    new PersistenceTableColumnModel(
                            columns.getString(JDBC_COLUMN_PROPERTY),
                            columns.getString(JDBC_COLUMN_TYPE),
                            false));
        }
    }

    public static String getPropertyNameFromDbColumnName(String DbColumnName, List<ODataProperty> oDataProperties, boolean prettyPrint) {
        for (ODataProperty next : oDataProperties) {
            if (DbColumnName.equals(next.getColumn())) {
                return next.getName();
            }
        }
        return prettyPrint ? addCorrectFormatting(DbColumnName) : DbColumnName;
    }

    public static boolean isPropColumnValidDBColumn(String propColumn, List<PersistenceTableColumnModel> dbColumns) {
        for (PersistenceTableColumnModel next : dbColumns) {
            if (next.getName().equals(propColumn)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNullable(PersistenceTableColumnModel column, List<ODataProperty> properties) {
        String columnName = column.getName();
        for (ODataProperty next : properties) {
            if (columnName.equals(next.getColumn())) {
                return next.isNullable();
            }
        }
        return column.isNullable();
    }

    public static String getType(PersistenceTableColumnModel column, List<ODataProperty> properties) {
        String columnName = column.getName();
        for (ODataProperty next : properties) {
            if (next.getType() != null) {
                if (columnName.equals(next.getColumn()) || !IS_CASE_SENSETIVE && columnName.equalsIgnoreCase(next.getColumn())) {
                    return next.getType();
                }
            }
        }
        return column.getType();
    }

    public static String addCorrectFormatting(String columnName) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, columnName);
    }

    public static String normalizeTableName(String table) {
        if (table != null && table.startsWith("\"") && table.endsWith("\"")) {
            table = table.substring(1, table.length() - 1);
        }
        return table;
    }

    private void addTableType(DatabaseMetaData databaseMetadata, Connection connection, PersistenceTableModel tableMetadata) throws SQLException {
        ResultSet tables = databaseMetadata.getTables(connection.getCatalog(), null, normalizeTableName(tableMetadata.getTableName()), null);
        if (!tables.isBeforeFirst() && !IS_CASE_SENSETIVE) {
            // Fallback for PostgreSQL
            tables = databaseMetadata.getTables(connection.getCatalog(), null, normalizeTableName(tableMetadata.getTableName().toLowerCase()), null);
        }
        while (tables.next()) {
            tableMetadata.setTableType(tables.getString("TABLE_TYPE"));
        }
    }

}
