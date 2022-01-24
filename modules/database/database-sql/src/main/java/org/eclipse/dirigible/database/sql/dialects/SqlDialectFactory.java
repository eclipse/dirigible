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
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.dialects.derby.DerbySqlDialect;
import org.eclipse.dirigible.database.sql.dialects.h2.H2SqlDialect;
import org.eclipse.dirigible.database.sql.dialects.hana.HanaSqlDialect;
import org.eclipse.dirigible.database.sql.dialects.mysql.MySQLSqlDialect;
import org.eclipse.dirigible.database.sql.dialects.postgres.PostgresSqlDialect;
import org.eclipse.dirigible.database.sql.dialects.sybase.SybaseSqlDialect;

/**
 * A factory for creating SqlDialect objects.
 */
public class SqlDialectFactory {

	/**
	 * Gets the dialect.
	 *
	 * @param connection
	 *            the connection
	 * @return the dialect
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static final ISqlDialect getDialect(Connection connection) throws SQLException {
		String productName = connection.getMetaData().getDatabaseProductName();
		return databaseTypeMappings.get(productName);
	}

	/** The Constant DATABASE_TYPE_DERBY. */
	public static final ISqlDialect DATABASE_TYPE_DERBY = new DerbySqlDialect();

	/** The Constant DATABASE_TYPE_H2. */
	public static final ISqlDialect DATABASE_TYPE_H2 = new H2SqlDialect();
	
	/** The Constant DATABASE_TYPE_MYSQL. */
	public static final ISqlDialect DATABASE_TYPE_MYSQL = new MySQLSqlDialect();
	
	/** The Constant DATABASE_TYPE_POSTGRES. */
	public static final ISqlDialect DATABASE_TYPE_POSTGRES = new PostgresSqlDialect();
	
	/** The Constant DATABASE_TYPE_HANA. */
	public static final ISqlDialect DATABASE_TYPE_HANA = new HanaSqlDialect();

	/** The Constant DATABASE_TYPE_SYBASE. */
	public static final ISqlDialect DATABASE_TYPE_SYBASE = new SybaseSqlDialect();
	
	// public static final ISqlDialect DATABASE_TYPE_MSSQL = "mssql";
	// public static final ISqlDialect DATABASE_TYPE_DB2 = "db2";
	// public static final ISqlDialect DATABASE_TYPE_HSQL = "hsql";
	// public static final ISqlDialect DATABASE_TYPE_ORACLE = "oracle";

	/** The Constant databaseTypeMappings. */
	// Lifted from Activiti
	protected static final Map<String, ISqlDialect> databaseTypeMappings = getDefaultDatabaseTypeMappings();

	/**
	 * Gets the default database type mappings.
	 *
	 * @return the default database type mappings
	 */
	protected static Map<String, ISqlDialect> getDefaultDatabaseTypeMappings() {
		Map<String, ISqlDialect> databaseTypeMappings = Collections.synchronizedMap(new HashMap<String, ISqlDialect>());
		databaseTypeMappings.put("Apache Derby", DATABASE_TYPE_DERBY);
		databaseTypeMappings.put("H2", DATABASE_TYPE_H2);
		databaseTypeMappings.put("PostgreSQL", DATABASE_TYPE_POSTGRES);
		databaseTypeMappings.put("HDB", DATABASE_TYPE_HANA);
		databaseTypeMappings.put("Adaptive Server Enterprise", DATABASE_TYPE_SYBASE);
		databaseTypeMappings.put("MySQL", DATABASE_TYPE_MYSQL);
		
		// databaseTypeMappings.setProperty("HSQL Database Engine", DATABASE_TYPE_HSQL);
		
		// databaseTypeMappings.setProperty("Oracle", DATABASE_TYPE_ORACLE);
		// databaseTypeMappings.setProperty("Microsoft SQL Server", DATABASE_TYPE_MSSQL);
		// databaseTypeMappings.setProperty(DATABASE_TYPE_DB2,DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/NT",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/NT64",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2 UDP",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/LINUX",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/LINUX390",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/LINUXX8664",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/LINUXZ64",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/LINUXPPC64",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/LINUXPPC64LE",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/400 SQL",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/6000",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2 UDB iSeries",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/AIX64",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/HPUX",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/HP64",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/SUN",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/SUN64",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/PTX",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2/2",DATABASE_TYPE_DB2);
		// databaseTypeMappings.setProperty("DB2 UDB AS400", DATABASE_TYPE_DB2);
		
		return databaseTypeMappings;
	}

}
