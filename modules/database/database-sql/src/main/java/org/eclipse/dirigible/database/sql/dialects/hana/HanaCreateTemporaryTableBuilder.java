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
import org.eclipse.dirigible.database.sql.builders.table.CreateTemporaryTableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HanaCreateTemporaryTableBuilder extends CreateTemporaryTableBuilder<HanaCreateTemporaryTableBuilder> {

    private static final Logger logger = LoggerFactory.getLogger(CreateTemporaryTableBuilder.class);

    /**
     * Instantiates a new abstract sql builder.
     *
     * @param dialect   the dialect
     * @param table
     * @param likeTable
     */
    protected HanaCreateTemporaryTableBuilder(ISqlDialect dialect, String table, String likeTable) {
        super(dialect, table, likeTable);
    }

    @Override
    public String generate() {

        StringBuilder sql = new StringBuilder();

        // CREATE
        generateCreate(sql);

        sql.append(SPACE).append(METADATA_LOCAL_TEMPORARY);

        // TABLE
        generateTable(sql);

        sql.append(SPACE).append(KEYWORD_LIKE).append(SPACE).append(this.getLikeTable())
                .append(SPACE).append(KEYWORD_WITH).append(SPACE)
                .append(KEYWORD_NO).append(SPACE).append(KEYWORD_DATA);

        String generated = sql.toString();

        logger.trace("generated: " + generated);

        return generated;
    }
}
