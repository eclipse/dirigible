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

import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.SelectItem;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding.ColumnInfo;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionWhere;

import java.util.*;

import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.REQUESTED_RANGE_NOT_SATISFIABLE;
import static org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils.evaluateDateTimeExpressions;

public class SQLQueryBuilder {

    /**
     * Maximum number of rows returned (threshold for server-side paging).
     */
    public static final int DEFAULT_SERVER_PAGING_SIZE = 1000;

    private final EdmTableBindingProvider tableMapping;

    public SQLQueryBuilder(EdmTableBindingProvider tableMapping) {
        this.tableMapping = tableMapping;
    }

    public SQLQuery buildSelectCountQuery(final UriInfo uri) throws ODataException {
        EdmEntityType target = uri.getTargetEntitySet().getEntityType();
        SQLQuery q = new SQLQuery(tableMapping);
        q.select().count().from(target).join(uri.getStartEntitySet(), uri.getTargetEntitySet(), uri.getNavigationSegments())
                .with(uri.getKeyPredicates()).filter(uri.getTargetEntitySet(), uri.getFilter());
        return q;
    }

    public SQLQuery buildSelectEntityQuery(final UriInfo uri) throws ODataException {
        EdmEntityType target = uri.getTargetEntitySet().getEntityType();
        SQLQuery q = new SQLQuery(tableMapping);
        q.select(uri.getSelect(), uri.getExpand()).from(target).filter(uri.getTargetEntitySet(), uri.getFilter())
                .join(uri.getStartEntitySet(), uri.getTargetEntitySet(), uri.getNavigationSegments()).with(uri.getKeyPredicates());
        if (uri.getKeyPredicates() != uri.getTargetKeyPredicates()) {
            q.and(whereClauseFromKeyPredicates(q, uri.getTargetEntitySet().getEntityType(), uri.getTargetKeyPredicates()));
        }
        return q;
    }

    public SQLQuery buildSelectEntitySetQuery(final UriInfo uri) throws ODataException {
        return buildSelectEntitySetQuery(uri, Collections.<String> emptyList());
    }

    public SQLQuery buildSelectEntitySetQuery(final UriInfo uri, List<String> readIdsForExpand) throws ODataException {
        EdmEntityType target = uri.getTargetEntitySet().getEntityType();

        SQLQuery q = new SQLQuery(tableMapping);

        if (readIdsForExpand == null || readIdsForExpand.isEmpty()) {
            //no expand, we filter as usual
            final boolean needsServersidePaging = calculateNeedsServersidePaging(uri);
            Integer effectiveTop;
            if (needsServersidePaging) {
                effectiveTop = getEntityPagingSize(target);
                q.setServersidePaging(true);
            } else {
                effectiveTop = uri.getTop();
            }

            final Integer effectiveSkip = calculateEffectiveSkip(uri);

            if (effectiveSkip != null && effectiveTop != null) {
                effectiveTop += effectiveSkip;
            }
            q.select(uri.getSelect(), uri.getExpand()).top(effectiveTop).skip(effectiveSkip).from(target);
            q.filter(uri.getTargetEntitySet(), uri.getFilter());
        } else {
            //we have the problem that top does not work for exapnd. Therefore we do 2 queries to select the ids of the target entities (with applied filter),
            //and then we do filter on these IDS with the expand, with no top and skip 
            // SELECT TOP XXX FROM TTTT AS M WHERE FILTER
            // SELECT XXX WHERE XXX.ID IN (...)
            q.select(uri.getSelect(), uri.getExpand()).from(target);
            q.filter(uri.getTargetEntitySet(), getKeyProperty(target), readIdsForExpand);
        }
        q.join(uri.getStartEntitySet(), uri.getTargetEntitySet(), uri.getNavigationSegments()).with(uri.getKeyPredicates());
        q.validateOrderBy(uri);
        q.orderBy(uri.getOrderBy(), uri.getTargetEntitySet().getEntityType());
        
        return q;
    }

    public SQLQuery buildSelectEntitySetIdsForTopAndExpandQuery(final UriInfo uri) throws ODataException {
        SQLQuery q = new SQLQuery(tableMapping);
        EdmEntityType target = uri.getTargetEntitySet().getEntityType();

        final boolean needsServersidePaging = calculateNeedsServersidePaging(uri);
        Integer effectiveTop;
        if (needsServersidePaging) {
            effectiveTop = getEntityPagingSize(target);
            q.setServersidePaging(true);
        } else {
            effectiveTop = uri.getTop();
        }

        final Integer effectiveSkip = calculateEffectiveSkip(uri);

        if (effectiveSkip != null && effectiveTop != null) {
            effectiveTop += effectiveSkip;
        }

        q.select(buildSelectItemsForPrimaryKey(target), null).top(effectiveTop).skip(effectiveSkip).from(target);
        q.filter(uri.getTargetEntitySet(), uri.getFilter())
                .join(uri.getStartEntitySet(), uri.getTargetEntitySet(), uri.getNavigationSegments()).with(uri.getKeyPredicates());
        //if the query is expanded, the order by is not necessary, because first the IDs will be selected
        if (uri.getExpand().isEmpty()) {
            q.orderBy(uri.getOrderBy(), uri.getTargetEntitySet().getEntityType());
        }

        return q;
    }

    public SQLQuery emptyQuery() {
        return new SQLQuery(tableMapping);

    }

    public EdmTableBindingProvider getTableMapping() {
        return tableMapping;
    }

    public Integer getEntityPagingSize(EdmEntityType targetType) {
        return DEFAULT_SERVER_PAGING_SIZE;
    }

    private EdmProperty getKeyProperty(EdmEntityType type) throws EdmException {
        List<String> keyProperties = type.getKeyPropertyNames();
        if (keyProperties.size() > 1) {
            throw new IllegalArgumentException(
                    "Complex key properties are not supported so far. Extend the default SQLQuery Builder with your own!");
        }
        return (EdmProperty) type.getProperty(keyProperties.get(0));
    }

    private List<SelectItem> buildSelectItemsForPrimaryKey(final EdmEntityType target) throws EdmException {
        List<SelectItem> result = new ArrayList<>();
        List<String> keyProperties = target.getKeyPropertyNames();
        for (final String prop : keyProperties) {
            SelectItem item = new SelectItem() {

                @Override
                public boolean isStar() {
                    return false;
                }

                @Override
                public EdmProperty getProperty() {
                    try {
                        return (EdmProperty) target.getProperty(prop);
                    } catch (EdmException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public List<NavigationPropertySegment> getNavigationPropertySegments() {
                    return null;
                }
            };

            result.add(item);
        }
        return result;
    }

    private static SQLExpressionWhere whereClauseFromKeyPredicates(SQLQuery query, EdmStructuralType type,
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

    private boolean calculateNeedsServersidePaging(UriInfo uri) throws EdmException {
        final Integer top = uri.getTop();
        boolean needsServersidePaging;
        if (top == null || top > getEntityPagingSize(uri.getTargetEntitySet().getEntityType())) {
            needsServersidePaging = true;
        } else {
            needsServersidePaging = false;
        }
        return needsServersidePaging;
    }

    /**
     * Calculates the effective value for skip which is based on $skip and
     * $skipToken.
     */
    private static Integer calculateEffectiveSkip(final UriInfo uri) {
        String skipTokenString = uri.getSkipToken();
        Integer skipToken;
        if (skipTokenString != null) {
            try {
                skipToken = Integer.parseInt(skipTokenString);
                if (skipToken < 0) {
                    throw new OData2Exception("$skipToken must be a positive number equal or greater than zero",
                            REQUESTED_RANGE_NOT_SATISFIABLE);
                }
            } catch (NumberFormatException e) {
                throw new OData2Exception("$skipToken must be a number", REQUESTED_RANGE_NOT_SATISFIABLE, e);
            }
        } else {
            skipToken = null;
        }

        Integer skip = uri.getSkip();
        Integer effectiveSkip;
        if (skipToken != null && skip != null) {
            effectiveSkip = skipToken + skip;
        } else if (skipToken == null && skip != null) {
            effectiveSkip = skip;
        } else if (skipToken != null && skip == null) {
            effectiveSkip = skipToken;
        } else {
            effectiveSkip = null;
        }
        return effectiveSkip;
    }
    
    public SQLQuery buildInsertEntityQuery(final UriInfo uri, ODataEntry entry) throws ODataException {
        EdmEntityType target = uri.getTargetEntitySet().getEntityType();
        SQLQuery q = new SQLQuery(tableMapping);
        q.insert(target, entry);
        return q;
    }
    
    public SQLQuery buildDeleteEntityQuery(final UriInfo uri, Map<String, Object> keys) throws ODataException {
        EdmEntityType target = uri.getStartEntitySet().getEntityType();
        SQLQuery q = new SQLQuery(tableMapping);
        q.delete().from(target).keys(keys);
        
        return q;
    }

	public SQLQuery buildUpdateEntityQuery(UriInfo uri, ODataEntry entry, Map<String, Object> uriKeys) throws ODataException {
		EdmEntityType target = uri.getTargetEntitySet().getEntityType();
        SQLQuery q = new SQLQuery(tableMapping);
        q.update(target, entry, uriKeys).build();
        return q;
	}
}
