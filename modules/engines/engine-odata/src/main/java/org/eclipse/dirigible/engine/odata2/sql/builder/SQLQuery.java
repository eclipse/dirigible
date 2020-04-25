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
package org.eclipse.dirigible.engine.odata2.sql.builder;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.BAD_REQUEST;
import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.INTERNAL_SERVER_ERROR;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType.FROM;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType.JOIN;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType.ORDERBY;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType.SELECT_COLUMN_LIST;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType.SELECT_PREFIX;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType.SELECT_SUFFIX;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpression.ExpressionType.WHERE;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.api.uri.SelectItem;
import org.apache.olingo.odata2.api.uri.expression.ExpressionKind;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding.ColumnInfo;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionJoin;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionOrderBy;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionSelect;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionSelect.SQLSelectBuilder;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionWhere;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionWhere.Param;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionWhere.TemporalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SQLQuery {
    private static final Logger LOG = LoggerFactory.getLogger(SQLQuery.class);

    private final Map<String, EdmStructuralType> tableAliasesForEntitiesInQuery;
    private final Set<String> structuralTypesInJoin;

    private final EdmTableBindingProvider tableMappingProvider;
    private SQLExpressionWhere whereExpression;
    private SQLExpressionSelect selectExpression;
    private final List<SQLExpressionJoin> joinExpressions = new ArrayList<SQLExpressionJoin>();
    private SQLExpressionOrderBy orderByExpressions;
    private boolean serversidePaging;
    private int row = 0;

    public SQLQuery(final EdmTableBindingProvider tableMappingProvider) {
        this.tableMappingProvider = tableMappingProvider;
        selectExpression = null;
        orderByExpressions = null;
        whereExpression = new SQLExpressionWhere();
        tableAliasesForEntitiesInQuery = new TreeMap<String, EdmStructuralType>();
        structuralTypesInJoin = new HashSet<String>();
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

    public SQLQuery orderBy(final OrderByExpression orderBy, final EdmEntityType entityType)
            throws EdmException, ODataNotImplementedException {
        if (orderBy != null && orderBy.getOrders() != null) {
            for (OrderExpression order : orderBy.getOrders()) {
                if (ExpressionKind.PROPERTY != order.getExpression().getKind()) {
                    LOG.error("OrderBy with non property expressions are not implemented yet!");
                    throw new ODataNotImplementedException();
                }
            }
        }
        this.orderByExpressions = new SQLExpressionOrderBy(this, entityType, orderBy);
        return this;
    }

    public SQLQuery filter(final EdmEntitySet filterTarget, final FilterExpression filter) throws ODataException {
        //TODO we do not search only filter target table. What if we filter on property that is complex type and is field of the target entity?
        SQLExpressionWhere where = SQLExpressionUtils.buildSQLWhereClause(this, filterTarget.getEntityType(), filter);
        if (!where.isEmpty()) {
            whereExpression.and(where);
        }
        return this;
    }

    public SQLQuery clearFilter(final EdmEntitySet filterTarget) throws ODataException {
        whereExpression = new SQLExpressionWhere();
        return this;
    }

    public SQLQuery filter(final EdmEntitySet filterTarget, final EdmProperty keyProperty, final List<String> idsOfLeadingEntities)
            throws ODataException {
        //String column = getSQLTableColumn(filterTarget.getEntityType(), keyProperty);
        ColumnInfo column = getSQLTableColumnInfo(filterTarget.getEntityType(), keyProperty);
        List<Param> params = new ArrayList<Param>();
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

    public String getSQLTableName(final EdmStructuralType target) {
        EdmTableBinding mapping = tableMappingProvider.getEdmTableBinding(target);
        return mapping.getTableName();
    }

    public String getSQLJoinTableName(final EdmStructuralType from, final EdmStructuralType to) throws EdmException {
        if (tableMappingProvider.getEdmTableBinding(from).hasJoinColumnTo(to))
            return tableMappingProvider.getEdmTableBinding(from).getJoinColumnTo(to);
        throw new IllegalArgumentException("No join coumn definition found from type " + from + " to type " + to);
    }

    public String getSQLTablePrimaryKey(final EdmStructuralType type) throws EdmException {
        return tableMappingProvider.getEdmTableBinding(type).getPrimaryKey();
    }

    public String getSQLTableColumn(final EdmStructuralType targetEnitityType, final EdmProperty p) throws EdmException {
        if (p.isSimple()) {
        	boolean caseSensitive = Boolean.parseBoolean(Configuration.get(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false"));
        	if (caseSensitive) {
        		return "\"" + getSQLTableAlias(targetEnitityType) + "\".\"" + tableMappingProvider.getEdmTableBinding(targetEnitityType).getColumnName(p) + "\"";
        	} else {
        		return getSQLTableAlias(targetEnitityType) + "." + tableMappingProvider.getEdmTableBinding(targetEnitityType).getColumnName(p);
        	}
        } else {
            throw new IllegalArgumentException("Unable to get the table column name of complex property " + p);
        }
    }

    public ColumnInfo getSQLTableColumnInfo(final EdmStructuralType targetEnitityType, final EdmProperty p) throws EdmException {
        if (p.isSimple()) {
        	ColumnInfo info = tableMappingProvider.getEdmTableBinding((targetEnitityType)).getColumnInfo(p);
        	boolean caseSensitive = Boolean.parseBoolean(Configuration.get(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false"));
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
     */
    public String getSQLTableColumnAlias(final EdmStructuralType targetEnitityType, final EdmProperty p) throws EdmException {
        if (p.isSimple())
            return tableMappingProvider.getEdmTableBinding(targetEnitityType).getColumnName(p) + "_" + getSQLTableAlias(targetEnitityType);
        else
            throw new IllegalArgumentException("Unable to get the table column name of complex property " + p);
    }

    public boolean isTransientType(final EdmStructuralType targetEnitityType, final EdmProperty p) {
        if (tableMappingProvider.getEdmTableBinding(targetEnitityType).isPropertyMapped(p))
            return false;
        return true;
    }

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

    private String evaluateJoins(final SQLContext context) throws EdmException, ODataException {
        StringBuilder builder = new StringBuilder();
        for (Iterator<SQLExpressionJoin> it = joinExpressions.iterator(); it.hasNext();) {
            SQLExpressionJoin join = it.next();
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
    public Iterator<String> getTablesAliasesForEntitiesInQuery() {
        return tableAliasesForEntitiesInQuery.keySet().iterator();
    }

    // This Method is for internal use ONLY !!! Do NEVER use it !!!
    // It will be hidden in future without further mitigation
    // TODO Refactor this method to private area
    public EdmStructuralType getEntityInQueryForAlias(final String tableAlias) {
        return tableAliasesForEntitiesInQuery.get(tableAlias);
    }

    // This Method is for internal use ONLY !!! Do NEVER use it !!!
    // It will be hidden in future without further mitigation
    // TODO Refactor this method to private area
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

    public void setParamsOnStatement(final PreparedStatement sqlQuery) throws SQLException {
        for (int i = 0; i < getParams().size(); i++) {
            Param param = getParams().get(i);
            if (param.isTemporalType()) {
                TemporalType type = param.getTemporalType();
                switch (type) {
                case DATE:
                    sqlQuery.setDate(i + 1, asSQLDate((Calendar) param.getValue()));
                    break;
                case TIMESTAMP:
                    sqlQuery.setTimestamp(i + 1, asTimeStamp((Calendar) param.getValue()));
                    break;
                case TIME:
                    sqlQuery.setTime(i + 1, asTime((Calendar) param.getValue()));
                }

            } else {
                Object value = convertToSqlType(param);
                sqlQuery.setObject(i + 1, value);
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
                    return new Long(String.valueOf(actualValue));
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

    public boolean next(final ResultSet rs) throws SQLException {
    	int top = selectExpression.getTop();
    	if (top > 0 && ++row > top)
    		return false;
    	return rs.next();
    }

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

    public boolean isServersidePaging() {
        return serversidePaging;
    }

    void setServersidePaging(final boolean serversidePaging) {
        this.serversidePaging = serversidePaging;
    }

    public class EdmTarget {
        private EdmStructuralType edmTargetType;
        private EdmProperty edmProperty;

        public EdmTarget(final EdmStructuralType edmTargetType, final EdmProperty edmProperty) {
            this.edmTargetType = edmTargetType;
            this.edmProperty = edmProperty;
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
                return "EdmTarget [" + edmTargetType.getName() + "." + edmProperty.getName() + "]";
            } catch (EdmException e) {
                throw new OData2Exception(HttpStatusCodes.INTERNAL_SERVER_ERROR);
            }
        }

    }

}
