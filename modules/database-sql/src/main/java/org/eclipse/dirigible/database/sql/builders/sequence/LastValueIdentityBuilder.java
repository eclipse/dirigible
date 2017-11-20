/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.builders.sequence;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractQuerySqlBuilder;

/**
 * The Last Value Identity Builder.
 */
public class LastValueIdentityBuilder extends AbstractQuerySqlBuilder {

	private String sequence = null;

	/**
	 * Instantiates a new last value identity builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public LastValueIdentityBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {
		StringBuilder sql = new StringBuilder();

		// SELECT
		generateSelect(sql);

		// LAST VALUE
		generateLastValue(sql);

		return sql.toString();
	}

	/**
	 * Generate select.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateSelect(StringBuilder sql) {
		sql.append(KEYWORD_SELECT);
	}

	/**
	 * Generate next value.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateLastValue(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_IDENTITY);
	}

}
