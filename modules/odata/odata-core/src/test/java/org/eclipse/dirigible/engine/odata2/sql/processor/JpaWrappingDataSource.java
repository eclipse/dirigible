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
package org.eclipse.dirigible.engine.odata2.sql.processor;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

public class JpaWrappingDataSource implements DataSource {

    private EntityManagerFactory emf;

    public JpaWrappingDataSource(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == EntityManagerFactory.class) {
            return (T) emf;
        }
        throw new PersistenceException("Unsupported type Error");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == EntityManagerFactory.class) {
            return true;
        }
        return false;
    }

    @Override
    public Connection getConnection() throws SQLException {
        final String url = "jdbc:derby:memory:testDb;create=true"; // (String) emf.createEntityManager().getProperties().get("javax.persistence.jdbc.url");
        return DriverManager.getConnection(url);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        final String url = "jdbc:derby:memory:testDb;create=true"; // (String) emf.createEntityManager().getProperties().get("javax.persistence.jdbc.url");
        return DriverManager.getConnection(url, username, password);
    }

};
