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
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.table.AlterTableBuilder;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableUniqueIndexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HanaAlterTableBuilder extends AlterTableBuilder {

    private static final Logger logger = LoggerFactory.getLogger(HanaAlterTableBuilder.class);
    /**
     * Instantiates a new creates the table builder.
     *
     * @param dialect the dialect
     * @param table
     */
    public HanaAlterTableBuilder(ISqlDialect dialect, String table) {
        super(dialect, table);
    }

    @Override
    protected void generateColumns(StringBuilder sql) {
        if (!this.getColumns().isEmpty()) {
            sql.append(OPEN).append(traverseColumns()).append(CLOSE);
        }
    }

    @Override
    protected void generateColumnsForAlter(StringBuilder sql) {
        if (!this.getColumns().isEmpty()) {
            sql.append(OPEN).append(traverseColumnsForAlter()).append(CLOSE);
        }
    }

    @Override
    protected String traverseColumnNamesForDrop() {
        StringBuilder snippet = new StringBuilder();
        for (String[] column : this.getColumns()) {
            String columnName = (isCaseSensitive()) ? encapsulate(column[0]) : column[0];
            snippet.append(KEYWORD_DROP).append(SPACE).append(OPEN);
            snippet.append(columnName).append(CLOSE).append(SPACE);
            snippet.append(COMMA).append(SPACE);
        }
        return snippet.substring(0, snippet.length() - 2);
    }

    @Override
    public String generate() {

        StringBuilder sql = new StringBuilder();

        // ALTER
        generateAlter(sql);

        // TABLE
        generateTable(sql);

        sql.append(SPACE);

        if (KEYWORD_ADD.equals(this.getAction())) {
            sql.append(KEYWORD_ADD);
            if (!getColumns().isEmpty()) {
                // COLUMNS
                generateColumns(sql);
            }
        } else if (KEYWORD_DROP.equals(this.getAction())) {
            if (!getColumns().isEmpty()) {
                // COLUMNS
                generateColumnNamesForDrop(sql);
            }
        } else {
            if (!getColumns().isEmpty()) {
                // COLUMNS
                sql.append(KEYWORD_ALTER);
                generateColumnsForAlter(sql);
            }
        }
        //logic for indices
        sql.append(SEMICOLON).append(SPACE);
        if(!this.getUniqueIndices().isEmpty()){
            generateUniqueIndices(sql);
        }

        String generated = sql.toString().trim();

        logger.trace("generated: " + generated);

        return generated;
    }

    @Override
    protected void generateUniqueIndices(StringBuilder sql) {
        for (CreateTableUniqueIndexBuilder uniqueIndex : this.getUniqueIndices()) {
            generateAlter(sql);
            generateTable(sql);
            sql.append(SPACE);

            generateUniqueIndex(sql, uniqueIndex);
            sql.append(SEMICOLON);
        }
    }
    @Override
    protected void generateUniqueIndex(StringBuilder sql, CreateTableUniqueIndexBuilder uniqueIndex) {
        if (uniqueIndex != null) {
            if (uniqueIndex.getName() != null) {
                String uniqueIndexName = (isCaseSensitive()) ? encapsulate(uniqueIndex.getName()) : uniqueIndex.getName();
                sql.append(KEYWORD_ADD)
                    .append(SPACE)
                    .append(KEYWORD_CONSTRAINT)
                    .append(SPACE)
                    .append(uniqueIndexName)
                    .append(SPACE);
            }
            sql.append(KEYWORD_UNIQUE).append(SPACE).append(OPEN).append(traverseNames(uniqueIndex.getColumns())).append(CLOSE);
        }
    }
}
