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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.datasource.NamedDataSourcesInitializer;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.task.IRunnableTask;
import org.eclipse.dirigible.runtime.task.TaskManagerShort;

/**
 * Enumerate and register the named DataSources on startup
 */
public class DataSourcesInitializerServlet extends HttpServlet {

	private static final long serialVersionUID = 6468050094756163896L;

	private static final Logger logger = Logger.getLogger(DataSourcesInitializerServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void registerInitRegister() {
		TaskManagerShort.getInstance().registerRunnableTask(new DataSourcesInitializerRegister(this));
		logger.info("DataSources Register has been registered");
	}

	class DataSourcesInitializerRegister implements IRunnableTask {

		DataSourcesInitializerServlet contentInitializerServlet;

		DataSourcesInitializerRegister(DataSourcesInitializerServlet contentInitializerServlet) {
			this.contentInitializerServlet = contentInitializerServlet;
		}

		@Override
		public String getName() {
			return "DataSources Register";
		}

		@Override
		public void start() {
			boolean ok = false;
			try {
				ok = registerDataSources(null);
			} catch (ServletException e) {
				logger.error(e.getMessage(), e);
			}
			if (ok) {
				TaskManagerShort.getInstance().unregisterRunnableTask(this);
				logger.info("DataSources Register has been un-registered");
			}
		}

	}

	private boolean registerDataSources(HttpServletRequest request) throws ServletException {
		try {
			NamedDataSourcesInitializer namedDataSourcesInitializer = new NamedDataSourcesInitializer();
			IRepository repository = RepositoryFacade.getInstance().getRepository(request);
			boolean result = namedDataSourcesInitializer.initializeAvailableDataSources(request, repository);
			return result;
		} catch (Exception e) {
			throw new ServletException("Initializing local database for Repository use failed", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		registerDataSources(req);
	}

}
