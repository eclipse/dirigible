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
package org.eclipse.dirigible.database.sql.builders.synonym;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractDropSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Drop Synonym Builder.
 */
public class DropSynonymBuilder extends AbstractDropSqlBuilder {

	private static final Logger logger = LoggerFactory.getLogger(DropSynonymBuilder.class);

	private String synonym = null;

	/**
	 * Instantiates a new drop synonym builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param synonym
	 *            the synonym
	 */
	public DropSynonymBuilder(ISqlDialect dialect, String synonym) {
		super(dialect);
		this.synonym = synonym;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// DROP
		generateDrop(sql);

		// SYNONYM
		generateSynonym(sql);

		String generated = sql.toString();

		logger.trace("generated: " + generated);

		return generated;
	}

	/**
	 * Generate synonym.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateSynonym(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_SYNONYM).append(SPACE).append('"').append(this.synonym).append('"');
	}

	/**
	 * Gets the synonym.
	 *
	 * @return the synonym
	 */
	public String getSynonym() {
		return synonym;
	}

}
