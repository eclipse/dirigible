/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.snowflake;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;

/**
 * The Snowflake Create Branching Builder.
 */
public class SnowflakeCreateBranchingBuilder extends CreateBranchingBuilder {

    /**
     * Instantiates a new Snowflake create branching builder.
     *
     * @param dialect the dialect
     */
    protected SnowflakeCreateBranchingBuilder(ISqlDialect dialect) {
        super(dialect);
    }

    /**
     * Table.
     *
     * @param table the table
     * @return the Snowflake create table builder
     */
    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder#table(java.lang.String)
     */
    @Override
    public SnowflakeCreateTableBuilder table(String table) {
        return new SnowflakeCreateTableBuilder(this.getDialect(), table);
    }

}
