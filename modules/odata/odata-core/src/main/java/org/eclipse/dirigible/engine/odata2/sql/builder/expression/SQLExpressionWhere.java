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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;

public final class SQLExpressionWhere implements SQLExpression {
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final char OPEN_BRACKET = '(';
    private static final char CLOSE_BRACKET = ')';

    private final List<Param> params;
    private final StringBuilder whereClause;

    public enum TemporalType {
        DATE, TIME, TIMESTAMP
    }

    public SQLExpressionWhere() {
        this(EMPTY_STRING);
    }

    public SQLExpressionWhere(String whereClause, List<Param> params) {
        this.whereClause = new StringBuilder(whereClause);
        this.params = new ArrayList<>();
        this.params.addAll(params);
    }

    public SQLExpressionWhere(String whereClause, Param... params) {
        this(whereClause, params == null ? new ArrayList<Param>() : Arrays.asList(params));
    }

    @Override
    public String evaluate(SQLContext context, ExpressionType type) throws EdmException {
        switch (type) {
        case WHERE:
            return getWhereClause();
        default:
            return "";
        }
    }

    public boolean isEmpty() {
        return (whereClause == null || whereClause.length() == 0) ? true : false;
    }

    public SQLExpressionWhere and(SQLExpressionWhere where) {
        return append(AND, where);
    }

    public SQLExpressionWhere or(SQLExpressionWhere where) {
        return append(OR, where);
    }

    public String getWhereClause() {
        return this.whereClause.toString();
    }

    public List<Param> getParams() {
        return Collections.unmodifiableList(params);
    }

    // For internal use (Unit Test purposes) only!
    Param getParamAt(int index) {
        return params.get(index);
    }

    private SQLExpressionWhere append(final String appendPredicate, final SQLExpressionWhere... whereClauses) {
        boolean useBraces = (!this.isEmpty() && whereClauses.length > 1) ? true : false;
        Iterator<SQLExpressionWhere> it = Arrays.asList(whereClauses).iterator();
        if (useBraces) {
            this.whereClause.append(appendPredicate);
            this.whereClause.append(OPEN_BRACKET);
        }
        while (it.hasNext()) {
            SQLExpressionWhere where = it.next();
            if (!where.isEmpty() && !endsWithOpenBracket() && !isEmpty()) {
                this.whereClause.append(AND);
            }
            if (!where.isEmpty()) {
                this.whereClause.append(where.whereClause);
                this.params.addAll(where.params);
            }
        }
        if (useBraces) {
            this.whereClause.append(CLOSE_BRACKET);
        }
        return this;
    }

    private boolean endsWithOpenBracket() {
        if (whereClause.length() < 1) {
            return false;
        } else {
            return OPEN_BRACKET == whereClause.charAt(whereClause.length() - 1);
        }
    }

    public static Param param(Object value) {
        return new Param(value);
    }

    public static Param param(Object parameter, final EdmSimpleType edmSimpleType) {
        if (edmSimpleType == EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance()) {
            return new Param(parameter, TemporalType.TIMESTAMP);
        } else if (edmSimpleType == EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance()) {
            return new Param(parameter, TemporalType.TIME);
        } else if (edmSimpleType == EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance()) {
            return new Param(parameter, TemporalType.DATE);
        } else {
            return new Param(parameter);
        }
    }

    public static Param param(Object parameter, final EdmSimpleType edmSimpleType, final String sqlType) {
        if (edmSimpleType == EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance()) {
            return new Param(parameter, TemporalType.TIMESTAMP);
        } else if (edmSimpleType == EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance()) {
            return new Param(parameter, TemporalType.TIME);
        } else if (edmSimpleType == EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance()) {
            return new Param(parameter, TemporalType.DATE);
        } else {
            return new Param(parameter, sqlType);
        }
    }

    public static class Param {
        private final Object value;
        private final TemporalType temporalType;
        private final String sqlType;

        public Param(Object value) {
            this(value, null, (String) null);
        }

        public Param(Object value, TemporalType temporalType) {
            this(value, temporalType, (String) null);
        }

        public Param(Object value, String sqlType) {
            this(value, null, sqlType);
        }

        public Param(Object value, TemporalType temporalType, String sqlType) {
            this.value = value;
            this.temporalType = temporalType;
            if (isTemporalType()) {
                if (value instanceof Calendar == false) {
                    throw new IllegalArgumentException(
                            "Only java.util.Calendar values are allowed for temporal parameters of the named queries!");
                }
            }
            this.sqlType = sqlType;
        }

        public boolean isTemporalType() {
            return temporalType == null ? false : true;
        }

        public TemporalType getTemporalType() {
            return temporalType;
        }

        public String getSqlType() {
            return this.sqlType;
        }

        @SuppressWarnings("unchecked")
        public <T> T getValue() {
            return (T) value;
        }
    }
}