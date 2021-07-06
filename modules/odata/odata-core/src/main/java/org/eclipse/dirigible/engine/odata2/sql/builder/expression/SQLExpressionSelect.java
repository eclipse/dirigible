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

import static java.util.Collections.EMPTY_LIST;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils.csv;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.hasExpand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.SelectItem;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext.DatabaseProduct;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery.EdmTarget;

public final class SQLExpressionSelect implements SQLExpression {
    public static final int NOT_SET = -1;
    //later we would like to bind the columns in the entity set to the correct entities/fields
    private final Map<Integer, EdmTarget> columnMapping;
    private final SQLQuery query;
    private int atColumn = 0;
    private boolean isCount;
    private final List<SelectItem> selectsFromTargetEntity;
    private final List<ArrayList<NavigationPropertySegment>> expands;
    private int top;
    private int skip;
    private EdmStructuralType target;

    @SuppressWarnings("unchecked")
    public SQLExpressionSelect(final SQLQuery parent) {
        this(parent, EMPTY_LIST, EMPTY_LIST);
    }

    public SQLExpressionSelect(final SQLQuery query, final List<SelectItem> selects,
            final List<ArrayList<NavigationPropertySegment>> expands) {
        columnMapping = new TreeMap<>();
        this.query = query;
        selectsFromTargetEntity = selects;
        this.expands = expands;
        isCount = false;
        top = NOT_SET;
        skip = NOT_SET;
    }

    @Override
    public String evaluate(final SQLContext context, final ExpressionType type) throws EdmException {
        switch (type) {
        case SELECT_PREFIX:
            return buildSelectPredicate(context);
        case SELECT_COLUMN_LIST:
            return buildColumnList(context);
        case FROM:
            return buildFrom(context);
        case JOIN:
            return buildJoin(context);
        case WHERE:
        case ORDERBY:
            return EMPTY_STRING;
        case SELECT_SUFFIX:
            return buildSelectSuffix(context);
        default:
            throw new OData2Exception("Unable to evaluate the SQLSelect to type " + type, HttpStatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean isEmpty() throws EdmException {
        if (isCount)
            return false;
        else
            return columnMapping.size() == 0 ? true : false;
    }

    public SQLExpressionSelect top(final Integer top) {
        if (top != null) {
            this.top = top;
        }
        return this;
    }

    public SQLExpressionSelect skip(final Integer skip) {
        if (skip != null && skip > 0) {
            this.skip = skip;
        } else if (skip != null && skip == 0) {
            //the odata impl of SAP UI5 uses skip=0 by default
            // in this case we do not have any skip
            this.skip = NOT_SET;
        }
        return this;
    }

    void setUsingCount(final boolean count) {
        isCount = count;
    }

    public boolean isCount() {
        return isCount;
    }

    public int getTop() {
        return top;
    }

    public EdmStructuralType getTarget() {
        return target;
    }

    public int getSkip() {
        return skip;
    }

    @SuppressWarnings("unchecked")
    public SQLQuery from(final EdmStructuralType target) throws ODataException {
        query.grantTableAliasForStructuralTypeInQuery(target);
        this.target = target;

        if (isCount) {
            columnMapping.put(atColumn++, query.new EdmTarget(target, null));
        } else {
            for (EdmProperty property : EdmUtils.getSelectedProperties(selectsFromTargetEntity, target)) {
                //TODO the selectsFromTargetEntity cannot be mapped only to properties. what happens in this case?
                columnMapping.put(atColumn++, query.new EdmTarget(target, property));
            }
            if (hasExpand(expands)) {
                //handle expands here
                for (ArrayList<NavigationPropertySegment> contentExpands : expands) {
                    EdmStructuralType toEntityType = target;
                    for (NavigationPropertySegment contentExpand : contentExpands) {
                        EdmEntityType expandEntityType = contentExpand.getTargetEntitySet().getEntityType();
                        Collection<EdmProperty> expandProperties = EdmUtils.getSelectedProperties(EMPTY_LIST, expandEntityType);
                        for (EdmProperty expandProperty : expandProperties) {
                            columnMapping.put(atColumn++, query.new EdmTarget(expandEntityType, expandProperty));
                            query.join(expandEntityType, toEntityType);
                        }
                        toEntityType = expandEntityType;
                    }
                }
            }
        }
        return query;
    }

    private String getPropertyName(final int columnIndex) throws EdmException {
        return getProperty(columnIndex).getName();
    }

    private EdmProperty getProperty(final int columnIndex) {
        return columnMapping.get(columnIndex).getEdmProperty();
    }

    private EdmStructuralType getTargetType(final int columnIndex) {
        return columnMapping.get(columnIndex).getEdmTargetType();
    }

    private String buildJoin(final SQLContext context) {
        // TODO Auto-generated method stub
        return null;
    }

    private String buildSelectSuffix(final SQLContext context) {
        if (isCount)
            return EMPTY_STRING;
        String selectPredicate = EMPTY_STRING;
        if (top > 0) {
            if (context.getDatabaseProduct() == DatabaseProduct.DERBY) {
                // Derby: FETCH { FIRST | NEXT } [integer-literal] {ROW | ROWS} ONLY
                selectPredicate = String.format("FETCH FIRST %d ROWS ONLY", top);
            } else if (context.getDatabaseProduct() == DatabaseProduct.POSTGRE_SQL || context.getDatabaseProduct() == DatabaseProduct.H2 || context.getDatabaseProduct() == DatabaseProduct.HANA) {
                // PostgreSQL: [LIMIT { number | ALL }] [OFFSET number]
                selectPredicate = String.format("LIMIT %d", top);
            }
        }

        return selectPredicate;
    }

    private String buildFrom(final SQLContext context) throws EdmException {
        List<String> tables = new ArrayList<>();
        Iterator<String> it = query.getTablesAliasesForEntitiesInQuery();
        while (it.hasNext()) {
            String tableAlias = it.next();
            EdmStructuralType target = query.getEntityInQueryForAlias(tableAlias);
            if (isSelectTarget(target)) {
            	boolean caseSensitive = Boolean.parseBoolean(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
            	if (caseSensitive) {
            		tables.add("\"" + query.getSQLTableName(target) + "\"" + " AS " + "\"" + tableAlias + "\"");
            	} else {
            		tables.add(query.getSQLTableName(target) + " AS " + tableAlias);
            	}
               
            }
        }
        return csv(tables);
    }

    private boolean isSelectTarget(final EdmStructuralType target) {
        //always select the entity target
        return fqn(query.getSelectExpression().getTarget()).equals(fqn(target)) ? true : false;
    }

    private String buildColumnList(final SQLContext context) throws EdmException {
        if (isCount)
            return "COUNT(*)";
        else {

            StringBuilder select = new StringBuilder();
            Iterator<Integer> i = columnMapping.keySet().iterator();
            while (i.hasNext()) {
                Integer column = i.next();
                EdmStructuralType type = getTargetType(column);
                String propertyName = getPropertyName(column);
                EdmTyped p = type.getProperty(propertyName);
                if (!(p instanceof EdmProperty))
                    throw new OData2Exception("You must map the column " + column + " to a EDM property! The current type of propery "
                            + propertyName + " is " + p, HttpStatusCodes.INTERNAL_SERVER_ERROR);
                if (p.getType().getKind() == EdmTypeKind.SIMPLE) {
                    EdmProperty prop = (EdmProperty) p;
                    select.append(tableColumnForSelect(type, prop));

                } else {
                    if (p.getType().getKind() == EdmTypeKind.COMPLEX) {
                        EdmStructuralType st = (EdmStructuralType) p.getType();

                        query.join(st, type);
                        Iterator<String> prit = st.getPropertyNames().iterator();
                        while (prit.hasNext()) {
                            EdmProperty stProp = (EdmProperty) st.getProperty(prit.next());
                            select.append(tableColumnForSelect(st, stProp));
                            if (prit.hasNext()) {
                                select.append(", ");
                            }
                        }

                    } else
                        throw new IllegalStateException("Unable to handle property " + p);
                }
                if (i.hasNext()) {
                    select.append(", ");
                }
            }
            return select.toString();
        }
    }

    private Object tableColumnForSelect(final EdmStructuralType type, final EdmProperty prop) throws EdmException {
        return query.getSQLTableColumn(type, prop) + " AS " + query.getSQLTableColumnAlias(type, prop);
    }

    private String buildSelectPredicate(final SQLContext context) {
        if (isCount)
            return EMPTY_STRING;
        String selectPredicate;
        if (context.getDatabaseProduct() == DatabaseProduct.SYBASE_ASE && top > 0) {
            selectPredicate = "TOP " + top;
        } else {
            selectPredicate = EMPTY_STRING;
        }

        return selectPredicate;
    }

    public static class SQLSelectBuilder {

        private final SQLExpressionSelect selectExpression;

        public SQLSelectBuilder(final SQLExpressionSelect select) {
            selectExpression = select;
        }

        public SQLExpressionSelect top(final int top) {
            selectExpression.top(top);
            return selectExpression;
        }

        public SQLExpressionSelect skip(final int skip) {
            selectExpression.skip(skip);
            return selectExpression;
        }

        public SQLExpressionSelect count() {
            selectExpression.setUsingCount(true);
            return selectExpression;
        }
    }

}
