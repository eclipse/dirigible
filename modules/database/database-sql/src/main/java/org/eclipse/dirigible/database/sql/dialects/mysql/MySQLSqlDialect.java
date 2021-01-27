/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
			"version"
			
			})));
	
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
		table = normalizeTableName(table);
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, null, DefaultSqlDialect.normalizeTableName(table.toUpperCase()), ISqlKeywords.METADATA_TABLE_TYPES.toArray(new String[] {}));
		if (resultSet.next()) {
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getFunctionsNames()
	 */
	@Override
	public Set<String> getFunctionsNames() {
		return FUNCTIONS;
	}

}
