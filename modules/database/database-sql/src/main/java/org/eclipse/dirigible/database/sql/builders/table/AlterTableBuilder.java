/**
 * Copyright (c) 2010-2020 SAP and others.
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
	
	private List<String[]> foreignKeys = new ArrayList<String[]>();

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
	public List<String[]> getForeignKeys() {
		return foreignKeys;
	}
	
	/**
	 * Foreign Key
	 * 
	 * @param name the name of the foreign key
	 * @param columns the local columns
	 * @param referenceTable the reference table
	 * @param referenceColumns the referenced columns
	 * @return the AlterTableBuilder object
	 */
	public AlterTableBuilder foreignKey(String name, String columns, String referenceTable, String referenceColumns) {
		logger.trace("foreignKey: " + name + ", columns: " + columns + ", referenceTable: " + referenceTable + ", referenceColumns: " + referenceColumns);
		String[] foreignKey = new String[] { name,  columns, referenceTable, referenceColumns};
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
			sql.append(KEYWORD_DROP);
			if (!getColumns().isEmpty()) {
				// COLUMNS
				generateColumnNames(sql);
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

	private void generateForeignKeys(StringBuilder sql) {
		if (this.foreignKeys.isEmpty()) {
			throw new IllegalArgumentException("Foreign keys array is empty while trying to alter a table foreign keys - " + this.getTable());
		}
		StringBuilder snippet = new StringBuilder();
		snippet.append(SPACE).append(KEYWORD_CONSTRAINT)
			.append(SPACE).append(this.foreignKeys.get(0))
			.append(SPACE).append(KEYWORD_FOREIGN).append(SPACE).append(KEYWORD_KEY)
			.append(SPACE).append("(" + this.foreignKeys.get(1) + ")")
			.append(SPACE).append(KEYWORD_REFERENCES)
			.append(SPACE).append(this.foreignKeys.get(2)).append("(" + this.foreignKeys.get(3) + ")");
		sql.append(snippet.toString());
	}
	
	private void generateForeignKeyNames(StringBuilder sql) {
		StringBuilder snippet = new StringBuilder();
		snippet.append(SPACE).append(KEYWORD_FOREIGN).append(SPACE).append(KEYWORD_KEY)
			.append(SPACE).append(this.foreignKeys.get(0));
		sql.append(snippet.toString());		
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
