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
public class CreateTableBuilder extends AbstractCreateSqlBuilder {

	private static final Logger logger = LoggerFactory.getLogger(CreateTableBuilder.class);

	private String table = null;

	private List<String[]> columns = new ArrayList<String[]>();

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
		super(dialect);
		this.table = table;
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	protected String getTable() {
		return table;
	}

	/**
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	protected List<String[]> getColumns() {
		return columns;
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity,
			String... args) {
		logger.trace("column: " + name + ", type: " + (type != null ? type.name() : null) + ", isPrimaryKey: " + isPrimaryKey + ", isNullable: "
				+ isNullable + ", isUnique: " + isUnique + ", isIdentity: " + isIdentity + ", args: " + Arrays.toString(args));
		String[] definition = new String[] { name, getDialect().getDataTypeName(type) };
		String[] column = null;
		if (isIdentity) {
			column = Stream.of(definition, args, new String[] { getDialect().getIdentityArgument() }).flatMap(Stream::of).toArray(String[]::new);
		} else {
			column = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		}
		if (!isNullable) {
			column = Stream.of(column, new String[] { getDialect().getNotNullArgument() }).flatMap(Stream::of).toArray(String[]::new);
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

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return column(name, type, isPrimaryKey, isNullable, isUnique, false, args);
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return column(name, type, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey, Boolean isNullable) {
		return column(name, type, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, DataType type, Boolean isPrimaryKey) {
		return column(name, type, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, DataType type) {
		return column(name, type, false, true, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return column(name, DataType.values()[type], isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return column(name, DataType.values()[type], isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey, Boolean isNullable) {
		return column(name, DataType.values()[type], isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey) {
		return column(name, DataType.values()[type], isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, int type) {
		return column(name, DataType.values()[type], false, true, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, int type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return column(name, DataType.values()[type], isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey, Boolean isNullable) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, Double type) {
		return column(name, DataType.values()[type.intValue()], false, true, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, Double type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return column(name, DataType.values()[type.intValue()], isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return column(name, DataType.valueOf(type), isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return column(name, DataType.valueOf(type), isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey, Boolean isNullable) {
		return column(name, DataType.valueOf(type), isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey) {
		return column(name, DataType.valueOf(type), isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, String type) {
		return column(name, DataType.valueOf(type), false, true, false, new String[] {});
	}

	/**
	 * Column.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder column(String name, String type, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return column(name, DataType.valueOf(type), isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column varchar.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity,
			String... args) {
		String[] definition = new String[] { OPEN + length + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.VARCHAR, isPrimaryKey, isNullable, isUnique, isIdentity, coulmn);
	}

	/**
	 * Column varchar.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity,
			String args) {
		return columnVarchar(name, length, isPrimaryKey, isNullable, isUnique, isIdentity, splitValues(args));
	}

	/**
	 * Column varchar.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity) {
		return columnVarchar(name, length, isPrimaryKey, isNullable, isUnique, isIdentity, new String[] {});
	}

	/**
	 * Column varchar.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnVarchar(name, length, isPrimaryKey, isNullable, isUnique, false);
	}

	/**
	 * Column varchar.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey, Boolean isNullable) {
		return columnVarchar(name, length, isPrimaryKey, isNullable, false);
	}

	/**
	 * Column varchar.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnVarchar(String name, int length, Boolean isPrimaryKey) {
		return columnVarchar(name, length, isPrimaryKey, true);
	}

	/**
	 * Column varchar.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnVarchar(String name, int length) {
		return columnVarchar(name, length, false);
	}

	/**
	 * Column char.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity,
			String... args) {
		String[] definition = new String[] { OPEN + length + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.CHAR, isPrimaryKey, isNullable, isUnique, isIdentity, coulmn);
	}

	/**
	 * Column char.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnChar(name, length, isPrimaryKey, isNullable, isUnique, false, splitValues(args));
	}

	/**
	 * Column char.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnChar(name, length, isPrimaryKey, isNullable, isUnique, false, new String[] {});
	}

	/**
	 * Column char.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey, Boolean isNullable) {
		return columnChar(name, length, isPrimaryKey, isNullable, false);
	}

	/**
	 * Column char.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnChar(String name, int length, Boolean isPrimaryKey) {
		return columnChar(name, length, isPrimaryKey, true);
	}

	/**
	 * Column char.
	 *
	 * @param name
	 *            the name
	 * @param length
	 *            the length
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnChar(String name, int length) {
		return columnChar(name, length, false);
	}

	/**
	 * Column date.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.DATE, isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column date.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnDate(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column date.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnDate(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column date.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnDate(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column date.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDate(String name, Boolean isPrimaryKey) {
		return columnDate(name, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column date.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDate(String name) {
		return columnDate(name, false, true, false, new String[] {});
	}

	/**
	 * Column time.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.TIME, isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column time.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnTime(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column time.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnTime(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column time.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnTime(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column time.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTime(String name, Boolean isPrimaryKey) {
		return columnTime(name, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column time.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTime(String name) {
		return columnTime(name, false, true, false, new String[] {});
	}

	/**
	 * Column timestamp.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.TIMESTAMP, isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column timestamp.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnTimestamp(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column timestamp.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnTimestamp(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column timestamp.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnTimestamp(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column timestamp.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTimestamp(String name, Boolean isPrimaryKey) {
		return columnTimestamp(name, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column timestamp.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTimestamp(String name) {
		return columnTimestamp(name, false, true, false, new String[] {});
	}

	/**
	 * Column integer.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity,
			String... args) {
		return this.column(name, DataType.INTEGER, isPrimaryKey, isNullable, isUnique, isIdentity, args);
	}

	/**
	 * Column integer.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity,
			String args) {
		return columnInteger(name, isPrimaryKey, isNullable, isUnique, isIdentity, splitValues(args));
	}

	/**
	 * Column integer.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnInteger(name, isPrimaryKey, isNullable, isUnique, false, new String[] {});
	}

	/**
	 * Column integer.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnInteger(name, isPrimaryKey, isNullable, false);
	}

	/**
	 * Column integer.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnInteger(String name, Boolean isPrimaryKey) {
		return columnInteger(name, isPrimaryKey, true);
	}

	/**
	 * Column integer.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnInteger(String name) {
		return columnInteger(name, false);
	}

	/**
	 * Column tinyint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.TINYINT, isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column tinyint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnTinyint(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column tinyint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnTinyint(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column tinyint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnTinyint(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column tinyint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTinyint(String name, Boolean isPrimaryKey) {
		return columnTinyint(name, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column tinyint.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTinyint(String name) {
		return columnTinyint(name, false, true, false, new String[] {});
	}

	/**
	 * Column bigint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity,
			String... args) {
		return this.column(name, DataType.BIGINT, isPrimaryKey, isNullable, isUnique, isIdentity, args);
	}

	/**
	 * Column bigint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, Boolean isIdentity, String args) {
		return columnBigint(name, isPrimaryKey, isNullable, isUnique, isIdentity, splitValues(args));
	}

	/**
	 * Column bigint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnBigint(name, isPrimaryKey, isNullable, isUnique, false, new String[] {});
	}

	/**
	 * Column bigint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnBigint(name, isPrimaryKey, isNullable, false);
	}

	/**
	 * Column bigint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBigint(String name, Boolean isPrimaryKey) {
		return columnBigint(name, isPrimaryKey, true);
	}

	/**
	 * Column bigint.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBigint(String name) {
		return columnBigint(name, false);
	}

	/**
	 * Column smallint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.SMALLINT, isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column smallint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnSmallint(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column smallint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnSmallint(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column smallint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnSmallint(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column smallint.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnSmallint(String name, Boolean isPrimaryKey) {
		return columnSmallint(name, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column smallint.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnSmallint(String name) {
		return columnSmallint(name, false, true, false, new String[] {});
	}

	/**
	 * Column real.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.REAL, isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column real.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnReal(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column real.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnReal(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column real.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnReal(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column real.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnReal(String name, Boolean isPrimaryKey) {
		return columnReal(name, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column real.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnReal(String name) {
		return columnSmallint(name, false, true, false, new String[] {});
	}

	/**
	 * Column double.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.DOUBLE, isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column double.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnDouble(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column double.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnDouble(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column double.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnDouble(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column double.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDouble(String name, Boolean isPrimaryKey) {
		return columnDouble(name, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column double.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDouble(String name) {
		return columnSmallint(name, false, true, false, new String[] {});
	}

	/**
	 * Column boolean.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String... args) {
		return this.column(name, DataType.BOOLEAN, isPrimaryKey, isNullable, isUnique, args);
	}

	/**
	 * Column boolean.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique, String args) {
		return columnBoolean(name, isPrimaryKey, isNullable, isUnique, splitValues(args));
	}

	/**
	 * Column boolean.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnBoolean(name, isPrimaryKey, isNullable, isUnique, new String[] {});
	}

	/**
	 * Column boolean.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey, Boolean isNullable) {
		return columnBoolean(name, isPrimaryKey, isNullable, false, new String[] {});
	}

	/**
	 * Column boolean.
	 *
	 * @param name
	 *            the name
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBoolean(String name, Boolean isPrimaryKey) {
		return columnBoolean(name, isPrimaryKey, true, false, new String[] {});
	}

	/**
	 * Column boolean.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBoolean(String name) {
		return columnBoolean(name, false, true, false, new String[] {});
	}

	/**
	 * Column blob.
	 *
	 * @param name
	 *            the name
	 * @param isNullable
	 *            the is nullable
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBlob(String name, Boolean isNullable, String... args) {
		return this.column(name, DataType.BLOB, false, isNullable, false, args);
	}

	/**
	 * Column blob.
	 *
	 * @param name
	 *            the name
	 * @param isNullable
	 *            the is nullable
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBlob(String name, Boolean isNullable, String args) {
		return columnBlob(name, isNullable, splitValues(args));
	}

	/**
	 * Column blob.
	 *
	 * @param name
	 *            the name
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBlob(String name, Boolean isNullable) {
		return columnBlob(name, isNullable, new String[] {});
	}

	/**
	 * Column blob.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBlob(String name) {
		return columnBlob(name, false, new String[] {});
	}

	/**
	 * Column decimal.
	 *
	 * @param name
	 *            the name
	 * @param precision
	 *            the precision
	 * @param scale
	 *            the scale
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique,
			Boolean isIdentity, String... args) {
		String[] definition = new String[] { OPEN + precision + "," + scale + CLOSE };
		String[] coulmn = Stream.of(definition, args).flatMap(Stream::of).toArray(String[]::new);
		return this.column(name, DataType.DECIMAL, isPrimaryKey, isNullable, isUnique, isIdentity, coulmn);
	}

	/**
	 * Column decimal.
	 *
	 * @param name
	 *            the name
	 * @param precision
	 *            the precision
	 * @param scale
	 *            the scale
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @param isIdentity
	 *            the is identity
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique,
			Boolean isIdentity, String args) {
		return columnDecimal(name, precision, scale, isPrimaryKey, isNullable, isUnique, isIdentity, splitValues(args));
	}

	/**
	 * Column decimal.
	 *
	 * @param name
	 *            the name
	 * @param precision
	 *            the precision
	 * @param scale
	 *            the scale
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @param isUnique
	 *            the is unique
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey, Boolean isNullable, Boolean isUnique) {
		return columnDecimal(name, precision, scale, isPrimaryKey, isNullable, isUnique, false, new String[] {});
	}

	/**
	 * Column decimal.
	 *
	 * @param name
	 *            the name
	 * @param precision
	 *            the precision
	 * @param scale
	 *            the scale
	 * @param isPrimaryKey
	 *            the is primary key
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey, Boolean isNullable) {
		return columnDecimal(name, precision, scale, isPrimaryKey, isNullable, false);
	}

	/**
	 * Column decimal.
	 *
	 * @param name
	 *            the name
	 * @param precision
	 *            the precision
	 * @param scale
	 *            the scale
	 * @param isPrimaryKey
	 *            the is primary key
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDecimal(String name, int precision, int scale, Boolean isPrimaryKey) {
		return columnDecimal(name, precision, scale, isPrimaryKey, true);
	}

	/**
	 * Column decimal.
	 *
	 * @param name
	 *            the name
	 * @param precision
	 *            the precision
	 * @param scale
	 *            the scale
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnDecimal(String name, int precision, int scale) {
		return columnDecimal(name, precision, scale, false);
	}
	
	/**
	 * Column bit.
	 *
	 * @param name
	 *            the name
	 * @param isNullable
	 *            the is nullable
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBit(String name, Boolean isNullable, String... args) {
		return this.column(name, DataType.BIT, false, isNullable, false, args);
	}

	/**
	 * Column bit.
	 *
	 * @param name
	 *            the name
	 * @param isNullable
	 *            the is nullable
	 * @param args
	 *            the args
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBit(String name, Boolean isNullable, String args) {
		return columnBit(name, isNullable, splitValues(args));
	}

	/**
	 * Column bit.
	 *
	 * @param name
	 *            the name
	 * @param isNullable
	 *            the is nullable
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBit(String name, Boolean isNullable) {
		return columnBit(name, isNullable, new String[] {});
	}

	/**
	 * Column bit.
	 *
	 * @param name
	 *            the name
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnBit(String name) {
		return columnBit(name, false, new String[] {});
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
	 * Generate table.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_TABLE).append(SPACE).append(this.table);
	}

	/**
	 * Generate columns.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateColumns(StringBuilder sql) {
		if (!this.columns.isEmpty()) {
			sql.append(traverseColumns());
		}
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
		for (String[] column : this.columns) {
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
