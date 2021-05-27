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

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.uri.PathSegment;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.core.ODataPathSegmentImpl;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.apache.olingo.odata2.core.uri.UriParserImpl;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.edm.*;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.EMPTY_MAP;
import static org.junit.Assert.assertEquals;

public class SQLQueryExpandTest {

    AnnotationEdmProvider provider;
    UriParser uriParser;
    SQLQueryBuilder builder;

    @Before
    public void setUp() throws Exception {
        Class<?>[] classes = { //
                Entity1.class, //
                Entity2.class, //
                Entity3.class, //
                Entity4.class, //
                Entity5.class //
        };
        provider = new AnnotationEdmProvider(Arrays.asList(classes));
        EdmImplProv edm = new EdmImplProv(provider);
        uriParser = new UriParserImpl(edm);
        EdmTableBindingProvider tableMappingProvider = new DefaultEdmTableMappingProvider(this.getClass().getClassLoader(),
                OData2TestUtils.resources(classes));
        builder = new SQLQueryBuilder(tableMappingProvider);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExpandOneToManyAssociation() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("$expand", "Entity2");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities1", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Collections.singletonList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0,"
                + " T0.LOGSTART AS LOGSTART_T0, T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0,"
                + " T0.RECEIVER AS RECEIVER_T0, T0.STATUS AS STATUS_T0,"
                + " T0.MESSAGEGUID AS MESSAGEGUID_T0, T1.ID AS ID_T1, T1.NAME AS NAME_T1, T1.VALUE AS VALUE_T1" //
                + " FROM MPLHEADER AS T0" // the primary entity
                + " LEFT JOIN ITOP_MPLUSERDEFINEDATTRIBUTE AS T1 ON T1.HEADER_ID = T0.ID" + // added by the expand
                " FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExpandOneToManyAssociationWithComposedKey() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("$expand", "Entity5");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities4", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Collections.singletonList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.ID4_1 AS ID4_1_T0, T0.ID4_2 AS ID4_2_T0, T1.ID5 AS ID5_T1, T1.FK_ID4_1 AS FK_ID4_1_T1, T1.FK_ID4_2 AS FK_ID4_2_T1"
                + " FROM ENTITY4_TABLE AS T0"
                + " LEFT JOIN ENTITY5_TABLE AS T1 ON T1.FK_ID4_1 = T0.ID4_1 AND T1.FK_ID4_2 = T0.ID4_2"
                + " FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExpandZeroOrOneAssociationWithComposedKey() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("$expand", "Entity4");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities5", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Collections.singletonList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();

        String expected = "SELECT T0.ID5 AS ID5_T0, T0.FK_ID4_1 AS FK_ID4_1_T0, T0.FK_ID4_2 AS FK_ID4_2_T0, T1.ID4_1 AS ID4_1_T1, T1.ID4_2 AS ID4_2_T1"
                + " FROM ENTITY5_TABLE AS T0"
                + " LEFT JOIN ENTITY4_TABLE AS T1 ON T1.ID4_1 = T0.FK_ID4_1 AND T1.ID4_2 = T0.FK_ID4_2"
                + " FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExpandZeroOrOneAssociation() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("$expand", "Entity1");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities2", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Collections.singletonList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();

        String expected = "SELECT T0.ID AS ID_T0, T0.NAME AS NAME_T0, T0.VALUE AS VALUE_T0, "
                + "T1.MESSAGEGUID AS MESSAGEGUID_T1, T1.LOGSTART AS LOGSTART_T1, T1.LOGEND AS LOGEND_T1, "
                + "T1.SENDER AS SENDER_T1, T1.RECEIVER AS RECEIVER_T1, " + "T1.STATUS AS STATUS_T1, T1.MESSAGEGUID AS MESSAGEGUID_T1 "
                + "FROM ITOP_MPLUSERDEFINEDATTRIBUTE AS T0 "
                + "LEFT JOIN MPLHEADER AS T1 ON T1.ID = T0.HEADER_ID FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExpandWithTwoPathSegments() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("$expand", "Entity1");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities1('1')", EMPTY_MAP);
        PathSegment ps2 = new ODataPathSegmentImpl("Entity2", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1, ps2), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.ID AS ID_T0, T0.NAME AS NAME_T0, T0.VALUE AS VALUE_T0, "
                + "T1.MESSAGEGUID AS MESSAGEGUID_T1, T1.LOGSTART AS LOGSTART_T1, T1.LOGEND AS LOGEND_T1, "
                + "T1.SENDER AS SENDER_T1, T1.RECEIVER AS RECEIVER_T1, " + "T1.STATUS AS STATUS_T1, T1.MESSAGEGUID AS MESSAGEGUID_T1 "
                + "FROM ITOP_MPLUSERDEFINEDATTRIBUTE AS T0 " //
                + "LEFT JOIN MPLHEADER AS T1 ON T1.ID = T0.HEADER_ID "//
                + "WHERE T1.MESSAGEGUID = ? "//
                + "FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExpandWithTwoPathSegmentsWithComposedKey() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("$expand", "Entity4");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities4(Id4_1=11,Id4_2=22)", EMPTY_MAP);
        PathSegment ps2 = new ODataPathSegmentImpl("Entity5", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1, ps2), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.ID5 AS ID5_T0, T0.FK_ID4_1 AS FK_ID4_1_T0, T0.FK_ID4_2 AS FK_ID4_2_T0, T1.ID4_1 AS ID4_1_T1, T1.ID4_2 AS ID4_2_T1"
                + " FROM ENTITY5_TABLE AS T0"
                + " LEFT JOIN ENTITY4_TABLE AS T1 ON T1.ID4_1 = T0.FK_ID4_1 AND T1.ID4_2 = T0.FK_ID4_2"
                + " WHERE T1.ID4_1 = ? AND T1.ID4_2 = ? FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    //    @Test(expected = UriSyntaxException.class)
    //    @SuppressWarnings("unchecked")
    //    // Olingo 2.0.6: one-to-many navigation with filter and attribute access is not supported by OData.
    //    //                      Hence the UriSyntaxException has been introduced for that case. 
    //    public void testExpandWithFilter() throws Exception {
    //        Map<String, String> params = new HashMap<>();
    //        params.put("$expand", "Entity2");
    //        params.put("$filter", "MessageGuid eq 'AAA' and Entity2/Name eq 'Something'");
    //        PathSegment ps1 = new ODataPathSegmentImpl("Entities1", EMPTY_MAP);
    //        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
    //
    //        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
    //        SQLContext context = new SQLContext();
    //        String expected = "SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0,"
    //                + " T0.LOGSTART AS LOGSTART_T0, T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0,"
    //                + " T0.RECEIVER AS RECEIVER_T0, T0.STATUS AS STATUS_T0,"
    //                + " T0.MESSAGEGUID AS MESSAGEGUID_T0, T1.ID AS ID_T1, T1.NAME AS NAME_T1," + " T1.VALUE AS VALUE_T1"
    //                + " FROM MPLHEADER AS T0" // 
    //                + " LEFT JOIN ITOP_MPLUSERDEFINEDATTRIBUTE AS T1 ON T1.HEADER_ID = T0.ID" //
    //                + " WHERE T0.MESSAGEGUID = ? AND T1.NAME = ?" //
    //                + " FETCH FIRST 1000 ROWS ONLY";
    //        assertEquals(expected, q.buildSelect(context));
    //    }

    //    @Test
    //    @Ignore
    @SuppressWarnings("unchecked")
    public void testExpandWithFilterAndSelect() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("$expand", "Entity2");
        params.put("$filter", "MessageGuid eq 'AAA' and Entity2/Name eq 'Something'");
        params.put("$select", "MessageGuid,Entity2/Name");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities1", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Collections.singletonList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0,"
                + " T0.LOGSTART AS LOGSTART_T0, T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0,"
                + " T0.RECEIVER AS RECEIVER_T0, T0.STATUS AS STATUS_T0,"
                + " T0.MESSAGEGUID AS MESSAGEGUID_T0, T1.ID AS ID_T1, T1.NAME AS NAME_T1," + " T1.VALUE AS VALUE_T1"
                + " FROM MPLHEADER AS T0" // 
                + " LEFT JOIN ITOP_MPLUSERDEFINEDATTRIBUTE AS T1 ON T1.HEADER_ID = T0.ID" //
                + " WHERE T0.MESSAGEGUID = ? AND T1.NAME = ?" //
                + " FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

    //    @Test
    //    @Ignore // This pattern should be enabled, see sample application
    // http://services.odata.org/OData/OData.svc/Categories?$select=Products/*,ID&$expand=Products
    // BTW: Even if OData spec 2.0 does not inhibit this, sample app throws an error
    // if navigation properties in $select are not expanded as well, though.
    @SuppressWarnings("unchecked")
    public void testExpandWithFilterAndSelectWithStar() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("$expand", "Entity2");
        params.put("$filter", "MessageGuid eq 'AAA' and Entity2/Name eq 'Something'");
        params.put("$select", "MessageGuid,Entity2/*");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities1", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Collections.singletonList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0,"
                + " T0.LOGSTART AS LOGSTART_T0, T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0,"
                + " T0.RECEIVER AS RECEIVER_T0, T0.STATUS AS STATUS_T0,"
                + " T0.MESSAGEGUID AS MESSAGEGUID_T0, T1.ID AS ID_T1, T1.NAME AS NAME_T1," + " T1.VALUE AS VALUE_T1"
                + " FROM MPLHEADER AS T0" // 
                + " LEFT JOIN ITOP_MPLUSERDEFINEDATTRIBUTE AS T1 ON T1.HEADER_ID = T0.ID" //
                + " WHERE T0.MESSAGEGUID = ? AND T1.NAME = ?" //
                + " FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }

}
