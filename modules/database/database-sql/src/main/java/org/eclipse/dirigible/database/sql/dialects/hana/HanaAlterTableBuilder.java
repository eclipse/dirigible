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
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.table.AlterTableBuilder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class HanaAlterTableBuilder extends AlterTableBuilder {
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
        return snippet.toString().substring(0, snippet.length() - 2);
    }

}
