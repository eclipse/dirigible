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

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Create Table Builder.
 */
public class AlterTableBuilder extends AbstractTableBuilder<AlterTableBuilder> {

	private static final Logger logger = LoggerFactory.getLogger(AlterTableBuilder.class);

	private String action = null;

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
			// COLUMNS
			generateColumns(sql);
		} else if (KEYWORD_DROP.equals(this.action)) {
			sql.append(KEYWORD_DROP);
			// COLUMNS
			generateColumnNames(sql);
		} else {
			logger.error("Action for alter table must be present and can be only ADD or DROP.");
		}

		String generated = sql.toString().trim();

		logger.trace("generated: " + generated);

		return generated;
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
