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
