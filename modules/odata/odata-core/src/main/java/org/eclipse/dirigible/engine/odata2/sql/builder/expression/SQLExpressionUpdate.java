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

public final class SQLExpressionUpdate implements SQLExpression {

	private final SQLQuery query;
	private EdmEntityType target;
	private ODataEntry entry;
	private final List<String> columnNames = new ArrayList<>();
	private final List<Object> columnData = new ArrayList<>();
	private final List<EdmProperty> columnProperties = new ArrayList<>();
	private Map<String, Object> entryKeys;
	@SuppressWarnings("unchecked")
	public SQLExpressionUpdate(final SQLQuery parent, EdmEntityType target, ODataEntry entry, Map<String, Object> entryKeys) {
		this.query = parent;
		this.target = target;
		this.entry = entry;
		this.entryKeys = entryKeys;
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
		return columnNames.size() == 0 ? true : false;
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
		Map<String, Object> entryValues = entry.getProperties();


		for (EdmProperty property : EdmUtils.getProperties(target)) { //we iterate first the own properties of the type
			if (entryValues.containsKey(property.getName()) && !isKeyProperty(target, property)) {
				String columnName = query.getSQLTableColumnNoAlias(target, property);
				columnNames.add(query.getSQLTableColumnNoAlias(target, property));
				columnData.add(entryValues.get(property.getName()));
				columnProperties.add(property);
			}
		}

		for (EdmNavigationProperty inlineEntry : EdmUtils.getNavigationProperties(target)) {
			if (entryValues.containsKey(inlineEntry.getName())) {
				Collection<EdmProperty> inlineEntityKeys = EdmUtils.getKeyProperties(inlineEntry);
				if (!inlineEntityKeys.isEmpty()) {
					columnNames.addAll(query.getSQLJoinColumnNoAlias(target, inlineEntry));
					for (EdmProperty inlineEntityKey : inlineEntityKeys) {
						columnData.add(OData2Utils.getInlineEntryKeyValue(entryValues, inlineEntry, inlineEntityKey));
						columnProperties.add(inlineEntityKey);
					}
				} else {
					throw new ODataException("Deep update not implemented yet. Please split the update requests!");
				}
			}
		}
		//add the properties in the end
		for (String name : entryKeys.keySet()){
			columnProperties.add((EdmProperty) target.getProperty(name));
			columnData.add(entryKeys.get(name));
		}

		return query;
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

	private boolean isUpdateTarget(final EdmStructuralType target) {
		// always select the entity target
		return fqn(query.getUpdateExpression().getTarget()).equals(fqn(target)) ? true : false;
	}

	private String buildColumnList(final SQLContext context) throws EdmException {
		return csv(columnNames.stream().map(n -> n + " = ?").collect(Collectors.toList()));
	}

	private String buildValues(final SQLContext context) throws EdmException {
		return csvInBrackets(columnNames.stream().map(n -> "?").collect(Collectors.toList()));
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public List<Object> getColumnData() {
		return columnData;
	}

	public List<EdmProperty> getColumnProperties() {
		return columnProperties;
	}

	private String buildWhereClauseForKeys(final SQLContext context) throws EdmException {
		List<String> updates = new ArrayList<>();
		Iterator<Map.Entry<String, Object>> i = entryKeys.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<String, Object> key = i.next();
			EdmProperty property = (EdmProperty) target.getProperty(key.getKey());
			String columnName = query.getSQLTableColumnNoAlias(target, property);
			updates.add(" " + columnName + " = ? ");
		}
		return csv(updates);
	}

}
