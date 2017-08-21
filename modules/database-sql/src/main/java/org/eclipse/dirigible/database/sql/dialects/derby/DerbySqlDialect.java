/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql.dialects.derby;

import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

public class DerbySqlDialect extends
		DefaultSqlDialect<SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, DerbyCreateBranchingBuilder, DerbyDropBranchingBuilder, DerbyNextValueSequenceBuilder> {

	@Override
	public DerbyNextValueSequenceBuilder nextval(String sequence) {
		return new DerbyNextValueSequenceBuilder(this, sequence);
	}

	@Override
	public DerbyCreateBranchingBuilder create() {
		return new DerbyCreateBranchingBuilder(this);
	}

	@Override
	public DerbyDropBranchingBuilder drop() {
		return new DerbyDropBranchingBuilder(this);
	}

}
