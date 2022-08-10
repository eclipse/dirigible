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
package org.eclipse.dirigible.runtime.core.listener;

import java.util.HashSet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.dirigible.runtime.core.initializer.DirigibleInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the initialization of all modules and all REST API
 * resources.
 *
 */
public class DirigibleServletContextListener implements ServletContextListener {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DirigibleServletContextListener.class);

	/** The initializer. */
	private static DirigibleInitializer initializer = new DirigibleInitializer();

	/**
	 * Context initialized.
	 *
	 * @param servletContextEvent the servlet context event
	 */
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		initializer.initialize();
	}

	/**
	 * Get singleton services registered to this application.
	 *
	 * @return all singleton services.
	 */
	public static HashSet<Object> getServices() {
		return initializer.getServices();
	}

	/**
	 * Context destroyed.
	 *
	 * @param servletContextEvent the servlet context event
	 */
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		initializer.destory();
	}

}
