/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects.hana;

import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

public class HanaSqlDialect extends DefaultSqlDialect<SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, HanaCreateBranchingBuilder, DropBranchingBuilder, HanaNextValueSequenceBuilder> {

	public HanaNextValueSequenceBuilder nextval(String sequence) {
		return new HanaNextValueSequenceBuilder(this, sequence);
	}

	@Override
	public HanaCreateBranchingBuilder create() {
		return new HanaCreateBranchingBuilder(this);
	}
}
