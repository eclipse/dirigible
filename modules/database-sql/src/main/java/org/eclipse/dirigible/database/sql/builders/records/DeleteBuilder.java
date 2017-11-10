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
import java.util.List;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractQuerySqlBuilder;

public class DeleteBuilder extends AbstractQuerySqlBuilder {

	private String table;
	private List<String> wheres = new ArrayList<String>();

	public DeleteBuilder(ISqlDialect dialect) {
		super(dialect);
	}
	
	public DeleteBuilder from(String table) {
		this.table = table;
		return this;
	}

	public DeleteBuilder where(String condition) {
		wheres.add(OPEN + condition + CLOSE);
		return this;
	}

	@Override
	public String generate() {
		StringBuilder sql = new StringBuilder();

		// UPDATE
		generateDelete(sql);

		// TABLE
		generateTable(sql);

		// WHERE
		generateWhere(sql, wheres);
	
		return sql.toString();
	}

	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_FROM)
			.append(SPACE)
			.append(this.table);
	}
	
	protected void generateDelete(StringBuilder sql) {
		sql.append(KEYWORD_DELETE);
	}

	public String getTable() {
		return table;
	}

	public List<String> getWheres() {
		return wheres;
	}
	
	
}
