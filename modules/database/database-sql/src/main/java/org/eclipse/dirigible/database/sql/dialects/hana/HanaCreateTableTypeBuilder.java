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
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.tableType.CreateTableTypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HanaCreateTableTypeBuilder extends CreateTableTypeBuilder {
    private static final Logger logger = LoggerFactory.getLogger(HanaCreateTableTypeBuilder.class);

    private String tableType;
    private List<String[]> columns = new ArrayList<>();

    /**
     * Instantiates a new hana create table builder.
     *
     * @param dialect
     *            the dialect
     * @param tableType
     *            the tableType
     */
    public HanaCreateTableTypeBuilder(ISqlDialect dialect, String tableType) {
        super(dialect, tableType);
        this.tableType=tableType;
    }

    @Override
    public String generate() {

        StringBuilder sql = new StringBuilder();

        // CREATE
        generateCreate(sql);

        // TABLE TYPE
        generateTableType(sql);

        // COLUMNS
        generateStructureColumns(sql);

        String generated = sql.toString();

        logger.trace("generated: " + generated);

        return generated;
    }

    /**
     * Generate structure columns.
     *
     * @param sql
     *            the sql
     */
    private void generateStructureColumns(StringBuilder sql) {
        if (!this.getColumns().isEmpty()) {
            sql.append(SPACE).append(OPEN).append(iterateColumns()).append(CLOSE);
        }
    }

    /**
     * Iterate columns.
     *
     * @return the string
     */
    private String iterateColumns() {
        StringBuilder snippet = new StringBuilder();
        snippet.append(SPACE);

        for (String[] column : this.columns) {
            boolean isColumnName = true;
            for (String arg : column) {
                if (isColumnName) {
                    String columnName = (isCaseSensitive()) ? encapsulate(arg) : arg;
                    snippet.append(columnName).append(SPACE);
                    isColumnName = false;
                    continue;
                }
                snippet.append(arg);
            }
            snippet.append(COMMA).append(SPACE);
        }
        return snippet.substring(0, snippet.length() - 2);
    }

    /**
     * Generate table type.
     *
     * @param sql
     *            the sql
     */
    private void generateTableType(StringBuilder sql) {
        String tableTypeName = (isCaseSensitive()) ? encapsulate(this.getTableType()) : this.getTableType();
        sql.append(SPACE).append(KEYWORD_TABLE_TYPE).append(SPACE).append(tableTypeName).append(SPACE).append(KEYWORD_AS).append(SPACE).append(KEYWORD_TABLE);
    }

    /**
     * Column.
     *
     * @param name         the name
     * @param type         the type
     * @return the creates the table type builder
     */
    public CreateTableTypeBuilder column(String name, DataType type) {
        String[] definition = new String[] { name, getDialect().getDataTypeName(type) };
        this.columns.add(definition);
        return this;
    }

    /**
     * Column.
     *
     * @param name         the name
     * @param type         the type
     * @param length       the length
     * @return the creates the table type builder
     */
    public CreateTableTypeBuilder column(String name, DataType type, int length) {
        if(type == DataType.VARCHAR || type==DataType.NVARCHAR || type==DataType.CHAR) {
            String[] definition = new String[] { name, String.valueOf(type), OPEN + length + CLOSE };
            this.columns.add(definition);
        }
        return this;
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
     * Gets the structure columns.
     *
     * @return the structure columns
     */
    public List<String[]> getColumns() {
        return columns;
    }
}
