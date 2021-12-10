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

    private DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);

    public static final Map<String, String> sqlToOdataEdmColumnTypes = new HashMap<>();
    
    private static final DatabaseMetadataUtil DATABASE_METADATA_UTIL = new DatabaseMetadataUtil();

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
        } catch (SQLException e) {
            throw e;
        }

        convertSqlTypesToOdataEdmTypes(tableMetadata.getColumns());
        return tableMetadata;
    }

    private void convertSqlTypesToOdataEdmTypes(List<PersistenceTableColumnModel> columnsMetadata) {
        columnsMetadata.forEach(column -> column.setType(sqlToOdataEdmColumnTypes.get(column.getType().toUpperCase())));
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
     * @param artifactName
     *              name of the artifact
     * @return  of a given artifact name
     * @throws SQLException SQLException
     *
     */
    public String getOdataArtifactTypeSchema( String artifactName) throws SQLException {
        return getArtifactSchema(artifactName, new String[]{ISqlKeywords.METADATA_TABLE, ISqlKeywords.METADATA_VIEW, ISqlKeywords.METADATA_CALC_VIEW});
    }

    public String getArtifactSchema( String artifactName, String[] types) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetadata = connection.getMetaData();
            ResultSet rs = databaseMetadata.getTables(connection.getCatalog(), null, artifactName, types);
            if (rs.next()) {
                return rs.getString("TABLE_SCHEM");
            }
            return null;
        } catch (SQLException e) {
            throw e;
        }
    }

}
