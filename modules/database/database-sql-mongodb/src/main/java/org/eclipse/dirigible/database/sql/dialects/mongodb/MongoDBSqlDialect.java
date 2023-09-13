/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects.mongodb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.sql.dialects.DefaultSqlDialect;

/**
 * The MongoDB SQL Dialect.
 */
public class MongoDBSqlDialect extends
		DefaultSqlDialect<SelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, CreateBranchingBuilder, AlterBranchingBuilder, DropBranchingBuilder, 
		NextValueSequenceBuilder, LastValueIdentityBuilder> {

	/** The Constant FUNCTIONS. */
	public static final Set<String> FUNCTIONS = Collections.synchronizedSet(new HashSet<String>(Arrays.asList(new String[] {})));
	
	/**
	 * Checks if is synonym supported.
	 *
	 * @return true, if is synonym supported
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#isSynonymSupported()
	 */
	@Override
	public boolean isSynonymSupported() {
		return false;
	}
	
	/**
	 * Gets the functions names.
	 *
	 * @return the functions names
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlDialect#getFunctionsNames()
	 */
	@Override
	public Set<String> getFunctionsNames() {
		return FUNCTIONS;
	}
    
    /**
	 * Exists schema.
	 *
	 * @param connection the connection
	 * @param schema the schema
	 * @return true, if successful
	 * @throws SQLException the SQL exception
	 */
	@Override
	public boolean existsSchema(Connection connection, String schema) throws SQLException {
		return false;
	}
	
	/**
	 * Count.
	 *
	 * @param connection the connection
	 * @param table the table
	 * @return the int
	 * @throws SQLException the SQL exception
	 */
	@Override
	public int count(Connection connection, String table) throws SQLException {
		// TODO Auto-generated method stub
		return super.count(connection, table);
	}
	
	/**
	 * All.
	 *
	 * @param connection the connection
	 * @param table the table
	 * @return the result set
	 * @throws SQLException the SQL exception
	 */
	@Override
	public ResultSet all(Connection connection, String table) throws SQLException {
		String sql = allQuery(connection, table);
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet resultSet = statement.executeQuery();
		return resultSet;
	}

	/**
	 * Count query.
	 *
	 * @param connection the connection
	 * @param table the table
	 * @return the string
	 */
	@Override
	public String countQuery(Connection connection, String table) {
		String sql = "{ 'count': '" + normalizeTableNameOnly(table) + "'}";
		return sql;
	}

	/**
	 * All query.
	 *
	 * @param connection the connection
	 * @param table the table
	 * @return the string
	 */
	@Override
	public String allQuery(Connection connection, String table) {
		String sql = "{ 'find': '" + normalizeTableNameOnly(table) + "'}";
		return sql;
	}
	
	

}
