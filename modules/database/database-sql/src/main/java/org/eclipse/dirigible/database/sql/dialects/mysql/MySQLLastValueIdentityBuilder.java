/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects.mysql;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;

/**
 * The H2 Next Value Sequence Builder.
 */
public class MySQLLastValueIdentityBuilder extends LastValueIdentityBuilder {

	private static final String PATTERN_SELECT_LAST_VALUE_IDENTITY = "SELECT LAST_INSERT_ID()";

	/**
	 * Instantiates a new H2 last value identity builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public MySQLLastValueIdentityBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder#generate()
	 */
	@Override
	public String generate() {
		String sql = format(PATTERN_SELECT_LAST_VALUE_IDENTITY);
		return sql;
	}
}
