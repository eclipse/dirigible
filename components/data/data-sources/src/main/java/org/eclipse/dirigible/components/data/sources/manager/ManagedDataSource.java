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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

import org.eclipse.dirigible.components.api.security.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The WrappedDataSource of the standard JDBC {@link DataSource} object with added some additional
 * capabilities..
 */
public class ManagedDataSource implements DataSource {

  /** The Constant LOGGER. */
  private static final Logger logger = LoggerFactory.getLogger(ManagedDataSource.class);

  /** The Constant DATABASE_NAME_HDB. */
  private static final String DATABASE_NAME_HDB = "HDB";

  /** The database name. */
  private String databaseName;

  /** The original data source. */
  private final DataSource originalDataSource;

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
  /*
   * (non-Javadoc)
   *
   * @see javax.sql.DataSource#getConnection()
   */
  @Override
  public Connection getConnection() throws SQLException {
    return getConnection(null, null);
  }

  /**
   * Gets the connection.
   *
   * @param username the username
   * @param password the password
   * @return the connection
   * @throws SQLException the SQL exception
   */
  /*
   * (non-Javadoc)
   *
   * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
   */
  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    Connection connection = originalDataSource.getConnection();

    if (this.databaseName == null) {
      this.databaseName = connection.getMetaData()
                                    .getDatabaseProductName();
    }

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

    return connection;
  }

  /**
   * Gets the log writer.
   *
   * @return the log writer
   * @throws SQLException the SQL exception
   */
  /*
   * (non-Javadoc)
   *
   * @see javax.sql.CommonDataSource#getLogWriter()
   */
  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return originalDataSource.getLogWriter();
  }

  /**
   * Gets the login timeout.
   *
   * @return the login timeout
   * @throws SQLException the SQL exception
   */
  /*
   * (non-Javadoc)
   *
   * @see javax.sql.CommonDataSource#getLoginTimeout()
   */
  @Override
  public int getLoginTimeout() throws SQLException {
    return originalDataSource.getLoginTimeout();
  }

  /**
   * Checks if is wrapper for.
   *
   * @param arg0 the arg 0
   * @return true, if is wrapper for
   * @throws SQLException the SQL exception
   */
  /*
   * (non-Javadoc)
   *
   * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
   */
  @Override
  public boolean isWrapperFor(Class<?> arg0) throws SQLException {
    return originalDataSource.isWrapperFor(arg0);
  }

  /**
   * Sets the log writer.
   *
   * @param arg0 the new log writer
   * @throws SQLException the SQL exception
   */
  /*
   * (non-Javadoc)
   *
   * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
   */
  @Override
  public void setLogWriter(PrintWriter arg0) throws SQLException {
    originalDataSource.setLogWriter(arg0);
  }

  /**
   * Sets the login timeout.
   *
   * @param arg0 the new login timeout
   * @throws SQLException the SQL exception
   */
  /*
   * (non-Javadoc)
   *
   * @see javax.sql.CommonDataSource#setLoginTimeout(int)
   */
  @Override
  public void setLoginTimeout(int arg0) throws SQLException {
    originalDataSource.setLoginTimeout(arg0);
  }

  /**
   * Unwrap.
   *
   * @param <T> the generic type
   * @param arg0 the arg 0
   * @return the t
   * @throws SQLException the SQL exception
   */
  /*
   * (non-Javadoc)
   *
   * @see java.sql.Wrapper#unwrap(java.lang.Class)
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
  /*
   * (non-Javadoc)
   *
   * @see javax.sql.CommonDataSource#getParentLogger()
   */
  @Override
  public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }

}
