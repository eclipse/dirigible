/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.sql.builders;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlException;
import org.eclipse.dirigible.database.sql.builders.sequence.CreateSequenceBuilder;
import org.eclipse.dirigible.database.sql.builders.synonym.CreateSynonymBuilder;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;
import org.eclipse.dirigible.database.sql.builders.view.CreateViewBuilder;

/**
 * The Create Branching Builder.
 */
public class CreateBranchingBuilder extends AbstractSqlBuilder {

	/**
	 * Instantiates a new creates the branching builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public CreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Table branch.
	 *
	 * @param table
	 *            the table
	 * @return the creates the table builder
	 */
	public CreateTableBuilder table(String table) {
		return new CreateTableBuilder(getDialect(), table);
	}

	/**
	 * View branch.
	 *
	 * @param view
	 *            the view
	 * @return the creates the view builder
	 */
	public CreateViewBuilder view(String view) {
		return new CreateViewBuilder(getDialect(), view);
	}

	/**
	 * Sequence branch.
	 *
	 * @param sequence
	 *            the sequence
	 * @return the creates the sequence builder
	 */
	public CreateSequenceBuilder sequence(String sequence) {
		return new CreateSequenceBuilder(getDialect(), sequence);
	}
	
	/**
	 * Synonym branch.
	 *
	 * @param synonym
	 *            the synonym
	 * @return the creates the synonym builder
	 */
	public CreateSynonymBuilder synonym(String synonym) {
		return new CreateSynonymBuilder(getDialect(), synonym);
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
