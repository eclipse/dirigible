/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.utils.DatabaseMetadataUtil;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.engine.odata2.definition.ODataProperty;


public class DBMetadataUtil {

    public static final String DIRIGIBLE_GENERATE_PRETTY_NAMES = "DIRIGIBLE_GENERATE_PRETTY_NAMES";

    private static final boolean IS_CASE_SENSETIVE = Boolean.parseBoolean(Configuration.get(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE));

    private final DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
    
    private static final DatabaseMetadataUtil DATABASE_METADATA_UTIL = new DatabaseMetadataUtil();
    
    public static final String JDBC_COLUMN_PROPERTY = "COLUMN_NAME";
    public static final String JDBC_COLUMN_TYPE = "TYPE_NAME";
    public static final String JDBC_FK_TABLE_NAME_PROPERTY = "FKTABLE_NAME";
    public static final String JDBC_FK_NAME_PROPERTY = "FK_NAME";
    public static final String JDBC_PK_NAME_PROPERTY = "PK_NAME";
    public static final String JDBC_PK_TABLE_NAME_PROPERTY = "PKTABLE_NAME";
    public static final String JDBC_FK_COLUMN_NAME_PROPERTY = "FKCOLUMN_NAME";
    public static final String JDBC_PK_COLUMN_NAME_PROPERTY = "PKCOLUMN_NAME";
    
    public static final Map<String, String> SQL_TO_ODATA_EDM_TYPES = new HashMap<>();

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
        SQL_TO_ODATA_EDM_TYPES.put("VARCHAR", "Edm.String");
        SQL_TO_ODATA_EDM_TYPES.put("NVARCHAR", "Edm.String");
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

    public PersistenceTableModel getTableMetadata(String tableName) throws SQLException {
        return getTableMetadata(tableName, null);
    }

    public PersistenceTableModel getTableMetadata(String tableName, String schemaName) throws SQLException {
        PersistenceTableModel tableMetadata = new PersistenceTableModel(tableName, new ArrayList<>(), new ArrayList<>());
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetadata = connection.getMetaData();
            DatabaseMetadataUtil.addFields(databaseMetadata, connection, tableMetadata, schemaName);
            DatabaseMetadataUtil.addPrimaryKeys(databaseMetadata, connection, tableMetadata, schemaName);
            DatabaseMetadataUtil.addForeignKeys(databaseMetadata, connection, tableMetadata, schemaName);
            DatabaseMetadataUtil.addTableType(databaseMetadata, connection, tableMetadata, schemaName);
            tableMetadata.setSchemaName(schemaName);
        }

        convertSqlTypesToOdataEdmTypes(tableMetadata.getColumns());
        return tableMetadata;
    }

    private void convertSqlTypesToOdataEdmTypes(List<PersistenceTableColumnModel> columnsMetadata) {
        columnsMetadata.forEach(column -> column.setType(convertSqlTypeToOdataEdmType(column.getType())));
    }

    private String convertSqlTypeToOdataEdmType(String sqlType) {
        String edmColumnType = SQL_TO_ODATA_EDM_TYPES.get(sqlType.toUpperCase());
        if(null != edmColumnType) {
            return edmColumnType;
        }

        throw new IllegalArgumentException("SQL Type [" + sqlType + "] is not supported.");
    }

    public static String getPropertyNameFromDbColumnName(String DbColumnName, List<ODataProperty> oDataProperties, boolean prettyPrint) {
        for (ODataProperty next : oDataProperties) {
            if (DbColumnName.equals(next.getColumn())) {
                return next.getName();
            }
        }
        return prettyPrint ? DatabaseMetadataUtil.addCorrectFormatting(DbColumnName) : DbColumnName;
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

    public String getArtifactSchema(String artifactName, String[] types) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetadata = connection.getMetaData();
            ResultSet rs = databaseMetadata.getTables(connection.getCatalog(), null, artifactName, types);
            if (rs.next()) {
                return rs.getString("TABLE_SCHEM");
            }
            return null;
        }
    }

    public HashMap<String, String> getSynonymTargetObjectMetadata(String synonymName) throws SQLException {
        HashMap<String, String> targetObjectMetadata = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement prepareStatement = connection.prepareStatement("SELECT * FROM \"SYS\".\"SYNONYMS\" WHERE \"SYNONYM_NAME\" = ?");
            prepareStatement.setString(1, synonymName);

            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("OBJECT_NAME");
                    String schema = resultSet.getString("OBJECT_SCHEMA");
                    String type = resultSet.getString("OBJECT_TYPE");

                    targetObjectMetadata.put(ISqlKeywords.KEYWORD_TABLE, name);
                    targetObjectMetadata.put(ISqlKeywords.KEYWORD_SCHEMA, schema);
                    targetObjectMetadata.put(ISqlKeywords.KEYWORD_TABLE_TYPE, type);
                }
            }
        }

        return targetObjectMetadata;
    }
}
