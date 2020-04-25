/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.sql.dialects.postgres;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

/**
 * The PostgreSQL SQL Dialect.
 */
public class PostgresSqlDialect extends
		DefaultSqlDialect<SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, PostgresCreateBranchingBuilder, AlterBranchingBuilder, DropBranchingBuilder, PostgresNextValueSequenceBuilder, LastValueIdentityBuilder> {

	/** The Constant FUNCTION_CURRENT_DATE. */
	public static final String FUNCTION_CURRENT_DATE = "current_date"; //$NON-NLS-1$

	/** The Constant FUNCTION_CURRENT_TIME. */
	public static final String FUNCTION_CURRENT_TIME = "current_time"; //$NON-NLS-1$

	/** The Constant FUNCTION_CURRENT_TIMESTAMP. */
	public static final String FUNCTION_CURRENT_TIMESTAMP = "current_timestamp"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#create()
	 */
	@Override
	public PostgresCreateBranchingBuilder create() {
		return new PostgresCreateBranchingBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
	 */
	@Override
	public PostgresNextValueSequenceBuilder nextval(String sequence) {
		return new PostgresNextValueSequenceBuilder(this, sequence);
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
	
	@Override
	public String getDataTypeName(DataType dataType) {
		switch (dataType) {
			case BLOB:
				return "bytea";
			case DOUBLE:
				return "DOUBLE PRECISION";
			default:
				return super.getDataTypeName(dataType);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#exists(java.sql.Connection, java.lang.String)
	 */
	@Override
	public boolean exists(Connection connection, String table) throws SQLException {
		table = normalizeTableName(table);
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, null, DefaultSqlDialect.normalizeTableName(table.toLowerCase()), ISqlKeywords.METADATA_TABLE_TYPES.toArray(new String[] {}));
		if (resultSet.next()) {
			return true;
		}
		return false;
	}

}
