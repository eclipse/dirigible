/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;

/**
 * The HANA Create Table Builder.
 */
public class HanaCreateTableBuilder extends CreateTableBuilder {

	private boolean isColumnTable = true;

	/**
	 * Instantiates a new hana create table builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param table
	 *            the table
	 * @param isColumnTable
	 *            the is column table
	 */
	public HanaCreateTableBuilder(ISqlDialect dialect, String table, boolean isColumnTable) {
		super(dialect, table);
		this.isColumnTable = isColumnTable;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder#generateTable(java.lang.StringBuilder)
	 */
	@Override
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE).append(isColumnTable ? KEYWORD_COLUMN : "").append(isColumnTable ? SPACE : "").append(KEYWORD_TABLE).append(SPACE)
				.append(getTable());
	}

}
