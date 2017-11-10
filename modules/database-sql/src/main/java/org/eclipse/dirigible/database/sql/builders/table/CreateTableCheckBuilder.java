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

public class CreateTableCheckBuilder extends AbstractCreateTableConstraintBuilder<CreateTableCheckBuilder> {

	private String expression;

	CreateTableCheckBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

	public String getExpression() {
		return expression;
	}

	public CreateTableCheckBuilder expression(String expression) {
		this.expression = expression;
		return this;
	}
}
