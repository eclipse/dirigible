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
package org.eclipse.dirigible.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

public class Driver implements java.sql.Driver {

	  public static final int MAJOR_VERSION = 1;
	  public static final int MINOR_VERSION = 0;
	  
	  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Driver.class);
	  
	  static {
	    try {
	      java.sql.DriverManager.registerDriver(new Driver());
	    } catch (SQLException e) {
	    	LOG.error("Error while registering the JDBC Driver", e);
			throw new RuntimeException(e);
	    }
	  }

	  public static String getVersion() {
	    return "MongoDB " + MAJOR_VERSION + "." + MINOR_VERSION + " JDBC Driver";
	  }

	  public boolean acceptsURL(String url) throws SQLException {
	    return url.startsWith("jdbc:mongodb:");
	  }

	  public Connection connect(String url, Properties info) throws SQLException {
	    return new MongoDBConnection(url, info);
	  }

	  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	  }

	  public boolean jdbcCompliant() {
	    return false;
	  }

	  public int getMajorVersion() {
	    return MAJOR_VERSION;
	  }

	  public int getMinorVersion() {
	    return MINOR_VERSION;
	  }

	  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
	    throw new SQLFeatureNotSupportedException("The Driver uses slf4j for logging");
	  }

}
