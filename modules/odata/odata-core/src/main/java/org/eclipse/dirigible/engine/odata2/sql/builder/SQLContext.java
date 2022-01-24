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

public class SQLContext {

    public enum DatabaseProduct {
        DERBY(false), SYBASE_ASE(false), POSTGRE_SQL(true), H2(false), HANA(true);

        private boolean caseSensitive;
        DatabaseProduct (boolean caseSensitive){
            this.caseSensitive = caseSensitive;
        }
        public boolean isCaseSensitive(){
            return caseSensitive;
        }
    }

    private final DatabaseProduct databaseProduct;
    private ODataContext odataContext;
    private DatabaseMetaData metadata;

    public SQLContext() {
        databaseProduct = DatabaseProduct.DERBY;
    }

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
     * @param databaseProduct the database product name
     */
    public SQLContext(final DatabaseProduct databaseProduct) {
        this.databaseProduct = databaseProduct;
    }

    /**
     * @return the database product
     */
    public DatabaseProduct getDatabaseProduct() {
        return databaseProduct;
    }

    public ODataContext getOdataContext() {
        return odataContext;
    }

    public DatabaseMetaData getDatabaseMetadata() {
        return metadata;
    }

    private String getDatabaseName(final DatabaseMetaData metadata) {
        try {
            return metadata.getDatabaseProductName();
        } catch (SQLException e) {
            throw new OData2Exception("Unable to get the database product name", SERVICE_UNAVAILABLE, e);
        }
    }

}
