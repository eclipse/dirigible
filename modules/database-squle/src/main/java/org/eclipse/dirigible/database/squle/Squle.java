/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.database.squle.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.ExpressionBuilder;
import org.eclipse.dirigible.database.squle.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.squle.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.squle.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.squle.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;
import org.eclipse.dirigible.database.squle.dialects.SquleDialectFactory;

public class Squle<SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder>
		implements ISqule<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> {

	private ISquleDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> dialect;

	public static Squle getDefault() {
		return new Squle();
	}

	public static <SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder> Squle<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> getNative(
			ISquleDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> dialect) {
		return new Squle<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT>(dialect);
	}

	public static Squle getNative(Connection connection) {
		return new Squle(deriveDialect(connection));
	}

	private Squle() {
		this(new DefaultSquleDialect());
	}

	private Squle(ISquleDialect dialect) {
		this.dialect = dialect;
	}

	public static ISquleDialect deriveDialect(Connection connection) {
		try {
			return SquleDialectFactory.getDialect(connection);
		} catch (SQLException e) {
			throw new SquleException("Error on deriving the database dialect from the connection", e);
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
	public ExpressionBuilder expr() {
		return this.dialect.expr();
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
