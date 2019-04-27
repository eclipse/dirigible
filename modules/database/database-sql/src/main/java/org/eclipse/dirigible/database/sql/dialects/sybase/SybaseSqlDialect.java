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

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

/**
 * The Sybase SQL Dialect.
 */
public class SybaseSqlDialect extends
		DefaultSqlDialect<SybaseSelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, CreateBranchingBuilder, AlterBranchingBuilder, DropBranchingBuilder, SybaseNextValueSequenceBuilder, SybaseLastValueIdentityBuilder> {

	/** The Constant FUNCTION_CURRENT_DATE. */
	public static final String FUNCTION_CURRENT_DATE = "current_date"; //$NON-NLS-1$

	/** The Constant FUNCTION_CURRENT_TIME. */
	public static final String FUNCTION_CURRENT_TIME = "current_time"; //$NON-NLS-1$

	/** The Constant FUNCTION_CURRENT_TIMESTAMP. */
	public static final String FUNCTION_CURRENT_TIMESTAMP = "getdate()"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#create()
	 */
	@Override
	public SybaseCreateBranchingBuilder create() {
		return new SybaseCreateBranchingBuilder(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#drop()
	 */
	@Override
	public SybaseDropBranchingBuilder drop() {
		return new SybaseDropBranchingBuilder(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
	 */
	@Override
	public SybaseNextValueSequenceBuilder nextval(String sequence) {
		return new SybaseNextValueSequenceBuilder(this, sequence);

	}

	@Override
	public String getDataTypeName(DataType dataType) {
		switch (dataType) {
			case TIMESTAMP:
				return "DATETIME";
			case BLOB:
				return "IMAGE";
			case BOOLEAN:
				return "BIT";
			case DOUBLE:
				return "DOUBLE PRECISION";
			default:
				return super.getDataTypeName(dataType);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
	 */
	@Override
	public SybaseLastValueIdentityBuilder lastval(String... args) {
		return new SybaseLastValueIdentityBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#select()
	 */
	@Override
	public SybaseSelectBuilder select() {
		return new SybaseSelectBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#functionCurrentDate()
	 */
	@Override
	public String functionCurrentDate() {
		return FUNCTION_CURRENT_DATE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#functionCurrentTime()
	 */
	@Override
	public String functionCurrentTime() {
		return FUNCTION_CURRENT_TIME;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#functionCurrentTimestamp()
	 */
	@Override
	public String functionCurrentTimestamp() {
		return FUNCTION_CURRENT_TIMESTAMP;
	}

}
