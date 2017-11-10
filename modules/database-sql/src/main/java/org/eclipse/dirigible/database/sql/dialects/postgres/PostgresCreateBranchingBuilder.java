/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects.postgres;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;

public class PostgresCreateBranchingBuilder extends CreateBranchingBuilder {

	public PostgresCreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	@Override
	public PostgresCreateViewBuilder view(String view) {
		return new PostgresCreateViewBuilder(this.getDialect(), view);
	}

}
