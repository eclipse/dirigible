/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.bridge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInjector implements IInjector {

	public static final String DATASOURCE_DEFAULT = "DEFAULT_DATASOURCE"; //$NON-NLS-1$
	public static final String DATABASE_PRODUCT_NAME = "DATABASE_PRODUCT_NAME"; //$NON-NLS-1$
	public static final String DATABASE_PRODUCT_VERSION = "DATABASE_PRODUCT_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_MINOR_VERSION = "DATABASE_MINOR_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_MAJOR_VERSION = "DATABASE_MAJOR_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_DRIVER_NAME = "DATABASE_DRIVER_NAME"; //$NON-NLS-1$
	public static final String DATABASE_DRIVER_MINOR_VERSION = "DATABASE_DRIVER_MINOR_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_DRIVER_MAJOR_VERSION = "DATABASE_DRIVER_MAJOR_VERSION"; //$NON-NLS-1$
	public static final String DATABASE_CONNECTION_CLASS_NAME = "DATABASE_CONNECTION_CLASS_NAME"; //$NON-NLS-1$
	public static final String CUSTOM_DATASOURCE_PARAM_PREFIX = "jndiCustomDataSource-"; //$NON-NLS-1$
	public static final String DATASOURCE_PREFIX = "DATASOURCE_"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(DatabaseInjector.class);

	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		injectDefaultDataSourceOnRequest(req);
		injectCustomDataSourcesOnRequest(servletConfig, req);
	}

	private void injectCustomDataSourcesOnRequest(ServletConfig servletConfig, HttpServletRequest req) {
		Enumeration<String> parameterNames = servletConfig.getInitParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String parameterValue = servletConfig.getInitParameter(parameterName);
			if (parameterName.startsWith(CUSTOM_DATASOURCE_PARAM_PREFIX)) {
				String customDataSourceName = parameterName.substring(CUSTOM_DATASOURCE_PARAM_PREFIX.length());
				try {
					DataSource dataSource = lookupDataSource(parameterValue);
					logger.error(parameterValue + " found and injected in the request");
					req.setAttribute(DATASOURCE_PREFIX + customDataSourceName, dataSource);
				} catch (NamingException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void injectOnStart(ServletConfig servletConfig) throws ServletException, IOException {
		injectDefaultDataSourceOnStart();
	}

	private void injectDefaultDataSourceOnRequest(HttpServletRequest req) {
		DataSource dataSource = (DataSource) System.getProperties().get(DATASOURCE_DEFAULT);
		if (dataSource == null) {
			try {

				String defaultDataSourceType = System.getProperty(InitParametersInjector.INIT_PARAM_DEFAULT_DATASOURCE_TYPE);
				if (!InitParametersInjector.INIT_PARAM_DEFAULT_DATASOURCE_TYPE_JNDI.equals(defaultDataSourceType)) {
					return;
				}

				String key = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_DEFAULT_DATASOURCE);
				dataSource = lookupDataSource(key);
				if (dataSource != null) {
					req.setAttribute(DATASOURCE_DEFAULT, dataSource);
					System.getProperties().put(DATASOURCE_DEFAULT, dataSource);
					Connection connection = null;
					try {
						try {
							connection = dataSource.getConnection();
							DatabaseMetaData metaData = connection.getMetaData();
							setMetaDataToSession(req, connection, metaData);
							setMetaDataToEnv(connection);
						} finally {
							if (connection != null) {
								connection.close();
							}
						}
					} catch (SQLException e) {
						logger.error(e.getMessage(), e);
						// throw new ServletException(ERROR_WHILE_GETTING_DATABASE_METADATA, e);
					}
				} else {
					logger.warn(InitParametersInjector.INIT_PARAM_JNDI_DEFAULT_DATASOURCE + " not present");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void injectDefaultDataSourceOnStart() {
		DataSource dataSource = (DataSource) System.getProperties().get(DATASOURCE_DEFAULT);

		if (dataSource == null) {
			try {

				String defaultDataSourceType = System.getProperty(InitParametersInjector.INIT_PARAM_DEFAULT_DATASOURCE_TYPE);
				if (!InitParametersInjector.INIT_PARAM_DEFAULT_DATASOURCE_TYPE_JNDI.equals(defaultDataSourceType)) {
					return;
				}

				String key = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_DEFAULT_DATASOURCE);
				dataSource = lookupDataSource(key);
				if (dataSource != null) {
					System.getProperties().put(DATASOURCE_DEFAULT, dataSource);
					Connection connection = null;
					try {
						try {
							connection = dataSource.getConnection();
							setMetaDataToEnv(connection);
						} finally {
							if (connection != null) {
								connection.close();
							}
						}
					} catch (SQLException e) {
						logger.error(e.getMessage(), e);
						// throw new ServletException(ERROR_WHILE_GETTING_DATABASE_METADATA, e);
					}
				} else {
					logger.warn(InitParametersInjector.INIT_PARAM_JNDI_DEFAULT_DATASOURCE + " not present");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void setMetaDataToSession(HttpServletRequest req, Connection connection, DatabaseMetaData metaData) throws SQLException {
		req.setAttribute(DATABASE_PRODUCT_NAME, metaData.getDatabaseProductName());
		req.setAttribute(DATABASE_PRODUCT_VERSION, metaData.getDatabaseProductVersion());
		req.setAttribute(DATABASE_MINOR_VERSION, metaData.getDatabaseMinorVersion());
		req.setAttribute(DATABASE_MAJOR_VERSION, metaData.getDatabaseMajorVersion());
		req.setAttribute(DATABASE_DRIVER_NAME, metaData.getDriverName());
		req.setAttribute(DATABASE_DRIVER_MINOR_VERSION, metaData.getDriverMinorVersion());
		req.setAttribute(DATABASE_DRIVER_MAJOR_VERSION, metaData.getDriverMajorVersion());
		req.setAttribute(DATABASE_CONNECTION_CLASS_NAME, connection.getClass().getCanonicalName());
	}

	private void setMetaDataToEnv(Connection connection) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		System.getProperties().put(DATABASE_PRODUCT_NAME, metaData.getDatabaseProductName());
		System.getProperties().put(DATABASE_PRODUCT_VERSION, metaData.getDatabaseProductVersion());
		System.getProperties().put(DATABASE_MINOR_VERSION, metaData.getDatabaseMinorVersion());
		System.getProperties().put(DATABASE_MAJOR_VERSION, metaData.getDatabaseMajorVersion());
		System.getProperties().put(DATABASE_DRIVER_NAME, metaData.getDriverName());
		System.getProperties().put(DATABASE_DRIVER_MINOR_VERSION, metaData.getDriverMinorVersion());
		System.getProperties().put(DATABASE_DRIVER_MAJOR_VERSION, metaData.getDriverMajorVersion());
		System.getProperties().put(DATABASE_CONNECTION_CLASS_NAME, connection.getClass().getCanonicalName());
	}

	/**
	 * Retrieve the DataSource from the target platform
	 *
	 * @return
	 * @throws NamingException
	 */
	private DataSource lookupDataSource(String key) throws NamingException {
		final InitialContext ctx = new InitialContext();
		if (key != null) {
			return (DataSource) ctx.lookup(key);
		}
		return null;
	}

}
