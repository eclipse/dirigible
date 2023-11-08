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
package org.eclipse.dirigible.components.data.management.domain;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Schema Metadata transport object.
 */
public class SchemaMetadata {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(SchemaMetadata.class);

    /** The name. */
    private String name;

    /** The kind. */
    private String kind = "schema";

    /** The tables. */
    private List<TableMetadata> tables;

    /** The procedures. */
    private List<ProcedureMetadata> procedures;

    /** The functions. */
    private List<FunctionMetadata> functions;

    /**
     * Instantiates a new schema metadata.
     *
     * @param name the name
     * @param connection the connection
     * @param catalogName the catalog name
     * @param nameFilter the name filter
     * @throws SQLException the SQL exception
     */
    public SchemaMetadata(String name, Connection connection, String catalogName, Filter<String> nameFilter) throws SQLException {
        super();

        this.name = name;

        this.tables = DatabaseMetadataHelper.listTables(connection, catalogName, name, nameFilter);

        try {
            this.procedures = DatabaseMetadataHelper.listProcedures(connection, catalogName, name, nameFilter);
        } catch (Exception e) {
            this.procedures = new ArrayList<ProcedureMetadata>();
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
        }

        try {
            this.functions = DatabaseMetadataHelper.listFunctions(connection, catalogName, name, nameFilter);
        } catch (Exception e) {
            this.functions = new ArrayList<FunctionMetadata>();
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the tables.
     *
     * @return the tables
     */
    public List<TableMetadata> getTables() {
        return tables;
    }



    /**
     * Get the procedures metadata.
     *
     * @return the procedures
     */
    public List<ProcedureMetadata> getProcedures() {
        return procedures;
    }

    /**
     * Get the functions metadata.
     *
     * @return the functions
     */
    public List<FunctionMetadata> getFunctions() {
        return functions;
    }

    /**
     * Gets the kind.
     *
     * @return the kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the kind.
     *
     * @param kind the new kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

}
