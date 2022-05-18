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

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.uri.expression.*;
import org.apache.olingo.odata2.core.uri.expression.ExpressionParserInternalError;
import org.apache.olingo.odata2.core.uri.expression.OrderByParserImpl;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLClause;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.INTERNAL_SERVER_ERROR;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.isPropertyParameter;

public class SQLOrderByClause implements SQLClause {

    private static final String EMPTY_STRING = "";
    private final OrderByExpression orderByExpression;
    private final SQLSelectBuilder query;
    private final EdmEntityType entityType;
    private final Logger LOG = LoggerFactory.getLogger(SQLOrderByClause.class);

    public SQLOrderByClause(final SQLSelectBuilder query, final EdmEntityType orderByEntityType, final OrderByExpression orderByExpression) {
        this.orderByExpression = orderByExpression;
        this.query = query;
        this.entityType = orderByEntityType;
    }


    @Override
    public String evaluate(final SQLContext context) throws EdmException {
        return isEmpty() ? getDefaultExpression(context) : parseExpression(context, orderByExpression);
    }

    @Override
    public boolean isEmpty() {
        return orderByExpression == null || orderByExpression.getOrders() == null;
    }

    private String getDefaultExpression(SQLContext context)
            throws EdmException {
        List<String> keyPropertyNames = entityType.getKeyPropertyNames();
        if (null == keyPropertyNames || keyPropertyNames.isEmpty()) {
            return EMPTY_STRING;
        }

        String defaultOrderByExpression = String.join(",", keyPropertyNames);
        OrderByParserImpl orderByParser = new OrderByParserImpl(entityType);
        OrderByExpression orderExpression;
        try {
            orderExpression = orderByParser.parseOrderByString(
                    defaultOrderByExpression);
        } catch (ExpressionParserException | ExpressionParserInternalError e) {
            throw new IllegalStateException("Failed to parse default OrderBy expression.", e);
        }

        return parseExpression(context, orderExpression);
    }

    private String parseExpression(SQLContext context, OrderByExpression orderExpression) throws EdmException {
        List<String> orderByClauses = new ArrayList<>();
        List<OrderExpression> orderBys = orderExpression.getOrders();
        for (OrderExpression orderBy : orderBys) {
            orderByClauses.add(orderByClause(context, orderBy));
        }
        return SQLUtils.csv(orderByClauses);
    }

    protected String orderByClause(SQLContext context, OrderExpression orderBy) throws EdmException {
        CommonExpression expression = orderBy.getExpression();
        EdmStructuralType entityType;
        EdmProperty prop;
        StringBuilder orderByClause = new StringBuilder();

        if (expression instanceof MemberExpression) {
            MemberExpression memberExpression = (MemberExpression) expression;
            CommonExpression pathExpression = memberExpression.getPath();
            entityType = (EdmStructuralType) pathExpression.getEdmType();
            PropertyExpression propertyExpression = (PropertyExpression) memberExpression.getProperty();
            prop = (EdmProperty) propertyExpression.getEdmProperty();

        } else if (expression instanceof PropertyExpression) {
            PropertyExpression propertyExpression = (PropertyExpression) expression;
            prop = (EdmProperty) propertyExpression.getEdmProperty();
            entityType = this.entityType;
        } else {
            throw new OData2Exception("Not Implemented", INTERNAL_SERVER_ERROR);
        }
        if (query.isTransientType(entityType, prop)) {
            // Unable to sort with a transient property in the list. This changes the semantic of order by and the result of the select
            LOG.error("Unmapped property {}! Unable to use an order by expression for properties that are not mapped to the DB.",
                    prop.getName());
            throw new OData2Exception(INTERNAL_SERVER_ERROR);
        }

        if ((context == null || context.getDatabaseProduct() != null)) {
            if (isPropertyParameter(prop, query, entityType)) {
                orderByClause.append(query.getSQLTableColumnAlias(entityType, prop));
            } else {
                orderByClause.append(query.getSQLTableColumn(entityType, prop));
            }
        } else {
            orderByClause.append(query.getSQLTableColumnAlias(entityType, prop)); // This gives the correct "order by" column name for Open SQL
        }
        orderByClause.append(" ").append(orderBy.getSortOrder() == SortOrder.asc ? "ASC" : "DESC");
        return orderByClause.toString();
    }
}
