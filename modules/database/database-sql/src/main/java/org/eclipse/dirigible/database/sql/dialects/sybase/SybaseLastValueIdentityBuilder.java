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
package org.eclipse.dirigible.database.sql.dialects.sybase;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;

/**
 * The Sybase Next Value Sequence Builder.
 */
public class SybaseLastValueIdentityBuilder extends LastValueIdentityBuilder {

	private static final String PATTERN_SELECT_LAST_VALUE_IDENTITY = "SELECT @@identity";

	/**
	 * Instantiates a new Sybase last value identity builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public SybaseLastValueIdentityBuilder(ISqlDialect dialect) {
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
