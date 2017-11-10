/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;

// TODO: Auto-generated Javadoc
/**
 * The Class HanaCreateBranchingBuilder.
 */
public class HanaCreateBranchingBuilder extends CreateBranchingBuilder {

	/**
	 * Instantiates a new hana create branching builder.
	 *
	 * @param dialect the dialect
	 */
	protected HanaCreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder#table(java.lang.String)
	 */
	@Override
	public CreateTableBuilder table(String table) {
		return new HanaCreateTableBuilder(this.getDialect(), table, false);
	}
	
	/**
	 * Column table.
	 *
	 * @param table the table
	 * @return the creates the table builder
	 */
	public CreateTableBuilder columnTable(String table) {
		return new HanaCreateTableBuilder(this.getDialect(), table, true);
	}
	
	/**
	 * Row table.
	 *
	 * @param table the table
	 * @return the creates the table builder
	 */
	public CreateTableBuilder rowTable(String table) {
		return new HanaCreateTableBuilder(this.getDialect(), table, false);
	}

}
