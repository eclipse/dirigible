/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.builders;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlException;
import org.eclipse.dirigible.database.sql.builders.sequence.CreateSequenceBuilder;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;
import org.eclipse.dirigible.database.sql.builders.view.CreateViewBuilder;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateBranchingBuilder.
 */
public class CreateBranchingBuilder extends AbstractSqlBuilder {

	/**
	 * Instantiates a new creates the branching builder.
	 *
	 * @param dialect the dialect
	 */
	public CreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Table.
	 *
	 * @param table the table
	 * @return the creates the table builder
	 */
	public CreateTableBuilder table(String table) {
		return new CreateTableBuilder(getDialect(), table);
	}

	/**
	 * View.
	 *
	 * @param view the view
	 * @return the creates the view builder
	 */
	public CreateViewBuilder view(String view) {
		return new CreateViewBuilder(getDialect(), view);
	}

	/**
	 * Sequence.
	 *
	 * @param sequence the sequence
	 * @return the creates the sequence builder
	 */
	public CreateSequenceBuilder sequence(String sequence) {
		return new CreateSequenceBuilder(getDialect(), sequence);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {
		throw new SqlException("Invalid method invocation of generate() for Create Branching Builder");
	}

}
