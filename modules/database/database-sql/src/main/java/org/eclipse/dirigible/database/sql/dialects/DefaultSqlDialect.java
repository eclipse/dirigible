/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlException;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.ExpressionBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder;

/**
 * The Default SQL Dialect.
 *
 * @param <SELECT>
 *            the generic type
 * @param <INSERT>
 *            the generic type
 * @param <UPDATE>
 *            the generic type
 * @param <DELETE>
 *            the generic type
 * @param <CREATE>
 *            the generic type
 * @param <DROP>
 *            the generic type
 * @param <NEXT>
 *            the generic type
 * @param <LAST>
 *            the generic type
 */
public class DefaultSqlDialect<SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder, LAST extends LastValueIdentityBuilder>
		implements ISqlDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT, LAST> {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#select()
	 */
	@Override
	public SELECT select() {
		return (SELECT) new SelectBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#insert()
	 */
	@Override
	public INSERT insert() {
		return (INSERT) new InsertBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#update()
	 */
	@Override
	public UPDATE update() {
		return (UPDATE) new UpdateBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#delete()
	 */
	@Override
	public DELETE delete() {
		return (DELETE) new DeleteBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#expression()
	 */
	@Override
	public ExpressionBuilder expression() {
		return new ExpressionBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#create()
	 */
	@Override
	public CREATE create() {
		return (CREATE) new CreateBranchingBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#drop()
	 */
	@Override
	public DROP drop() {
		return (DROP) new DropBranchingBuilder(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#nextval(java.lang.String)
	 */
	@Override
	public NEXT nextval(String sequence) {
		return (NEXT) new NextValueSequenceBuilder(this, sequence);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getDataTypeName(org.eclipse.dirigible.database.sql.DataType)
	 */
	@Override
	public String getDataTypeName(DataType dataType) {
		return dataType.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getPrimaryKeyArgument()
	 */
	@Override
	public String getPrimaryKeyArgument() {
		return KEYWORD_PRIMARY + SPACE + KEYWORD_KEY;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getPrimaryKeyArgument()
	 */
	@Override
	public String getIdentityArgument() {
		return KEYWORD_IDENTITY;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getNotNullArgument()
	 */
	@Override
	public String getNotNullArgument() {
		return KEYWORD_NOT + SPACE + KEYWORD_NULL;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getUniqueArgument()
	 */
	@Override
	public String getUniqueArgument() {
		return KEYWORD_UNIQUE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#exists(java.sql.Connection, java.lang.String)
	 */
	@Override
	public boolean exists(Connection connection, String table) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, null, table, ISqlKeywords.METADATA_TABLE_TYPES.toArray(new String[] {}));
		if (resultSet.next()) {
			return true;
		}
		resultSet = metadata.getTables(null, null, table.toLowerCase(), ISqlKeywords.METADATA_TABLE_TYPES.toArray(new String[] {}));
		if (resultSet.next()) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#count(java.sql.Connection, java.lang.String)
	 */
	@Override
	public int count(Connection connection, String table) throws SQLException {
		String sql = new SelectBuilder(this).column("COUNT(*)").from(table).build();
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet resultSet = statement.executeQuery();
		if (resultSet.next()) {
			return resultSet.getInt(1);
		}
		throw new SQLException("Cannot calculate the count of records of table: " + table);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#isSchemaFilterSupported()
	 */
	@Override
	public boolean isSchemaFilterSupported() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getSchemaFilterScript()
	 */
	@Override
	public String getSchemaFilterScript() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#isCatalogForSchema()
	 */
	@Override
	public boolean isCatalogForSchema() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#functionCurrentDate()
	 */
	@Override
	public String functionCurrentDate() {
		return ISqlKeywords.FUNCTION_CURRENT_DATE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#functionCurrentTime()
	 */
	@Override
	public String functionCurrentTime() {
		return ISqlKeywords.FUNCTION_CURRENT_TIME;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#functionCurrentTimestamp()
	 */
	@Override
	public String functionCurrentTimestamp() {
		return ISqlKeywords.FUNCTION_CURRENT_TIMESTAMP;
	}

	@Override
	public LAST lastval(String... args) {
		return (LAST) new LastValueIdentityBuilder(this);
	}

	@Override
	public boolean isSequenceSupported() {
		return true;
	}

	@Override
	public String getDatabaseName(Connection connection) {
		try {
			return connection.getMetaData().getDatabaseProductName();
		} catch (Exception e) {
			throw new SqlException("Cannot retrieve the database name", e);
		}
	}

}
