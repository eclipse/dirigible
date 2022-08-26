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
package org.eclipse.dirigible.database.sql.dialects.derby;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.table.AlterTableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DerbyAlterTableBuilder.
 */
public class DerbyAlterTableBuilder extends AlterTableBuilder {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(DerbyAlterTableBuilder.class);

    /**
     * Instantiates a new creates the table builder.
     *
     * @param dialect the dialect
     * @param table   the table
     */
    public DerbyAlterTableBuilder(ISqlDialect dialect, String table) {
        super(dialect, table);
    }

    /**
     * Generate.
     *
     * @return the string
     */
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

        if (!getForeignKeys().isEmpty()) {
            // FOREIGN KEYS
            generateForeignKeyNames(sql);
        }
        if (!getUniqueIndices().isEmpty()) {
            generateUniqueIndices(sql);
        }

        String generated = sql.toString().trim();
        if (logger.isTraceEnabled()) {logger.trace("generated: " + generated);}

        return generated;
    }
}
