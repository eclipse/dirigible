/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.sources.manager;

import org.eclipse.dirigible.components.api.security.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * The WrappedDataSource of the standard JDBC {@link DataSource} object with added some additional
 * capabilities..
 */
public class ManagedDataSource implements DataSource {

    /** The Constant LOGGER. */
    private static final Logger logger = LoggerFactory.getLogger(ManagedDataSource.class);

    /** The Constant DATABASE_NAME_HDB. */
    private static final String DATABASE_NAME_HDB = "HDB";
    /** The Constant DATABASE_NAME_SNOWFLAKE. */
    private static final String DATABASE_NAME_SNOWFLAKE = "Snowflake";
    /** The original data source. */
    private final DataSource originalDataSource;
    /** The database name. */
    private String databaseName;

    /**
     * Wrapper of the default datasource provided by the underlying platform It has some fault tolerance
     * features, which are not available by default in the popular JDBC drivers.
     *
     * @param originalDataSource the original data source
     */
    public ManagedDataSource(DataSource originalDataSource) {
        this.originalDataSource = originalDataSource;
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     * @throws SQLException the SQL exception
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = originalDataSource.getConnection();

        enhanceConnection(connection);

        return connection;
    }

    /**
     * Enhance connection.
     *
     * @param connection the connection
     * @throws SQLException the SQL exception
     */
    private void enhanceConnection(Connection connection) throws SQLException {

        if (this.databaseName == null) {
            this.databaseName = connection.getMetaData()
                                          .getDatabaseProductName();
        }

        // HANA
        if (databaseName.equals(DATABASE_NAME_HDB)) {
            Authentication authentication = SecurityContextHolder.getContext()
                                                                 .getAuthentication();
            String userName;
            if (authentication != null) {
                userName = authentication.getName();
            } else {
                userName = UserFacade.getName();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Setting APPLICATIONUSER:{} for connection: {}", userName, connection);
            }
            connection.setClientInfo("APPLICATIONUSER", userName);

            if (logger.isDebugEnabled()) {
                logger.debug("Setting XS_APPLICATIONUSER:{} for connection: {}", userName, connection);
            }
            connection.setClientInfo("XS_APPLICATIONUSER", userName);
        }

        // Snowflake
        if (databaseName.equals(DATABASE_NAME_SNOWFLAKE)) {
            connection.createStatement()
                      .executeQuery("ALTER SESSION SET JDBC_QUERY_RESULT_FORMAT='JSON'");
        }
    }

    /**
     * Gets the connection.
     *
     * @param username the username
     * @param password the password
     * @return the connection
     * @throws SQLException the SQL exception
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = originalDataSource.getConnection(username, password);

        enhanceConnection(connection);

        return connection;
    }

    /**
     * Gets the log writer.
     *
     * @return the log writer
     * @throws SQLException the SQL exception
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return originalDataSource.getLogWriter();
    }

    /**
     * Sets the log writer.
     *
     * @param arg0 the new log writer
     * @throws SQLException the SQL exception
     */
    @Override
    public void setLogWriter(PrintWriter arg0) throws SQLException {
        originalDataSource.setLogWriter(arg0);
    }

    /**
     * Gets the login timeout.
     *
     * @return the login timeout
     * @throws SQLException the SQL exception
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return originalDataSource.getLoginTimeout();
    }

    /**
     * Sets the login timeout.
     *
     * @param arg0 the new login timeout
     * @throws SQLException the SQL exception
     */
    @Override
    public void setLoginTimeout(int arg0) throws SQLException {
        originalDataSource.setLoginTimeout(arg0);
    }

    /**
     * Checks if is wrapper for.
     *
     * @param arg0 the arg 0
     * @return true, if is wrapper for
     * @throws SQLException the SQL exception
     */
    @Override
    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        return originalDataSource.isWrapperFor(arg0);
    }

    /**
     * Unwrap.
     *
     * @param <T> the generic type
     * @param arg0 the arg 0
     * @return the t
     * @throws SQLException the SQL exception
     */
    @Override
    public <T> T unwrap(Class<T> arg0) throws SQLException {
        return originalDataSource.unwrap(arg0);
    }

    /**
     * Gets the parent logger.
     *
     * @return the parent logger
     * @throws SQLFeatureNotSupportedException the SQL feature not supported exception
     */
    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public String toString() {
        return "ManagedDataSource{" + "databaseName='" + databaseName + '\'' + '}';
    }
}
