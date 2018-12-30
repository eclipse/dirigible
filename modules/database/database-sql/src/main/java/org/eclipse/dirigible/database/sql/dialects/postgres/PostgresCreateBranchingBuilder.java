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
package org.eclipse.dirigible.database.sql.dialects.postgres;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;

/**
 * The PostgreSQL Create Branching Builder.
 */
public class PostgresCreateBranchingBuilder extends CreateBranchingBuilder {

	/**
	 * Instantiates a new PostgreSQL create branching builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public PostgresCreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder#view(java.lang.String)
	 */
	@Override
	public PostgresCreateViewBuilder view(String view) {
		return new PostgresCreateViewBuilder(this.getDialect(), view);
	}

}
