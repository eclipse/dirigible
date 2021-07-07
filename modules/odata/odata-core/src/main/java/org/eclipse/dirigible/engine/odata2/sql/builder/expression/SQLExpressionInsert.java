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
import static org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionUtils.csvInBrackets;

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

public final class SQLExpressionInsert implements SQLExpression {

	private final SQLQuery query;

	private final Map<Integer, EdmTarget> columnMapping;

	private Map<String, Object> values;

	private EdmStructuralType target;

	private int atColumn = 0;

	@SuppressWarnings("unchecked")
	public SQLExpressionInsert(final SQLQuery parent) {
		this.query = parent;
		this.columnMapping = new TreeMap<>();
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
		return columnMapping.size() == 0 ? true : false;
	}

	public EdmStructuralType getTarget() {
		return target;
	}

	@SuppressWarnings("unchecked")
	public SQLQuery into(final EdmStructuralType target) throws ODataException {
		query.grantTableAliasForStructuralTypeInQuery(target);
		this.target = target;

		for (EdmProperty property : EdmUtils.getProperties(target)) {
			columnMapping.put(atColumn++, query.new EdmTarget(target, property));
		}

		return query;
	}

	@SuppressWarnings("unchecked")
	public SQLQuery values(final Map<String, Object> values) throws ODataException {

		this.values = values;

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

	private String buildInto(final SQLContext context) throws EdmException {
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

	private boolean isInsertTarget(final EdmStructuralType target) {
		// always select the entity target
		return fqn(query.getInsertExpression().getTarget()).equals(fqn(target)) ? true : false;
	}

	private String buildColumnList(final SQLContext context) throws EdmException {

		Iterator<Integer> i = columnMapping.keySet().iterator();
		List<String> columnNames = new ArrayList<>();
		
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
				if (values.containsKey(p.getName())) {
					
					EdmProperty prop = (EdmProperty) p;
					columnNames.add(tableColumnForInsert(type, prop));
				}
			} else {
				throw new IllegalStateException("Unable to handle property " + p);
			}
		}
		return csvInBrackets(columnNames);
	}

	private String tableColumnForInsert(final EdmStructuralType type, final EdmProperty prop) throws EdmException {
		return query.getSQLTableColumnNoAlias(type, prop);
	}

	private String buildValues(final SQLContext context) throws EdmException {
		List<String> columnValues = new ArrayList<>();
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
				if (values.containsKey(p.getName())) {
					columnValues.add("?");
				}
			} else {
				throw new IllegalStateException("Unable to handle property " + p);
			}
		}
		return csvInBrackets(columnValues);
	}

	public Map<String, Object> getValues() {
		return values;
	}

}
