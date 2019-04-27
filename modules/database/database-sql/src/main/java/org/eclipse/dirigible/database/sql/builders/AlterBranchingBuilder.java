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
package org.eclipse.dirigible.database.sql.builders;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlException;
import org.eclipse.dirigible.database.sql.builders.table.AlterTableBuilder;

/**
 * The Create Branching Builder.
 */
public class AlterBranchingBuilder extends AbstractSqlBuilder {

	/**
	 * Instantiates a new creates the branching builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public AlterBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Table branch.
	 *
	 * @param table
	 *            the table
	 * @return the alters the table builder
	 */
	public AlterTableBuilder table(String table) {
		return new AlterTableBuilder(getDialect(), table);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {
		throw new SqlException("Invalid method invocation of generate() for Create Branching Builder");
	}

}
