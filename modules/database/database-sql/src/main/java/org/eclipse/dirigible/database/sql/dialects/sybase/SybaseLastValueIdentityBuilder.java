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
package org.eclipse.dirigible.database.sql.dialects.sybase;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;

/**
 * The Sybase Next Value Sequence Builder.
 */
public class SybaseLastValueIdentityBuilder extends LastValueIdentityBuilder {

	/** The Constant PATTERN_SELECT_LAST_VALUE_IDENTITY. */
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

	/**
	 * Generate.
	 *
	 * @return the string
	 */
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
