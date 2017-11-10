/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.builders.records;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractQuerySqlBuilder;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateBuilder.
 */
public class UpdateBuilder extends AbstractQuerySqlBuilder {

	/** The table. */
	private String table;
	
	/** The values. */
	private Map<String, String> values = new LinkedHashMap<String, String>();
	
	/** The wheres. */
	private List<String> wheres = new ArrayList<String>();

	/**
	 * Instantiates a new update builder.
	 *
	 * @param dialect the dialect
	 */
	public UpdateBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Table.
	 *
	 * @param table the table
	 * @return the update builder
	 */
	public UpdateBuilder table(String table) {
		this.table = table;
		return this;
	}

	/**
	 * Sets the.
	 *
	 * @param column the column
	 * @param value the value
	 * @return the update builder
	 */
	public UpdateBuilder set(String column, String value) {
		values.put(column, value);
		return this;
	}

	/**
	 * Where.
	 *
	 * @param condition the condition
	 * @return the update builder
	 */
	public UpdateBuilder where(String condition) {
		wheres.add(OPEN + condition + CLOSE);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {
		StringBuilder sql = new StringBuilder();

		// UPDATE
		generateUpdate(sql);

		// TABLE
		generateTable(sql);

		// SET
		generateSetValues(sql);

		// WHERE
		generateWhere(sql, wheres);

		return sql.toString();
	}

	/**
	 * Generate table.
	 *
	 * @param sql the sql
	 */
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE).append(this.table);
	}

	/**
	 * Generate set values.
	 *
	 * @param sql the sql
	 */
	protected void generateSetValues(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_SET);
		for (Entry<String, String> next : values.entrySet()) {
			sql.append(SPACE).append(next.getKey()).append(SPACE).append(EQUALS).append(SPACE).append(next.getValue()).append(COMMA);
		}
		sql.delete(sql.length() - 1, sql.length());
	}

	/**
	 * Generate update.
	 *
	 * @param sql the sql
	 */
	protected void generateUpdate(StringBuilder sql) {
		sql.append(KEYWORD_UPDATE);
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public Map<String, String> getValues() {
		return values;
	}

	/**
	 * Gets the wheres.
	 *
	 * @return the wheres
	 */
	public List<String> getWheres() {
		return wheres;
	}

}
