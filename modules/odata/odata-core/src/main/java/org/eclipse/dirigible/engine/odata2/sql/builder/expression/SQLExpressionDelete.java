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

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils.*;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

public final class SQLExpressionDelete implements SQLExpression {
	private final SQLQuery query;
	private Map<String, Object> deleteKeys;
    private EdmEntityType target;

    private final List<String> deleteKeysColumnNames = new ArrayList<>();
    private final List<Object> deleteKeyValues = new ArrayList<>();
    private final List<EdmProperty> queryOdataProperties = new ArrayList<>();

    public SQLExpressionDelete(final SQLQuery parent) {
        this.query = parent;
    }

    @Override
    public String evaluate(final SQLContext context, final ExpressionType type) throws EdmException {
        switch (type) {
        case FROM:
            return buildFrom(context);
        case KEYS:
            return buildDeleteWhereClauseOnKeys(context);
        default:
            throw new OData2Exception("Unable to evaluate the SQLSelect to type " + type, HttpStatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean isEmpty() throws EdmException {
        return deleteKeys.size() == 0 ? true : false;
    }

    public EdmEntityType getTarget() {
        return target;
    }

    public SQLQuery from(final EdmEntityType target) throws ODataException {
        query.grantTableAliasForStructuralTypeInQuery(target);
        this.target = target;
        return query;
    }
    
    public SQLQuery keys(final Map<String, Object> keys) {
        this.deleteKeys = keys;
        return query;
    }

    protected String buildFrom(final SQLContext context) throws EdmException {
        query.grantTableAliasForStructuralTypeInQuery(target);

        for (EdmProperty deleteProperty: target.getKeyProperties()) { //we iterate first the own properties of the type
            if (deleteKeys.containsKey(deleteProperty.getName())) {
                String columnName = query.getSQLTableColumnNoAlias(target, deleteProperty);
                deleteKeysColumnNames.add(query.getSQLTableColumnNoAlias(target, deleteProperty));
                queryOdataProperties.add(deleteProperty);
                Object keyValue = deleteKeys.get(deleteProperty.getName());
                if (!isValidKeyValue(keyValue)) {
                    throw new OData2Exception("Invalid key value for property  " + deleteProperty.getName(), HttpStatusCodes.BAD_REQUEST);
                }
                deleteKeyValues.add(deleteKeys.get(deleteProperty.getName()));
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
        for (Iterator<String> it = query.getTablesAliasesForEntitiesInQuery(); it.hasNext();) {
            String tableAlias = it.next();
            EdmStructuralType target = query.getEntityInQueryForAlias(tableAlias);
            if (isDeleteTarget(target)) {
            	boolean caseSensitive = Boolean.parseBoolean(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
            	if (caseSensitive) {
            		from.append("\"" + query.getSQLTableName(target) + "\"");
            	} else {
            		from.append(query.getSQLTableName(target));
            	}
                break;
            }
        }
        return from.toString();
    }

    private boolean isDeleteTarget(final EdmStructuralType target) {
        //always select the entity target
        return fqn(query.getDeleteExpression().getTarget()).equals(fqn(target)) ? true : false;
    }

    private String buildDeleteWhereClauseOnKeys(final SQLContext context) throws EdmException {
        List<String> deleteKeyExpressions = new ArrayList<String>();
        for (String columnName : deleteKeysColumnNames) {
            deleteKeyExpressions.add(columnName + "=?");
        }
        return join(deleteKeyExpressions, " AND " );
    }
    
    public Map<String, Object> getDeleteKeyValues() {
		return deleteKeys;
	}


}
