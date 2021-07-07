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

import static org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils.evaluateDateTimeExpressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding.ColumnInfo;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;

public final class SQLExpressionUtils {

    private SQLExpressionUtils() {
    }

    //TODO handle also Grouping Operators () for example /Products?$filter=(Price sub 5) gt 10
    public static SQLExpressionWhere buildSQLWhereClause(SQLQuery query, EdmStructuralType targetEntityType,
            final FilterExpression expression) throws EdmException, ExceptionVisitExpression, ODataApplicationException {
        // Better make use of a visitor pattern
        //TODO the filter might have join tables. Table alias must be removed. We must know in what context we are. 
        SQLWhereClauseVisitor visitor = new SQLWhereClauseVisitor(query, targetEntityType);
        return expression == null ? new SQLExpressionWhere() : (SQLExpressionWhere) expression.accept(visitor);
    }

    static SQLExpressionWhere whereClauseFromKeyPredicates(SQLQuery query, EdmStructuralType type,
            final List<KeyPredicate> keyPredicates) throws EdmException {
        StringBuilder whereClause = new StringBuilder();
        List<SQLExpressionWhere.Param> params = new ArrayList<SQLExpressionWhere.Param>();
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
                    whereClause.append(info.getColumnName() + " = ?");
                    params.add(SQLExpressionWhere.param(literal, edmSimpleType, info.getSqlType()));
                } else {
                    //TODO what to do with complex properties?
                    throw new IllegalStateException();
                }
                if (it.hasNext()) {
                    whereClause.append(" AND ");
                }
            }
            SQLExpressionWhere where = new SQLExpressionWhere(whereClause.toString(),
                    params.toArray(new SQLExpressionWhere.Param[params.size()]));
            return where;
        } else {
            return new SQLExpressionWhere();
        }
    }
    

	public static String csvInBrackets(List<String> columnValues) {
		return columnValues.stream().collect(Collectors.joining( ",", "(", ")"));
	}
	
	public static String csv(List<String> values) {
		return values.stream().collect(Collectors.joining(","));
	}

}
