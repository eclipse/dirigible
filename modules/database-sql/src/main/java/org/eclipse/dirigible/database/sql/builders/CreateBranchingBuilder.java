/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.builders;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.SqlException;
import org.eclipse.dirigible.database.sql.builders.sequence.CreateSequenceBuilder;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;
import org.eclipse.dirigible.database.sql.builders.view.CreateViewBuilder;

public class CreateBranchingBuilder extends AbstractSqlBuilder {

	public CreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	public CreateTableBuilder table(String table) {
		return new CreateTableBuilder(getDialect(), table);
	}

	public CreateViewBuilder view(String view) {
		return new CreateViewBuilder(getDialect(), view);
	}

	public CreateSequenceBuilder sequence(String sequence) {
		return new CreateSequenceBuilder(getDialect(), sequence);
	}

	@Override
	public String generate() {
		throw new SqlException("Invalid method invocation of generate() for Create Branching Builder");
	}

}
