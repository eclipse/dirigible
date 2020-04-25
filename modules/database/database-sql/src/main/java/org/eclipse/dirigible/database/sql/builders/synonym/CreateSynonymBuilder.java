/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.sql.builders.synonym;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractCreateSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Create Synonym Builder.
 */
public class CreateSynonymBuilder extends AbstractCreateSqlBuilder {

	private static final Logger logger = LoggerFactory.getLogger(CreateSynonymBuilder.class);

	private String synonym = null;
	
	private String source = null;


	/**
	 * Instantiates a new creates the synonym builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param synonym
	 *            the synonym
	 */
	public CreateSynonymBuilder(ISqlDialect dialect, String synonym) {
		super(dialect);
		this.synonym = synonym;
	}

	/**
	 * Source.
	 *
	 * @param source
	 *            the source
	 * @return the creates the synonym builder
	 */
	public CreateSynonymBuilder forSource(String source) {
		logger.trace("source: " + source);
		this.source = source;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// CREATE
		generateCreate(sql);

		// SYNONYM
		generateSynonym(sql);

		// Source
		generateSource(sql);

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
		sql.append(SPACE)/*.append(KEYWORD_PUBLIC).append(SPACE)*/.append(KEYWORD_SYNONYM).append(SPACE).append('"').append(this.synonym).append('"');
	}

	/**
	 * Generate start.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateSource(StringBuilder sql) {
		 sql.append(SPACE)
			 .append(KEYWORD_FOR)
			 .append(SPACE)
			 .append(this.source);
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
