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

import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils.csvInBrackets;

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

public final class SQLExpressionInsert implements SQLExpression {

	private final SQLQuery query;

	private EdmStructuralType target;
	private ODataEntry entry;

	private final List<String> columnNames = new ArrayList<>();
	private final List<Object> columnData = new ArrayList<>();
	private final List<EdmProperty> columnProperties = new ArrayList<>();

	public SQLExpressionInsert(final SQLQuery parent) {
		this.query = parent;
	}

	@Override
	public String evaluate(final SQLContext context, final ExpressionType type) throws EdmException {
		switch (type) {
		case INTO:
			return buildInto(context);
		case VALUES:
			return buildValues(context);
		default:
			throw new OData2Exception("Unable to evaluate the SQLSelect to type " + type,
					HttpStatusCodes.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public boolean isEmpty() throws EdmException {
		return columnNames.size() == 0 ? true : false;
	}

	@SuppressWarnings("unchecked")
	public SQLQuery into(final EdmEntityType target, ODataEntry entry) throws ODataException {
		query.grantTableAliasForStructuralTypeInQuery(target);
		this.target = target;
		this.entry = entry;
		Map<String, Object> entryValues = entry.getProperties();

		for (EdmProperty property : EdmUtils.getProperties(target)) { //we iterate first the own properties of the type
			if (entryValues.containsKey(property.getName())) {
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
					//TODO if the entry does not have ids, create the entry.
					throw new ODataException("Deep insert not implemented yet. Please create a request if you need it");
				}
			}
		}

		return query;
	}

	public String buildInto(final SQLContext context) throws EdmException {
		StringBuilder into = new StringBuilder();
		Iterator<String> it = query.getTablesAliasesForEntitiesInQuery();
		while (it.hasNext()) {
			String tableAlias = it.next();
			EdmStructuralType target = query.getEntityInQueryForAlias(tableAlias);
			if (isInsertTarget(target)) {
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
		into.append(" ").append(buildColumnList(context));
		return into.toString();
	}

	public EdmStructuralType getTarget(){
		return target;
	}

	private boolean isInsertTarget(final EdmStructuralType target) {
		// always select the entity target
		return fqn(query.getInsertExpression().target).equals(fqn(target)) ? true : false;
	}

	private String buildColumnList(final SQLContext context) throws EdmException {
		return csvInBrackets(columnNames);
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
}
