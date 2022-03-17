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
package org.eclipse.dirigible.database.sql.dialects;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.DatabaseArtifactTypes;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlException;
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
public class DefaultSqlDialect<SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, ALTER extends AlterBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder, LAST extends LastValueIdentityBuilder>
		implements ISqlDialect<SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, NEXT, LAST> {
	
	public static final Set<String> FUNCTIONS = Collections.synchronizedSet(new HashSet<String>(Arrays.asList(new String[] {
			"ascii",
			"char_length",
			"character_length",
			"concat",
			"concat_ws",
			"field",
			"find_in_set",
			"format",
			"insert",
			"instr",
			"lcase",
			"left",
			"length",
			"locate",
			"lower",
			"lpad",
			"ltrim",
			"mid",
			"position",
			"repeat",
			"replace",
			"reverse",
			"right",
			"rpad",
			"rtrim",
			"space",
			"strcmp",
			"substr",
			"substring",
			"substring_index",
			"trim",
			"ucase",
			"upper",
			
			"abs",
			"acos",
			"asin",
			"atan",
			"atan2",
			"avg",
			"ceil",
			"ceiling",
			"cos",
			"cot",
			"count",
			"degrees",
			"div",
			"exp",
			"floor",
			"greatest",
			"least",
			"ln",
			"log",
			"log10",
			"log2",
			"max",
			"min",
			"mod",
			"pi",
			"pow",
			"power",
			"radians",
			"rand",
			"round",
			"sign",
			"sin",
			"sqrt",
			"sum",
			"tan",
			"truncate",
			
			"adddate",
			"addtime",
			"curdate",
			"current_date",
			"current_time",
			"current_timestamp",
			"curtime",
			"date",
			"datediff",
			"date_add",
			"date_format",
			"date_sub",
			"day",
			"dayname",
			"dayofmonth",
			"dayofweek",
			"dayofyear",
			"extract",
			"from_days",
			"hour",
			"last_day",
			"localtime",
			"localtimestamp",
			"makedate",
			"maketime",
			"microsecond",
			"minute",
			"month",
			"monthname",
			"now",
			"period_add",
			"period_diff",
			"quarter",
			"second",
			"sec_to_time",
			"str_to_date",
			"subdate",
			"subtime",
			"sysdate",
			"time",
			"time_format",
			"time_to_sec",
			"timediff",
			"timestamp",
			"to_days",
			"week",
			"weekday",
			"weekofyear",
			"year",
			"yearweek",
			
			"bin",
			"binary",
			"case",
			"cast",
			"coalesce",
			"connection_id",
			"conv",
			"convert",
			"current_user",
			"database",
			"if",
			"ifnull",
			"isnull",
			"last_insert_id",
			"nullif",
			"session_user",
			"system_user",
			"user",
			"version",
			
			"and",
			"or",
			"between",
			"binary",
			"case",
			"div",
			"in",
			"is",
			"not",
			"null",
			"like",
			"rlike",
			"xor"
			
			})));

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
	 * @see org.eclipse.dirigible.database.sql.ISqlFactory#create()
	 */
	@Override
	public ALTER alter() {
		return (ALTER) new AlterBranchingBuilder(this);
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
		return exists(connection, table, DatabaseArtifactTypes.TABLE);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#exists(java.sql.Connection, java.lang.String, java.lang.int)
	 */
	@Override
	public boolean exists(Connection connection, String table, int type) throws SQLException {
		return exists(connection, null, table, type);
	}

	@Override
	public boolean exists(Connection connection, String schema, String table, int type) throws SQLException {
		table = normalizeTableName(table);
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, schema, normalizeTableName(table), ISqlKeywords.METADATA_TABLE_TYPES.toArray(new String[] {}));
		if (resultSet.next()) {
			return true;
		}
		return false;
	}

	public static String normalizeTableName(String table) {
		if (table != null && table.startsWith("\"") && table.endsWith("\"")) {
			table = table.substring(1, table.length()-1);
		}
		return table;
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

	@Override
	public boolean isSynonymSupported() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getFunctionsNames()
	 */
	@Override
	public Set<String> getFunctionsNames() {
		return FUNCTIONS;
	}

	@Override
	public String getFuzzySearchIndex() {
		return " ";
	}

}
