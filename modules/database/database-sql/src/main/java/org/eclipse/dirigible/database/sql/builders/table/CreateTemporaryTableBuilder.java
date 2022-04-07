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
package org.eclipse.dirigible.database.sql.builders.table;

import org.eclipse.dirigible.database.sql.ISqlDialect;

public class CreateTemporaryTableBuilder<TABLE_BUILDER extends CreateTemporaryTableBuilder> extends AbstractTableBuilder<TABLE_BUILDER> {

    private String likeTable;

    private static final String TEMPORARY_TABLES_NOT_SUPPORTED_FOR_THIS_DATABASE_TYPE = "Temporary tables not supported for this Database type!";

    /**
     * Instantiates a new abstract sql builder.
     *
     * @param dialect the dialect
     */
    public CreateTemporaryTableBuilder(ISqlDialect dialect, String table, String likeTable) {
        super(dialect, table);

        this.likeTable = likeTable;
    }

    @Override
    public String generate() {
        throw new IllegalStateException(TEMPORARY_TABLES_NOT_SUPPORTED_FOR_THIS_DATABASE_TYPE);
    }

    public String getLikeTable() {
        return this.likeTable;
    }
}
