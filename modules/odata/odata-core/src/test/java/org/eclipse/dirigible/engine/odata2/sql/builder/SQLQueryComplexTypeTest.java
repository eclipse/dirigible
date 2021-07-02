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

import static java.util.Collections.EMPTY_MAP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.uri.PathSegment;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.UriSyntaxException;
import org.apache.olingo.odata2.core.ODataPathSegmentImpl;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.apache.olingo.odata2.core.uri.UriParserImpl;
import org.eclipse.dirigible.engine.odata2.sql.edm.CTEntity;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity1;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity2;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity3;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.junit.Before;
import org.junit.Test;

public class SQLQueryComplexTypeTest {

    AnnotationEdmProvider provider;
    UriParser uriParser;
    SQLQueryBuilder builder;

    @Before
    public void setUp() throws Exception {
        Class<?>[] classes = { //
                Entity1.class, //
                Entity2.class, //
                Entity3.class, //
                CTEntity.class //
        };
        provider = new AnnotationEdmProvider(Arrays.asList(classes));
        EdmImplProv edm = new EdmImplProv(provider);
        uriParser = new UriParserImpl(edm);

        DefaultEdmTableMappingProvider tableMappingProvider = new DefaultEdmTableMappingProvider(OData2TestUtils.resources(classes));
        builder = new SQLQueryBuilder(tableMappingProvider);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryWithComplexTypePropertySimple() throws Exception {
        PathSegment ps1 = new ODataPathSegmentImpl("Entities3", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), EMPTY_MAP);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.ID AS ID_T0, T0.DESCRIPTION AS DESCRIPTION_T0, " //
                + "T1.CT_ID AS CT_ID_T1, T1.CT_DETAIL AS CT_DETAIL_T1 " //
                + "FROM ENTITY3_TABLE AS T0 " //
                + "LEFT JOIN COMPLEX_TYPE_ENTITY_TABLE AS T1 ON T1.CT_ID = T0.COMPLEX_TYPE_JOIN_COLUMN " //
                + "FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryWithComplexTypePropertyWithFilterOnTheComplexProperty() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("$filter", "ComplexTypeProperty/Id eq 'AAA'");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities3", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.ID AS ID_T0, T0.DESCRIPTION AS DESCRIPTION_T0, " //
                + "T1.CT_ID AS CT_ID_T1, T1.CT_DETAIL AS CT_DETAIL_T1 " //
                + "FROM ENTITY3_TABLE AS T0 " //
                + "LEFT JOIN COMPLEX_TYPE_ENTITY_TABLE AS T1 ON T1.CT_ID = T0.COMPLEX_TYPE_JOIN_COLUMN "//
                + "WHERE T1.CT_ID = ? "//
                + "FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryWithComplexTypePropertyWithFilter() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("$filter", "ComplexTypeProperty/Id eq 'AAA' and Id eq 1");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities3", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.ID AS ID_T0, T0.DESCRIPTION AS DESCRIPTION_T0, " //
                + "T1.CT_ID AS CT_ID_T1, T1.CT_DETAIL AS CT_DETAIL_T1 " //
                + "FROM ENTITY3_TABLE AS T0 " //
                + "LEFT JOIN COMPLEX_TYPE_ENTITY_TABLE AS T1 ON T1.CT_ID = T0.COMPLEX_TYPE_JOIN_COLUMN " //
                + "WHERE T1.CT_ID = ? AND T0.ID = ? " + //
                "FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryWithComplexTypePropertyWithFilterAndBrackets() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("$filter", "(ComplexTypeProperty/Id eq 'AAA' or Id eq 1) and Id eq 2");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities3", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.ID AS ID_T0, T0.DESCRIPTION AS DESCRIPTION_T0, " //
                + "T1.CT_ID AS CT_ID_T1, T1.CT_DETAIL AS CT_DETAIL_T1 " //
                + "FROM ENTITY3_TABLE AS T0 " //
                + "LEFT JOIN COMPLEX_TYPE_ENTITY_TABLE AS T1 ON T1.CT_ID = T0.COMPLEX_TYPE_JOIN_COLUMN " //
                + "WHERE (T1.CT_ID = ? OR T0.ID = ?) AND T0.ID = ? " + //
                "FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryWithComplexTypePropertySelectWholeComplexType() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("$select", "ComplexTypeProperty, Id");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities3", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T1.CT_ID AS CT_ID_T1, T1.CT_DETAIL AS CT_DETAIL_T1, T0.ID AS ID_T0 " //
                + "FROM ENTITY3_TABLE AS T0 " //
                + "LEFT JOIN COMPLEX_TYPE_ENTITY_TABLE AS T1 ON T1.CT_ID = T0.COMPLEX_TYPE_JOIN_COLUMN " //
                + "FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    /*
     * OData Specification 2.0/3.0: Negative test: It is not supported to use
     * single properties of a complex type within a $select expression
     */
    @Test(expected = UriSyntaxException.class)
    @SuppressWarnings("unchecked")
    public void testQueryWithComplexTypePropertySelectComplexTypeFields() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("$select", "ComplexTypeProperty/Id, Id");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities3", EMPTY_MAP);
        uriParser.parse(Arrays.asList(ps1), params);

        fail("This pattern must be prohibited by URI Parser of Olingo");
    }
}
