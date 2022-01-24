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

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatementParam;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLUtils;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity1;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity2;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity3;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.eclipse.dirigible.engine.odata2.sql.api.SQLStatement.EMPTY_STRING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class SQLWhereClauseTest {

    AnnotationEdmProvider provider;
    EdmImplProv edm;
    EdmTableBindingProvider tableMappingProvider;

    @Before
    public void setUp() throws Exception {
        Class<?>[] classes = { //
                Entity1.class, //
                Entity2.class, //
                Entity3.class //
        };
        provider = new AnnotationEdmProvider(Arrays.asList(classes));
        edm = new EdmImplProv(provider);
        tableMappingProvider = new DefaultEdmTableMappingProvider(OData2TestUtils.resources(classes));
    }

    @Test
    public void testEmptyAndEmptyClause() {

        SQLWhereClause where = new SQLWhereClause(EMPTY_STRING);
        where.and(new SQLWhereClause(EMPTY_STRING));

        assertTrue(where.getStatementParams().isEmpty());
        assertEquals(EMPTY_STRING, where.getWhereClause());
        assertTrue(where.isEmpty());
    }

    @Test
    public void testAndClauseWithEquals() throws Exception {

        // AND combined with empty clause
        //
        // Right
        SQLWhereClause where = createWhereClause("MessageGuid eq '1234'").and(new SQLWhereClause());
        assertParamListEquals(new String[]{"1234"}, where.getStatementParams());
        assertEquals("T0.MESSAGEGUID = ?", where.getWhereClause());
        // Left
        where = new SQLWhereClause().and(createWhereClause("MessageGuid eq '1234'"));
        assertParamListEquals(new String[]{"1234"}, where.getStatementParams());
        assertEquals("T0.MESSAGEGUID = ?", where.getWhereClause());

        //
        // 2 WHERE clauses with eq combined with AND
        where = createWhereClause( //
                "Status eq 'SUCCESS' and MessageGuid eq '1234'");

        assertParamListEquals(new String[]{"SUCCESS", "1234"}, where.getStatementParams());
        assertEquals("T0.STATUS = ? AND T0.MESSAGEGUID = ?", where.getWhereClause());

        //
        // 4 WHERE clauses combined with AND, an empty clause at different places
        //
        where = createWhereClause("MessageGuid eq '1234'") //
                .and(new SQLWhereClause()) //
                .and(createWhereClause("Status eq 'SUCCESS'")) //
                .and(createWhereClause("Sender eq 'sender'")) //
                .and(createWhereClause("MessageGuid eq '0815'"));

        assertParamListEquals(new String[]{"1234", "SUCCESS", "sender", "0815"}, where.getStatementParams());
        assertEquals("T0.MESSAGEGUID = ? AND T0.STATUS = ? AND T0.SENDER = ? AND T0.MESSAGEGUID = ?", where.getWhereClause());

    }

    @Test
    public void testOrClauseWithEquals() throws Exception {

        // OR combined with empty clause
        //
        // Right
        SQLWhereClause where = createWhereClause("MessageGuid eq '1234'").or(new SQLWhereClause());
        assertParamListEquals(new String[]{"1234"}, where.getStatementParams());
        assertEquals("T0.MESSAGEGUID = ?", where.getWhereClause());
        // Left
        where = new SQLWhereClause().or(createWhereClause("MessageGuid eq '1234'"));
        assertParamListEquals(new String[]{"1234"}, where.getStatementParams());
        assertEquals("T0.MESSAGEGUID = ?", where.getWhereClause());

        //
        // 2 WHERE clauses with eq combined with OR
        where = createWhereClause( //
                "Status eq 'SUCCESS' or MessageGuid eq '1234'");

        assertParamListEquals(new String[]{"SUCCESS", "1234"}, where.getStatementParams());
        assertEquals("T0.STATUS = ? OR T0.MESSAGEGUID = ?", where.getWhereClause());

    }

    @Test
    public void testFilterFunctionToUpperToLower() throws Exception {

        SQLWhereClause where = createWhereClause( //
                "toupper(Status) eq 'SUCCESS' and tolower(MessageGuid) eq '4711'");

        assertParamListEquals(new String[]{"SUCCESS", "4711"}, where.getStatementParams());
        assertEquals("UPPER(T0.STATUS) = ? AND LOWER(T0.MESSAGEGUID) = ?", where.getWhereClause());
    }

    @Test
    public void testFilterFunctionToUpperAppliedTwice() throws Exception {
        SQLWhereClause where = createWhereClause( //
                "toupper(MessageGuid) eq  tolower(MessageGuid)");
        assertEquals("UPPER(T0.MESSAGEGUID) = LOWER(T0.MESSAGEGUID)", where.getWhereClause());
    }

    @Test
    public void testConcatFunction() throws Exception {
        SQLWhereClause where = createWhereClause( //
                "concat(MessageGuid, 'test')");
        assertParamListEquals(new String[]{"test"}, where.getStatementParams());
        assertEquals("CONCAT(T0.MESSAGEGUID,?)", where.getWhereClause());
    }

    @Test
    public void testConcatFunction2() throws Exception {
        SQLWhereClause where = createWhereClause( //
                "concat('test', MessageGuid)");
        assertParamListEquals(new String[]{"test"}, where.getStatementParams());
        assertEquals("CONCAT(?,T0.MESSAGEGUID)", where.getWhereClause());
    }

    @Test
    public void testConcatFunction3() throws Exception {
        SQLWhereClause where = createWhereClause( //
                "concat(MessageGuid, MessageGuid)");
        assertEquals(0, where.getStatementParams().size());
        assertEquals("CONCAT(T0.MESSAGEGUID,T0.MESSAGEGUID)", where.getWhereClause());
    }

    @Test
    public void testLengthFunction() throws Exception {
        SQLWhereClause where = createWhereClause( //
                "length(MessageGuid)");
        assertEquals(0, where.getStatementParams().size());
        assertEquals("LENGTH(T0.MESSAGEGUID)", where.getWhereClause());
    }

    @Test
    public void testFilterFunctionStartsWith() throws Exception {
        SQLWhereClause where = createWhereClause("Status eq 'ERROR' and startswith(MessageGuid, 'fl')");

        assertParamListEquals(new String[]{"ERROR", "fl%"}, where.getStatementParams());
        assertEquals("T0.STATUS = ? AND T0.MESSAGEGUID LIKE ?", where.getWhereClause());
    }

    @Test(expected = OData2Exception.class)
    public void testFilterFunctionStartsWith2() throws Exception {
        SQLWhereClause where = createWhereClause("Status eq 'ERROR' and startswith('fl',MessageGuid)");
        where.getWhereClause();
    }

    @Test
    public void testFilterFunctionEndsWith() throws Exception {
        SQLWhereClause where = createWhereClause(
                "Status eq 'ERROR' and endswith(MessageGuid, 'fl')");
        assertParamListEquals(new String[]{"ERROR", "%fl"}, where.getStatementParams());
        assertEquals("T0.STATUS = ? AND T0.MESSAGEGUID LIKE ?", where.getWhereClause());
    }

    public void testFilterFunctionSubtringOf() throws Exception {
        SQLWhereClause where = createWhereClause("Status eq 'ERROR' and substringof('fl', MessageGuid)");
        assertEquals("T0.STATUS = ? AND T0.MESSAGEGUID LIKE ?", where.getWhereClause());
    }

    @Test
    public void testNotEqualsAndWithDateParam() throws Exception {
        String date = "2014-10-02T09:14:00"; //This is a date in the client locale. 

        SQLWhereClause where = createWhereClause( //
                String.format("Status ne 'ERROR' and LogEnd lt datetime'%s'", date));

        assertEquals("T0.STATUS <> ? AND T0.LOGEND < ?", where.getWhereClause());
        assertEquals("ERROR", where.getParamAt(0).getValue());
        assertTrue(where.getParamAt(1).getValue() instanceof Calendar);
        Calendar c = (Calendar) where.getParamAt(1).getValue();

        //We are expecting to get the client date parsed with a UTC locale
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(f.parse(date), c.getTime());
    }

    @Test
    public void testLessThanGreaterEqualsWithDateParam() throws Exception {
        String from = "2015-03-24T23:00:00"; //This is a date in the client locale. 
        String to = "2015-03-25T00:00:00";

        SQLWhereClause where = createWhereClause( //
                String.format("Status eq 'FAILED' and LogEnd ge datetime'%s' and LogEnd lt datetime'%s'", from, to));

        assertEquals("T0.STATUS = ? AND T0.LOGEND >= ? AND T0.LOGEND < ?", where.getWhereClause());
        assertTrue(where.getParamAt(1).getValue() instanceof Calendar);
        assertTrue(where.getParamAt(2).getValue() instanceof Calendar);

        Calendar cfr = (Calendar) where.getParamAt(1).getValue();

        //We are expecting to get the client date parsed with a UTC locale
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(f.parse(from), cfr.getTime());

        Calendar cto = (Calendar) where.getParamAt(2).getValue();
        //We are expecting to get the client date parsed with a UTC locale
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(f.parse(to), cto.getTime());
    }

    @Test
    public void testOperatorPrecedenceAndOrNot() throws Exception {

        /////////////////////////////////////////
        // a AND ( b AND c ) AND d
        SQLWhereClause where = createWhereClause( //
                "Status eq 'ERROR' and ( Sender eq 'From' and Receiver eq 'To' ) and Status eq 'RETRY'");

        assertParamListEquals(new String[]{"ERROR", "From", "To", "RETRY"}, where.getStatementParams());
        assertEquals("T0.STATUS = ? AND T0.SENDER = ? AND T0.RECEIVER = ? AND T0.STATUS = ?", where.getWhereClause());

        /////////////////////////////////////////
        // a OR ( b AND c ) OR d
        where = createWhereClause( //
                "Status eq 'ERROR' or ( Sender eq 'From' and Receiver eq 'To' ) or Status eq 'RETRY'");

        assertParamListEquals(new String[]{"ERROR", "From", "To", "RETRY"}, where.getStatementParams());
        assertEquals("T0.STATUS = ? OR T0.SENDER = ? AND T0.RECEIVER = ? OR T0.STATUS = ?", where.getWhereClause());

        /////////////////////////////////////////
        // a OR b AND c OR d
        where = createWhereClause( //
                "Status eq 'ERROR' or Sender eq 'From' and Receiver eq 'To' or Status eq 'RETRY'");

        assertParamListEquals(new String[]{"ERROR", "From", "To", "RETRY"}, where.getStatementParams());
        assertEquals("T0.STATUS = ? OR T0.SENDER = ? AND T0.RECEIVER = ? OR T0.STATUS = ?", where.getWhereClause());

        /////////////////////////////////////////
        // ( a OR b ) AND ( c OR d )
        where = createWhereClause( //
                "(Status eq 'ERROR' or Sender eq 'From') and (Receiver eq 'To' or Status eq 'RETRY')");

        assertParamListEquals(new String[]{"ERROR", "From", "To", "RETRY"}, where.getStatementParams());
        assertEquals("(T0.STATUS = ? OR T0.SENDER = ?) AND (T0.RECEIVER = ? OR T0.STATUS = ?)", where.getWhereClause());

        /////////////////////////////////////////
        // ( NOT ( a ) OR b ) AND NOT c OR d
        where = createWhereClause( //
                "( not ( Status eq 'ERROR' ) or Sender eq 'From' ) and not ( Receiver eq 'To' ) or Status eq 'RETRY'");

        assertParamListEquals(new String[]{"ERROR", "From", "To", "RETRY"}, where.getStatementParams());
        assertEquals("(NOT(T0.STATUS = ?) OR T0.SENDER = ?) AND NOT(T0.RECEIVER = ?) OR T0.STATUS = ?", where.getWhereClause());

        /////////////////////////////////////////
        // ( a OR NOT b ) AND NOT ( c OR d )
        where = createWhereClause( //
                "( Status eq 'ERROR' or not ( Sender eq 'From' ) ) and not ( Receiver eq 'To' or Status eq 'RETRY' )");

        assertParamListEquals(new String[]{"ERROR", "From", "To", "RETRY"}, where.getStatementParams());
        assertEquals("(T0.STATUS = ? OR NOT(T0.SENDER = ?)) AND NOT(T0.RECEIVER = ? OR T0.STATUS = ?)", where.getWhereClause());

    }

    @Test
    public void testLessEqualGreaterThanWithDateParam() throws Exception {
        String from = "2015-03-24T23:00:00"; //This is a date in the client locale. 
        String to = "2015-03-25T00:00:00";

        SQLWhereClause where = createWhereClause( //
                String.format("Status eq 'FAILED' and LogEnd gt datetime'%s' and LogEnd le datetime'%s'", from, to));

        assertEquals("T0.STATUS = ? AND T0.LOGEND > ? AND T0.LOGEND <= ?", where.getWhereClause());
        assertTrue(where.getParamAt(1).getValue() instanceof Calendar);
        assertTrue(where.getParamAt(2).getValue() instanceof Calendar);

        Calendar cfr = (Calendar) where.getParamAt(1).getValue();

        //We are expecting to get the client date parsed with a UTC locale
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(f.parse(from), cfr.getTime());

        Calendar cto = (Calendar) where.getParamAt(2).getValue();
        //We are expecting to get the client date parsed with a UTC locale
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(f.parse(to), cto.getTime());
    }

    private SQLWhereClause createWhereClause(final String expression) throws ODataException {
        EdmEntityType type = edm.getEntityType(Entity1.class.getPackage().getName(), Entity1.class.getSimpleName());
        FilterExpression filter = UriParser.parseFilter(edm, type, expression);
        SQLSelectBuilder noop = new SQLSelectBuilder(tableMappingProvider);
        return SQLUtils.buildSQLWhereClause(noop, type, filter);
    }

    private void assertParamListEquals(final String[] paramsExp, final List<SQLStatementParam> paramsAct) {

        assertEquals(paramsExp.length, paramsAct.size());

        final Iterator<SQLStatementParam> itParam = paramsAct.iterator();
        for (String p : paramsExp) {
            assertTrue(itParam.hasNext());
            assertEquals(p, itParam.next().getValue());
        }
    }
}
