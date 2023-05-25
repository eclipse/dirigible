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
package org.eclipse.dirigible.database.sql.builders.schema;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractCreateSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Create Schema Builder.
 */
public class CreateSchemaBuilder extends AbstractCreateSqlBuilder {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CreateSchemaBuilder.class);

    /** The name. */
    private String name;

    /**
     * Instantiates a new creates the schema builder.
     *
     * @param dialect the dialect
     * @param name    the schema name
     */
    public CreateSchemaBuilder(ISqlDialect dialect, String name) {
        super(dialect);
        this.name = name;
    }

    /**
     * Generate.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
     */
    @Override
    public String generate() {

        StringBuilder sql = new StringBuilder();

        // CREATE
        generateCreate(sql);

        // SCHEMA
        generateSchema(sql);

        String generated = sql.toString();

        logger.trace("generated: " + generated);

        return generated;
    }

    /**
     * Generate schema.
     *
     * @param sql the sql
     */
    protected void generateSchema(StringBuilder sql) {
        String schemaName = (isCaseSensitive()) ? encapsulate(this.getName(), true) : this.getName();
        sql.append(SPACE).append(KEYWORD_SCHEMA).append(SPACE).append(schemaName);
    }

    /**
     * Gets the schema name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
