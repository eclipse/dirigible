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

import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.EMPTY_STRING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataMessageException;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.ExpressionParserException;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionWhere.Param;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity1;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity2;
import org.eclipse.dirigible.engine.odata2.sql.edm.Entity3;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.junit.Before;
import org.junit.Test;

public final class SQLExpressionWhereTest {

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

        SQLExpressionWhere where = new SQLExpressionWhere(EMPTY_STRING);
        where.and(new SQLExpressionWhere(EMPTY_STRING));

        assertTrue(where.getParams().isEmpty());
        assertEquals(EMPTY_STRING, where.getWhereClause());
        assertTrue(where.isEmpty());
    }

    @Test
    public void testAndClauseWithEquals() throws Exception {

        // AND combined with empty clause
        //
        // Right
        SQLExpressionWhere where = createWhereClause("MessageGuid eq '1234'").and(new SQLExpressionWhere());
        assertParamListEquals(new String[] { "1234" }, where.getParams());
        assertEquals("T0.MESSAGEGUID = ?", where.getWhereClause());
        // Left
        where = new SQLExpressionWhere().and(createWhereClause("MessageGuid eq '1234'"));
        assertParamListEquals(new String[] { "1234" }, where.getParams());
        assertEquals("T0.MESSAGEGUID = ?", where.getWhereClause());

        //
        // 2 WHERE clauses with eq combined with AND
        where = createWhereClause( //
                "Status eq 'SUCCESS' and MessageGuid eq '1234'");

        assertParamListEquals(new String[] { "SUCCESS", "1234" }, where.getParams());
        assertEquals("T0.STATUS = ? AND T0.MESSAGEGUID = ?", where.getWhereClause());

        //
        // 4 WHERE clauses combined with AND, an empty clause at different places
        //
        where = createWhereClause("MessageGuid eq '1234'") //
                .and(new SQLExpressionWhere()) //
                .and(createWhereClause("Status eq 'SUCCESS'")) //
                .and(createWhereClause("Sender eq 'sender'")) //
                .and(createWhereClause("MessageGuid eq '0815'"));

        assertParamListEquals(new String[] { "1234", "SUCCESS", "sender", "0815" }, where.getParams());
        assertEquals("T0.MESSAGEGUID = ? AND T0.STATUS = ? AND T0.SENDER = ? AND T0.MESSAGEGUID = ?", where.getWhereClause());

    }

    @Test
    public void testOrClauseWithEquals() throws Exception {

        // OR combined with empty clause
        //
        // Right
        SQLExpressionWhere where = createWhereClause("MessageGuid eq '1234'").or(new SQLExpressionWhere());
        assertParamListEquals(new String[] { "1234" }, where.getParams());
        assertEquals("T0.MESSAGEGUID = ?", where.getWhereClause());
        // Left
        where = new SQLExpressionWhere().or(createWhereClause("MessageGuid eq '1234'"));
        assertParamListEquals(new String[] { "1234" }, where.getParams());
        assertEquals("T0.MESSAGEGUID = ?", where.getWhereClause());

        //
        // 2 WHERE clauses with eq combined with OR
        where = createWhereClause( //
                "Status eq 'SUCCESS' or MessageGuid eq '1234'");

        assertParamListEquals(new String[] { "SUCCESS", "1234" }, where.getParams());
        assertEquals("T0.STATUS = ? OR T0.MESSAGEGUID = ?", where.getWhereClause());

    }

    @Test
    public void testFilterFunctionToUpperToLower() throws Exception {

        SQLExpressionWhere where = createWhereClause( //
                "toupper(Status) eq 'SUCCESS' and tolower(MessageGuid) eq '4711'");

        assertParamListEquals(new String[] { "SUCCESS", "4711" }, where.getParams());
        assertEquals("UPPER(T0.STATUS) = ? AND LOWER(T0.MESSAGEGUID) = ?", where.getWhereClause());
    }

    @Test
    public void testFilterFunctionStartsWith() throws Exception {

        SQLExpressionWhere where = createWhereClause( //
                "Status eq 'ERROR' and startswith(MessageGuid, 'fl')");

        assertParamListEquals(new String[] { "ERROR", "fl%" }, where.getParams());
        assertEquals("T0.STATUS = ? AND T0.MESSAGEGUID LIKE ?", where.getWhereClause());
    }

    @Test
    public void testFilterFunctionEndsWith() throws Exception {

        SQLExpressionWhere where = createWhereClause( //
                "Status eq 'ERROR' and endswith(MessageGuid, 'fl')");

        assertParamListEquals(new String[] { "ERROR", "%fl" }, where.getParams());
        assertEquals("T0.STATUS = ? AND T0.MESSAGEGUID LIKE ?", where.getWhereClause());
    }

    @Test
    public void testFilterFunctionSubtringOf() throws Exception {

        SQLExpressionWhere where = createWhereClause( //
                "Status eq 'ERROR' and substringof('fl', MessageGuid)");

        assertParamListEquals(new String[] { "ERROR", "%fl%" }, where.getParams());
        assertEquals("T0.STATUS = ? AND T0.MESSAGEGUID LIKE ?", where.getWhereClause());
    }

    @Test
    public void testNotEqualsAndWithDateParam() throws Exception {
        String date = "2014-10-02T09:14:00"; //This is a date in the client locale. 

        SQLExpressionWhere where = createWhereClause( //
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

        SQLExpressionWhere where = createWhereClause( //
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
        SQLExpressionWhere where = createWhereClause( //
                "Status eq 'ERROR' and ( Sender eq 'From' and Receiver eq 'To' ) and Status eq 'RETRY'");

        assertParamListEquals(new String[] { "ERROR", "From", "To", "RETRY" }, where.getParams());
        assertEquals("T0.STATUS = ? AND T0.SENDER = ? AND T0.RECEIVER = ? AND T0.STATUS = ?", where.getWhereClause());

        /////////////////////////////////////////
        // a OR ( b AND c ) OR d
        where = createWhereClause( //
                "Status eq 'ERROR' or ( Sender eq 'From' and Receiver eq 'To' ) or Status eq 'RETRY'");

        assertParamListEquals(new String[] { "ERROR", "From", "To", "RETRY" }, where.getParams());
        assertEquals("T0.STATUS = ? OR T0.SENDER = ? AND T0.RECEIVER = ? OR T0.STATUS = ?", where.getWhereClause());

        /////////////////////////////////////////
        // a OR b AND c OR d
        where = createWhereClause( //
                "Status eq 'ERROR' or Sender eq 'From' and Receiver eq 'To' or Status eq 'RETRY'");

        assertParamListEquals(new String[] { "ERROR", "From", "To", "RETRY" }, where.getParams());
        assertEquals("T0.STATUS = ? OR T0.SENDER = ? AND T0.RECEIVER = ? OR T0.STATUS = ?", where.getWhereClause());

        /////////////////////////////////////////
        // ( a OR b ) AND ( c OR d )
        where = createWhereClause( //
                "(Status eq 'ERROR' or Sender eq 'From') and (Receiver eq 'To' or Status eq 'RETRY')");

        assertParamListEquals(new String[] { "ERROR", "From", "To", "RETRY" }, where.getParams());
        assertEquals("(T0.STATUS = ? OR T0.SENDER = ?) AND (T0.RECEIVER = ? OR T0.STATUS = ?)", where.getWhereClause());

        /////////////////////////////////////////
        // ( NOT ( a ) OR b ) AND NOT c OR d
        where = createWhereClause( //
                "( not ( Status eq 'ERROR' ) or Sender eq 'From' ) and not ( Receiver eq 'To' ) or Status eq 'RETRY'");

        assertParamListEquals(new String[] { "ERROR", "From", "To", "RETRY" }, where.getParams());
        assertEquals("(NOT(T0.STATUS = ?) OR T0.SENDER = ?) AND NOT(T0.RECEIVER = ?) OR T0.STATUS = ?", where.getWhereClause());

        /////////////////////////////////////////
        // ( a OR NOT b ) AND NOT ( c OR d )
        where = createWhereClause( //
                "( Status eq 'ERROR' or not ( Sender eq 'From' ) ) and not ( Receiver eq 'To' or Status eq 'RETRY' )");

        assertParamListEquals(new String[] { "ERROR", "From", "To", "RETRY" }, where.getParams());
        assertEquals("(T0.STATUS = ? OR NOT(T0.SENDER = ?)) AND NOT(T0.RECEIVER = ? OR T0.STATUS = ?)", where.getWhereClause());

    }

    @Test
    public void testLessEqualGreaterThanWithDateParam() throws Exception {
        String from = "2015-03-24T23:00:00"; //This is a date in the client locale. 
        String to = "2015-03-25T00:00:00";

        SQLExpressionWhere where = createWhereClause( //
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

    private SQLExpressionWhere createWhereClause(final String expression)
            throws ExpressionParserException, ODataMessageException, EdmException, ODataException {
        EdmEntityType type = edm.getEntityType(Entity1.class.getPackage().getName(), Entity1.class.getSimpleName());
        FilterExpression filter = UriParser.parseFilter(edm, type, expression);
        SQLQuery noop = new SQLQuery(tableMappingProvider);
        return SQLExpressionUtils.buildSQLWhereClause(noop, type, filter);
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
