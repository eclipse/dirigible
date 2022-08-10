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
package org.eclipse.dirigible.engine.odata2.transformers;

import com.google.common.base.CaseFormat;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableRelationModel;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.engine.odata2.definition.ODataProperty;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The Class DBMetadataUtil.
 */
public class DBMetadataUtil {

    /** The Constant DIRIGIBLE_GENERATE_PRETTY_NAMES. */
    public static final String DIRIGIBLE_GENERATE_PRETTY_NAMES = "DIRIGIBLE_GENERATE_PRETTY_NAMES";

    /** The Constant IS_CASE_SENSETIVE. */
    private static final boolean IS_CASE_SENSETIVE = Boolean.parseBoolean(Configuration.get(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE));

    /** The data source. */
    private DataSource dataSource = null;

    /** The Constant JDBC_COLUMN_NAME_PROPERTY. */
    public static final String JDBC_COLUMN_NAME_PROPERTY = "COLUMN_NAME";
    
    /** The Constant JDBC_COLUMN_TYPE_PROPERTY. */
    public static final String JDBC_COLUMN_TYPE_PROPERTY = "TYPE_NAME";
    
    /** The Constant JDBC_COLUMN_NULLABLE_PROPERTY. */
    public static final String JDBC_COLUMN_NULLABLE_PROPERTY = "NULLABLE";
    
    /** The Constant JDBC_COLUMN_SIZE_PROPERTY. */
    public static final String JDBC_COLUMN_SIZE_PROPERTY = "COLUMN_SIZE";
    
    /** The Constant JDBC_COLUMN_DECIMAL_DIGITS_PROPERTY. */
    public static final String JDBC_COLUMN_DECIMAL_DIGITS_PROPERTY = "DECIMAL_DIGITS";
    
    /** The Constant JDBC_FK_TABLE_NAME_PROPERTY. */
    public static final String JDBC_FK_TABLE_NAME_PROPERTY = "FKTABLE_NAME";
    
    /** The Constant JDBC_FK_NAME_PROPERTY. */
    public static final String JDBC_FK_NAME_PROPERTY = "FK_NAME";
    
    /** The Constant JDBC_PK_NAME_PROPERTY. */
    public static final String JDBC_PK_NAME_PROPERTY = "PK_NAME";
    
    /** The Constant JDBC_PK_TABLE_NAME_PROPERTY. */
    public static final String JDBC_PK_TABLE_NAME_PROPERTY = "PKTABLE_NAME";
    
    /** The Constant JDBC_FK_COLUMN_NAME_PROPERTY. */
    public static final String JDBC_FK_COLUMN_NAME_PROPERTY = "FKCOLUMN_NAME";
    
    /** The Constant JDBC_PK_COLUMN_NAME_PROPERTY. */
    public static final String JDBC_PK_COLUMN_NAME_PROPERTY = "PKCOLUMN_NAME";
    
    /** The Constant SQL_TO_ODATA_EDM_TYPES. */
    private static final Map<String, String> SQL_TO_ODATA_EDM_TYPES = new HashMap<>();

    static {
        SQL_TO_ODATA_EDM_TYPES.put("TIME", "Edm.Time");
        SQL_TO_ODATA_EDM_TYPES.put("DATE", "Edm.DateTime");
        SQL_TO_ODATA_EDM_TYPES.put("SECONDDATE", "Edm.DateTime");
        SQL_TO_ODATA_EDM_TYPES.put("TIMESTAMP", "Edm.DateTime");
        SQL_TO_ODATA_EDM_TYPES.put("TINYINT", "Edm.Byte");
        SQL_TO_ODATA_EDM_TYPES.put("SMALLINT", "Edm.Int16");
        SQL_TO_ODATA_EDM_TYPES.put("INTEGER", "Edm.Int32");
        SQL_TO_ODATA_EDM_TYPES.put("INT4", "Edm.Int32");
        SQL_TO_ODATA_EDM_TYPES.put("BIGINT", "Edm.Int64");
        SQL_TO_ODATA_EDM_TYPES.put("SMALLDECIMAL", "Edm.Decimal");
        SQL_TO_ODATA_EDM_TYPES.put("DECIMAL", "Edm.Decimal");
        SQL_TO_ODATA_EDM_TYPES.put("REAL", "Edm.Single");
        SQL_TO_ODATA_EDM_TYPES.put("FLOAT", "Edm.Single");
        SQL_TO_ODATA_EDM_TYPES.put("DOUBLE", "Edm.Double");
        SQL_TO_ODATA_EDM_TYPES.put("DOUBLE PRECISION", "Edm.Double");
        SQL_TO_ODATA_EDM_TYPES.put("VARCHAR", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("NVARCHAR", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("CHARACTER VARYING", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("CHARACTER", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("CHAR", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("NCHAR", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("BINARY", "Edm.Binary");
        SQL_TO_ODATA_EDM_TYPES.put("VARBINARY", "Edm.Binary");
        SQL_TO_ODATA_EDM_TYPES.put("BOOLEAN", "Edm.Boolean");
        SQL_TO_ODATA_EDM_TYPES.put("BYTE", "Edm.Byte");
        SQL_TO_ODATA_EDM_TYPES.put("BIT", "Edm.Byte");
        SQL_TO_ODATA_EDM_TYPES.put("BLOB", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("NCLOB", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("CLOB", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("TEXT", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("BINTEXT", "Edm.Binary");
        SQL_TO_ODATA_EDM_TYPES.put("ALPHANUM", "Edm.String");
    }
    
    /**
     * Gets the data source.
     *
     * @return the data source
     */
    protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
		}
		return dataSource;
	}

    /**
     * Gets the table metadata.
     *
     * @param tableName the table name
     * @return the table metadata
     * @throws SQLException the SQL exception
     */
    public PersistenceTableModel getTableMetadata(String tableName) throws SQLException {
        return getTableMetadata(tableName, null);
    }

    /**
     * Gets the table metadata.
     *
     * @param tableName the table name
     * @param schemaName the schema name
     * @return the table metadata
     * @throws SQLException the SQL exception
     */
    public PersistenceTableModel getTableMetadata(String tableName, String schemaName) throws SQLException {
        PersistenceTableModel tableMetadata = new PersistenceTableModel(tableName, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        try (Connection connection = getDataSource().getConnection()) {
            DatabaseMetaData databaseMetadata = connection.getMetaData();
            String artifactType = getArtifactType(databaseMetadata, connection, tableName, schemaName);
            if (null == artifactType) {
                return null;
            }

            List<PersistenceTableColumnModel> columns = getColumns(databaseMetadata, connection, tableName, schemaName);
            List<PersistenceTableRelationModel> foreignKeys = getForeignKeys(databaseMetadata, connection, tableName, schemaName);
            tableMetadata.setTableType(artifactType);
            tableMetadata.setSchemaName(schemaName);
            tableMetadata.setColumns(columns);
            tableMetadata.setRelations(foreignKeys);
        }

        return tableMetadata;
    }

    /**
     * Gets the foreign keys.
     *
     * @param databaseMetadata the database metadata
     * @param connection the connection
     * @param artifactName the artifact name
     * @param schemaName the schema name
     * @return the foreign keys
     * @throws SQLException the SQL exception
     */
    private List<PersistenceTableRelationModel> getForeignKeys(DatabaseMetaData databaseMetadata, Connection connection, String artifactName, String schemaName) throws SQLException {
        ResultSet foreignKeys = databaseMetadata.getImportedKeys(connection.getCatalog(), schemaName, normalizeTableName(artifactName));
        if (!foreignKeys.isBeforeFirst() && !IS_CASE_SENSETIVE) {
            // Fallback for PostgreSQL
            foreignKeys = databaseMetadata.getImportedKeys(connection.getCatalog(), schemaName, normalizeTableName(artifactName.toLowerCase()));
        }

        List<PersistenceTableRelationModel> foreignKeysModel = new ArrayList<>();

        while (foreignKeys.next()) {
            PersistenceTableRelationModel relationMetadata = new PersistenceTableRelationModel(foreignKeys.getString(JDBC_FK_TABLE_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_PK_TABLE_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_FK_COLUMN_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_PK_COLUMN_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_FK_NAME_PROPERTY),
                    foreignKeys.getString(JDBC_PK_NAME_PROPERTY)
            );

            foreignKeysModel.add(relationMetadata);
        }

        return foreignKeysModel;
    }

    /**
     * Convert sql type to odata edm type.
     *
     * @param sqlType the sql type
     * @return the string
     */
    public String convertSqlTypeToOdataEdmType(String sqlType) {
        String edmColumnType = SQL_TO_ODATA_EDM_TYPES.get(sqlType.toUpperCase());
        if(null != edmColumnType) {
            return edmColumnType;
        }

        throw new IllegalArgumentException("SQL Type [" + sqlType + "] is not supported.");
    }

    /**
     * Gets the primary keys.
     *
     * @param databaseMetadata the database metadata
     * @param connection the connection
     * @param artifactName the artifact name
     * @param schemaName the schema name
     * @return the primary keys
     * @throws SQLException the SQL exception
     */
    private List<String> getPrimaryKeys(DatabaseMetaData databaseMetadata, Connection connection, String artifactName, String schemaName) throws SQLException {
        ResultSet primaryKeys = databaseMetadata.getPrimaryKeys(connection.getCatalog(), schemaName, normalizeTableName(artifactName));
        if (!primaryKeys.isBeforeFirst() && !IS_CASE_SENSETIVE) {
            // Fallback for PostgreSQL
            primaryKeys = databaseMetadata.getPrimaryKeys(connection.getCatalog(), schemaName, normalizeTableName(artifactName.toLowerCase()));
        }

        List<String> primaryKeyColumns = new ArrayList<>();
        while (primaryKeys.next()) {
            primaryKeyColumns.add(primaryKeys.getString(JDBC_COLUMN_NAME_PROPERTY));
        }

        return primaryKeyColumns;
    }

    /**
     * Gets the columns.
     *
     * @param databaseMetadata the database metadata
     * @param connection the connection
     * @param artifactName the artifact name
     * @param schemaPattern the schema pattern
     * @return the columns
     * @throws SQLException the SQL exception
     */
    private List<PersistenceTableColumnModel> getColumns(DatabaseMetaData databaseMetadata, Connection connection, String artifactName, String schemaPattern) throws SQLException {
        ResultSet columns = databaseMetadata.getColumns(connection.getCatalog(), schemaPattern, normalizeTableName(artifactName), null);
        if (!columns.isBeforeFirst() && !IS_CASE_SENSETIVE) {
            // Fallback for PostgreSQL
            columns = databaseMetadata.getColumns(connection.getCatalog(), schemaPattern, normalizeTableName(artifactName.toLowerCase()), null);
        }

        List<String> primaryKeys = getPrimaryKeys(databaseMetadata, connection, artifactName, schemaPattern);

        List<PersistenceTableColumnModel> tableColumnModels = new ArrayList<>();
        while (columns.next()) {
            String columnName = columns.getString(JDBC_COLUMN_NAME_PROPERTY);
            String columnType = convertSqlTypeToOdataEdmType(columns.getString(JDBC_COLUMN_TYPE_PROPERTY));
            boolean isNullable = columns.getBoolean(JDBC_COLUMN_NULLABLE_PROPERTY);
            boolean isPrimaryKey = primaryKeys.contains(columnName);
            int length = columns.getInt(JDBC_COLUMN_SIZE_PROPERTY);
            int scale = columns.getInt(JDBC_COLUMN_DECIMAL_DIGITS_PROPERTY);

            tableColumnModels.add(new PersistenceTableColumnModel(columnName, columnType, isNullable, isPrimaryKey, length, scale));
        }

        return tableColumnModels;
    }

    /**
     * Gets the property name from db column name.
     *
     * @param DbColumnName the db column name
     * @param oDataProperties the o data properties
     * @param prettyPrint the pretty print
     * @return the property name from db column name
     */
    public static String getPropertyNameFromDbColumnName(String DbColumnName, List<ODataProperty> oDataProperties, boolean prettyPrint) {
        for (ODataProperty next : oDataProperties) {
            if (DbColumnName.equals(next.getColumn())) {
                return next.getName();
            }
        }
        return prettyPrint ? addCorrectFormatting(DbColumnName) : DbColumnName;
    }

    /**
     * Checks if is prop column valid DB column.
     *
     * @param propColumn the prop column
     * @param dbColumns the db columns
     * @return true, if is prop column valid DB column
     */
    public static boolean isPropColumnValidDBColumn(String propColumn, List<PersistenceTableColumnModel> dbColumns) {
        for (PersistenceTableColumnModel next : dbColumns) {
            if (next.getName().equals(propColumn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is nullable.
     *
     * @param column the column
     * @param properties the properties
     * @return true, if is nullable
     */
    public static boolean isNullable(PersistenceTableColumnModel column, List<ODataProperty> properties) {
        String columnName = column.getName();
        for (ODataProperty next : properties) {
            if (columnName.equals(next.getColumn())) {
                return next.isNullable();
            }
        }
        return column.isNullable();
    }

    /**
     * Gets the type.
     *
     * @param column the column
     * @param properties the properties
     * @return the type
     */
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

    /**
     * Adds the correct formatting.
     *
     * @param columnName the column name
     * @return the string
     */
    public static String addCorrectFormatting(String columnName) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, columnName);
    }

    /**
     * Normalize table name.
     *
     * @param table the table
     * @return the string
     */
    public static String normalizeTableName(String table) {
        if (table != null && table.startsWith("\"") && table.endsWith("\"")) {
            table = table.substring(1, table.length() - 1);
        }
        return table;
    }

    /**
     * Gets the artifact type.
     *
     * @param databaseMetadata the database metadata
     * @param connection the connection
     * @param artifactName the artifact name
     * @param schemaPattern the schema pattern
     * @return the artifact type
     * @throws SQLException the SQL exception
     */
    public String getArtifactType(DatabaseMetaData databaseMetadata, Connection connection, String artifactName, String schemaPattern) throws SQLException {
        ResultSet tables = databaseMetadata.getTables(connection.getCatalog(), schemaPattern, normalizeTableName(artifactName), null);
        if (!tables.isBeforeFirst() && !IS_CASE_SENSETIVE) {
            // Fallback for PostgreSQL
            tables = databaseMetadata.getTables(connection.getCatalog(), schemaPattern, normalizeTableName(artifactName.toLowerCase()), null);
        }

        return tables.next() ? tables.getString("TABLE_TYPE") : null;
    }

    /**
     * Find schema of a given artifact name.
     * The searchable artifacts are TABLE, VIEW, CALC_VIEW
     *
     * @param artifactName name of the artifact
     * @return of a given artifact name
     * @throws SQLException SQLException
     */
    public String getOdataArtifactTypeSchema(String artifactName) throws SQLException {
        return getArtifactSchema(artifactName, new String[]{ISqlKeywords.METADATA_TABLE, ISqlKeywords.METADATA_VIEW, ISqlKeywords.METADATA_CALC_VIEW});
    }

    /**
     * Gets the artifact schema.
     *
     * @param artifactName the artifact name
     * @param types the types
     * @return the artifact schema
     * @throws SQLException the SQL exception
     */
    public String getArtifactSchema(String artifactName, String[] types) throws SQLException {
        try (Connection connection = getDataSource().getConnection()) {
            DatabaseMetaData databaseMetadata = connection.getMetaData();
            ResultSet rs = databaseMetadata.getTables(connection.getCatalog(), null, artifactName, types);
            if (rs.next()) {
                return rs.getString("TABLE_SCHEM");
            }
            return null;
        }
    }
}