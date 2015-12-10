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

import org.eclipse.dirigible.runtime.scheduler.SchedulerActivator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Activator of the Initializer plugin
 */
public class InitActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {

		// Initializes the default database content - tables, views and records
		DBInitializerServlet dbInitializerServlet = new DBInitializerServlet();
		dbInitializerServlet.registerInitRegister();

		// Initializes the default Repository content - resources
		ContentInitializerServlet contentInitializerServlet = new ContentInitializerServlet();
		contentInitializerServlet.registerInitRegister();

		// Initializes the default data-sources definitions from the configuration store
		DataSourcesInitializerServlet datasourcesInitializerServlet = new DataSourcesInitializerServlet();
		datasourcesInitializerServlet.registerInitRegister();

		// Initializes the content from the pre-configured Master Repository - DB, Git, FileSystem, ...
		MasterRepositorySynchronizerServlet masterRepositorySynchronizerServlet = new MasterRepositorySynchronizerServlet();
		masterRepositorySynchronizerServlet.registerInitRegister();

		// Start all the scheduled tasks
		SchedulerActivator.getSchedulerServlet().startSchedulers();
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

}
