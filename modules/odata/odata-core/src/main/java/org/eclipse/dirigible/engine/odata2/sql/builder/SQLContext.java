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
package org.eclipse.dirigible.engine.odata2.sql.builder;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.SERVICE_UNAVAILABLE;

/**
 * The Class SQLContext.
 */
public class SQLContext {

    /**
     * The Enum DatabaseProduct.
     */
    public enum DatabaseProduct {
        
        /** The derby. */
        DERBY(false), 
 /** The sybase ase. */
 SYBASE_ASE(false), 
 /** The postgre sql. */
 POSTGRE_SQL(true), 
 /** The h2. */
 H2(false), 
 /** The hana. */
 HANA(true);

        /** The case sensitive. */
        private boolean caseSensitive;
        
        /**
         * Instantiates a new database product.
         *
         * @param caseSensitive the case sensitive
         */
        DatabaseProduct (boolean caseSensitive){
            this.caseSensitive = caseSensitive;
        }
        
        /**
         * Checks if is case sensitive.
         *
         * @return true, if is case sensitive
         */
        public boolean isCaseSensitive(){
            return caseSensitive;
        }
    }

    /** The database product. */
    private final DatabaseProduct databaseProduct;
    
    /** The odata context. */
    private ODataContext odataContext;
    
    /** The metadata. */
    private DatabaseMetaData metadata;

    /**
     * Instantiates a new SQL context.
     */
    public SQLContext() {
        databaseProduct = DatabaseProduct.DERBY;
    }

    /**
     * Instantiates a new SQL context.
     *
     * @param metadata the metadata
     * @param odataContext the odata context
     */
    public SQLContext(final DatabaseMetaData metadata, final ODataContext odataContext) {
        this.metadata = metadata;
        this.odataContext = odataContext;
        String dbProductName = getDatabaseName(metadata);
        if (dbProductName.toLowerCase().contains("derby")) {
            databaseProduct = DatabaseProduct.DERBY;
        } else if (dbProductName.toLowerCase().contains("adaptive server enterprise")) {
            databaseProduct = DatabaseProduct.SYBASE_ASE;
        } else if (dbProductName.toLowerCase().contains("postgre")) {
            databaseProduct = DatabaseProduct.POSTGRE_SQL;
        } else if (dbProductName.toLowerCase().contains("h2")) {
            databaseProduct = DatabaseProduct.H2;
        } else if (dbProductName.toLowerCase().contains("hdb")) {
            databaseProduct = DatabaseProduct.HANA;
        } else
            throw new OData2Exception("Unsupported database " + dbProductName, SERVICE_UNAVAILABLE);
    }

    /**
     * Instantiates a new SQL context.
     *
     * @param databaseProduct the database product name
     */
    public SQLContext(final DatabaseProduct databaseProduct) {
        this.databaseProduct = databaseProduct;
    }

    /**
     * Gets the database product.
     *
     * @return the database product
     */
    public DatabaseProduct getDatabaseProduct() {
        return databaseProduct;
    }

    /**
     * Gets the odata context.
     *
     * @return the odata context
     */
    public ODataContext getOdataContext() {
        return odataContext;
    }

    /**
     * Gets the database metadata.
     *
     * @return the database metadata
     */
    public DatabaseMetaData getDatabaseMetadata() {
        return metadata;
    }

    /**
     * Gets the database name.
     *
     * @param metadata the metadata
     * @return the database name
     */
    private String getDatabaseName(final DatabaseMetaData metadata) {
        try {
            return metadata.getDatabaseProductName();
        } catch (SQLException e) {
            throw new OData2Exception("Unable to get the database product name", SERVICE_UNAVAILABLE, e);
        }
    }

}
