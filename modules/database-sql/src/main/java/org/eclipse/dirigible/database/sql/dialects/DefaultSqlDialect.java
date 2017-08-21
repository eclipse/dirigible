/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql.dialects;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.ExpressionBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder;

public class DefaultSqlDialect<SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder>
		implements ISqlDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, NEXT> {

	public SELECT select() {
		return (SELECT) new SelectBuilder(this);
	}

	public INSERT insert() {
		return (INSERT) new InsertBuilder(this);
	}

	public UPDATE update() {
		return (UPDATE) new UpdateBuilder(this);
	}

	public DELETE delete() {
		return (DELETE) new DeleteBuilder(this);
	}

	public ExpressionBuilder expression() {
		return new ExpressionBuilder(this);
	}

	public CREATE create() {
		return (CREATE) new CreateBranchingBuilder(this);
	}

	public DROP drop() {
		return (DROP) new DropBranchingBuilder(this);
	}

	public NEXT nextval(String sequence) {
		return (NEXT) new NextValueSequenceBuilder(this, sequence);
	}

	@Override
	public String getDataTypeName(DataType dataType) {
		return dataType.toString();
	}

	@Override
	public String getPrimaryKeyArgument() {
		return KEYWORD_PRIMARY + SPACE + KEYWORD_KEY;
	}

	@Override
	public String getNotNullArgument() {
		return KEYWORD_NOT + SPACE + KEYWORD_NULL;
	}

	@Override
	public String getUniqueArgument() {
		return KEYWORD_UNIQUE;
	}

	@Override
	public boolean exists(Connection connection, String table) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, null, table, ISqlKeywords.METADATA_TABLE_TYPES.toArray(new String[]{}));
		if (resultSet.next()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isSchemaFilterSupported() {
		return false;
	}

	@Override
	public String getSchemaFilterScript() {
		return null;
	}

	@Override
	public boolean isCatalogForSchema() {
		return false;
	}

	@Override
	public String functionCurrentDate() {
		return ISqlKeywords.FUNCTION_CURRENT_DATE;
	}

	@Override
	public String functionCurrentTime() {
		return ISqlKeywords.FUNCTION_CURRENT_TIME;
	}

	@Override
	public String functionCurrentTimestamp() {
		return ISqlKeywords.FUNCTION_CURRENT_TIMESTAMP;
	}

}
