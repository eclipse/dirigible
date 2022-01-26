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

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.api.uri.SelectItem;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatement;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatementParam;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding.ColumnInfo;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.clause.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.eclipse.dirigible.engine.odata2.sql.clause.SQLSelectClause.EvaluationType.*;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.isOrderByEntityInExpand;
public class SQLSelectBuilder extends AbstractQueryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SQLSelectBuilder.class);

    private final Map<String, EdmStructuralType> tableAliasesForEntitiesInQuery;
    private final Set<String> structuralTypesInJoin;
    private final List<SQLJoinClause> joinExpressions = new ArrayList<>();
    private SQLSelectClause selectExpression;
    private SQLOrderByClause orderByClause;
    private boolean serversidePaging;
    private int row = 0;

    public SQLSelectBuilder(final EdmTableBindingProvider tableMappingProvider) {
        super(tableMappingProvider);
        selectExpression = null;
        orderByClause = null;
        tableAliasesForEntitiesInQuery = new TreeMap<>();
        structuralTypesInJoin = new HashSet<>();
    }

    public SQLSelectClause select(final List<SelectItem> selects, final List<ArrayList<NavigationPropertySegment>> expand) {
        selectExpression = new SQLSelectClause(this, selects, expand);
        return selectExpression;
    }

    public SQLSelectClause.SQLSelectBuilder select() {
        //this is the count
        selectExpression = new SQLSelectClause(this);
        return new SQLSelectClause.SQLSelectBuilder(selectExpression);
    }

    public SQLSelectBuilder and(final SQLWhereClause whereClause) {
        getWhereClause().and(whereClause);
        return this;
    }

    public SQLSelectBuilder or(final SQLWhereClause whereClause) {
        getWhereClause().or(whereClause);
        return this;
    }

    public SQLSelectBuilder orderBy(final OrderByExpression orderBy, final EdmEntityType entityType) throws ODataNotImplementedException {
        if (orderBy != null && orderBy.getOrders() != null) {
            for (OrderExpression order : orderBy.getOrders()) {
                switch (order.getExpression().getKind()) {
                    case PROPERTY:
                    case MEMBER:
                        continue;
                    default:
                        LOG.error("OrderBy with non property or member expressions are not implemented yet!");
                        throw new ODataNotImplementedException();
                }
            }
        }
        this.orderByClause = new SQLOrderByClause(this, entityType, orderBy);
        return this;
    }

    public SQLOrderByClause getOrderByClause() {
        return orderByClause;
    }

    public void validateOrderBy(final UriInfo uriInfo)
            throws EdmException, ODataNotImplementedException {
        boolean usingOrderBy = uriInfo.getOrderBy() != null && uriInfo.getOrderBy().getOrders() != null;
        if (usingOrderBy) {
            for (OrderExpression orderExpression : uriInfo.getOrderBy().getOrders()) {
                switch (orderExpression.getExpression().getKind()) {
                    case PROPERTY:
                        continue;
                    case MEMBER:
                        //For a member order by to work we need a corresponding expand (which also means a join).
                        //Otherwise the table in the member order by clause will not be part of the query
                        //E.g. SELECT T0.ID AS "ID_T0" FROM CARS AS T0 ORDER BY T1.FIRSTNAME DESC (we need a join to define T1)
                        if (!isOrderByEntityInExpand(orderExpression, uriInfo)) {
                            throw new OData2Exception("Missing $expand of the entity in the OrderBy clause", HttpStatusCodes.BAD_REQUEST); //no expand, but member order by is not allowed
                        }
                        continue;
                    default:
                        LOG.error("OrderBy is implmented only for properties or member properties!");
                        throw new ODataNotImplementedException();
                }
            }
        }
    }

    public SQLSelectBuilder filter(final EdmEntitySet filterTarget, final FilterExpression filter) throws ODataException {
        //TODO we do not search only filter target table. What if we filter on property that is complex type and is field of the target entity?
        SQLWhereClause where = SQLUtils.buildSQLWhereClause(this, filterTarget.getEntityType(), filter);
        if (!where.isEmpty()) {
           getWhereClause().and(where);
        }
        return this;
    }

    public SQLSelectBuilder filter(final EdmEntitySet filterTarget, final EdmProperty keyProperty, final List<String> idsOfLeadingEntities)  throws ODataException {
        ColumnInfo column = getSQLTableColumnInfo(filterTarget.getEntityType(), keyProperty);
        List<SQLStatementParam> filterParams = new ArrayList<>();
        StringBuilder filterClause = new StringBuilder(column.getColumnName() + " IN (");
        for (String leadingEntityId : idsOfLeadingEntities) {
            filterParams.add(new SQLStatementParam(leadingEntityId, keyProperty, column));
            filterClause.append("?,");
        }
        //delete the last ,
        filterClause.deleteCharAt(filterClause.length() - 1);
        filterClause.append(")");
        SQLWhereClause where = new SQLWhereClause(filterClause.toString(), filterParams);
        if (!where.isEmpty()) {
            getWhereClause().and(where);
        }
        return this;
    }

    public SQLSelectClause getSelectExpression() {
        return selectExpression;
    }

    public SQLJoinClause join(final EdmStructuralType from, final EdmStructuralType to) throws EdmException {
        SQLJoinClause join = new SQLJoinClause(this, from, to);
        if (!join.isEmpty()) {
            //adds the join if it does not exist
            if (!joinExpressions.contains(join)) {
                joinExpressions.add(join);
                structuralTypesInJoin.add(fqn(from));
            }
        }
        return join;
    }


    public SQLJoinClause join(final EdmEntitySet start, final EdmEntitySet target, final List<NavigationSegment> expands)
            throws ODataException {
        SQLJoinClause clause = this.join(start.getEntityType(), target.getEntityType());
        return clause;
    }

    // Do NOT use this method; For Unit testing purposes ONLY
    Iterator<SQLJoinClause> getJoinWhereClauses() {
        return joinExpressions.iterator();
    }

    @Override
    public List<SQLStatementParam> getStatementParams() {
        return getWhereClause().getStatementParams();
    }

    private String evaluateJoins(final SQLContext context) throws ODataException {
        StringBuilder builder = new StringBuilder();
        for (SQLJoinClause join : joinExpressions) {
            if (join.isEmpty()) {
                continue;
            }
            builder.append(join.evaluate(context)).append(" ");
        }
        return builder.toString();
    }

    /**
     * Next row
     *
     * @param resultSet the result set
     * @return true if there is a next row
     * @throws SQLException in case of an error
     */
    public boolean next(final ResultSet resultSet) throws SQLException {
        int top = selectExpression.getTop();
        if (top > 0 && ++row > top)
            return false;
        return resultSet.next();
    }

    /**
     * Set an offset
     *
     * @param resultSet the result set
     * @throws SQLException in case of an error
     */
    public void setOffset(final ResultSet resultSet) throws SQLException {
        if (selectExpression != null && selectExpression.getSkip() >= 1) {
            // observation: next() needs to be invoked to get an entry as expected by relative()
            //    the call sequence is not required by the JDBC specification but 
            //    is working with the currently used jConnect JDBC driver 7.07.x
            //    Also, we saw strange effects when relative() is called with numbers
            //    larger than the fetchSize (specified on the statement;
            //    see AbstractSQLProcessor.createStatement(SQLSelectBuilder, Connection)).
            //    This is mitigated by calling resultSet.next() && resultSet.relative(fetchSize - 1)
            //    in a loop (which requires some extra round trips to the database depending on the
            //    chosen "skip" value).

            int fetchSize = SQLQueryBuilder.DEFAULT_SERVER_PAGING_SIZE;
            int skip = selectExpression.getSkip();

            int alreadySkipped = 0;
            boolean hasMoreResults = true;

            while (hasMoreResults && alreadySkipped < skip) {
                if (alreadySkipped % fetchSize == 0) {
                    hasMoreResults = resultSet.next();
                    alreadySkipped += 1;
                } else {
                    final int relativeSize;
                    if (skip - alreadySkipped < (fetchSize - 1)) {
                        relativeSize = skip - alreadySkipped;
                    } else {
                        relativeSize = fetchSize - 1;
                    }
                    hasMoreResults = resultSet.relative(relativeSize);
                    alreadySkipped += relativeSize;
                }
            }
            row += alreadySkipped;
        }
    }

    /**
     * Build select clause
     *
     * @param context the context
     * @return the clause
     * @throws EdmException   in case of an error
     * @throws ODataException in case of an error
     */
    public String buildSelect(final SQLContext context) throws ODataException {
        //TODO make immutable

        StringBuilder builder = new StringBuilder();
        if (selectExpression == null)
            throw new IllegalStateException("Please initialize the select clause!");
        builder.append("SELECT ");
        String selectPrefix = (String) selectExpression.evaluate(context, SELECT_PREFIX);
        if (!selectPrefix.isEmpty()) {
            builder.append(selectPrefix).append(" ");
        }
        builder.append(selectExpression.evaluate(context, SELECT_COLUMN_LIST));
        builder.append(" FROM ");
        builder.append(selectExpression.evaluate(context, FROM)).append(" ");
        builder.append(evaluateJoins(context));
        if (!getWhereClause().isEmpty()) {
            builder.append(" WHERE ");
            builder.append(getWhereClause().evaluate(context)).append(" ");
        }
        SQLOrderByClause ob = getOrderByClause();
        if (ob != null && !ob.isEmpty()) {
            builder.append("ORDER BY ").append(ob.evaluate(context)).append(" ");
        }
        String selectSuffix = (String) selectExpression.evaluate(context, SELECT_LIMIT);
        if (!selectSuffix.isEmpty()) {
            builder.append(selectSuffix);
        }
        return SQLUtils.normalizeSQLExpression(builder);
    }

    /**
     * Check if server side paging is enabled
     *
     * @return true if it is enabled
     */
    public boolean isServersidePaging() {
        return serversidePaging;
    }

    public SQLSelectBuilder setServersidePaging(final boolean serversidePaging) {
        this.serversidePaging = serversidePaging;
        return this;
    }


    @Override
    public SQLStatement build(SQLContext context) throws ODataException {
        return  new SQLStatement() {
            @Override
            public String sql() throws ODataException {
                return buildSelect(context);
            }

            @Override
            public List<SQLStatementParam> getStatementParams() {
                return SQLSelectBuilder.this.getStatementParams();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
    }
}
