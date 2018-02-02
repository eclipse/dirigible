/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects.hana;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;

/**
 * The HANA Next Value Sequence Builder.
 */
public class HanaLastValueIdentityBuilder extends LastValueIdentityBuilder {

	private static final String PATTERN_SELECT_LAST_VALUE_IDENTITY = "SELECT CURRENT_IDENTITY_VALUE() FROM ";

	private String[] args = null;

	/**
	 * Instantiates a new HANA last value identity builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public HanaLastValueIdentityBuilder(ISqlDialect dialect, String... args) {
		super(dialect);
		if ((args == null) || (args.length < 1)) {
			throw new IllegalArgumentException("HANA does not support current identity value without a table specified");
		}
		this.args = args;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder#generate()
	 */
	@Override
	public String generate() {
		String sql = format(PATTERN_SELECT_LAST_VALUE_IDENTITY + args[0]);
		return sql;
	}
}
