/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql.builders.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlException;
import org.eclipse.dirigible.database.sql.builders.AbstractCreateSqlBuilder;

public class CreateTableBuilder extends AbstractCreateSqlBuilder {

	private String table = null;
	private List<String[]> columns = new ArrayList<String[]>();
	private CreateTablePrimaryKeyBuilder primaryKey;
	private List<CreateTableForeignKeyBuilder> foreignKeys = new ArrayList<CreateTableForeignKeyBuilder>();
	private List<CreateTableUniqueIndexBuilder> uniqueIndices = new ArrayList<CreateTableUniqueIndexBuilder>();
	private List<CreateTableCheckBuilder> checks = new ArrayList<CreateTableCheckBuilder>();

	public CreateTableBuilder(ISqlDialect dialect, String table) {
		super(dialect);
		this.table = table;
	}

	protected String getTable() {
		return table;
	}

	protected List<String[]> getColumns() {
		return columns;
	}

	public CreateTableBuilder column(String name, DataType type, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		String[] definition = new String[] { name, getDialect().getDataTypeName(type) };
		String[] column = null;
		if (!isNullable) {
			column = Stream.of(definition, args, new String[] { getDialect().getNotNullArgument() }).flatMap(Stream::of).toArray(String[]::new);
		} else {
			column = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		}
		if (isPrimaryKey) {
			column = Stream.of(column, new String[] { getDialect().getPrimaryKeyArgument() }).flatMap(Stream::of).toArray(String[]::new);
		}
		if (isUnique && !isPrimaryKey) {
			column = Stream.of(column, new String[] { getDialect().getUniqueArgument() }).flatMap(Stream::of).toArray(String[]::new);
		}

		this.columns.add(column);
		return this;
	}

	public CreateTableBuilder columnVarchar(String name, int length, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		String[] definition = new String[] { OPEN + length + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.VARCHAR, isPrimaryKey, isNullable, isUnique, coulmn);
	}

	public CreateTableBuilder columnChar(String name, int length, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		String[] definition = new String[] { OPEN + length + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.CHAR, isPrimaryKey, isNullable, isUnique, coulmn);
	}

	public CreateTableBuilder columnDate(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.DATE, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnTime(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.TIME, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnTimestamp(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.TIMESTAMP, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnInteger(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.INTEGER, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnTinyint(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.TINYINT, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnBigint(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.BIGINT, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnReal(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.REAL, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnDouble(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.DOUBLE, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnBoolean(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, String... args) {
		return this.column(name, DataType.BOOLEAN, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnBlob(String name, boolean isNullable, String... args) {
		return this.column(name, DataType.BLOB, false, isNullable, false, args);
	}

	public CreateTableBuilder columnDecimal(String name, boolean isPrimaryKey, boolean isNullable, boolean isUnique, int precision, int scale,
			String... args) {
		String[] definition = new String[] { OPEN + precision + "," + scale + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.CHAR, isPrimaryKey, isNullable, isUnique, coulmn);
	}

	public CreateTableBuilder primaryKey(String name, String[] columns) {
		if (this.primaryKey != null) {
			throw new SqlException("Setting of primary key must be called only once");
		}
		this.primaryKey = new CreateTablePrimaryKeyBuilder(this.getDialect(), name);
		for (String column : columns) {
			this.primaryKey.column(column);
		}
		return this;
	}

	public CreateTableBuilder primaryKey(String[] columns) {
		return primaryKey(null, columns);
	}

	public CreateTableBuilder foreignKey(String name, String[] columns, String referencedTable, String[] referencedColumns) {
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

	public CreateTableBuilder unique(String name, String[] columns) {
		CreateTableUniqueIndexBuilder uniqueIndex = new CreateTableUniqueIndexBuilder(this.getDialect(), name);
		for (String column : columns) {
			uniqueIndex.column(column);
		}
		this.uniqueIndices.add(uniqueIndex);
		return this;
	}

	public CreateTableBuilder check(String name, String expression) {
		CreateTableCheckBuilder check = new CreateTableCheckBuilder(this.getDialect(), name);
		check.expression(expression);
		this.checks.add(check);
		return this;
	}

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

		return sql.toString();
	}

	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_TABLE).append(SPACE).append(this.table);
	}

	protected void generateColumns(StringBuilder sql) {
		if (!this.columns.isEmpty()) {
			sql.append(traverseColumns());
		}
	}

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

	protected void generateForeignKeys(StringBuilder sql) {
		for (CreateTableForeignKeyBuilder foreignKey : this.foreignKeys) {
			generateForeignKey(sql, foreignKey);
		}
	}

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

	protected void generateUniqueIndices(StringBuilder sql) {
		for (CreateTableUniqueIndexBuilder uniqueIndex : this.uniqueIndices) {
			generateUniqueIndex(sql, uniqueIndex);
		}
	}

	protected void generateUniqueIndex(StringBuilder sql, CreateTableUniqueIndexBuilder uniqueIndex) {
		if (uniqueIndex != null) {
			sql.append(COMMA).append(SPACE);
			if (uniqueIndex.getName() != null) {
				sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(uniqueIndex.getName()).append(SPACE);
			}
			sql.append(KEYWORD_UNIQUE).append(SPACE).append(OPEN).append(traverseColumnNames(uniqueIndex.getColumns())).append(CLOSE);
		}
	}

	protected void generateChecks(StringBuilder sql) {
		for (CreateTableCheckBuilder index : this.checks) {
			generateCheck(sql, index);
		}
	}

	protected void generateCheck(StringBuilder sql, CreateTableCheckBuilder check) {
		if (check != null) {
			sql.append(COMMA).append(SPACE);
			if (check.getName() != null) {
				sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(check.getName()).append(SPACE);
			}
			sql.append(KEYWORD_CHECK).append(SPACE).append(OPEN).append(check.getExpression()).append(CLOSE);
		}
	}

	protected String traverseColumns() {
		StringBuilder snippet = new StringBuilder();
		snippet.append(SPACE);
		for (String[] column : this.columns) {
			for (String arg : column) {
				snippet.append(arg).append(SPACE);
			}
			snippet.append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	protected String traverseColumnNames(Set<String> columns) {
		StringBuilder snippet = new StringBuilder();
		snippet.append(SPACE);
		for (String column : columns) {
			snippet.append(column).append(SPACE).append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

}
