/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.derby;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;

/**
 * The Derby Next Value Sequence Builder.
 */
public class DerbyLastValueIdentityBuilder extends LastValueIdentityBuilder {

	private static final String PATTERN_SELECT_LAST_VALUE_IDENTITY = "SELECT IDENTITY_VAL_LOCAL() FROM ";

	private String[] args = null;

	/**
	 * Instantiates a new Derby last value identity builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param args
	 *            the args
	 */
	public DerbyLastValueIdentityBuilder(ISqlDialect dialect, String... args) {
		super(dialect);
		if ((args == null) || (args.length < 1)) {
			throw new IllegalArgumentException("Derby does not support identity value local without a table specified");
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
