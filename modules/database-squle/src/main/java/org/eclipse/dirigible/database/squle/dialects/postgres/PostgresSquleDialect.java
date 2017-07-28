/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle.dialects.postgres;

import org.eclipse.dirigible.database.squle.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.squle.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.squle.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.squle.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class PostgresSquleDialect extends DefaultSquleDialect<SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, CreateBranchingBuilder, DropBranchingBuilder, PostgresNextValueSequenceBuilder> {
	
	public static final String FUNCTION_CURRENT_DATE = "current_date"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIME = "current_time"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIMESTAMP = "current_timestamp"; //$NON-NLS-1$

	public PostgresNextValueSequenceBuilder nextval(String sequence) {
		return new PostgresNextValueSequenceBuilder(this, sequence);
	}
	
	@Override
	public String functionCurrentDate() {
		return FUNCTION_CURRENT_DATE;
	}

	@Override
	public String functionCurrentTime() {
		return FUNCTION_CURRENT_TIME;
	}

	@Override
	public String functionCurrentTimestamp() {
		return FUNCTION_CURRENT_TIMESTAMP;
	}

}
