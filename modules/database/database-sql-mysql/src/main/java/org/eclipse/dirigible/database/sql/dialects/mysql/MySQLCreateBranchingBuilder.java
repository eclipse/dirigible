/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.mysql;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.CreateSequenceBuilder;

/**
 * The MySQL Create Branching Builder.
 */
public class MySQLCreateBranchingBuilder extends CreateBranchingBuilder {

	/**
	 * Instantiates a new mySQL create branching builder.
	 *
	 * @param dialect the dialect
	 */
	public MySQLCreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/**
	 * Sequence.
	 *
	 * @param sequence the sequence
	 * @return the creates the sequence builder
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder#sequence(java.lang.String)
	 */
	@Override
	public CreateSequenceBuilder sequence(String sequence) {
		return new MySQLCreateSequenceBuilder(this.getDialect(), sequence);
	}

	/**
	 * View.
	 *
	 * @param view the view
	 * @return the my SQL create view builder
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder#view(java.lang.String)
	 */
	@Override
	public MySQLCreateViewBuilder view(String view) {
		return new MySQLCreateViewBuilder(this.getDialect(), view);
	}

}
