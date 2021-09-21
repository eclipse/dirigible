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
package org.eclipse.dirigible.engine.odata2.sql.builder;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.api.uri.SelectItem;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.apache.olingo.odata2.core.edm.EdmDateTime;
import org.apache.olingo.odata2.core.edm.EdmGuid;
import org.apache.olingo.odata2.core.edm.EdmTime;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding.ColumnInfo;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.*;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionSelect.SQLSelectBuilder;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionWhere.Param;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionWhere.TemporalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.BAD_REQUEST;
import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.INTERNAL_SERVER_ERROR;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType.*;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.isOrderByEntityInExpand;

public final class SQLQuery {

    private static final Logger LOG = LoggerFactory.getLogger(SQLQuery.class);

    private final Map<String, EdmStructuralType> tableAliasesForEntitiesInQuery;
    private final Set<String> structuralTypesInJoin;

    private final EdmTableBindingProvider tableMappingProvider;
    private SQLExpressionWhere whereExpression;
    private SQLExpressionSelect selectExpression;
    private SQLExpressionInsert insertExpression;
    private SQLExpressionDelete deleteExpression;
    private SQLExpressionUpdate updateExpression;
    private final List<SQLExpressionJoin> joinExpressions = new ArrayList<>();
    private SQLExpressionOrderBy orderByExpressions;
    private boolean serversidePaging;
    private int row = 0;

    public SQLQuery(final EdmTableBindingProvider tableMappingProvider) {
        this.tableMappingProvider = tableMappingProvider;
        selectExpression = null;
        orderByExpressions = null;
        whereExpression = new SQLExpressionWhere();
        tableAliasesForEntitiesInQuery = new TreeMap<>();
        structuralTypesInJoin = new HashSet<>();
    }

    public SQLExpressionSelect select(final List<SelectItem> selects, final List<ArrayList<NavigationPropertySegment>> expand) {
        selectExpression = new SQLExpressionSelect(this, selects, expand);
        return selectExpression;
    }

    public SQLSelectBuilder select() {
        //this is the count
        selectExpression = new SQLExpressionSelect(this);
        return new SQLSelectBuilder(selectExpression);
    }

    public SQLQuery and(final SQLExpressionWhere whereClause) {
        this.whereExpression.and(whereClause);
        return this;
    }

    public SQLQuery or(final SQLExpressionWhere whereClause) {
        this.whereExpression.or(whereClause);
        return this;
    }

    public SQLQuery orderBy(final OrderByExpression orderBy, final EdmEntityType entityType) throws ODataNotImplementedException {
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
        this.orderByExpressions = new SQLExpressionOrderBy(this, entityType, orderBy);
        return this;
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
                        throw new OData2Exception("Missing $expand of the entity in the OrderBy expression", HttpStatusCodes.BAD_REQUEST); //no expand, but member order by is not allowed
                    }
                    continue;
                default:
                    LOG.error("OrderBy is implmented only for properties or member properties!");
                    throw new ODataNotImplementedException();
                }
            }
        }
    }

    public SQLQuery filter(final EdmEntitySet filterTarget, final FilterExpression filter) throws ODataException {
        //TODO we do not search only filter target table. What if we filter on property that is complex type and is field of the target entity?
        SQLExpressionWhere where = SQLExpressionUtils.buildSQLWhereClause(this, filterTarget.getEntityType(), filter);
        if (!where.isEmpty()) {
            whereExpression.and(where);
        }
        return this;
    }

    public SQLQuery clearFilter(final EdmEntitySet filterTarget) {
        whereExpression = new SQLExpressionWhere();
        return this;
    }

    public SQLQuery filter(final EdmEntitySet filterTarget, final EdmProperty keyProperty, final List<String> idsOfLeadingEntities)
            throws ODataException {
        //String column = getSQLTableColumn(filterTarget.getEntityType(), keyProperty);
        ColumnInfo column = getSQLTableColumnInfo(filterTarget.getEntityType(), keyProperty);
        List<Param> params = new ArrayList<>();
        StringBuilder query = new StringBuilder(column.getColumnName() + " IN (");
        for (String leadingEntityId : idsOfLeadingEntities) {
            params.add(new Param(leadingEntityId, column.getSqlType()));
            query.append("?,");
        }
        //delete the last ,
        query.deleteCharAt(query.length() - 1);
        query.append(")");
        SQLExpressionWhere where = new SQLExpressionWhere(query.toString(), params);
        if (!where.isEmpty()) {
            whereExpression.and(where);
        }
        return this;
    }

    public String getWhereClause() {
        return whereExpression.getWhereClause();
    }

    public SQLExpressionWhere getWhereExpression() {
        return whereExpression;
    }

    public SQLExpressionSelect getSelectExpression() {
        return selectExpression;
    }

    public SQLExpressionInsert getInsertExpression() {
        return insertExpression;
    }

    public SQLExpressionDelete getDeleteExpression() {
        return deleteExpression;
    }

    public SQLExpressionUpdate getUpdateExpression() {
        return updateExpression;
    }

    public String getSQLTableName(final EdmStructuralType target) {
        EdmTableBinding mapping = tableMappingProvider.getEdmTableBinding(target);
        return mapping.getTableName();
    }

    public List<String> getSQLJoinTableName(final EdmStructuralType from, final EdmStructuralType to) throws EdmException {
        if (tableMappingProvider.getEdmTableBinding(from).hasJoinColumnTo(to))
            return tableMappingProvider.getEdmTableBinding(from).getJoinColumnTo(to);
        throw new IllegalArgumentException("No join column definition found from type " + from + " to type " + to);
    }

    public String getSQLTablePrimaryKey(final EdmStructuralType type) throws EdmException {
        return tableMappingProvider.getEdmTableBinding(type).getPrimaryKey();
    }

    public String getSQLTableColumn(final EdmStructuralType targetEnitityType, final EdmProperty p) throws EdmException {
        if (p.isSimple()) {
            boolean caseSensitive = Boolean.parseBoolean(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
            if (caseSensitive) {
                return "\"" + getSQLTableAlias(targetEnitityType) + "\".\"" + tableMappingProvider.getEdmTableBinding(targetEnitityType).getColumnName(p) + "\"";
            } else {
                return getSQLTableAlias(targetEnitityType) + "." + tableMappingProvider.getEdmTableBinding(targetEnitityType).getColumnName(p);
            }
        } else {
            throw new IllegalArgumentException("Unable to get the table column name of complex property " + p);
        }
    }

    public String getSQLTableColumnNoAlias(final EdmStructuralType targetEnitityType, final EdmProperty p) {
        if (p.isSimple()) {
            boolean caseSensitive = Boolean.parseBoolean(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
            if (caseSensitive) {
                return "\"" + tableMappingProvider.getEdmTableBinding(targetEnitityType).getColumnName(p) + "\"";
            } else {
                return tableMappingProvider.getEdmTableBinding(targetEnitityType).getColumnName(p);
            }
        } else {
            throw new IllegalArgumentException("Unable to get the table column name of complex property " + p);
        }
    }

    //TODO use me
    private String fixDatabaseNamesCase(String column) {
        boolean caseSensitive = Boolean.parseBoolean(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
        return caseSensitive ? "\"" + column + "\"" : column;
    }


    public List<String> getSQLJoinColumnNoAlias(final EdmStructuralType targetEnitityType, final EdmNavigationProperty p) throws EdmException {
        List<String> joinColums = tableMappingProvider.getEdmTableBinding(targetEnitityType).getJoinColumnTo((EdmStructuralType) p.getType());
        return joinColums.stream().map(c -> fixDatabaseNamesCase(c)).collect(Collectors.toList());
    }

    public ColumnInfo getSQLTableColumnInfo(final EdmStructuralType targetEnitityType, final EdmProperty p) throws EdmException {
        if (p.isSimple()) {
            ColumnInfo info = tableMappingProvider.getEdmTableBinding((targetEnitityType)).getColumnInfo(p);
            boolean caseSensitive = Boolean.parseBoolean(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
            if (caseSensitive) {
                return new ColumnInfo("\"" + getSQLTableAlias(targetEnitityType) + "\".\"" + info.getColumnName() + "\"", info.getSqlType());
                //return getSQLTableAlias(targetEnitityType) + "." + tableMappingProvider.getTableMapping(targetEnitityType).getColumnName(p);
            } else {
                return new ColumnInfo(getSQLTableAlias(targetEnitityType) + "." + info.getColumnName(), info.getSqlType());
                //return getSQLTableAlias(targetEnitityType) + "." + tableMappingProvider.getTableMapping(targetEnitityType).getColumnName(p);
            }
        } else {
            throw new IllegalArgumentException("Unable to get the table column name of complex property " + p);
        }
    }

    /**
     * Use the table column alias to read result set
     * 
     * @param targetEnitityType the target entity type
     * @param property the property
     * @return the alias
     * @throws EdmException in case of an error
     */
    public String getSQLTableColumnAlias(final EdmStructuralType targetEnitityType, final EdmProperty property) throws EdmException {
        if (property.isSimple())
            return tableMappingProvider.getEdmTableBinding(targetEnitityType).getColumnName(property) + "_" + getSQLTableAlias(targetEnitityType);
        else
            throw new IllegalArgumentException("Unable to get the table column name of complex property " + property);
    }

    /**
     * Check whether it is transient type
     * 
     * @param targetEnitityType the target entity type
     * @param property the property
     * @return true if it is transient and false otherwise
     */
    public boolean isTransientType(final EdmStructuralType targetEnitityType, final EdmProperty property) {
        return !tableMappingProvider.getEdmTableBinding(targetEnitityType).isPropertyMapped(property);
    }

    /**
     * Generate join expression
     * 
     * @param from from segment
     * @param to to segment
     * @return the generated expression
     * @throws EdmException in case of an error
     */
    public SQLExpressionJoin join(final EdmStructuralType from, final EdmStructuralType to) throws EdmException {
        SQLExpressionJoin join = new SQLExpressionJoin(this, from, to);
        if (!join.isEmpty()) {
            //adds the join if it does not exist
            if (!joinExpressions.contains(join)) {
                joinExpressions.add(join);
                structuralTypesInJoin.add(fqn(from));
            }
        }
        return join;
    }

    /**
     * Generate join expression
     * 
     * @param start start segment
     * @param target target segment
     * @param expands expands segment
     * @return the generated expression
     * @throws ODataException in case of an error
     */
    public SQLExpressionJoin join(final EdmEntitySet start, final EdmEntitySet target, final List<NavigationSegment> expands)
            throws ODataException {
        return this.join(start.getEntityType(), target.getEntityType());
    }

    // Do NOT use this method; For Unit testing purposes ONLY
    Iterator<SQLExpressionJoin> getJoinWhereClauses() {
        return joinExpressions.iterator();
    }

    List<SQLExpressionWhere.Param> getParams() {
        return whereExpression.getParams();
    }

    private String evaluateJoins(final SQLContext context) throws ODataException {
        StringBuilder builder = new StringBuilder();
        for (SQLExpressionJoin join : joinExpressions) {
            if (join.isEmpty()) {
                continue;
            }
            builder.append(join.evaluate(context, JOIN)).append(" ");
        }
        return builder.toString();
    }

    // This Method is for internal use ONLY !!! Do NEVER use it !!!
    // It will be hidden in future without further mitigation
    // TODO Refactor this method to private area
    public String getSQLTableAlias(final EdmType type) {
        if (type instanceof EdmStructuralType)
            return getTableAliasForType((EdmStructuralType) type);
        else
            throw new IllegalArgumentException("Mapping of types other than EdmEntityType and EdmComplexType is not supported!");
    }

    private String getTableAliasForType(final EdmStructuralType st) {
        Collection<String> keys = tableAliasesForEntitiesInQuery.keySet();
        try {
            for (String key : keys) {
                EdmStructuralType type = tableAliasesForEntitiesInQuery.get(key);
                if (fqn(type).equals(fqn(st)))
                    return key;
            }
            return grantTableAliasForStructuralTypeInQuery(st);
        } catch (Exception e) {
            throw new OData2Exception("No mapping has been defined for type " + fqn(st), INTERNAL_SERVER_ERROR);
        }
    }

    // This Method is for internal use ONLY !!! Do NEVER use it !!!
    // It will be hidden in future without further mitigation
    // TODO Refactor this method to private area
    /**
     * Get table aliases
     * 
     * @return the aliases
     */
    public Iterator<String> getTablesAliasesForEntitiesInQuery() {
        return tableAliasesForEntitiesInQuery.keySet().iterator();
    }

    // This Method is for internal use ONLY !!! Do NEVER use it !!!
    // It will be hidden in future without further mitigation
    // TODO Refactor this method to private area
    /**
     * Get entity for alias
     * @param tableAlias the table alias
     * @return the type
     */
    public EdmStructuralType getEntityInQueryForAlias(final String tableAlias) {
        return tableAliasesForEntitiesInQuery.get(tableAlias);
    }

    // This Method is for internal use ONLY !!! Do NEVER use it !!!
    // It will be hidden in future without further mitigation
    // TODO Refactor this method to private area
    /**
     * Get table alias for structural type
     * @param entity the entity
     * @return the alias
     */
    public String grantTableAliasForStructuralTypeInQuery(final EdmStructuralType entity) {
        try {
            Collection<EdmStructuralType> targets = tableAliasesForEntitiesInQuery.values();
            for (EdmStructuralType type : targets) {
                if (fqn(type).equals(fqn(entity)))
                    // Alias is already contained in the map
                    return getTableAliasForType(type);
            }
            String alias = "T" + tableAliasesForEntitiesInQuery.size();
            LOG.debug("Grant Alias '" + alias + "' for " + entity.getName());
            // Add alias to map
            tableAliasesForEntitiesInQuery.put("T" + tableAliasesForEntitiesInQuery.size(), entity);
            return alias;
        } catch (EdmException e) {
            throw new OData2Exception(INTERNAL_SERVER_ERROR, e);
        }
    }

    public void setParamsOnStatement(final PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < getParams().size(); i++) {
            Param param = getParams().get(i);
            if (param.isTemporalType()) {
                TemporalType type = param.getTemporalType();
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

    private boolean setInteger(final PreparedStatement preparedStatement, int i, Object value) throws SQLException {
        try {
            preparedStatement.setInt(i + 1, Integer.valueOf(value.toString()));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean setLong(final PreparedStatement preparedStatement, int i, Object value) throws SQLException {
        try {
            preparedStatement.setLong(i + 1, Long.valueOf(value.toString()));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void setObject(final PreparedStatement preparedStatement, int i, Object value) throws SQLException {
        preparedStatement.setObject(i + 1, value);
    }

    /**
     * Setter for values on statement
     * 
     * @param preparedStatement the statement
     * @throws SQLException in case of an error
     * @throws EdmException in case of an error
     */
    public void setValuesOnStatement(final PreparedStatement preparedStatement) throws SQLException, EdmException {
        List<Object> values = getInsertExpression().getColumnData();
        List<EdmProperty> columnProperties = getInsertExpression().getColumnProperties();

        setParamsOnPreparedStatement(preparedStatement, values, columnProperties);
    }

    private Object asGuid(Object value) {
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

    public void setKeysOnStatement(final PreparedStatement preparedStatement) throws SQLException {
        Map<String, Object> keys = getDeleteExpression().getKeys();
        int i = 0;
        for (Map.Entry<String, Object> key : keys.entrySet()) {
            preparedStatement.setObject(++i, key.getValue());
        }
    }

    public void setValuesAndKeysOnStatement(final PreparedStatement preparedStatement) throws SQLException, EdmException {
        SQLExpressionUpdate updateExpression = getUpdateExpression();
        List<Object> values = updateExpression.getQueryData();
        List<EdmProperty> columnProperties = updateExpression.getQueryOdataProperties();
        setParamsOnPreparedStatement(preparedStatement, values, columnProperties);
    }

    private void setParamsOnPreparedStatement(PreparedStatement preparedStatement, List<Object> values, List<EdmProperty> columnProperties) throws EdmException, SQLException {
        for (int i = 1; i <= values.size(); i++) {
            Object value = values.get(i - 1);
            EdmProperty property = columnProperties.get(i - 1);
            EdmType type = property.getType();
            if (type instanceof EdmTime) {
                preparedStatement.setTime(i, asTime((Calendar) value));
            } else if (type instanceof EdmGuid) {
                preparedStatement.setObject(i, asGuid(value));
            } else if (type instanceof EdmDateTime) {
                preparedStatement.setDate(i, asSQLDate((Calendar) value));
            } else {
                preparedStatement.setObject(i, value);
            }
        }
    }

    private Object convertToSqlType(final Param parameter) {
        Object actualValue = parameter.getValue();
        if (actualValue == null) {
            return actualValue;
        }
        String targetSqlType = parameter.getSqlType();
        if (targetSqlType == null) {
            return actualValue;
        } else {//do conversion 
            try {
                if ("NUMERIC".equals(targetSqlType)) {
                    return Long.valueOf(String.valueOf(actualValue));
                } else {
                    throw new OData2Exception("Conversion to specified SQL Type " + targetSqlType + " not implemented!", BAD_REQUEST);
                }

            } catch (RuntimeException e) {
                throw new OData2Exception(
                        "Conversion of " + actualValue.toString() + " to specified SQL Type " + targetSqlType + " failed!", BAD_REQUEST, e);
            }

        }

    }

    private Timestamp asTimeStamp(final Calendar calendar) {
        return new Timestamp(calendar.getTime().getTime());
    }

    private Time asTime(final Calendar calendar) {
        return new Time(calendar.getTime().getTime());
    }

    private Date asSQLDate(final Calendar calendar) {
        return new Date(calendar.getTime().getTime());
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
            //    see AbstractSQLProcessor.createStatement(SQLQuery, Connection)).
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
     * Build select expression
     * 
     * @param context the context
     * @return the expression
     * @throws EdmException in case of an error
     * @throws ODataException in case of an error
     */
    public String buildSelect(final SQLContext context) throws EdmException, ODataException {
        StringBuilder builder = new StringBuilder();
        if (selectExpression == null)
            throw new IllegalStateException("Please initialize the select clause!");
        builder.append("SELECT ");
        String selectPrefix = selectExpression.evaluate(context, SELECT_PREFIX);
        if (!selectPrefix.isEmpty()) {
            builder.append(selectPrefix).append(" ");
        }
        builder.append(selectExpression.evaluate(context, SELECT_COLUMN_LIST));
        builder.append(" FROM ");
        builder.append(selectExpression.evaluate(context, FROM)).append(" ");
        builder.append(evaluateJoins(context));
        if (!whereExpression.isEmpty()) {
            builder.append(" WHERE ");
            builder.append(whereExpression.evaluate(context, WHERE)).append(" ");
        }
        if (orderByExpressions != null && !orderByExpressions.isEmpty()) {
            builder.append("ORDER BY ").append(orderByExpressions.evaluate(context, ORDERBY)).append(" ");
        }
        String selectSuffix = selectExpression.evaluate(context, SELECT_SUFFIX);
        if (!selectSuffix.isEmpty()) {
            builder.append(selectSuffix);
        }
        return normalizedString(context, builder);
    }

    private String normalizedString(final SQLContext context, final StringBuilder s) throws ODataException {
        String sql = s.toString().replaceAll("  ", " ").trim();
        if (context != null && context.getOdataContext() != null) {
            LOG.debug("SQL for URL {}: {}", context.getOdataContext().getPathInfo(), sql);
        }
        return sql;
    }

    /**
     * Check if server side paging is enabled
     * @return true if it is enabled
     */
    public boolean isServersidePaging() {
        return serversidePaging;
    }

    void setServersidePaging(final boolean serversidePaging) {
        this.serversidePaging = serversidePaging;
    }

    public class EdmTarget {
        private EdmStructuralType edmTargetType;
        private EdmProperty edmProperty;
        private EdmNavigationProperty edmNavigationProperty;

        public EdmTarget(final EdmStructuralType edmTargetType, final EdmProperty edmProperty) {
            this.edmTargetType = edmTargetType;
            this.edmProperty = edmProperty;
            this.edmNavigationProperty = null;
        }

        public EdmTarget(final EdmStructuralType edmTargetType, EdmNavigationProperty edmNavigationProperty, final EdmProperty edmProperty) {
            this.edmTargetType = edmTargetType;
            this.edmProperty = edmProperty;
            this.edmNavigationProperty = edmNavigationProperty;
        }

        public EdmNavigationProperty getEdmNavigationProperty() {
            return edmNavigationProperty;
        }

        public boolean isInlineTarget() {
            return edmNavigationProperty != null;
        }

        public EdmStructuralType getEdmTargetType() {
            return edmTargetType;
        }

        public void setEdmTargetType(final EdmStructuralType edmTargetType) {
            this.edmTargetType = edmTargetType;
        }

        public EdmProperty getEdmProperty() {
            return edmProperty;
        }

        public void setEdmProperty(final EdmProperty edmProperty) {
            this.edmProperty = edmProperty;
        }

        @Override
        public String toString() {
            try {
                if (isInlineTarget()) {
                    return "EdmTarget [" + edmTargetType.getName() + "." + edmNavigationProperty.getName() + edmProperty.getName() + "]";
                } else {
                    return "EdmTarget [" + edmTargetType.getName() + "." + edmProperty.getName() + "]";
                }
            } catch (EdmException e) {
                throw new OData2Exception(HttpStatusCodes.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public SQLExpressionInsert insert(EdmEntityType target, ODataEntry entry) throws ODataException {
        insertExpression = new SQLExpressionInsert(this);
        insertExpression.into(target, entry);
        return insertExpression;
    }

    public String buildInsert(final SQLContext context) throws ODataException {
        StringBuilder builder = new StringBuilder();
        if (insertExpression == null)
            throw new IllegalStateException("Please initialize the insert clause!");
        builder.append("INSERT ");
        builder.append(" INTO ");
        builder.append(insertExpression.evaluate(context, INTO));
        builder.append(" VALUES ");
        builder.append(insertExpression.evaluate(context, VALUES));

        return normalizedString(context, builder);
    }

    public SQLExpressionDelete delete() {
        deleteExpression = new SQLExpressionDelete(this);
        return deleteExpression;
    }

    public SQLExpressionDelete keys(Map<String, Object> keys) throws ODataException {
        deleteExpression.keys(keys);
        return deleteExpression;
    }

    public String buildDelete(SQLContext context) throws ODataException {
        StringBuilder builder = new StringBuilder();
        if (deleteExpression == null)
            throw new IllegalStateException("Please initialize the delete clause!");
        builder.append("DELETE ");
        builder.append(" FROM ");
        builder.append(deleteExpression.evaluate(context, FROM));
        builder.append(" WHERE ");
        builder.append(deleteExpression.evaluate(context, KEYS));

        return normalizedString(context, builder);
    }

    public SQLExpressionUpdate update(EdmEntityType target, ODataEntry entry, Map<String, Object> uriKeys) {
        updateExpression = new SQLExpressionUpdate(this, target, entry, uriKeys);
        return updateExpression;
    }

    public String buildUpdate(SQLContext context) throws ODataException {
        StringBuilder builder = new StringBuilder();
        if (updateExpression == null)
            throw new IllegalStateException("Please initialize the delete clause!");
        builder.append("UPDATE ");
        builder.append(updateExpression.evaluate(context, TABLE));

        return normalizedString(context, builder);
    }

}
