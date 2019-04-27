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
package org.eclipse.dirigible.database.sql.dialects.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

/**
 * The MySQL SQL Dialect.
 */
public class MySQLSqlDialect extends
		DefaultSqlDialect<SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, MySQLCreateBranchingBuilder, AlterBranchingBuilder, MySQLDropBranchingBuilder, MySQLNextValueSequenceBuilder, MySQLLastValueIdentityBuilder> {

	private static final String MYSQL_KEYWORD_IDENTITY = "AUTO_INCREMENT";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#create()
	 */
	@Override
	public MySQLCreateBranchingBuilder create() {
		return new MySQLCreateBranchingBuilder(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#drop()
	 */
	@Override
	public MySQLDropBranchingBuilder drop() {
		return new MySQLDropBranchingBuilder(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
	 */
	@Override
	public MySQLNextValueSequenceBuilder nextval(String sequence) {
		return new MySQLNextValueSequenceBuilder(this, sequence);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect#nextval(java.lang.String)
	 */
	@Override
	public MySQLLastValueIdentityBuilder lastval(String... args) {
		return new MySQLLastValueIdentityBuilder(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getPrimaryKeyArgument()
	 */
	@Override
	public String getIdentityArgument() {
		return MYSQL_KEYWORD_IDENTITY;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#isSequenceSupported()
	 */
	@Override
	public boolean isSequenceSupported() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#exists(java.sql.Connection, java.lang.String)
	 */
	@Override
	public boolean exists(Connection connection, String table) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, null, table.toUpperCase(), ISqlKeywords.METADATA_TABLE_TYPES.toArray(new String[] {}));
		if (resultSet.next()) {
			return true;
		}
		return false;
	}

}
