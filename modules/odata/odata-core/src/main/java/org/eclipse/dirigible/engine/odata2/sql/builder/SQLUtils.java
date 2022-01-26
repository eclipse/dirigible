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
package org.eclipse.dirigible.engine.odata2.sql.builder;

import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatementParam;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding.ColumnInfo;
import org.eclipse.dirigible.engine.odata2.sql.clause.SQLWhereClause;
import org.eclipse.dirigible.engine.odata2.sql.clause.SQLWhereClauseVisitor;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.BAD_REQUEST;
import static org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils.evaluateDateTimeExpressions;

public final class SQLUtils {

    private SQLUtils() {
    }


    public static void setParamsOnStatement(final PreparedStatement preparedStatement, List<SQLStatementParam> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            SQLStatementParam param = params.get(i);
            if (param.isTemporalType()) {
                SQLStatementParam.TemporalType type = param.getTemporalType();
                switch (type) {
                    case DATE:
                        preparedStatement.setDate(i + 1, asSQLDate(param.getValue()));
                        break;
                    case TIMESTAMP:
                        preparedStatement.setTimestamp(i + 1, asTimeStamp(param.getValue()));
                        break;
                    case TIME:
                        preparedStatement.setTime(i + 1, asTime(param.getValue()));
                }
            } else {
                Object value = convertToSqlType(param);
                boolean successfullySet = setInteger(preparedStatement, i, value);
                if (!successfullySet) {
                    successfullySet = setLong(preparedStatement, i, value);
                }
                if (!successfullySet) {
                    setObject(preparedStatement, i, value);
                }
            }
        }
    }

    public static boolean setInteger(final PreparedStatement preparedStatement, int i, Object value) throws SQLException {
        try {
            Object checkedValue = (value == null ? 0 : value);
            preparedStatement.setInt(i + 1, Integer.parseInt(checkedValue.toString()));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean setLong(final PreparedStatement preparedStatement, int i, Object value) throws SQLException {
        try {
            Object checkedValue = (value == null ? 0l : value);
            preparedStatement.setLong(i + 1, Long.parseLong(checkedValue.toString()));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void setObject(final PreparedStatement preparedStatement, int i, Object value) throws SQLException {
        preparedStatement.setObject(i + 1, value);
    }

    public static Object asGuid(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return UUID.fromString((String) value);
        } else if (value instanceof UUID) {
            return value;
        } else {
            throw new IllegalArgumentException("Unable to convert object " + value + " of type " + value.getClass() + " to GUID!");
        }
    }

    //TODO extract interface
    public static Object convertToSqlType(final SQLStatementParam param) {
        Object actualValue = param.getValue();
        if (actualValue == null) {
            return null;
        }
        String targetSqlType = param.getSqlType();
        if (targetSqlType == null) { //no explicit conversion needed
            return actualValue;
        } else {//do conversion
            try {
                if ("NUMERIC".equals(targetSqlType)) {
                    return Long.valueOf(String.valueOf(actualValue));
                } else if ("GUID".equalsIgnoreCase(targetSqlType)) {
                    return asGuid(actualValue);
                } else {
                    throw new OData2Exception("Conversion to specified SQL Type " + targetSqlType + " not implemented!", BAD_REQUEST);
                }

            } catch (RuntimeException e) {
                throw new OData2Exception(
                        "Conversion of " + actualValue + " to specified SQL Type " + targetSqlType + " failed!", BAD_REQUEST, e);
            }

        }

    }

    public static Timestamp asTimeStamp(final Calendar calendar) {
        return new Timestamp(calendar.getTime().getTime());
    }

    public static Time asTime(final Calendar calendar) {
        return new Time(calendar.getTime().getTime());
    }

    public static Date asSQLDate(final Calendar calendar) {
        return new Date(calendar.getTime().getTime());
    }

    public static String normalizeSQLExpression(final StringBuilder expression) {
        return normalizeSQLExpression(expression.toString());
    }

    public static String normalizeSQLExpression(final String expression) {
        return expression.replaceAll("  ", " ").trim();
    }

    public static String assertParametersCount(String sql, List<SQLStatementParam> params) {
        long count = sql.chars().filter(ch -> ch == '?').count();
        if (count != params.size()) {
            throw new IllegalStateException("The count of the ? symbols in the generated SQL "
                    + sql + " does not match the existing params " + params + ". Make sure that the count is the same");
        }
        return sql;
    }

    //TODO handle also Grouping Operators () for example /Products?$filter=(Price sub 5) gt 10
    public static SQLWhereClause buildSQLWhereClause(SQLSelectBuilder query, EdmStructuralType targetEntityType,
                                                     final FilterExpression expression) throws ExceptionVisitExpression, ODataApplicationException {
        // Better make use of a visitor pattern
        //TODO the filter might have join tables. Table alias must be removed. We must know in what context we are. 
        SQLWhereClauseVisitor visitor = new SQLWhereClauseVisitor(query, targetEntityType);
        return expression == null ? new SQLWhereClause() : (SQLWhereClause) expression.accept(visitor);
    }

    public static SQLWhereClause whereClauseFromKeyPredicates(SQLSelectBuilder query, EdmStructuralType type,
                                                       final List<KeyPredicate> keyPredicates) throws EdmException {
        StringBuilder whereClause = new StringBuilder();
        List<SQLStatementParam> params = new ArrayList<>();
        if (keyPredicates != null) {
            Iterator<KeyPredicate> it = keyPredicates.iterator();
            while (it.hasNext()) {
                KeyPredicate keyPredicate = it.next();
                Object literal = keyPredicate.getLiteral();

                EdmProperty property = keyPredicate.getProperty();
                if (property.isSimple()) {
                    EdmSimpleType edmSimpleType = (EdmSimpleType) keyPredicate.getProperty().getType();
                    literal = evaluateDateTimeExpressions(literal, edmSimpleType);
                    ColumnInfo info = query.getSQLTableColumnInfo(type, property);
                    whereClause.append(info.getColumnName()).append(" = ?");
                    params.add(SQLWhereClause.param(literal, edmSimpleType, info));
                } else {
                    //TODO what to do with complex properties?
                    throw new IllegalStateException();
                }
                if (it.hasNext()) {
                    whereClause.append(" AND ");
                }
            }
            return new SQLWhereClause(whereClause.toString(), params.toArray(new SQLStatementParam[params.size()]));
        } else {
            return new SQLWhereClause();
        }
    }


    public static String csvInBrackets(List<String> columnValues) {
        return columnValues.stream().collect(Collectors.joining(",", "(", ")"));
    }

    public static String csv(List<String> values) {
        return join(values, ", ");
    }

    public static String join(List<String> values, String delimiter) {
        return String.join(delimiter, values);
    }


    /**
     * Basic validity check for the values. Prevents that someone deletes an entity with an invalid request
     * In short, a composite key makes sense if all elements are not-null (otherwise the non-null element suffices)
     *
     * @param keyValue the value of the key
     * @return if the key value is valid
     */
    public static boolean isValidKeyValue(Object keyValue) {
        return keyValue != null;
    }

    public static boolean isKeyProperty(EdmEntityType target, EdmProperty prop) throws EdmException {
        for (String name : target.getKeyPropertyNames()) {
            if (name.equals(prop.getName())) {
                return true;
            }
        }
        return false;
    }

}
