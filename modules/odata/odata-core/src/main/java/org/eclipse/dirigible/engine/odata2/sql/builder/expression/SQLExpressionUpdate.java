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

import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils.csv;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery.EdmTarget;

public final class SQLExpressionUpdate implements SQLExpression {

	private final SQLQuery query;

	private final Map<Integer, EdmTarget> columnMapping;

	private Map<String, Object> values;

	private Map<String, Object> keys;

	private EdmStructuralType target;

	private int atColumn = 0;

	@SuppressWarnings("unchecked")
	public SQLExpressionUpdate(final SQLQuery parent) {
		this.query = parent;
		this.columnMapping = new TreeMap<>();
	}

	@Override
	public String evaluate(final SQLContext context, final ExpressionType type) throws EdmException {
		switch (type) {
		case TABLE:
			return buildTable(context);

		default:
			throw new OData2Exception("Unable to evaluate the SQLSelect to type " + type,
					HttpStatusCodes.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public boolean isEmpty() throws EdmException {
		return columnMapping.size() == 0 ? true : false;
	}

	public EdmStructuralType getTarget() {
		return target;
	}

	@SuppressWarnings("unchecked")
	public SQLQuery table(final EdmStructuralType target) throws ODataException {
		query.grantTableAliasForStructuralTypeInQuery(target);
		this.target = target;

		for (EdmProperty property : EdmUtils.getProperties(target)) {
			columnMapping.put(atColumn++, query.new EdmTarget(target, property));
		}

		return query;
	}

	@SuppressWarnings("unchecked")
	public SQLQuery with(final Map<String, Object> values, final Map<String, Object> keys) throws ODataException {

		this.values = values;
		this.keys = keys;

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

	private String buildTable(final SQLContext context) throws EdmException {
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

		into.append(" SET ").append(buildColumnList(context)).append(" WHERE ").append(buildKeys(context));

		return into.toString();
	}

	private boolean isUpdateTarget(final EdmStructuralType target) {
		// always select the entity target
		return fqn(query.getUpdateExpression().getTarget()).equals(fqn(target)) ? true : false;
	}

	private String buildColumnList(final SQLContext context) throws EdmException {

		List<String> columns = new ArrayList<String>();
		Iterator<Integer> i = columnMapping.keySet().iterator();
		while (i.hasNext()) {
			Integer column = i.next();
			EdmStructuralType type = getTargetType(column);
			String propertyName = getPropertyName(column);
			EdmTyped p = type.getProperty(propertyName);
			if (!(p instanceof EdmProperty))
				throw new OData2Exception("You must map the column " + column
						+ " to a EDM property! The current type of propery " + propertyName + " is " + p,
						HttpStatusCodes.INTERNAL_SERVER_ERROR);
			if (p.getType().getKind() == EdmTypeKind.SIMPLE) {
				if (values.containsKey(p.getName()) && !keys.containsKey(p.getName())) {
					
					EdmProperty prop = (EdmProperty) p;
					columns.add(tableColumnForUpdate(type, prop) + " = ?");
				}
			} else {
				throw new IllegalStateException("Unable to handle property " + p);
			}
		}
		return csv(columns);

	}

	private Object tableColumnForUpdate(final EdmStructuralType type, final EdmProperty prop) throws EdmException {
		return query.getSQLTableColumnNoAlias(type, prop);
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public Map<String, Object> getKeys() {
		return keys;
	}

	private String buildKeys(final SQLContext context) throws EdmException {
		List<String> updates = new ArrayList<>();
		Iterator<Map.Entry<String, Object>> i = keys.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<String, Object> key = i.next();
			updates.add(" " + key.getKey() + " = ? ");
		}
		return csv(updates);
	}

}
