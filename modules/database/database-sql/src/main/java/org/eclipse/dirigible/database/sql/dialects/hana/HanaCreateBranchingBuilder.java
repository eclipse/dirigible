/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;
import org.eclipse.dirigible.database.sql.builders.tableType.CreateTableTypeBuilder;

/**
 * The HANA Create Branching Builder.
 */
public class HanaCreateBranchingBuilder extends CreateBranchingBuilder {

	/**
	 * Instantiates a new HANA create branching builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	protected HanaCreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder#table(java.lang.String)
	 */
	@Override
	public HanaCreateTableBuilder table(String table) {
		return new HanaCreateTableBuilder(this.getDialect(), table, true);
	}

	@Override
	public HanaCreateTableBuilder table(String table, String tableType) {
		if(tableType.equalsIgnoreCase(KEYWORD_COLUMNSTORE)){
			return this.columnTable(table);
		}else if (tableType.equalsIgnoreCase(KEYWORD_ROWSTORE)){
			return this.rowTable(table);
		} else {
			throw new IllegalStateException(String.format("Unsupported table type is defined for table %s", table));
	}
	}

	/**
	 * Column table.
	 *
	 * @param table
	 *            the table
	 * @return the creates the table builder
	 */
	public HanaCreateTableBuilder columnTable(String table) {
		return new HanaCreateTableBuilder(this.getDialect(), table, true);
	}

	/**
	 * Row table.
	 *
	 * @param table
	 *            the table
	 * @return the creates the table builder
	 */
	public HanaCreateTableBuilder rowTable(String table) {
		return new HanaCreateTableBuilder(this.getDialect(), table, false);
	}

	public HanaCreateTableTypeBuilder tableType(String tableType){
		return new HanaCreateTableTypeBuilder(this.getDialect(), tableType);
	}
}
