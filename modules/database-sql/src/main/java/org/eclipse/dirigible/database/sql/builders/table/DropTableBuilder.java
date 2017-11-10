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

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractDropSqlBuilder;

public class DropTableBuilder extends AbstractDropSqlBuilder {
	
	private String table = null;
	
	public DropTableBuilder(ISqlDialect dialect, String table) {
		super(dialect);
		this.table = table;
	}

	@Override
	public String generate() {
		
		StringBuilder sql = new StringBuilder();
		
		// DROP
		generateDrop(sql);
		
		// TABLE
		generateTable(sql);
		
		return sql.toString();
	}
	
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_TABLE)
			.append(SPACE)
			.append(this.table);
	}
	
}
