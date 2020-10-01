/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.sql.builder.expression;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.INTERNAL_SERVER_ERROR;

import java.util.Iterator;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;
import org.apache.olingo.odata2.api.uri.expression.SortOrder;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;

public final class SQLExpressionOrderBy implements SQLExpression {

    private final OrderByExpression orderByExpression;
    private final SQLQuery query;
    private final EdmEntityType orderByEnitityType;

    public SQLExpressionOrderBy(final SQLQuery query, final EdmEntityType orderByEnitityType, final OrderByExpression orderByExpression) {
        //TODO extract the target enttiy from the orderByExpression
        this.orderByExpression = orderByExpression;
        this.query = query;
        this.orderByEnitityType = orderByEnitityType;
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
     * @param context
     * 
     * @param orderByExpression
     * @return a map of JPA attributes and their sort order
     * @throws ODataJPARuntimeException
     */
    private String parseExpression(SQLContext context) throws EdmException {
        StringBuilder result = new StringBuilder();
        if (!isEmpty()) {
            List<OrderExpression> orderBys = orderByExpression.getOrders();
            String orderByField = null;
            String orderByDirection = null;

            Iterator<OrderExpression> it = orderBys.iterator();
            while (it.hasNext()) {
                OrderExpression orderBy = it.next();
                try {
                    EdmProperty prop = ((EdmProperty) ((PropertyExpression) orderBy.getExpression()).getEdmProperty());
                    if (query.isTransientType(orderByEnitityType, prop)) {
                        //unable to sort with a transient property in the list. This changes the semantic of order by and the result of the select
                        throw new OData2Exception(
                                "Unmapped property " + prop.getName()
                                        + "! Unable to use an order by expresson for properties that are not mapped to the DB.",
                                INTERNAL_SERVER_ERROR);
                    }

                    if (context == null || context.getDatabaseProduct() != null)
                    	orderByField = query.getSQLTableColumn(orderByEnitityType, prop);
                    else
                    	orderByField = query.getSQLTableColumnAlias(orderByEnitityType, prop); // this gives the correct "order by" column name for Open SQL // TODO: add new enum type DatabaseProduct for OpenSQL

                    orderByDirection = (orderBy.getSortOrder() == SortOrder.asc) ? "" : " DESC"; //$NON-NLS-1$
                    result.append(orderByField).append(orderByDirection);
                    if (it.hasNext()) {
                        result.append(", ");
                    }
                } catch (EdmException e) {
                    throw new OData2Exception("Unable to create ORDER BY clause: " + e.getMessage(), INTERNAL_SERVER_ERROR, e);
                }
            }
        }
        return result.toString();
    }
}
