/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.sql.builder;

import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;

public class SQLQueryTestUtils {

    private SQLQueryTestUtils() {
        // Static class
    }

    /**
     * @param sqlQuery
     * @param structuralType
     */
    public static void grantTableAliasForStructuralTypeInQuery(final SQLQuery sqlQuery, final EdmStructuralType structuralType) {
        sqlQuery.grantTableAliasForStructuralTypeInQuery(structuralType);
    }

}
