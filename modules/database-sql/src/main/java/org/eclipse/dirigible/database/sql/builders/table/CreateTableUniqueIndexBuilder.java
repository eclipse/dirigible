/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.builders.table;

import org.eclipse.dirigible.database.sql.ISqlDialect;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateTableUniqueIndexBuilder.
 */
public class CreateTableUniqueIndexBuilder extends AbstractCreateTableConstraintBuilder<CreateTableUniqueIndexBuilder> {

	/**
	 * Instantiates a new creates the table unique index builder.
	 *
	 * @param dialect the dialect
	 * @param name the name
	 */
	CreateTableUniqueIndexBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

}
