/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql.dialects.sybase;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;

public class SybaseSelectBuilder extends SelectBuilder {

	public SybaseSelectBuilder(ISqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected void generateLimit(StringBuilder sql, int limit) {
		if (limit > -1) {
			sql.append(SPACE)
				.append(KEYWORD_ROWS)
				.append(SPACE)
				.append(KEYWORD_LIMIT)
				.append(SPACE)
				.append(limit);
		}
	}

}
