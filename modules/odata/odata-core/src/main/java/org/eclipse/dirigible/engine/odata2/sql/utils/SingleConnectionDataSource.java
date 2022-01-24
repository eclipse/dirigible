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
package org.eclipse.dirigible.engine.odata2.sql.utils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.logging.Logger;
/**
 * The operations in a OData changeset must be executed within one transaction (connection)
 * Therefore we create a single and non-closable connection datasource for simplicity
 */
public class SingleConnectionDataSource implements DataSource {


    private final Connection nonClosableConnection;

    public SingleConnectionDataSource(Connection connection){
       this.nonClosableConnection = getNonClosableConnection(connection);
    }

    @Override
    public Connection getConnection() {
        return nonClosableConnection;
    }

    @Override
    public Connection getConnection(String username, String password){
        return nonClosableConnection;
    }

    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) {
        throw new UnsupportedOperationException("Not supported");

    }

    @Override
    public void setLoginTimeout(int i) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public int getLoginTimeout() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Logger getParentLogger() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) {
        throw new UnsupportedOperationException("Not supported");
    }

    Connection getNonClosableConnection(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class<?>[] {Connection.class},
                new NonClosableConnection(connection));
    }


    private static class NonClosableConnection implements InvocationHandler {

        private final Connection delegate;

        public NonClosableConnection(Connection delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            switch (method.getName()) {
                case "equals":
                    return (proxy == args[0]);
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "close":
                    return null;
                case "isClosed":
                    return delegate.isClosed();
            }
            try {
                return method.invoke(this.delegate, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}
