/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.ExpressionBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;
import org.eclipse.dirigible.database.sql.dialects.SqlDialectFactory;

public class SqlFactory<SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder>
		implements ISqlFactory<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> {

	private ISqlDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> dialect;

	public static SqlFactory getDefault() {
		return new SqlFactory();
	}

	public static <SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder> SqlFactory<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> getNative(
			ISqlDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> dialect) {
		return new SqlFactory<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT>(dialect);
	}

	public static SqlFactory getNative(Connection connection) {
		return new SqlFactory(deriveDialect(connection));
	}

	private SqlFactory() {
		this(new DefaultSqlDialect());
	}

	private SqlFactory(ISqlDialect dialect) {
		this.dialect = dialect;
	}

	public static ISqlDialect deriveDialect(Connection connection) {
		try {
			return SqlDialectFactory.getDialect(connection);
		} catch (SQLException e) {
			throw new SqlException("Error on deriving the database dialect from the connection", e);
		}

	}

	@Override
	public SELECT select() {
		return this.dialect.select();
	}

	@Override
	public INSERT insert() {
		return this.dialect.insert();
	}

	@Override
	public UPDATE update() {
		return this.dialect.update();
	}

	@Override
	public DELETE delete() {
		return this.dialect.delete();
	}

	@Override
	public ExpressionBuilder expression() {
		return this.dialect.expression();
	}

	@Override
	public CREATE create() {
		return this.dialect.create();
	}

	@Override
	public DROP drop() {
		return this.dialect.drop();
	}

	@Override
	public NEXT nextval(String sequence) {
		return this.dialect.nextval(sequence);
	}

	@Override
	public boolean exists(Connection connection, String table) throws SQLException {
		return this.dialect.exists(connection, table);
	}

}
