/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.sql.builders.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlException;
import org.eclipse.dirigible.database.sql.builders.AbstractCreateSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Create Table Builder.
 */
public class CreateTableBuilder extends AbstractTableBuilder<CreateTableBuilder> {

	private static final Logger logger = LoggerFactory.getLogger(CreateTableBuilder.class);

	private CreateTablePrimaryKeyBuilder primaryKey;

	private List<CreateTableForeignKeyBuilder> foreignKeys = new ArrayList<CreateTableForeignKeyBuilder>();

	private List<CreateTableUniqueIndexBuilder> uniqueIndices = new ArrayList<CreateTableUniqueIndexBuilder>();

	private List<CreateTableCheckBuilder> checks = new ArrayList<CreateTableCheckBuilder>();

	/**
	 * Instantiates a new creates the table builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param table
	 *            the table
	 */
	public CreateTableBuilder(ISqlDialect dialect, String table) {
		super(dialect, table);
	}

	/**
	 * Primary key.
	 *
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder primaryKey(String name, String[] columns) {
		logger.trace("primaryKey: " + name + ", columns" + Arrays.toString(columns));
		if (this.primaryKey != null) {
			throw new SqlException("Setting of primary key must be called only once");
		}
		this.primaryKey = new CreateTablePrimaryKeyBuilder(this.getDialect(), name);
		for (String column : columns) {
			this.primaryKey.column(column);
		}
		return this;
	}

	/**
	 * Primary key.
	 *
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder primaryKey(String name, String columns) {
		logger.trace("primaryKey: " + name + ", columns" + columns);
		String[] array = splitValues(columns);
		return primaryKey(name, array);
	}

	/**
	 * Primary key.
	 *
	 * @param columns
	 *            the columns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder primaryKey(String[] columns) {
		logger.trace("primaryKey: <unnamed>, columns" + Arrays.toString(columns));
		return primaryKey(null, columns);
	}

	/**
	 * Primary key.
	 *
	 * @param columns
	 *            the columns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder primaryKey(String columns) {
		logger.trace("primaryKey: <unnamed>, columns" + columns);
		return primaryKey(null, splitValues(columns));
	}

	/**
	 * Foreign key.
	 *
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 * @param referencedTable
	 *            the referenced table
	 * @param referencedColumns
	 *            the referenced columns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder foreignKey(String name, String[] columns, String referencedTable, String[] referencedColumns) {
		logger.trace("foreignKey: " + name + ", columns" + Arrays.toString(columns) + ", referencedTable: " + referencedTable
				+ ", referencedColumns: " + Arrays.toString(referencedColumns));
		CreateTableForeignKeyBuilder foreignKey = new CreateTableForeignKeyBuilder(this.getDialect(), name);
		for (String column : columns) {
			foreignKey.column(column);
		}
		foreignKey.referencedTable(referencedTable);
		for (String column : referencedColumns) {
			foreignKey.referencedColumn(column);
		}
		this.foreignKeys.add(foreignKey);
		return this;
	}

	/**
	 * Foreign key.
	 *
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 * @param referencedTable
	 *            the referenced table
	 * @param referencedColumns
	 *            the referenced columns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder foreignKey(String name, String columns, String referencedTable, String referencedColumns) {
		logger.trace("foreignKey: " + name + ", columns" + columns + ", referencedTable: " + referencedTable + ", referencedColumns: "
				+ referencedColumns);
		return foreignKey(name, splitValues(columns), referencedTable, splitValues(referencedColumns));
	}

	/**
	 * Unique.
	 *
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder unique(String name, String[] columns) {
		logger.trace("unique: " + name + ", columns" + Arrays.toString(columns));
		CreateTableUniqueIndexBuilder uniqueIndex = new CreateTableUniqueIndexBuilder(this.getDialect(), name);
		for (String column : columns) {
			uniqueIndex.column(column);
		}
		this.uniqueIndices.add(uniqueIndex);
		return this;
	}

	/**
	 * Unique.
	 *
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 * @return the creates the table builder
	 */
	public CreateTableBuilder unique(String name, String columns) {
		logger.trace("unique: " + name + ", columns" + columns);
		return unique(name, splitValues(columns));
	}

	/**
	 * Check.
	 *
	 * @param name
	 *            the name
	 * @param expression
	 *            the expression
	 * @return the creates the table builder
	 */
	public CreateTableBuilder check(String name, String expression) {
		logger.trace("check: " + name + ", expression" + expression);
		CreateTableCheckBuilder check = new CreateTableCheckBuilder(this.getDialect(), name);
		check.expression(expression);
		this.checks.add(check);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// CREATE
		generateCreate(sql);

		// TABLE
		generateTable(sql);

		sql.append(SPACE).append(OPEN);

		// COLUMNS
		generateColumns(sql);

		// PRIMARY KEY
		generatePrimaryKey(sql);

		// FOREIGN KEYS
		generateForeignKeys(sql);

		// UNIQUE INDICES
		generateUniqueIndices(sql);

		// INDICES
		generateChecks(sql);

		sql.append(CLOSE);

		String generated = sql.toString();

		logger.trace("generated: " + generated);

		return generated;
	}

	/**
	 * Generate primary key.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generatePrimaryKey(StringBuilder sql) {
		if (this.primaryKey != null) {
			sql.append(COMMA).append(SPACE);
			if (this.primaryKey.getName() != null) {
				sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(this.primaryKey.getName()).append(SPACE);
			}
			sql.append(KEYWORD_PRIMARY).append(SPACE).append(KEYWORD_KEY).append(SPACE).append(OPEN)
					.append(traverseColumnNames(this.primaryKey.getColumns())).append(CLOSE);
		}
	}

	/**
	 * Generate foreign keys.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateForeignKeys(StringBuilder sql) {
		for (CreateTableForeignKeyBuilder foreignKey : this.foreignKeys) {
			generateForeignKey(sql, foreignKey);
		}
	}

	/**
	 * Generate foreign key.
	 *
	 * @param sql
	 *            the sql
	 * @param foreignKey
	 *            the foreign key
	 */
	protected void generateForeignKey(StringBuilder sql, CreateTableForeignKeyBuilder foreignKey) {
		if (foreignKey != null) {
			sql.append(COMMA).append(SPACE);
			if (foreignKey.getName() != null) {
				sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(foreignKey.getName()).append(SPACE);
			}
			sql.append(KEYWORD_FOREIGN).append(SPACE).append(KEYWORD_KEY).append(SPACE).append(OPEN)
					.append(traverseColumnNames(foreignKey.getColumns())).append(CLOSE).append(SPACE).append(KEYWORD_REFERENCES).append(SPACE)
					.append(foreignKey.getReferencedTable()).append(OPEN).append(traverseColumnNames(foreignKey.getReferencedColumns()))
					.append(CLOSE);
		}
	}

	/**
	 * Generate unique indices.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateUniqueIndices(StringBuilder sql) {
		for (CreateTableUniqueIndexBuilder uniqueIndex : this.uniqueIndices) {
			generateUniqueIndex(sql, uniqueIndex);
		}
	}

	/**
	 * Generate unique index.
	 *
	 * @param sql
	 *            the sql
	 * @param uniqueIndex
	 *            the unique index
	 */
	protected void generateUniqueIndex(StringBuilder sql, CreateTableUniqueIndexBuilder uniqueIndex) {
		if (uniqueIndex != null) {
			sql.append(COMMA).append(SPACE);
			if (uniqueIndex.getName() != null) {
				sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(uniqueIndex.getName()).append(SPACE);
			}
			sql.append(KEYWORD_UNIQUE).append(SPACE).append(OPEN).append(traverseColumnNames(uniqueIndex.getColumns())).append(CLOSE);
		}
	}

	/**
	 * Generate checks.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateChecks(StringBuilder sql) {
		for (CreateTableCheckBuilder index : this.checks) {
			generateCheck(sql, index);
		}
	}

	/**
	 * Generate check.
	 *
	 * @param sql
	 *            the sql
	 * @param check
	 *            the check
	 */
	protected void generateCheck(StringBuilder sql, CreateTableCheckBuilder check) {
		if (check != null) {
			sql.append(COMMA).append(SPACE);
			if (check.getName() != null) {
				sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(check.getName()).append(SPACE);
			}
			sql.append(KEYWORD_CHECK).append(SPACE).append(OPEN).append(check.getExpression()).append(CLOSE);
		}
	}

	/**
	 * Traverse columns.
	 *
	 * @return the string
	 */
	protected String traverseColumns() {
		StringBuilder snippet = new StringBuilder();
		snippet.append(SPACE);
		for (String[] column : this.getColumns()) {
			for (String arg : column) {
				snippet.append(arg).append(SPACE);
			}
			snippet.append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	/**
	 * Traverse column names.
	 *
	 * @param columns
	 *            the columns
	 * @return the string
	 */
	protected String traverseColumnNames(Set<String> columns) {
		StringBuilder snippet = new StringBuilder();
		snippet.append(SPACE);
		for (String column : columns) {
			snippet.append(column).append(SPACE).append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	/**
	 * Split values.
	 *
	 * @param columns
	 *            the columns
	 * @return the string[]
	 */
	private String[] splitValues(String columns) {
		String[] array = new String[] {};
		if (columns != null) {
			array = columns.split(",");
		}
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].trim();
		}
		return array;
	}

}
