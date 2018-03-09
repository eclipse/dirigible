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
import org.eclipse.dirigible.database.sql.builders.sequence.DropSequenceBuilder;
import org.eclipse.dirigible.database.sql.builders.table.DropConstraintBuilder;
import org.eclipse.dirigible.database.sql.builders.table.DropTableBuilder;
import org.eclipse.dirigible.database.sql.builders.view.DropViewBuilder;

/**
 * The Drop Branching Builder.
 */
public class DropBranchingBuilder extends AbstractSqlBuilder {

	/**
	 * Instantiates a new drop branching builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public DropBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Table branch.
	 *
	 * @param table
	 *            the table
	 * @return the drop table builder
	 */
	public DropTableBuilder table(String table) {
		return new DropTableBuilder(getDialect(), table);
	}

	/**
	 * View branch.
	 *
	 * @param view
	 *            the view
	 * @return the drop view builder
	 */
	public DropViewBuilder view(String view) {
		return new DropViewBuilder(getDialect(), view);
	}

	/**
	 * Sequence branch.
	 *
	 * @param sequence
	 *            the sequence
	 * @return the drop sequence builder
	 */
	public DropSequenceBuilder sequence(String sequence) {
		return new DropSequenceBuilder(getDialect(), sequence);
	}
	
	/**
	 * Constraint branch.
	 *
	 * @param constraint
	 *            the constraint
	 * @return the drop constraint builder
	 */
	public DropConstraintBuilder constraint(String constraint) {
		return new DropConstraintBuilder(getDialect(), constraint);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {
		throw new SqlException("Invalid method invocation of generate() for Drop Branching Builder");
	}

}
