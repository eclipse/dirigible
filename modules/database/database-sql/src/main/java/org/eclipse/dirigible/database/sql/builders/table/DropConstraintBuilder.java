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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Drop Constraint Builder.
 */
public class DropConstraintBuilder extends AbstractDropSqlBuilder {

	private static final Logger logger = LoggerFactory.getLogger(DropConstraintBuilder.class);

	private String constraint = null;

	/**
	 * Instantiates a new drop constraint builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param constraint
	 *            the constraint
	 */
	public DropConstraintBuilder(ISqlDialect dialect, String constraint) {
		super(dialect);
		this.constraint = constraint;
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

		// CONSTRAINT
		generateConstraint(sql);

		String generated = sql.toString();

		logger.trace("generated: " + generated);

		return generated;
	}

	/**
	 * Generate constraint.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateConstraint(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_CONSTRAINT).append(SPACE).append(this.constraint);
	}

}
