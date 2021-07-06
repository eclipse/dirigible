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
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.PathSegment;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.core.ODataPathSegmentImpl;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.apache.olingo.odata2.core.uri.UriParserImpl;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext.DatabaseProduct;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionSelect;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity1;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity2;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity3;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity4;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity5;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.junit.Before;
import org.junit.Test;

public class SQLQueryTest {

    private static final String SERVER_SIDE_PAGING_DEFAULT_SUFFIX = String.format(" FETCH FIRST %d ROWS ONLY",
            SQLQueryBuilder.DEFAULT_SERVER_PAGING_SIZE);
    AnnotationEdmProvider provider;
    UriParser uriParser;
    SQLQueryBuilder builder;
    SQLContext context;

    @Before
    public void setUp() throws Exception {
        Class<?>[] classes = { //
                Entity1.class, //
                Entity2.class, //
                Entity3.class, //
                Entity4.class,//
                Entity5.class
        };
        provider = new AnnotationEdmProvider(Arrays.asList(classes));
        EdmImplProv edm = new EdmImplProv(provider);
        uriParser = new UriParserImpl(edm);
        EdmTableBindingProvider tableMappingProvider = new DefaultEdmTableMappingProvider(OData2TestUtils.resources(classes));
        builder = new SQLQueryBuilder(tableMappingProvider);
        context = new SQLContext();
    }

    private ODataPathSegmentImpl createPathSegment() {
        return new ODataPathSegmentImpl("Entities1", Collections.<String, List<String>>emptyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMPLCount() throws Exception {
        //MessageProcessingLogs/$count
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = new ODataPathSegmentImpl("Entities1", EMPTY_MAP);
        PathSegment ps3 = new ODataPathSegmentImpl("$count", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1, ps3), EMPTY_MAP);

        SQLQuery q = builder.buildSelectCountQuery(uriInfo);
        SQLContext context = new SQLContext();
        assertEquals("SELECT COUNT(*) FROM MPLHEADER AS T0", q.buildSelect(context));
    }

    @Test
    public void testCountWithDate() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        PathSegment ps2 = new ODataPathSegmentImpl("$count", Collections.<String, List<String>>emptyMap());
        Map<String, String> params = new HashMap<>();
        params.put("$filter", "Status eq 'ERROR' and LogEnd lt datetime'2014-10-02T09:14:00'");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1, ps2), params);

        SQLQuery q = builder.buildSelectCountQuery(uriInfo);
        assertEquals("T0.STATUS = ? AND T0.LOGEND < ?", q.getWhereClause());
        assertEquals(false, q.getJoinWhereClauses().hasNext());

        assertEquals("ERROR", q.getParams().get(0).getValue());
        assertTrue(q.getParams().get(1).getValue() instanceof Calendar);
        assertEquals("SELECT COUNT(*) FROM MPLHEADER AS T0 WHERE T0.STATUS = ? AND T0.LOGEND < ?", q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithFilter() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$filter", "Status eq 'ERROR' and LogEnd lt datetime'2014-10-02T09:14:00'");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        assertEquals("T0.STATUS = ? AND T0.LOGEND < ?", q.getWhereClause());
        assertEquals(false, q.getJoinWhereClauses().hasNext());

        assertEquals("ERROR", q.getParams().get(0).getValue());
        assertTrue(q.getParams().get(1).getValue() instanceof Calendar);
        //the AlternateWebLink is mapped to MESSAGEID, therefore 2 times MESSAGEID
        assertEquals(
                "SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0, " + "T0.LOGSTART AS LOGSTART_T0, T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0, "
                        + "T0.RECEIVER AS RECEIVER_T0, " + "T0.STATUS AS STATUS_T0, T0.MESSAGEGUID AS MESSAGEGUID_T0 "
                        + "FROM MPLHEADER AS T0 WHERE T0.STATUS = ? AND T0.LOGEND < ?" + SERVER_SIDE_PAGING_DEFAULT_SUFFIX,
                q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithDynamicFilter() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$filter", "Status eq 'ERROR' and LogEnd lt datetime'2014-10-02T09:14:00'");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        assertEquals("T0.STATUS = ? AND T0.LOGEND < ?", q.getWhereClause());
        assertEquals(false, q.getJoinWhereClauses().hasNext());

        assertEquals("ERROR", q.getParams().get(0).getValue());
        assertTrue(q.getParams().get(1).getValue() instanceof Calendar);
        //the AlternateWebLink is mapped to MESSAGEID, therefore 2 times MESSAGEID
        assertEquals(
                "SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0, " + "T0.LOGSTART AS LOGSTART_T0, T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0, "
                        + "T0.RECEIVER AS RECEIVER_T0, " + "T0.STATUS AS STATUS_T0, T0.MESSAGEGUID AS MESSAGEGUID_T0 "
                        + "FROM MPLHEADER AS T0 WHERE T0.STATUS = ? AND T0.LOGEND < ?" + SERVER_SIDE_PAGING_DEFAULT_SUFFIX,
                q.buildSelect(context));
        q.clearFilter(uriInfo.getTargetEntitySet());
        assertEquals("SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0, "
                        + "T0.LOGSTART AS LOGSTART_T0, T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0, " + "T0.RECEIVER AS RECEIVER_T0, "
                        + "T0.STATUS AS STATUS_T0, T0.MESSAGEGUID AS MESSAGEGUID_T0 " + "FROM MPLHEADER AS T0" + SERVER_SIDE_PAGING_DEFAULT_SUFFIX,
                q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithOrderBy() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$orderby", "Status, LogStart desc");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);

        //the AlternateWebLink is mapped to MESSAGEID, therefore 2 times MESSAGEID
        assertEquals("SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0, T0.LOGSTART AS LOGSTART_T0,"
                + " T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0, T0.RECEIVER AS RECEIVER_T0, T0.STATUS AS STATUS_T0, "
                + "T0.MESSAGEGUID AS MESSAGEGUID_T0 FROM MPLHEADER AS T0 ORDER BY T0.STATUS, T0.LOGSTART DESC"
                + SERVER_SIDE_PAGING_DEFAULT_SUFFIX, q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithSkip0AndTop() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$skip", "0");
        params.put("$top", "10");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        assertEquals("SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0,"
                        + " T0.LOGSTART AS LOGSTART_T0, T0.LOGEND AS LOGEND_T0, T0.SENDER AS SENDER_T0, T0.RECEIVER AS RECEIVER_T0"
                        + ", T0.STATUS AS STATUS_T0, T0.MESSAGEGUID AS MESSAGEGUID_T0" + " FROM MPLHEADER AS T0 " + "FETCH FIRST 10 ROWS ONLY",
                q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithSelect() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$select", "Status");
        params.put("$orderby", "Status, LogStart desc");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);

        //The primary key is always selected in addition
        assertEquals(
                "SELECT T0.STATUS AS STATUS_T0, T0.MESSAGEGUID AS MESSAGEGUID_T0 FROM MPLHEADER AS T0 ORDER BY T0.STATUS, T0.LOGSTART DESC"
                        + SERVER_SIDE_PAGING_DEFAULT_SUFFIX,
                q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithSelectPrimaryKey() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$select", "MessageGuid");
        params.put("$orderby", "Status, LogStart desc");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);

        //The primary key is always selected in addition
        assertEquals("SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0 FROM MPLHEADER AS T0 ORDER BY T0.STATUS, T0.LOGSTART DESC"
                + SERVER_SIDE_PAGING_DEFAULT_SUFFIX, q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithSelectAttribute_PrimaryKeyIsAlsoSelected() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$select", "Status");
        params.put("$orderby", "Status, LogStart desc");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);

        //The primary key is always selected in addition
        assertEquals(
                "SELECT T0.STATUS AS STATUS_T0, T0.MESSAGEGUID AS MESSAGEGUID_T0 FROM MPLHEADER AS T0 ORDER BY T0.STATUS, T0.LOGSTART DESC"
                        + SERVER_SIDE_PAGING_DEFAULT_SUFFIX,
                q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithSelectTop() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$select", "MessageGuid");
        params.put("$orderby", "Status, LogStart desc");
        params.put("$top", "2");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);

        //We expect to have FETCH FIRST expression with derby
        assertEquals("SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0 FROM MPLHEADER AS T0 ORDER BY T0.STATUS, T0.LOGSTART DESC"
                + " FETCH FIRST 2 ROWS ONLY", q.buildSelect(context));
    }

    @Test
    public void testGetMessageProcessingLogsWithSelectTopPostgres() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$select", "MessageGuid");
        params.put("$orderby", "Status, LogStart desc");
        params.put("$top", "2");
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext(DatabaseProduct.POSTGRE_SQL);

        //We expect to have FETCH FIRST expression with derby
        assertEquals("SELECT T0.MESSAGEGUID AS MESSAGEGUID_T0 FROM MPLHEADER AS T0 ORDER BY T0.STATUS, T0.LOGSTART DESC" + " LIMIT 2",
                q.buildSelect(context));
    }

    @Test
    public void testCalculateEffectiveSkipFromSkipAndSkiptoken() throws EdmException, ODataException {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$skip", "3");
        params.put("$skiptoken", "5");

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        int actualSkip = q.getSelectExpression().getSkip();

        assertEquals(8, actualSkip);
    }

    @Test
    public void testCalculateEffectiveSkipFromSkiptokenWithoutSkip() throws EdmException, ODataException {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$skiptoken", "5");

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        int actualSkip = q.getSelectExpression().getSkip();

        assertEquals(5, actualSkip);
    }

    @Test
    public void testCalculateEffectiveSkipFromSkipWithoutSkiptoken() throws EdmException, ODataException {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$skip", "3");

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        int actualSkip = q.getSelectExpression().getSkip();

        assertEquals(3, actualSkip);
    }

    @Test
    public void testCalculateEffectiveSkipWithoutSkipAndSkiptoken() throws EdmException, ODataException {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        int actualSkip = q.getSelectExpression().getSkip();

        assertEquals(SQLExpressionSelect.NOT_SET, actualSkip);
    }

    @Test
    public void testCalculateEffectiveTopFromTopInUri() throws EdmException, ODataException {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$top", "3");

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        int actualTop = q.getSelectExpression().getTop();

        assertEquals(3, actualTop);
        assertEquals(false, q.isServersidePaging());
    }

    @Test
    public void testCalculateEffectiveTopWithoutTopInUri() throws EdmException, ODataException {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        int actualTop = q.getSelectExpression().getTop();

        assertEquals(SQLQueryBuilder.DEFAULT_SERVER_PAGING_SIZE, actualTop);
        assertEquals(true, q.isServersidePaging());
    }

    @Test
    public void testCalculateEffectiveTopBeyondServersidePagingInUri() throws EdmException, ODataException {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$top", Integer.toString(SQLQueryBuilder.DEFAULT_SERVER_PAGING_SIZE + 10));

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        int actualTop = q.getSelectExpression().getTop();

        assertEquals(SQLQueryBuilder.DEFAULT_SERVER_PAGING_SIZE, actualTop);
        assertEquals(true, q.isServersidePaging());
    }

    @Test
    public void testNextWithTop() throws Exception {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$top", Integer.toString(10));

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);

        ResultSet rs = EasyMock.createNiceMock(ResultSet.class);
        EasyMock.expect(rs.next()).andReturn(true).times(100);
        EasyMock.replay(rs);

        int counter = 0;
        while (q.next(rs)) {
            counter++;
        }

        assertEquals(10, counter);
    }

    @Test
    public void testNextWithSkip() throws Exception {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$skip", Integer.toString(30));

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);

        ResultSet rs = EasyMock.createNiceMock(ResultSet.class);
        EasyMock.expect(rs.next()).andReturn(true).times(100);
        Capture<Integer> capturedRelativeMoves = new Capture<>();
        EasyMock.expect(rs.relative(EasyMock.captureInt(capturedRelativeMoves))).andReturn(true);
        EasyMock.replay(rs);

        q.setOffset(rs); // skip

        List<Integer> relativeMoves = capturedRelativeMoves.getValues();
        for (Integer relative : relativeMoves) {
            if (relative != null && relative > 0) {
                for (int i = 0; i < relative; i++) {
                    rs.next();
                }
            } else if (relative != null && relative < 0) {
                for (int i = 0; i > relative; i--) {
                    rs.previous();
                }
            }
        }

        int counter = 0;
        while (q.next(rs)) {
            counter++;
        }

        assertEquals(70, counter);
    }

    @Test
    public void testSelectWithComposedKey() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        Map<String, String> params = new HashMap<String, String>();
        PathSegment ps1 = new ODataPathSegmentImpl("Entities4(Id4_1=11,Id4_2=22)", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);
        SQLContext context = new SQLContext();
        String expected = "SELECT T0.ID4_1 AS ID4_1_T0, T0.ID4_2 AS ID4_2_T0"
                + " FROM ENTITY4_TABLE AS T0"
                + " WHERE T0.ID4_1 = ? AND T0.ID4_2 = ? FETCH FIRST 1000 ROWS ONLY";
        assertEquals(expected, q.buildSelect(context));
    }


    @Test
    public void testDeleteWithComposedKey() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        Map<String, String> params = new HashMap<String, String>();
        PathSegment ps1 = new ODataPathSegmentImpl("Entities4(Id4_1=11,Id4_2=22)", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        SQLQuery q = builder.buildDeleteEntityQuery(uriInfo, mapKeys(uriInfo.getKeyPredicates()));
        SQLContext context = new SQLContext();
        String expected = "DELETE FROM ENTITY4_TABLE WHERE Id4_1 = ? , Id4_2 = ?";
        assertEquals(expected, q.buildDelete(context));
    }

    @Test
    public void testInsertWithComposedKey() throws Exception {
        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
        Map<String, String> params = new HashMap<String, String>();
        PathSegment ps1 = new ODataPathSegmentImpl("Entities4(Id4_1=11,Id4_2=22)", EMPTY_MAP);
        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);

        Map<String, Object> entity4=new HashMap<>();
        entity4.put("Id4_1", "1");
        entity4.put("Id4_2", "2");
        ODataEntry entity =new ODataEntryImpl(entity4, null, null, null);

        SQLQuery q = builder.buildInsertEntityQuery(uriInfo, entity);
        SQLContext context = new SQLContext();
        String expected = "INSERT INTO ENTITY4_TABLE (ID4_1,ID4_2) VALUES (?,?)";
        assertEquals(expected, q.buildInsert(context));

        Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "true");
        q = builder.buildInsertEntityQuery(uriInfo, entity);
        expected = "INSERT INTO \"ENTITY4_TABLE\" (\"ID4_1\",\"ID4_2\") VALUES (?,?)";
        assertEquals(expected, q.buildInsert(context));
    }

    @Test
    public void testNextWithTopSkip1() throws Exception {
        testNextWithTopSkip(10, 3, 100);
    }

    @Test
    public void testNextWithTopSkip2() throws Exception {
        testNextWithTopSkip(10, 3, 8);
    }

    private static Map<String, Object> mapKeys(final List<KeyPredicate> keys) throws EdmException {
        Map<String, Object> keyMap = new HashMap<String, Object>();
        for (final KeyPredicate key : keys) {
            final EdmProperty property = key.getProperty();
            final EdmSimpleType type = (EdmSimpleType) property.getType();
            keyMap.put(property.getName(), type.valueOfString(key.getLiteral(), EdmLiteralKind.DEFAULT,
                    property.getFacets(), type.getDefaultType()));
        }
        return keyMap;
    }

    private void testNextWithTopSkip(final int top, final int skip, final int resultSetSize) throws Exception {
        PathSegment ps1 = createPathSegment();
        Map<String, String> params = new HashMap<>();
        params.put("$top", Integer.toString(top));
        params.put("$skip", Integer.toString(skip));

        UriInfo uriInfo = uriParser.parse(Arrays.asList(ps1), params);
        SQLQuery q = builder.buildSelectEntitySetQuery(uriInfo);

        ResultSet rs = EasyMock.createNiceMock(ResultSet.class);

        EasyMock.expect(rs.next()).andReturn(true).times(resultSetSize);
        Capture<Integer> capturedRelativeMoves = new Capture<>();
        EasyMock.expect(rs.relative(EasyMock.captureInt(capturedRelativeMoves))).andReturn(true);
        EasyMock.replay(rs);

        q.setOffset(rs); // skip

        List<Integer> relativeMoves = capturedRelativeMoves.getValues();
        for (Integer relative : relativeMoves) {
            if (relative != null && relative > 0) {
                for (int i = 0; i < relative; i++) {
                    rs.next();
                }
            } else if (relative != null && relative < 0) {
                for (int i = 0; i > relative; i--) {
                    rs.previous();
                }
            }
        }

        int counter = 0;
        while (q.next(rs)) {
            counter++;
        }

        int expectedCounter = Math.min(resultSetSize - skip, top);
        assertEquals(expectedCounter, counter);
    }
}
