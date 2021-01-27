/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.builders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.sql.ISqlBuilder;
import org.eclipse.dirigible.database.sql.ISqlDialect;

/**
 * The Abstract SQL Builder.
 */
public abstract class AbstractSqlBuilder implements ISqlBuilder {

	private ISqlDialect dialect;
	
	/**
	 * Instantiates a new abstract sql builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	protected AbstractSqlBuilder(ISqlDialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * Gets the dialect.
	 *
	 * @return the dialect
	 */
	protected ISqlDialect getDialect() {
		return dialect;
	}

	/**
	 * Usually returns the default generated snippet.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return build();
	}

	/**
	 * Returns the default generated snippet.
	 *
	 * @return the string
	 */
	@Override
	public String build() {
		return generate();
	}
	
	/**
	 * Whether the names of tables, columns, indices are case sensitive
	 * 
	 * @return
	 */
	protected boolean isCaseSensitive() {
		return Boolean.parseBoolean(Configuration.get("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false"));
	}
	
	/**
	 * Encapsulate the name within qutes
	 * 
	 * @param name the name
	 * @return the encapsulated name
	 */
	protected String encapsulate(String name) {
		if (!name.startsWith("\"")
				&& !"*".equals(name.trim())
				&& isColumn(name.trim())) {
			name = "\"" + name + "\"";
		}
		return name;
	}
	
	/**
	 * Check whether the name is a column (one word) or it is complex expression containing functions, etc. (count(*))
	 * @param name the name of the eventual column
	 * @return true if it is one word
	 */
	protected boolean isColumn(String name) {
		String pattern = "^([\\w\\-]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(name);
		m.find();
		String found = m.group(1);
		return name.equals(found);
	}

}
