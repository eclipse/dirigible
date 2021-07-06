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

import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;

public final class SQLExpressionDelete implements SQLExpression {
    
	private final SQLQuery query;
	
	private Map<String, Object> keys;
    
    private EdmStructuralType target;
    
    @SuppressWarnings("unchecked")
    public SQLExpressionDelete(final SQLQuery parent) {
        this.query = parent;
    }

    @Override
    public String evaluate(final SQLContext context, final ExpressionType type) throws EdmException {
        switch (type) {
        case FROM:
            return buildFrom(context);
        case KEYS:
            return buildKeys(context);
        
        default:
            throw new OData2Exception("Unable to evaluate the SQLSelect to type " + type, HttpStatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean isEmpty() throws EdmException {
        return keys.size() == 0 ? true : false;
    }

    public EdmStructuralType getTarget() {
        return target;
    }

    @SuppressWarnings("unchecked")
    public SQLQuery from(final EdmStructuralType target) throws ODataException {
        query.grantTableAliasForStructuralTypeInQuery(target);
        this.target = target;
             
        return query;
    }
    
    @SuppressWarnings("unchecked")
    public SQLQuery keys(final Map<String, Object> keys) throws ODataException {
    	
        this.keys = keys;
        
        return query;
    }


    private String buildFrom(final SQLContext context) throws EdmException {
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

    private String buildKeys(final SQLContext context) throws EdmException {
    	List<String> deleteKeys = new ArrayList<String>();
    	Iterator<Map.Entry<String, Object>> i = keys.entrySet().iterator();
        while (i.hasNext()) {
        	Map.Entry<String, Object> key = i.next();
        	deleteKeys.add(" " + key.getKey() + " = ? ");
        }
        return deleteKeys.stream().collect(Collectors.joining(","));
    }
    
    public Map<String, Object> getKeys() {
		return keys;
	}


}
