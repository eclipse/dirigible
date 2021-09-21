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

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.INTERNAL_SERVER_ERROR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.uri.expression.CommonExpression;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;
import org.apache.olingo.odata2.api.uri.expression.SortOrder;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SQLExpressionOrderBy implements SQLExpression {

    private final OrderByExpression orderByExpression;
    private final SQLQuery query;
    private final EdmEntityType entityType;
    private final Logger LOG = LoggerFactory.getLogger(SQLExpressionOrderBy.class);

    public SQLExpressionOrderBy(final SQLQuery query, final EdmEntityType orderByEnitityType, final OrderByExpression orderByExpression) {
        this.orderByExpression = orderByExpression;
        this.query = query;
        this.entityType  = orderByEnitityType;
    }

    @Override
    public String evaluate(final SQLContext context, final ExpressionType type) throws EdmException {
        switch (type) {
        case ORDERBY:
            //no difference in the DB products so far
            //TODO fix this
            return parseExpression(context);
        default:
            return "";
        }
    }

    @Override
    public boolean isEmpty() throws EdmException {
        if (orderByExpression != null && orderByExpression.getOrders() != null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method parses the order by condition in the query.
     * 
     * @param context
     * 
     * @param orderByExpression
     * @return a map of JPA attributes and their sort order
     * @throws ODataJPARuntimeException
     */
    private String parseExpression(SQLContext context) throws EdmException {
        List<String> orderByClauses = new ArrayList<>();
        if (!isEmpty()) {
            List<OrderExpression> orderBys = orderByExpression.getOrders();
            Iterator<OrderExpression> it = orderBys.iterator();
            while (it.hasNext()) {
                OrderExpression orderBy = it.next();
                orderByClauses.add(orderByClause(context, orderBy));
            }
        }
        return SQLExpressionUtils.csv(orderByClauses);
    }

    protected String orderByClause(SQLContext context, OrderExpression orderBy) throws EdmException {
        CommonExpression expression = orderBy.getExpression();
        EdmStructuralType entityType = null;
        EdmProperty prop = null;
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
            //unable to sort with a transient property in the list. This changes the semantic of order by and the result of the select
            LOG.error("Unmapped property {}! Unable to use an order by expresson for properties that are not mapped to the DB.",
                    prop.getName());
            throw new OData2Exception(INTERNAL_SERVER_ERROR);
        }

        if (context == null || context.getDatabaseProduct() != null) {
            orderByClause.append(query.getSQLTableColumn(entityType, prop));
        } else {
            orderByClause.append(query.getSQLTableColumnAlias(entityType, prop)); // this gives the correct "order by" column name for Open SQL // TODO: add new enum type DatabaseProduct for OpenSQL
        }
        orderByClause.append(" ").append(orderBy.getSortOrder() == SortOrder.asc ? "ASC" : "DESC");
        return orderByClause.toString();
    }
}
