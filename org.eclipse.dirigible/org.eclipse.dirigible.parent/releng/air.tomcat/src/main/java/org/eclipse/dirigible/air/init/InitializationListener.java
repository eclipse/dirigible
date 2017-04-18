/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.air.init;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Initialization Context Listener for AIR
 */
public class InitializationListener implements ServletContextListener {

	public static final Logger logger = Logger.getLogger(InitializationListener.class.getCanonicalName());

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.info("Eclipse Dirigible AIR started. Initialization of databases, repository and configurations triggerred.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("ServletContextListener destroyed");
	}

}
