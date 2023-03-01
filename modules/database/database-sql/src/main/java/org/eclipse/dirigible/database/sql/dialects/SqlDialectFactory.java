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
package org.eclipse.dirigible.database.sql.dialects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.ISqlDialectProvider;

/**
 * A factory for creating SqlDialect objects.
 */
public class SqlDialectFactory {
	
	/** The Constant ACCESS_MANAGERS. */
	private static final ServiceLoader<ISqlDialectProvider> SQL_PROVIDERS = ServiceLoader.load(ISqlDialectProvider.class);

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
		
		for (ISqlDialectProvider next : SQL_PROVIDERS) {
			databaseTypeMappings.put(next.getName(), next.getDialect());
		}
		
		return databaseTypeMappings;
	}

}
