/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.ExpressionBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;
import org.eclipse.dirigible.database.sql.dialects.SqlDialectFactory;

/**
 * A factory for creating SQL objects.
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
 * @param <ALTER>
 *            the generic type
 * @param <DROP>
 *            the generic type
 * @param <NEXT>
 *            the generic type
 * @param <LAST>
 *            the generic type
 */
public class SqlFactory<SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, ALTER extends AlterBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder, LAST extends LastValueIdentityBuilder>
		implements ISqlFactory<SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, NEXT, LAST> {

	/** The dialect. */
	private ISqlDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, NEXT, LAST> dialect;

	/**
	 * Gets the default.
	 *
	 * @return the default
	 */
	public static SqlFactory getDefault() {
		return new SqlFactory();
	}

	/**
	 * Gets the native.
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
	 * @param <ALTER>
     *            the generic type
	 * @param <DROP>
	 *            the generic type
	 * @param <NEXT>
	 *            the generic type
	 * @param <LAST>
	 *            the generic type
	 * @param dialect
	 *            the dialect
	 * @return the native
	 */
	public static <SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, ALTER extends AlterBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder, LAST extends LastValueIdentityBuilder> SqlFactory<SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, NEXT, LAST> getNative(
			ISqlDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, NEXT, LAST> dialect) {
		return new SqlFactory<SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, NEXT, LAST>(dialect);
	}

	/**
	 * Gets the native.
	 *
	 * @param connection
	 *            the connection
	 * @return the native
	 */
	public static SqlFactory getNative(Connection connection) {
		return new SqlFactory(deriveDialect(connection));
	}

	/**
	 * Instantiates a new sql factory.
	 */
	private SqlFactory() {
		this(new DefaultSqlDialect());
	}

	/**
	 * Instantiates a new sql factory.
	 *
	 * @param dialect
	 *            the dialect
	 */
	private SqlFactory(ISqlDialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * Derive dialect.
	 *
	 * @param connection
	 *            the connection
	 * @return the i sql dialect
	 */
	public static ISqlDialect deriveDialect(Connection connection) {
		try {
			return SqlDialectFactory.getDialect(connection);
		} catch (SQLException e) {
			throw new SqlException("Error on deriving the database dialect from the connection", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#select()
	 */
	@Override
	public SELECT select() {
		return this.dialect.select();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#insert()
	 */
	@Override
	public INSERT insert() {
		return this.dialect.insert();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#update()
	 */
	@Override
	public UPDATE update() {
		return this.dialect.update();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#delete()
	 */
	@Override
	public DELETE delete() {
		return this.dialect.delete();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#expression()
	 */
	@Override
	public ExpressionBuilder expression() {
		return this.dialect.expression();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#create()
	 */
	@Override
	public CREATE create() {
		return this.dialect.create();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#alter()
	 */
	@Override
	public ALTER alter() {
		return this.dialect.alter();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#drop()
	 */
	@Override
	public DROP drop() {
		return this.dialect.drop();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#nextval(java.lang.String)
	 */
	@Override
	public NEXT nextval(String sequence) {
		return this.dialect.nextval(sequence);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#exists(java.sql.Connection, java.lang.String)
	 */
	@Override
	public boolean exists(Connection connection, String table) throws SQLException {
		return this.dialect.exists(connection, table);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#exists(java.sql.Connection, java.lang.String, java.lang.int)
	 */
	@Override
	public boolean exists(Connection connection, String name, int type) throws SQLException {
		return this.dialect.exists(connection, name, type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#exists(java.sql.Connection, java.lang.String, java.lang.String, java.lang.int)
	 */
	@Override
	public boolean exists(Connection connection, String schema, String name, int type) throws SQLException {
		return this.dialect.exists(connection, schema, name, type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#count(java.sql.Connection, java.lang.String)
	 */
	@Override
	public int count(Connection connection, String table) throws SQLException {
		return this.dialect.count(connection, table);
	}

	@Override
	public LAST lastval(String... args) {
		return this.dialect.lastval(args);
	}

}
