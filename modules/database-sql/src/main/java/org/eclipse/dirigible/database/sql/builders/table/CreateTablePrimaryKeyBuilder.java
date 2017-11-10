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
 * The Class CreateTablePrimaryKeyBuilder.
 */
public class CreateTablePrimaryKeyBuilder extends AbstractCreateTableConstraintBuilder<CreateTablePrimaryKeyBuilder> {

	/**
	 * Instantiates a new creates the table primary key builder.
	 *
	 * @param dialect the dialect
	 * @param name the name
	 */
	CreateTablePrimaryKeyBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

}
