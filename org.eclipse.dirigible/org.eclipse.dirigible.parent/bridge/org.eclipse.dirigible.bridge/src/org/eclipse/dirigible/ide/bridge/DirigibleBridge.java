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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.equinox.servletbridge.BridgeServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhanced Bridge Servlet which retrieves specific parameters and objects
 * from the target environment and provide them to RAP OSGi environment thru the Session object
 */
public class DirigibleBridge extends BridgeServlet {

	private static final long serialVersionUID = -8043662807856187626L;

	private static final Logger logger = LoggerFactory.getLogger(DirigibleBridge.class.getCanonicalName());

	static Properties ENV_PROPERTIES = new Properties();

	static Class<IInjector>[] INJECTOR_CLASSES;

	public static Map<String, Object> BRIDGES = Collections.synchronizedMap(new HashMap<String, Object>());

	static {
		INJECTOR_CLASSES = new Class[] { InitParametersInjector.class, InitialContextInjector.class, DatabaseInjector.class,
				ProxyParametersInjector.class, LocalParametersInjector.class, MailInjector.class, ConnectivityInjector.class, CmisInjector.class,
				AnonymousUserInjector.class };
	}

	@Override
	public void init() throws ServletException {

		ENV_PROPERTIES.putAll(System.getProperties());

		for (Object property : ENV_PROPERTIES.keySet()) {
			logger.info("SYSTEM_" + property + ": " + ENV_PROPERTIES.getProperty(property.toString()));
		}

		ServletConfig servletConfig = getServletConfig();

		for (Class injectorClass : INJECTOR_CLASSES) {
			try {
				IInjector injector = (IInjector) injectorClass.newInstance();
				injector.injectOnStart(servletConfig);
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
		}

		super.init();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ServletConfig servletConfig = getServletConfig();

		for (Class injectorClass : INJECTOR_CLASSES) {
			try {
				IInjector injector = (IInjector) injectorClass.newInstance();
				injector.injectOnRequest(servletConfig, req, resp);
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
		}

		super.service(req, resp);
	}

}
