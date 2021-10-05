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
package org.eclipse.dirigible.engine.odata2.sql.builder;

import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatement;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLStatementParam;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.clause.SQLUtils;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils;

import java.util.*;
import java.util.stream.Collectors;

import static org.eclipse.dirigible.engine.odata2.sql.clause.SQLUtils.csvInBrackets;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

public class SQLInsertBuilder extends AbstractQueryBuilder {

	private EdmStructuralType target;
	private final List<String> columnNames = new ArrayList<>();

	public SQLInsertBuilder(final EdmTableBindingProvider tableMappingProvider) {
		super(tableMappingProvider);
	}

	@Override
	public SQLStatement build(final SQLContext context) {
		return new SQLStatement() {

			@Override
			public String sql() {
					StringBuilder builder = new StringBuilder();
					builder.append("INSERT ");
					builder.append(" INTO ");
					builder.append(buildInto(context));
					builder.append(" VALUES ");
					builder.append(buildValues(context));
					return SQLUtils.normalizeSQLExpression(builder.toString());
			}

			@Override
			public List<SQLStatementParam> getStatementParams() {
				return SQLInsertBuilder.this.getStatementParams();
			}

			@Override
			public boolean isEmpty() {
				return columnNames.isEmpty();
			}
		};
	}

	public SQLInsertBuilder into(final EdmEntityType target, ODataEntry entry) throws ODataException {
		grantTableAliasForStructuralTypeInQuery(target);
		this.target = target;
		Map<String, Object> entryValues = entry.getProperties();

		for (EdmProperty property : EdmUtils.getProperties(target)) { //we iterate first the own properties of the type
			if (entryValues.containsKey(property.getName())) {
				String columnName = getSQLTableColumnNoAlias(target, property);
				columnNames.add(columnName);
				this.addStatementParam(target, property, entryValues.get(property.getName()));

			}
		}

		for (EdmNavigationProperty inlineEntry : EdmUtils.getNavigationProperties(target)) {
			if (entryValues.containsKey(inlineEntry.getName())) {
					Collection<EdmProperty> inlineEntityKeys = EdmUtils.getKeyProperties(inlineEntry);
				if (!inlineEntityKeys.isEmpty()) {
					columnNames.addAll(getSQLJoinColumnNoAlias(target, inlineEntry));
					for (EdmProperty inlineEntityKey : inlineEntityKeys) {
						Object value = OData2Utils.getInlineEntryKeyValue(entryValues, inlineEntry, inlineEntityKey);
						this.addStatementParam(inlineEntry, inlineEntityKey, value);
					}
				} else {
					//TODO if the entry does not have ids, create the entry.
					throw new ODataException("Deep insert not implemented yet. Please create a request if you need it");
				}
			}
		}

		return this;
	}

	protected String buildInto(final SQLContext context) {
		StringBuilder into = new StringBuilder();
		Iterator<String> it = getTablesAliasesForEntitiesInQuery();
		while (it.hasNext()) {
			String tableAlias = it.next();
			EdmStructuralType target = getEntityInQueryForAlias(tableAlias);
			if (isInsertTarget(target)) {
				boolean caseSensitive = Boolean.parseBoolean(
						Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
				if (caseSensitive) {
					into.append("\"" + getSQLTableName(target) + "\"");
				} else {
					into.append(getSQLTableName(target));
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

	protected boolean isInsertTarget(final EdmStructuralType target) {
		// always select the entity target
		return fqn(getTarget()).equals(fqn(target));
	}

	private String buildColumnList(final SQLContext context) {
		return csvInBrackets(columnNames);
	}

	private String buildValues(final SQLContext context) {
		return csvInBrackets(columnNames.stream().map(n -> "?").collect(Collectors.toList()));
	}

}
