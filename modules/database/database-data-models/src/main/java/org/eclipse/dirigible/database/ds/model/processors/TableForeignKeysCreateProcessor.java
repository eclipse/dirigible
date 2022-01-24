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
package org.eclipse.dirigible.database.ds.model.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintForeignKeyModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.ds.model.util.DBModelUtils;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.table.AlterTableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableForeignKeysCreateProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TableForeignKeysCreateProcessor.class);

    /**
     * Execute the corresponding statement.
     *
     * @param connection the connection
     * @param tableModel the table model
     * @throws SQLException the SQL exception
     */
    public static void execute(Connection connection, DataStructureTableModel tableModel) throws SQLException {
        boolean caseSensitive = Boolean.parseBoolean(Configuration.get(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false"));
        String tableName = tableModel.getName();
        if (caseSensitive) {
            tableName = "\"" + tableName + "\"";
        }

        if (tableModel.getConstraints() != null) {
            if (tableModel.getConstraints().getForeignKeys() != null && !tableModel.getConstraints().getForeignKeys().isEmpty()) {
                logger.info("Processing Alter Table Create Foreign Keys Table: " + tableName);
                AlterTableBuilder alterTableBuilder = SqlFactory.getNative(connection).alter().table(tableName);
                for (DataStructureTableConstraintForeignKeyModel foreignKey : tableModel.getConstraints().getForeignKeys()) {
                    
                    List<String> valsToHashFKName = new ArrayList<>(Arrays.asList(foreignKey.getColumns()));
                    valsToHashFKName.add(foreignKey.getReferencedTable());
                    String hashedFKName = "fk" + DBModelUtils.generateHashedName(valsToHashFKName);
                    String foreignKeyName = Objects.isNull(foreignKey.getName()) ? hashedFKName : foreignKey.getName();
                    if (caseSensitive) {
                        foreignKeyName = "\"" + foreignKeyName + "\"";
                    }
                    alterTableBuilder.add().foreignKey(foreignKeyName, foreignKey.getColumns(), foreignKey.getReferencedTable(), foreignKey.getReferencedColumns());
                }
                final String sql = alterTableBuilder.build();
                logger.info(sql);
                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.executeUpdate();
                } catch (SQLException e) {
                    logger.error(sql);
                    logger.error(e.getMessage(), e);
                    throw new SQLException(e.getMessage(), e);
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
        }
    }

}
