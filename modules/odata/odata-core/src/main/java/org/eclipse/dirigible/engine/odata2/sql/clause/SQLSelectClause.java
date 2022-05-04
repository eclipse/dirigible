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

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.SelectItem;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatementParam;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding;
import org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext.DatabaseProduct;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLUtils;

import java.util.*;

import static java.util.Collections.EMPTY_LIST;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.hasExpand;

public final class SQLSelectClause {

    private static final String EMPTY_STRING = "";

    public enum EvaluationType {
        SELECT_COLUMN_LIST, FROM, JOIN, WHERE, SELECT_LIMIT, INTO, VALUES, KEYS, SELECT_OFFSET, TABLE
    }

    public static final int NOT_SET = -1;
    //later we would like to bind the columns in the entity set to the correct entities/fields
    private final Map<Integer, EdmTarget> columnMapping;
    private final Map<EdmTarget, String> targetParameters;
    private final org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder query;
    private int atColumn = 0;
    private boolean isCount;
    private final List<SelectItem> selectsFromTargetEntity;
    private final List<ArrayList<NavigationPropertySegment>> expands;
    private int top;
    private int skip;
    private EdmStructuralType target;

    private final List<SQLStatementParam> statementParams;

    @SuppressWarnings("unchecked")
    public SQLSelectClause(final org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder parent) {
        this(parent, EMPTY_LIST, EMPTY_LIST);
    }

    public SQLSelectClause(final org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder query, final List<SelectItem> selects,
                           final List<ArrayList<NavigationPropertySegment>> expands) {
        columnMapping = new TreeMap<>();
        targetParameters = new HashMap<>();
        this.statementParams = new ArrayList<>();
        this.query = query;
        selectsFromTargetEntity = selects;
        this.expands = expands;
        isCount = false;
        top = NOT_SET;
        skip = NOT_SET;
    }

    public String evaluate(final SQLContext context, final EvaluationType type) throws EdmException {
        switch (type) {
            case SELECT_COLUMN_LIST:
                return buildColumnList(context);
            case FROM:
                return buildFrom(context);
            case SELECT_LIMIT:
                return buildLimit(context);
            case SELECT_OFFSET:
                return buildOffset(context);
            default:
                throw new OData2Exception("Unable to evaluate the SQLSelect to type " + type, HttpStatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildOffset(SQLContext context) {
        if (skip <= 0) {
            return EMPTY_STRING;
        }

        return "OFFSET " + skip;
    }

    public boolean isEmpty() {
        if (isCount)
            return false;
        else
            return columnMapping.size() == 0;
    }

    public SQLSelectClause top(final Integer top) {
        if (top != null) {
            this.top = top;
        }
        return this;
    }

    public SQLSelectClause skip(final Integer skip) {
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
    public org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder from(final EdmStructuralType target, List<KeyPredicate> keyPredicates) throws ODataException {
        query.grantTableAliasForStructuralTypeInQuery(target);
        this.target = target;

        if (isCount) {
            columnMapping.put(atColumn++, new EdmTarget(target, null));
        } else {
            Collection<EdmProperty> properties = EdmUtils.getSelectedProperties(selectsFromTargetEntity, target);

            setTargetParameters(target, keyPredicates, properties);

            for (EdmProperty property : EdmUtils.getSelectedProperties(selectsFromTargetEntity, target)) {
                //TODO the selectsFromTargetEntity cannot be mapped only to properties. what happens in this case?

                boolean isPropertyParameter = false;

                for ( Map.Entry<EdmTarget, String> targetParameter : this.targetParameters.entrySet()) {
                    if (targetParameter.getKey().getEdmProperty().getName().equals(property.getName())) {
                        isPropertyParameter = true;
                    }
                }

                if (!isPropertyParameter) {
                    columnMapping.put(atColumn++, new EdmTarget(target, property));
                }

            }
            if (hasExpand(expands)) {
                //handle expands here
                for (ArrayList<NavigationPropertySegment> contentExpands : expands) {
                    EdmStructuralType toEntityType = target;
                    for (NavigationPropertySegment contentExpand : contentExpands) {
                        EdmEntityType expandEntityType = contentExpand.getTargetEntitySet().getEntityType();
                        Collection<EdmProperty> expandProperties = EdmUtils.getSelectedProperties(EMPTY_LIST, expandEntityType);
                        for (EdmProperty expandProperty : expandProperties) {
                            columnMapping.put(atColumn++, new EdmTarget(expandEntityType, expandProperty));
                            query.join(expandEntityType, toEntityType);
                        }
                        toEntityType = expandEntityType;
                    }
                }
            }
        }
        return query;
    }

    private void setTargetParameters(EdmStructuralType target, List<KeyPredicate> keyPredicates, Collection<EdmProperty> properties) throws EdmException {
        String targetSqlTableType = this.query.getSQLTableType(target);

        if (targetSqlTableType.equalsIgnoreCase("CALC VIEW")) {
            List<String> targetParameters = this.query.getSQLTableParameters(this.target);

            for ( EdmProperty property : properties) {
                String propertyName = property.getName();

                for ( KeyPredicate keyPredicate : keyPredicates) {
                    if (keyPredicate.getProperty().getName().equals(propertyName) && targetParameters.contains(propertyName)) {
                        String keyPredicateValue = keyPredicate.getLiteral();
                        this.targetParameters.put(new EdmTarget(target, property), keyPredicateValue);
                    }
                }
            }
        }
    }

    public List<String> getTargetParameterNames() throws EdmException {
        List<String> targetParameterNames = new ArrayList<>();

        for (Map.Entry<EdmTarget, String> targetParameter : this.targetParameters.entrySet()) {
            targetParameterNames.add(targetParameter.getKey().getEdmProperty().getName());
        }

        return targetParameterNames;
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

    public List<SQLStatementParam> getStatementParams() {
        return Collections.unmodifiableList(statementParams);
    }

    private String buildLimit(final SQLContext context) {
        if (isCount)
            return EMPTY_STRING;
        String selectPredicate = EMPTY_STRING;
        if (top > 0) {
            if (context.getDatabaseProduct() == DatabaseProduct.DERBY) {
                // Derby: FETCH { FIRST | NEXT } [integer-literal] {ROW | ROWS} ONLY
                return String.format("FETCH FIRST %d ROWS ONLY", top);
            } else {
                // PostgreSQL: [LIMIT { number | ALL }] [OFFSET number]
                return String.format("LIMIT %d", top);
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
                if (this.targetParameters.isEmpty()) {
                    tables.add(query.getSQLTableName(target) + " AS " + tableAlias);
                }
                else {
                    tables.add(query.getSQLTableName(target) + buildTargetParameters() + " AS " + tableAlias);
                }
            }
        }
        return SQLUtils.csv(tables);
    }

    private String buildTargetParameters() throws EdmException {
        StringBuilder parameters = new StringBuilder();

        Integer targetParameterCount = this.targetParameters.size();
        for (Map.Entry<EdmTarget,String> targetParameter : this.targetParameters.entrySet()) {
            EdmStructuralType type = targetParameter.getKey().getEdmTargetType();
            EdmProperty prop = targetParameter.getKey().getEdmProperty();
            String targetParameterValue = targetParameter.getValue();
            EdmTableBinding.ColumnInfo info = this.query.getSQLTableColumnInfo(type, prop);
            this.statementParams.add(new SQLStatementParam(targetParameterValue, prop, info));

            parameters.append("placeholder.\"$$" + prop.getName() + "$$\"" + " => " + " ? ");

            targetParameterCount--;

            if (targetParameterCount > 0) {
                parameters.append(",");
            }
        }

        return "(" + parameters + ")";
    }

    private boolean isSelectTarget(final EdmStructuralType target) {
        //always select the entity target
        return fqn(query.getSelectExpression().getTarget()).equals(fqn(target));
    }

    private String buildColumnList(final SQLContext context) throws EdmException {
        if (isCount)
            return "COUNT(*)";
        else {

            StringBuilder select = new StringBuilder();

            if (!this.targetParameters.isEmpty()) {

                Integer targetParametersCount = this.targetParameters.size();

                for (Map.Entry<EdmTarget, String> targetParameter : this.targetParameters.entrySet()) {
                    EdmProperty prop = targetParameter.getKey().getEdmProperty();
                    EdmStructuralType type = targetParameter.getKey().getEdmTargetType();
                    String targetParameterValue = targetParameter.getValue();
                    EdmTableBinding.ColumnInfo info = this.query.getSQLTableColumnInfo(type, prop);
                    this.statementParams.add(new SQLStatementParam(targetParameterValue, prop, info));

                    select.append(tableColumnForSelectWhenParameter(type, prop));

                    targetParametersCount--;

                    if (targetParametersCount > 0) {
                        select.append(", ");
                    }
                }
            }

            if (!this.targetParameters.isEmpty() && !this.columnMapping.isEmpty()) {
                select.append(", ");
            }

            Iterator<Integer> ci = columnMapping.keySet().iterator();

            while (ci.hasNext()) {
                Integer column = ci.next();
                EdmStructuralType type = getTargetType(column);
                String propertyName = getPropertyName(column);
                EdmTyped p = type.getProperty(propertyName);
                if (!(p instanceof EdmProperty))
                    throw new OData2Exception("You must map the column " + column + " to a EDM property! The current type of property "
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
                if (ci.hasNext()) {
                    select.append(", ");
                }
            }

            return select.toString();
        }
    }

    private Object tableColumnForSelect(final EdmStructuralType type, final EdmProperty prop) {
        return query.getSQLTableColumn(type, prop) + " AS \"" + query.getSQLTableColumnAlias(type, prop) + "\"";
    }

    private Object tableColumnForSelectWhenParameter(final EdmStructuralType type, final EdmProperty prop) {
        return "?" + " AS " + query.getSQLTableColumnAlias(type, prop);
    }

    public static class SQLSelectBuilder {

        private final SQLSelectClause selectExpression;

        public SQLSelectBuilder(final SQLSelectClause select) {
            selectExpression = select;
        }

        public SQLSelectClause top(final int top) {
            selectExpression.top(top);
            return selectExpression;
        }

        public SQLSelectClause skip(final int skip) {
            selectExpression.skip(skip);
            return selectExpression;
        }

        public SQLSelectClause count() {
            selectExpression.setUsingCount(true);
            return selectExpression;
        }
    }

    private class EdmTarget {
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

}
