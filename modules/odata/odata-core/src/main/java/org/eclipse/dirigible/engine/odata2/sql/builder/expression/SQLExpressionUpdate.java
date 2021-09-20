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

import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils.csv;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils.join;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils.csvInBrackets;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils;

public class SQLExpressionUpdate implements SQLExpression {

	private final SQLQuery query;
	private EdmEntityType target;
	private ODataEntry requestEntry;
	private final List<String> nonKeyColumnNames = new ArrayList<>();
	private final List<Object> queryData = new ArrayList<>();
	private final List<EdmProperty> queryOdataProperties = new ArrayList<>();
	private Map<String, Object> uriKeyProperties;

	public SQLExpressionUpdate(final SQLQuery parent, EdmEntityType target, ODataEntry requestEntry, Map<String, Object> uriKeys) {
		this.query = parent;
		this.target = target;
		this.requestEntry = requestEntry;
		this.uriKeyProperties = uriKeys;
	}

	@Override
	public String evaluate(final SQLContext context, final ExpressionType type) throws EdmException {
		switch (type) {
		case TABLE:
			return buildStatement(context);
		default:
			throw new OData2Exception("Unable to evaluate the SQLSelect to type " + type,
					HttpStatusCodes.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public boolean isEmpty() throws EdmException {
		return (nonKeyColumnNames.size() == 0 ? true : false);
	}

	public EdmStructuralType getTarget() {
		return target;
	}


	boolean isKeyProperty(EdmEntityType target, EdmProperty prop) throws EdmException{
		for (String name : target.getKeyPropertyNames()){
			if (name.equals(prop.getName())){
				return true;
			}
		}
		return false;
	}


	public SQLQuery build() throws ODataException {
		query.grantTableAliasForStructuralTypeInQuery(target);
		Map<String, Object> entryValues = requestEntry.getProperties();


		for (EdmProperty property : EdmUtils.getProperties(target)) { //we iterate first the own properties of the type
			if (entryValues.containsKey(property.getName()) && !isKeyProperty(target, property)) {
				String columnName = query.getSQLTableColumnNoAlias(target, property);
				nonKeyColumnNames.add(query.getSQLTableColumnNoAlias(target, property));
				queryOdataProperties.add(property);
				queryData.add(entryValues.get(property.getName()));
			}
		}

		for (EdmNavigationProperty inlineEntry : EdmUtils.getNavigationProperties(target)) {
			if (entryValues.containsKey(inlineEntry.getName())) {
				Collection<EdmProperty> inlineEntityKeys = EdmUtils.getKeyProperties(inlineEntry);
				if (!inlineEntityKeys.isEmpty()) {
					nonKeyColumnNames.addAll(query.getSQLJoinColumnNoAlias(target, inlineEntry));
					for (EdmProperty inlineEntityKey : inlineEntityKeys) {
						queryOdataProperties.add(inlineEntityKey);
						queryData.add(OData2Utils.getInlineEntryKeyValue(entryValues, inlineEntry, inlineEntityKey));
					}
				} else {
					throw new ODataException("Deep update not implemented yet. Please split the update requests!");
				}
			}
		}
		//Add the key values in the end. The syntax is update table set (col=val) where key1=val1,key2=val2
		for (String key : target.getKeyPropertyNames()){ //the order matters
			Object value = uriKeyProperties.get(key);
			if (!isValidKey(value)){
				throw new OData2Exception("Invalid key property" + key, HttpStatusCodes.BAD_REQUEST);
			}
			queryOdataProperties.add((EdmProperty) target.getProperty(key));
			queryData.add(uriKeyProperties.get(key));
		}
		return query;
	}

	//Basic validity check for the values. Prevents that someone deletes an entity with an invalid request
	//in short a composite key makes sense if all elements are not-null (otherwise the non-null element suffices)
	protected boolean isValidKey(Object value){
		return value != null;
	}

	private String buildStatement(final SQLContext context) throws EdmException {
		StringBuilder into = new StringBuilder();
		Iterator<String> it = query.getTablesAliasesForEntitiesInQuery();
		while (it.hasNext()) {
			String tableAlias = it.next();
			EdmStructuralType target = query.getEntityInQueryForAlias(tableAlias);
			if (isUpdateTarget(target)) {
				boolean caseSensitive = Boolean.parseBoolean(
						Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
				if (caseSensitive) {
					into.append("\"" + query.getSQLTableName(target) + "\"");
				} else {
					into.append(query.getSQLTableName(target));
				}
				break;
			}
		}
		into.append(" SET ").append(buildColumnList(context)).append(" WHERE ").append(buildWhereClauseForKeys(context));
		return into.toString();
	}

	protected boolean isUpdateTarget(final EdmStructuralType target) {
		// always select the entity target
		return fqn(query.getUpdateExpression().getTarget()).equals(fqn(target)) ? true : false;
	}

	protected String buildColumnList(final SQLContext context) {
		return csv(nonKeyColumnNames.stream().map(n -> n + "=?").collect(Collectors.toList()));
	}

	protected String buildValues(final SQLContext context) {
		return csvInBrackets(nonKeyColumnNames.stream().map(n -> "?").collect(Collectors.toList()));
	}

	public List<String> getNonKeyColumnNames() {
		return nonKeyColumnNames;
	}

	public List<Object> getQueryData() {
		return queryData;
	}

	public List<EdmProperty> getQueryOdataProperties() {
		return queryOdataProperties;
	}

	protected String buildWhereClauseForKeys(final SQLContext context) throws EdmException {
		List<String> keyConditions = new ArrayList<>();
		for (String key : target.getKeyPropertyNames()){
			EdmProperty keyProperty = (EdmProperty) target.getProperty(key);
			String keyColumnName = query.getSQLTableColumnNoAlias(target, keyProperty);
			keyConditions.add(String.format("%s=?", keyColumnName));
		}
		return join(keyConditions, " AND "); //this plays a role when we are dealing with composite IDs
	}

}
