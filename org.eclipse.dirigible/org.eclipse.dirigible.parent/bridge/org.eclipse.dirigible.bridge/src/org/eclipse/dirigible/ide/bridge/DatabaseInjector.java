/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.bridge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

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
	
	private static final Logger logger = LoggerFactory.getLogger(DatabaseInjector.class);
	
	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		DataSource dataSource = (DataSource) req.getSession().getAttribute(DATASOURCE_DEFAULT);
		if (dataSource == null) {
			try {
				dataSource = lookupDataSource();
				if (dataSource != null) {
					req.getSession().setAttribute(DATASOURCE_DEFAULT, dataSource);
					Connection connection = null;
					try {
						try {
							connection = dataSource.getConnection();
							DatabaseMetaData metaData = connection.getMetaData();
							req.getSession().setAttribute(DATABASE_PRODUCT_NAME, metaData.getDatabaseProductName());
							req.getSession().setAttribute(DATABASE_PRODUCT_VERSION, metaData.getDatabaseProductVersion());
							req.getSession().setAttribute(DATABASE_MINOR_VERSION, metaData.getDatabaseMinorVersion());
							req.getSession().setAttribute(DATABASE_MAJOR_VERSION, metaData.getDatabaseMajorVersion());
							req.getSession().setAttribute(DATABASE_DRIVER_NAME, metaData.getDriverName());
							req.getSession().setAttribute(DATABASE_DRIVER_MINOR_VERSION, metaData.getDriverMinorVersion());
							req.getSession().setAttribute(DATABASE_DRIVER_MAJOR_VERSION, metaData.getDriverMajorVersion());
							req.getSession().setAttribute(DATABASE_CONNECTION_CLASS_NAME, connection.getClass().getCanonicalName());
						} finally {
							if (connection != null) {
								connection.close();
							}
						}
					} catch (SQLException e) {
						logger.error(e.getMessage(), e);
						//throw new ServletException(ERROR_WHILE_GETTING_DATABASE_METADATA, e);
					}
				} else {
					logger.warn(InitParametersInjector.JNDI_DEFAULT_DATASOURCE + " not present");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public void injectOnStart(ServletConfig servletConfig) throws ServletException, IOException {
		DataSource dataSource = (DataSource) System.getProperties().get(DATASOURCE_DEFAULT);
		if (dataSource == null) {
			try {
				dataSource = lookupDataSource();
				if (dataSource != null) {
					System.getProperties().put(DATASOURCE_DEFAULT, dataSource);
					Connection connection = null;
					try {
						try {
							connection = dataSource.getConnection();
							DatabaseMetaData metaData = connection.getMetaData();
							System.getProperties().put(DATABASE_PRODUCT_NAME, metaData.getDatabaseProductName());
							System.getProperties().put(DATABASE_PRODUCT_VERSION, metaData.getDatabaseProductVersion());
							System.getProperties().put(DATABASE_MINOR_VERSION, metaData.getDatabaseMinorVersion());
							System.getProperties().put(DATABASE_MAJOR_VERSION, metaData.getDatabaseMajorVersion());
							System.getProperties().put(DATABASE_DRIVER_NAME, metaData.getDriverName());
							System.getProperties().put(DATABASE_DRIVER_MINOR_VERSION, metaData.getDriverMinorVersion());
							System.getProperties().put(DATABASE_DRIVER_MAJOR_VERSION, metaData.getDriverMajorVersion());
							System.getProperties().put(DATABASE_CONNECTION_CLASS_NAME, connection.getClass().getCanonicalName());
						} finally {
							if (connection != null) {
								connection.close();
							}
						}
					} catch (SQLException e) {
						logger.error(e.getMessage(), e);
						//throw new ServletException(ERROR_WHILE_GETTING_DATABASE_METADATA, e);
					}
				} else {
					logger.warn(InitParametersInjector.JNDI_DEFAULT_DATASOURCE + " not present");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	
	/**
	 * Retrieve the DataSource from the target platform
	 * 
	 * @return
	 * @throws NamingException
	 */
	private DataSource lookupDataSource() throws NamingException {
		final InitialContext ctx = new InitialContext();
		String key = InitParametersInjector.get(InitParametersInjector.JNDI_DEFAULT_DATASOURCE);
		if (key != null) {
			return (DataSource) ctx.lookup(key);
		}
		return null;
	}

}
