/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

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

	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
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

	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return column(name, type, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey, Boolean isNullable) {
		return column(name, type, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey) {
		return column(name, type, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder column(String name, DataType type) {
		return column(name, type, false, true, false, new String[] {});
	}

	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return column(name, DataType.values()[type], isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return column(name, DataType.values()[type], isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey, Boolean isNullable) {
		return column(name, DataType.values()[type], isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey) {
		return column(name, DataType.values()[type], isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder column(String name, int type) {
		return column(name, DataType.values()[type], false, true, false, new String[] {});
	}

	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return column(name, DataType.values()[type], isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey, Boolean isNullable) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder column(String name, Double type) {
		return column(name, DataType.values()[type.intValue()], false, true, false, new String[] {});
	}

	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return column(name, DataType.valueOf(type), isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return column(name, DataType.valueOf(type), isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey, Boolean isNullable) {
		return column(name, DataType.valueOf(type), isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey) {
		return column(name, DataType.valueOf(type), isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder column(String name, String type) {
		return column(name, DataType.valueOf(type), false, true, false, new String[] {});
	}

	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return column(name, DataType.valueOf(type), isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		String[] definition = new String[] { OPEN + length + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.VARCHAR, isPrimaryKey, isNullable, isUnique, coulmn);
	}

	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnVarchar(name, length, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnVarchar(name, length, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable) {
		return columnVarchar(name, length, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey) {
		return columnVarchar(name, length, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnVarchar(String name, int length) {
		return columnVarchar(name, length, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnVarchar(String name, Double length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnVarchar(name, length.intValue(), isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnVarchar(String name, Double length, Boolean isPrimaryKey, Boolean isNullable) {
		return columnVarchar(name, length.intValue(), isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnVarchar(String name, Double length, Boolean isPrimaryKey) {
		return columnVarchar(name, length.intValue(), isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnVarchar(String name, Double length) {
		return columnVarchar(name, length.intValue(), false, true, false, new String[] {});
	}

	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		String[] definition = new String[] { OPEN + length + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.CHAR, isPrimaryKey, isNullable, isUnique, coulmn);
	}

	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnChar(name, length, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnChar(name, length, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey, Boolean isNullable) {
		return columnChar(name, length, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey) {
		return columnChar(name, length, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnChar(String name, int length) {
		return columnChar(name, length, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnChar(String name, Double length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnChar(name, length.intValue(), isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnChar(String name, Double length, Boolean isPrimaryKey, Boolean isNullable) {
		return columnChar(name, length.intValue(), isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnChar(String name, Double length, Boolean isPrimaryKey) {
		return columnChar(name, length.intValue(), isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnChar(String name, Double length) {
		return columnChar(name, length.intValue(), false, true, false, new String[] {});
	}

	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.DATE, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnDate(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnDate(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnDate(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey) {
		return columnDate(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnDate(String name) {
		return columnDate(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.TIME, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnTime(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnTime(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnTime(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey) {
		return columnTime(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnTime(String name) {
		return columnTime(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.TIMESTAMP, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnTimestamp(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnTimestamp(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnTimestamp(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey) {
		return columnTimestamp(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnTimestamp(String name) {
		return columnTimestamp(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.INTEGER, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnInteger(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnInteger(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnInteger(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey) {
		return columnInteger(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnInteger(String name) {
		return columnInteger(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.TINYINT, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnTinyint(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnTinyint(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnTinyint(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey) {
		return columnTinyint(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnTinyint(String name) {
		return columnTinyint(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.BIGINT, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnBigint(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnBigint(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnBigint(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey) {
		return columnBigint(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnBigint(String name) {
		return columnBigint(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.SMALLINT, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnSmallint(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnSmallint(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnSmallint(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey) {
		return columnSmallint(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnSmallint(String name) {
		return columnSmallint(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.REAL, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnReal(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnReal(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnReal(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey) {
		return columnReal(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnReal(String name) {
		return columnSmallint(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.DOUBLE, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnDouble(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnDouble(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnDouble(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey) {
		return columnDouble(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnDouble(String name) {
		return columnSmallint(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.BOOLEAN, isPrimaryKey, isNullable, isUnique, args);
	}

	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnBoolean(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnBoolean(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnBoolean(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey) {
		return columnBoolean(name, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnBoolean(String name) {
		return columnBoolean(name, false, true, false, new String[] {});
	}

	public CreateTableBuilder columnBlob(String name, Boolean isNullable, String... args) {
		return this.column(name, DataType.BLOB, false, isNullable, false, args);
	}

	public CreateTableBuilder columnBlob(String name, Boolean isNullable, String args) {
		return columnBlob(name, isNullable, splitValues(args));
	}

	public CreateTableBuilder columnBlob(String name, Boolean isNullable) {
		return columnBlob(name, isNullable, new String[] {});
	}

	public CreateTableBuilder columnBlob(String name) {
		return columnBlob(name, false, new String[] {});
	}

	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique,
			String... args) {
		String[] definition = new String[] { OPEN + precision + "," + scale + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.CHAR, isPrimaryKey, isNullable, isUnique, coulmn);
	}

	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique,
			String args) {
		return columnDecimal(name, precision, scale, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnDecimal(name, precision, scale, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey, Boolean isNullable) {
		return columnDecimal(name, precision, scale, isPrimaryKey, isNullable, false, new String[] {});
	}

	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey) {
		return columnDecimal(name, precision, scale, isPrimaryKey, true, false, new String[] {});
	}

	public CreateTableBuilder columnDecimal(String name, int precision, int scale) {
		return columnDecimal(name, precision, scale, false, true, false, new String[] {});
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

	public CreateTableBuilder primaryKey(String name, String columns) {
		String[] array = splitValues(columns);
		return primaryKey(name, array);
	}

	public CreateTableBuilder primaryKey(String[] columns) {
		return primaryKey(null, columns);
	}

	public CreateTableBuilder primaryKey(String columns) {
		return primaryKey(null, splitValues(columns));
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

	public CreateTableBuilder foreignKey(String name, String columns, String referencedTable, String referencedColumns) {
		return foreignKey(name, splitValues(columns), referencedTable, splitValues(referencedColumns));
	}

	public CreateTableBuilder unique(String name, String[] columns) {
		CreateTableUniqueIndexBuilder uniqueIndex = new CreateTableUniqueIndexBuilder(this.getDialect(), name);
		for (String column : columns) {
			uniqueIndex.column(column);
		}
		this.uniqueIndices.add(uniqueIndex);
		return this;
	}

	public CreateTableBuilder unique(String name, String columns) {
		return unique(name, splitValues(columns));
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
