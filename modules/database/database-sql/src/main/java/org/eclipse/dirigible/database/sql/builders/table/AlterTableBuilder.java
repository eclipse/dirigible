/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.builders.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Create Table Builder.
 */
public class AlterTableBuilder extends AbstractTableBuilder<AlterTableBuilder> {

	private static final Logger logger = LoggerFactory.getLogger(AlterTableBuilder.class);

	private String action = null;
	
	private List<CreateTableForeignKeyBuilder> foreignKeys = new ArrayList<CreateTableForeignKeyBuilder>();

	/**
	 * Instantiates a new creates the table builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param table
	 *            the table
	 */
	public AlterTableBuilder(ISqlDialect dialect, String table) {
		super(dialect, table);
	}
	
	public AlterTableBuilder add() {
		this.action = KEYWORD_ADD;
		return this;
	}
	
	public AlterTableBuilder drop() {
		this.action = KEYWORD_DROP;
		return this;
	}
	
	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	protected String getAction() {
		return action;
	}
	
	/**
	 * Gets the foreignKeys list
	 * 
	 * @return the foreignKeys
	 */
	public List<CreateTableForeignKeyBuilder> getForeignKeys() {
		return foreignKeys;
	}
	
	/**
	 * Foreign Key
	 * 
	 * @param name the name of the foreign key
	 * @param columns the local columns
	 * @param referencedTable the reference table
	 * @param referencedColumns the referenced columns
	 * @return the AlterTableBuilder object
	 */
	public AlterTableBuilder foreignKey(String name, String[] columns, String referencedTable, String[] referencedColumns) {
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// ALTER
		generateAlter(sql);

		// TABLE
		generateTable(sql);

		sql.append(SPACE);

		if (KEYWORD_ADD.equals(this.action)) {
			sql.append(KEYWORD_ADD);
			if (!getColumns().isEmpty()) {
				// COLUMNS
				generateColumns(sql);
			} else if (!getForeignKeys().isEmpty()) {
				// FOREIGN KEYS
				generateForeignKeys(sql);
			}
		} else if (KEYWORD_DROP.equals(this.action)) {
			if (!getColumns().isEmpty()) {
				// COLUMNS
				generateColumnNamesForDrop(sql);
			} else if (!getForeignKeys().isEmpty()) {
				// FOREIGN KEYS
				generateForeignKeyNames(sql);
			}
		} else {
			logger.error("Action for alter table must be present and can be only ADD or DROP.");
		}

		String generated = sql.toString().trim();

		logger.trace("generated: " + generated);

		return generated;
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
			sql.append(SPACE);
			if (foreignKey.getName() != null) {
				String foreignKeyName = (isCaseSensitive()) ? encapsulate(foreignKey.getName()) : foreignKey.getName();
				sql.append(KEYWORD_CONSTRAINT).append(SPACE).append(foreignKeyName).append(SPACE);
			}
			String referencedTableName = (isCaseSensitive()) ? encapsulate(foreignKey.getReferencedTable()) : foreignKey.getReferencedTable();
			sql.append(KEYWORD_FOREIGN).append(SPACE).append(KEYWORD_KEY).append(SPACE).append(OPEN)
					.append(traverseNames(foreignKey.getColumns())).append(CLOSE).append(SPACE).append(KEYWORD_REFERENCES).append(SPACE)
					.append(referencedTableName).append(OPEN).append(traverseNames(foreignKey.getReferencedColumns()))
					.append(CLOSE);
		}
	}
	
	private void generateForeignKeyNames(StringBuilder sql) {
		StringBuilder snippet = new StringBuilder();
		for (CreateTableForeignKeyBuilder foreignKey : this.foreignKeys) {
			String foreignKeyName = (isCaseSensitive()) ? encapsulate(foreignKey.getName()) : foreignKey.getName();
			snippet.append(KEYWORD_DROP).append(SPACE).append(KEYWORD_CONSTRAINT).append(SPACE);
			snippet.append(foreignKeyName).append(SPACE);
			snippet.append(COMMA).append(SPACE);
		}
		sql.append(snippet.toString().substring(0, snippet.length() - 2));
	}

//	/**
//	 * Traverse columns.
//	 *
//	 * @return the string
//	 */
//	protected String traverseColumns() {
//		StringBuilder snippet = new StringBuilder();
//		snippet.append(SPACE);
//		for (String[] column : this.getColumns()) {
//			snippet.append(ALTER).append(SPACE);
//			for (String arg : column) {
//				snippet.append(arg).append(SPACE);
//			}
//			snippet.append(COMMA).append(SPACE);
//		}
//		return snippet.toString().substring(0, snippet.length() - 2);
//	}

}
