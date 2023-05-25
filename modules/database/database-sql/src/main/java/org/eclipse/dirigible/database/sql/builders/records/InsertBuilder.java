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
package org.eclipse.dirigible.database.sql.builders.records;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Insert Builder.
 */
public class InsertBuilder extends AbstractSqlBuilder {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(InsertBuilder.class);

	/** The table. */
	private String table = null;

	/** The columns. */
	private List<String> columns = new ArrayList<String>();

	/** The values. */
	private List<String> values = new ArrayList<String>();

	/** The select. */
	private String select = null;

	/**
	 * Instantiates a new insert builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public InsertBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Into.
	 *
	 * @param table
	 *            the table
	 * @return the insert builder
	 */
	public InsertBuilder into(String table) {
		logger.trace("into: " + table);
		this.table = table;
		return this;
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @return the insert builder
	 */
	public InsertBuilder column(String name) {
		logger.trace("column: " + name);
		this.columns.add(name);
		return this;
	}

	/**
	 * Value.
	 *
	 * @param value
	 *            the value
	 * @return the insert builder
	 */
	public InsertBuilder value(String value) {
		logger.trace("value: " + value);
		this.values.add(value);
		return this;
	}

	/**
	 * Select.
	 *
	 * @param select
	 *            the select
	 * @return the insert builder
	 */
	public InsertBuilder select(String select) {
		logger.trace("select: " + select);
		this.select = select;
		return this;
	}

	/**
	 * Generate.
	 *
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// INSERT
		generateInsert(sql);

		// TABLE
		generateTable(sql);

		// COLUMNS
		generateColumns(sql);

		// VALUES
		generateValues(sql);

		// SELECT
		generateSelect(sql);

		String generated = sql.toString();

		logger.trace("generated: " + generated);

		return generated;
	}

	/**
	 * Generate table.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateTable(StringBuilder sql) {
		String tableName = (isCaseSensitive()) ? encapsulate(this.getTable(), true) : this.getTable();
		sql.append(SPACE).append(KEYWORD_INTO).append(SPACE).append(tableName);
	}

	/**
	 * Generate columns.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateColumns(StringBuilder sql) {
		if (!this.columns.isEmpty()) {
			sql.append(SPACE).append(OPEN).append(traverseColumns()).append(CLOSE);
		}
	}

	/**
	 * Generate values.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateValues(StringBuilder sql) {
		if (!this.values.isEmpty()) {
			sql.append(SPACE).append(KEYWORD_VALUES).append(SPACE).append(OPEN).append(traverseValues()).append(CLOSE);
		} else if (!this.columns.isEmpty() && (this.select == null)) {
			sql.append(SPACE).append(KEYWORD_VALUES).append(SPACE).append(OPEN).append(enumerateValues()).append(CLOSE);
		}
	}

	/**
	 * Generate select.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateSelect(StringBuilder sql) {
		if (this.select != null) {
			sql.append(SPACE).append(this.select);
		}
	}

	/**
	 * Traverse columns.
	 *
	 * @return the string
	 */
	protected String traverseColumns() {
		StringBuilder snippet = new StringBuilder();
		for (String column : this.columns) {
			String columnName = (isCaseSensitive()) ? encapsulate(column, false) : column;
			snippet.append(columnName).append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	/**
	 * Traverse values.
	 *
	 * @return the string
	 */
	protected String traverseValues() {
		StringBuilder snippet = new StringBuilder();
		for (String value : this.values) {
			snippet.append(value).append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	/**
	 * Enumerate values.
	 *
	 * @return the string
	 */
	protected String enumerateValues() {
		StringBuilder snippet = new StringBuilder();
		for (int i = 0; i < columns.size(); i++) {
			snippet.append(QUESTION).append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	/**
	 * Generate insert.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateInsert(StringBuilder sql) {
		sql.append(KEYWORD_INSERT);
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
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	public List<String> getColumns() {
		return columns;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * Gets the select.
	 *
	 * @return the select
	 */
	public String getSelect() {
		return select;
	}

}
