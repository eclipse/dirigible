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
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatement;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatementParam;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.clause.SQLWhereClause;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.eclipse.dirigible.engine.odata2.sql.builder.SQLUtils.*;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

public class SQLDeleteBuilder extends AbstractQueryBuilder {
    private final List<String> deleteKeysColumnNames = new ArrayList<>();
    private Map<String, Object> deleteKeys;
    private EdmEntityType target;
    private String tableName;

    public SQLDeleteBuilder(final EdmTableBindingProvider tableMappingProvider) {
        super(tableMappingProvider);
    }

    private EdmEntityType getTarget() {
        return target;
    }

    public SQLDeleteBuilder deleteFrom(final EdmEntityType target) {
        grantTableAliasForStructuralTypeInQuery(target);
        this.target = target;
        return this;
    }

    public SQLDeleteBuilder keys(final Map<String, Object> deleteKeys) {
        this.deleteKeys = deleteKeys;
        return this;
    }

    public Map<String, Object> getDeleteKeys() {
        return deleteKeys;
    }

    protected String buildFrom() throws EdmException {
        grantTableAliasForStructuralTypeInQuery(target);

        for (EdmProperty deleteProperty : target.getKeyProperties()) { //we iterate first the own properties of the type
            if (deleteKeys.containsKey(deleteProperty.getName())) {
                String columnName = getSQLTableColumnNoAlias(target, deleteProperty);
                deleteKeysColumnNames.add(getSQLTableColumnNoAlias(target, deleteProperty));
                Object keyValue = deleteKeys.get(deleteProperty.getName());
                if (!isValidKeyValue(keyValue)) {
                    throw new OData2Exception("Invalid key value for property  " + deleteProperty.getName(), HttpStatusCodes.BAD_REQUEST);
                }
                Object value = deleteKeys.get(deleteProperty.getName());
                this.addStatementParam(target, deleteProperty, value);

            } else {
                throw new OData2Exception(String.format("Key property %s is missing in the DELETE request!", deleteProperty.getName()),
                        HttpStatusCodes.BAD_REQUEST);
            }
        }

        for (EdmNavigationProperty inlineEntry : EdmUtils.getNavigationProperties(target)) {
            if (deleteKeys.containsKey(inlineEntry.getName())) {
                throw new OData2Exception("Delete by non-id property is not allowed!", HttpStatusCodes.BAD_REQUEST);
            }
        }
        return buildDeleteFrom();
    }

    private String buildDeleteFrom() {
        StringBuilder from = new StringBuilder();
        for (Iterator<String> it = getTablesAliasesForEntitiesInQuery(); it.hasNext(); ) {
            String tableAlias = it.next();
            EdmStructuralType target = getEntityInQueryForAlias(tableAlias);
            if (isDeleteTarget(target)) {
                from.append(getSQLTableName(target));
                break;
            }
        }
        return from.toString();
    }

    private boolean isDeleteTarget(final EdmStructuralType target) {
        //always select the entity target
        return fqn(getTarget()).equals(fqn(target));
    }

    private String buildDeleteWhereClauseOnKeys(final SQLContext context) {
        List<String> deleteKeyExpressions = new ArrayList<>();
        for (String columnName : deleteKeysColumnNames) {
            deleteKeyExpressions.add(columnName + "=?");
        }
        return join(deleteKeyExpressions, " AND ");
    }


    @Override
    public SQLStatement build(final SQLContext context) {
        return new SQLStatement() {

            @Override
            public String sql() throws EdmException {
                //TODO make immutable
                StringBuilder builder = new StringBuilder();
                builder.append("DELETE ");
                builder.append(" FROM ");
                builder.append(getTargetTableName());

                SQLWhereClause where = new SQLWhereClause(buildDeleteWhereClauseOnKeys(context));
                where.and(getWhereClause()); //add any update where clause set by the interceptors

                builder.append(" WHERE ");
                builder.append(where.getWhereClause());
                return SQLUtils.assertParametersCount(normalizeSQLExpression(builder.toString()), getStatementParams());
            }


            @Override
            public List<SQLStatementParam> getStatementParams() {
                return SQLDeleteBuilder.this.getStatementParams();
            }

            @Override
            public boolean isEmpty() {
                return deleteKeys.isEmpty();
            }
        };
    }

    public SQLDeleteBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getTargetTableName() throws EdmException {
        return tableName != null ? tableName : buildFrom();
    }
}
