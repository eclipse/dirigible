/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.builder.expression;

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataMessageException;
import org.apache.olingo.odata2.api.uri.expression.ExpressionParserException;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionWhere.Param;
import org.eclipse.dirigible.engine.odata2.sql.edm.*;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public final class SQLExpressionUpdateTest {

    AnnotationEdmProvider provider;
    EdmImplProv edm;
    EdmTableBindingProvider tableMappingProvider;

    @Before
    public void setUp() throws Exception {
        Class<?>[] classes = { //
                Entity1.class, //
                Entity2.class, //
                Entity3.class, //
                Entity4.class, //
                Entity5.class
        };
        provider = new AnnotationEdmProvider(Arrays.asList(classes));
        edm = new EdmImplProv(provider);
        tableMappingProvider = new DefaultEdmTableMappingProvider(OData2TestUtils.resources(classes));
    }



    @Test
    public void testUpdateExpression() throws Exception {
        SQLQuery noop = new SQLQuery(tableMappingProvider);
        String path = "Entity4(id4_1='1',id4_1='2')";
        SQLExpressionUpdate update = createUpdateExpression(noop, path);
        assertNotNull(update);
        update.build();
        String result = update.evaluate(null, SQLExpression.ExpressionType.TABLE);
        Assert.assertEquals("ENTITY4_TABLE SET ID4_3=? WHERE ID4_1=? AND ID4_2=?", result);
        List<Object> columnData = update.getQueryData();
        //the values must be ordered as the elements in the query
        assertEquals("UpdateIt", columnData.get(0));
        assertEquals("1", columnData.get(1));
        assertEquals("2", columnData.get(2));
    }


    @Test
    public void testUpdateExpressionDifferentKeyOrder() throws Exception {
        SQLQuery noop = new SQLQuery(tableMappingProvider);
        String path = "Entity4(id4_2='2',id4_1='1')";
        SQLExpressionUpdate update = createUpdateExpression(noop, path);
        assertNotNull(update);
        update.build();
        String result = update.evaluate(null, SQLExpression.ExpressionType.TABLE);
        Assert.assertEquals("ENTITY4_TABLE SET ID4_3=? WHERE ID4_1=? AND ID4_2=?", result);
        List<Object> columnData = update.getQueryData();
        //the values must be ordered as the elements in the query
        assertEquals("UpdateIt", columnData.get(0));
        assertEquals("1", columnData.get(1));
        assertEquals("2", columnData.get(2));
    }

    private SQLExpressionUpdate createUpdateExpression(SQLQuery query, final String path) throws ODataException {
        EdmEntityType type = edm.getEntityType(Entity4.class.getPackage().getName(), Entity4.class.getSimpleName());


        final Map<String, Object> keys = new HashMap<>();
        keys.put("Id4_1", "1");
        keys.put("Id4_2", "2");

        final Map<String, Object> data = new HashMap<>(keys);
        data.put("Id4_3", "UpdateIt");
        ODataEntry entry = new ODataEntryImpl(data, null, null, null);

        return new SQLExpressionUpdate(query, type, entry, keys){
            protected boolean isUpdateTarget(final EdmStructuralType target) {
                return true;
            }
        };
    }

    private void assertParamListEquals(final String[] paramsExp, final List<Param> paramsAct) {

        assertEquals(paramsExp.length, paramsAct.size());

        final Iterator<Param> itParam = paramsAct.iterator();
        for (String p : paramsExp) {
            assertTrue(itParam.hasNext());
            assertEquals(p, itParam.next().getValue());
        }
    }
}
