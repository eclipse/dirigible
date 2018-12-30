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
import org.eclipse.dirigible.database.sql.builders.AbstractDropSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Drop Table Builder.
 */
public class DropTableBuilder extends AbstractDropSqlBuilder {

	private static final Logger logger = LoggerFactory.getLogger(DropTableBuilder.class);

	private String table = null;

	/**
	 * Instantiates a new drop table builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param table
	 *            the table
	 */
	public DropTableBuilder(ISqlDialect dialect, String table) {
		super(dialect);
		this.table = table;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// DROP
		generateDrop(sql);

		// TABLE
		generateTable(sql);

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

}
