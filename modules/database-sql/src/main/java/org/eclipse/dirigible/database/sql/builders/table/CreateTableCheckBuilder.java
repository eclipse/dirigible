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

/**
 * The Create Table Check Builder.
 */
public class CreateTableCheckBuilder extends AbstractCreateTableConstraintBuilder<CreateTableCheckBuilder> {

	private String expression;

	/**
	 * Instantiates a new creates the table check builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param name
	 *            the name
	 */
	CreateTableCheckBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

	/**
	 * Gets the expression.
	 *
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Expression.
	 *
	 * @param expression
	 *            the expression
	 * @return the creates the table check builder
	 */
	public CreateTableCheckBuilder expression(String expression) {
		this.expression = expression;
		return this;
	}
}
