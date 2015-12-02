/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.content;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.db.init.DBRepositoryInitializer;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.task.IRunnableTask;
import org.eclipse.dirigible.runtime.task.TaskManagerShort;

/**
 * Checks and imports if needed the default content into the Registry on startup
 */
public class DBInitializerServlet extends HttpServlet {

	private static final long serialVersionUID = 6468050094756163896L;

	private static final Logger logger = Logger.getLogger(DBInitializerServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void registerInitRegister() {
		TaskManagerShort.getInstance().registerRunnableTask(new DBInitializerRegister(this));
		logger.info("Database Initializer Register has been registered");
	}

	class DBInitializerRegister implements IRunnableTask {

		DBInitializerServlet contentInitializerServlet;

		DBInitializerRegister(DBInitializerServlet contentInitializerServlet) {
			this.contentInitializerServlet = contentInitializerServlet;
		}

		@Override
		public String getName() {
			return "Database Initializer Register";
		}

		@Override
		public void start() {
			boolean ok = false;
			try {
				ok = initDefaultDatabaseContent(null);
			} catch (ServletException e) {
				logger.error(e.getMessage(), e);
			}
			if (ok) {
				TaskManagerShort.getInstance().unregisterRunnableTask(this);
				logger.info("Database Initializer Register has been un-registered");
			}
		}

	}

	private boolean initDefaultDatabaseContent(HttpServletRequest request) throws ServletException {

		DataSource dataSource = DataSourceFacade.getInstance().getDataSource(request);

		try {
			Connection connection = dataSource.getConnection();
			try {
				DBRepositoryInitializer dbRepositoryInitializer = new DBRepositoryInitializer(dataSource, connection, false);
				boolean result = dbRepositoryInitializer.initialize();
				return result;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			throw new ServletException("Initializing local database for Repository use failed", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		initDefaultDatabaseContent(req);
	}

}
