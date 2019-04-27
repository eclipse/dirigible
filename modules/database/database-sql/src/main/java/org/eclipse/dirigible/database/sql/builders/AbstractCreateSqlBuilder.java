/**
 * Copyright (c) 2010-2019 SAP and others.
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

/**
 * The Abstract Create SQL Builder.
 */
public abstract class AbstractCreateSqlBuilder extends AbstractSqlBuilder {

	/**
	 * Instantiates a new abstract create sql builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	protected AbstractCreateSqlBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Generate create.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateCreate(StringBuilder sql) {
		sql.append(KEYWORD_CREATE);
	}
	
	/**
	 * Generate alter.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateAlter(StringBuilder sql) {
		sql.append(KEYWORD_ALTER);
	}

}
