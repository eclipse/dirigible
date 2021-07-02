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
package org.eclipse.dirigible.engine.odata2.sql.builder.expression;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity1;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity2;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity3;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.junit.Before;
import org.junit.Test;

public class SQLExpressionOrderByTest {

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
        SQLExpressionOrderBy sqlOrderBy = createOrderByExpression("Status");
        assertEquals("T0.STATUS", sqlOrderBy.evaluate(null, ExpressionType.ORDERBY));
    }

    @Test
    public void testOrderByWith2Cols() throws Exception {
        SQLExpressionOrderBy sqlOrderBy = createOrderByExpression("Status, LogStart desc");
        assertEquals("T0.STATUS, T0.LOGSTART DESC", sqlOrderBy.evaluate(null, ExpressionType.ORDERBY));
    }

    @Test
    public void testOrderByWith3Cols() throws Exception {
        SQLExpressionOrderBy sqlOrderBy = createOrderByExpression("Status desc, LogEnd, LogStart desc");
        assertEquals("T0.STATUS DESC, T0.LOGEND, T0.LOGSTART DESC", sqlOrderBy.evaluate(null, ExpressionType.ORDERBY));
    }

    private SQLExpressionOrderBy createOrderByExpression(final String expression) {
        OrderByExpression orderBy;
        try {
            orderBy = UriParser.parseOrderBy(edm, edm.getEntityType(Entity1.class.getPackage().getName(), Entity1.class.getSimpleName()),
                    expression);
            EdmEntityType type = edm.getEntityType(Entity1.class.getPackage().getName(), Entity1.class.getSimpleName());
            SQLQuery noop = new SQLQuery(tableMappingProvider);
            return new SQLExpressionOrderBy(noop, type, orderBy);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
