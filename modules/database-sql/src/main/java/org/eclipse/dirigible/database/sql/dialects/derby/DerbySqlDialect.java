/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects.derby;

import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

/**
 * The Derby SQL Dialect.
 */
public class DerbySqlDialect extends
		DefaultSqlDialect<DerbySelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, DerbyCreateBranchingBuilder, DerbyDropBranchingBuilder, DerbyNextValueSequenceBuilder, LastValueIdentityBuilder> {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
	 */
	@Override
	public DerbyNextValueSequenceBuilder nextval(String sequence) {
		return new DerbyNextValueSequenceBuilder(this, sequence);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#create()
	 */
	@Override
	public DerbyCreateBranchingBuilder create() {
		return new DerbyCreateBranchingBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#drop()
	 */
	@Override
	public DerbyDropBranchingBuilder drop() {
		return new DerbyDropBranchingBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#select()
	 */
	@Override
	public DerbySelectBuilder select() {
		return new DerbySelectBuilder(this);
	}

}
