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
package org.eclipse.dirigible.engine.odata2.sql.clause;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity1;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity2;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity3;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SQLOrderByClauseTest {

    EdmImplProv edm;
    EdmTableBindingProvider tableMappingProvider;

    @Before
    public void setUp() throws Exception {
        Class<?>[] classes = { //
                Entity1.class, //
                Entity2.class, //
                Entity3.class //
        };
        AnnotationEdmProvider provider = new AnnotationEdmProvider(Arrays.asList(classes));
        edm = new EdmImplProv(provider);
        tableMappingProvider = new DefaultEdmTableMappingProvider(OData2TestUtils.resources(classes));
    }

    @Test
    public void testSimpleOrderBy() throws Exception {
        SQLOrderByClause sqlOrderBy = createOrderByExpression("Status");
        assertEquals("T0.STATUS ASC", sqlOrderBy.evaluate(null));
    }

    @Test
    public void testOrderByWith2Cols() throws Exception {
        SQLOrderByClause sqlOrderBy = createOrderByExpression("Status, LogStart desc");
        assertEquals("T0.STATUS ASC, T0.LOGSTART DESC", sqlOrderBy.evaluate(null));
    }

    @Test
    public void testOrderByWith3Cols() throws Exception {
        SQLOrderByClause sqlOrderBy = createOrderByExpression("Status desc, LogEnd, LogStart desc");
        assertEquals("T0.STATUS DESC, T0.LOGEND ASC, T0.LOGSTART DESC", sqlOrderBy.evaluate(null));
    }

    @Test
    public void testOrderByWithNoOrderBy() throws Exception {
        OrderByExpression orderBy = UriParser.parse(edm, new ArrayList<>(), new HashMap<>()).getOrderBy();
        EdmEntityType type = edm.getEntityType(Entity1.class.getPackage().getName(), Entity1.class.getSimpleName());
        SQLSelectBuilder noop = new SQLSelectBuilder(tableMappingProvider);
        SQLOrderByClause sqlOrderBy = new SQLOrderByClause(noop, type, orderBy);

        assertEquals("T0.MESSAGEGUID ASC", sqlOrderBy.evaluate(null));
    }

    private SQLOrderByClause createOrderByExpression(final String expression) {
        try {
            OrderByExpression orderBy = UriParser.parseOrderBy(edm, edm.getEntityType(Entity1.class.getPackage().getName(), Entity1.class.getSimpleName()),
                    expression);
            EdmEntityType type = edm.getEntityType(Entity1.class.getPackage().getName(), Entity1.class.getSimpleName());
            SQLSelectBuilder noop = new SQLSelectBuilder(tableMappingProvider);
            return new SQLOrderByClause(noop, type, orderBy);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
