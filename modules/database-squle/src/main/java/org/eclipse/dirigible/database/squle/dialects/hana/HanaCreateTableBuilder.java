/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle.dialects.hana;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.table.CreateTableBuilder;

public class HanaCreateTableBuilder extends CreateTableBuilder {
	
	private boolean isColumnTable;

	public HanaCreateTableBuilder(ISquleDialect dialect, String table, boolean isColumnTable) {
		super(dialect, table);
		this.isColumnTable = isColumnTable;
	}
	
	@Override
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(isColumnTable ? KEYWORD_COLUMN : "")
			.append(isColumnTable ? SPACE : "")
			.append(KEYWORD_TABLE)
			.append(SPACE)
			.append(getTable());
	}

}
