/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.sql.dialects.sybase;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.CreateSequenceBuilder;

/**
 * The Sybase Create Branching Builder.
 */
public class SybaseCreateBranchingBuilder extends CreateBranchingBuilder {

	/**
	 * Instantiates a new Sybase create branching builder.
	 *
	 * @param dialect
	 *            the dialect
	 */
	public SybaseCreateBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder#sequence(java.lang.String)
	 */
	@Override
	public CreateSequenceBuilder sequence(String sequence) {
		return new SybaseCreateSequenceBuilder(this.getDialect(), sequence);
	}

}
