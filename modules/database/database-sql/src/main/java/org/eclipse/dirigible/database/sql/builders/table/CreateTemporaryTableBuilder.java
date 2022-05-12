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

    protected String likeTable;
    protected String asSelectQuery;
    protected boolean selectWithNoData;

    private static final String TEMPORARY_TABLES_NOT_SUPPORTED_FOR_THIS_DATABASE_TYPE = "Temporary tables not supported for this Database type!";

    /**
     * Instantiates a new abstract sql builder.
     *
     * @param dialect the dialect
     */
    public CreateTemporaryTableBuilder(ISqlDialect dialect, String table) {
        super(dialect, table);
        this.selectWithNoData = false;
    }

    @Override
    public String generate() {
        throw new IllegalStateException(TEMPORARY_TABLES_NOT_SUPPORTED_FOR_THIS_DATABASE_TYPE);
    }

    public CreateTemporaryTableBuilder<TABLE_BUILDER> setLikeTable(String likeTable) {
        this.likeTable = likeTable;
        return this;
    }

    public CreateTemporaryTableBuilder<TABLE_BUILDER> setAsSelectQuery(String asSelectQuery) {
        this.asSelectQuery = asSelectQuery;
        return this;
    }

    public CreateTemporaryTableBuilder<TABLE_BUILDER> setSelectWithNoData(boolean selectWithNoData) {
        this.selectWithNoData = selectWithNoData;
        return this;
    }
}
