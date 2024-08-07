/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.dialects;

import org.eclipse.dirigible.components.database.DatabaseSystem;
import org.eclipse.dirigible.components.database.DirigibleDataSource;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.ISqlDialectProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * A factory for creating SqlDialect objects.
 */
public class SqlDialectFactory {

    /** The Constant ACCESS_MANAGERS. */
    private static final ServiceLoader<ISqlDialectProvider> SQL_PROVIDERS = ServiceLoader.load(ISqlDialectProvider.class);

    /** The Constant dialectsByName. */
    // Lifted from Activiti
    private static final Map<String, ISqlDialect> dialectsByName = Collections.synchronizedMap(new HashMap<>());

    private static final Map<DatabaseSystem, ISqlDialect> dialectsBySystem = Collections.synchronizedMap(new HashMap<>());

    static {
        loadDefaultDialectsByName();
    }

    public static final ISqlDialect getDialect(DataSource dataSource) throws SQLException {
        if (dataSource instanceof DirigibleDataSource dds) {
            return getDialect(dds);
        }

        try (Connection connection = dataSource.getConnection()) {
            return getDialect(connection);
        }
    }

    /**
     * Gets the dialect.
     *
     * @param connection the connection
     * @return the dialect
     * @throws SQLException the SQL exception
     */
    public static final ISqlDialect getDialect(Connection connection) throws SQLException {
        String productName = connection.getMetaData()
                                       .getDatabaseProductName();
        ISqlDialect dialect = dialectsByName.get(productName);
        if (dialect == null) {
            loadDefaultDialectsByName();
            dialect = dialectsByName.get(productName);
            if (dialect == null) {
                throw new IllegalStateException("Database dialect for " + productName + " is not available.");
            }
        }
        return dialect;
    }

    private static void loadDefaultDialectsByName() {
        for (ISqlDialectProvider provider : SQL_PROVIDERS) {
            dialectsByName.put(provider.getName(), provider.getDialect());
        }
    }

    public static final ISqlDialect getDialect(DirigibleDataSource dataSource) throws SQLException {
        DatabaseSystem databaseSystem = dataSource.getDatabaseSystem();
        return getDialect(databaseSystem);
    }

    public static final ISqlDialect getDialect(DatabaseSystem databaseSystem) throws SQLException {
        ISqlDialect dialect = dialectsBySystem.get(databaseSystem);
        if (dialect == null) {
            loadDefaultDialectsBySystem();
            dialect = dialectsBySystem.get(databaseSystem);
            if (dialect == null) {
                throw new IllegalStateException("Database dialect for [" + databaseSystem + "] is not available.");
            }
        }
        return dialect;
    }

    private static void loadDefaultDialectsBySystem() {
        for (ISqlDialectProvider provider : SQL_PROVIDERS) {
            dialectsBySystem.put(provider.getDatabaseSystem(), provider.getDialect());
        }
    }

}
