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
package org.eclipse.dirigible.database.sql.builders.view;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractDropSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Drop View Builder.
 */
public class DropViewBuilder extends AbstractDropSqlBuilder {

	private static final Logger logger = LoggerFactory.getLogger(DropViewBuilder.class);

	private String view = null;

	/**
	 * Instantiates a new drop view builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param view
	 *            the view
	 */
	public DropViewBuilder(ISqlDialect dialect, String view) {
		super(dialect);
		this.view = view;
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

		// VIEW
		generateView(sql);

		String generated = sql.toString();

		logger.trace("generated: " + generated);

		return generated;
	}

	/**
	 * Generate view.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateView(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_VIEW).append(SPACE).append(this.view);
	}

}
