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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectivityInjector implements IInjector {
	
	public static final String CONNECTIVITY_CONFIGURATION = "ConnectivityConfiguration"; //$NON-NLS-1$
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectivityInjector.class);
	
	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Object connectivityConfiguration = req.getSession().getAttribute(CONNECTIVITY_CONFIGURATION);
		if (connectivityConfiguration == null) {
			try {
				connectivityConfiguration = lookupConnectivityConfiguration();
				if (connectivityConfiguration != null) {
					req.getSession().setAttribute(CONNECTIVITY_CONFIGURATION, connectivityConfiguration);
					System.getProperties().put(CONNECTIVITY_CONFIGURATION, connectivityConfiguration);
				} else {
					logger.warn(InitParametersInjector.JNDI_CONNECTIVITY_CONFIGURATION + " not present");
				}
			} catch (Exception e) {
				logger.error(DirigibleBridge.class.getCanonicalName(), e);
			}
		}
	}
	
	@Override
	public void injectOnStart(ServletConfig servletConfig) throws ServletException, IOException {
		
		Object connectivityConfiguration = System.getProperties().get(CONNECTIVITY_CONFIGURATION);
		if (connectivityConfiguration == null) {
			try {
				connectivityConfiguration = lookupConnectivityConfiguration();
				if (connectivityConfiguration != null) {
					System.getProperties().put(CONNECTIVITY_CONFIGURATION, connectivityConfiguration);
				} else {
					logger.warn(InitParametersInjector.JNDI_CONNECTIVITY_CONFIGURATION + " not present");
				}
			} catch (Exception e) {
				logger.error(DirigibleBridge.class.getCanonicalName(), e);
			}
		}
	}
	
	/**
	 * Retrieve the Connectivity Configuration from the target platform
	 * 
	 * @return
	 * @throws NamingException
	 */
	private Object lookupConnectivityConfiguration() throws NamingException {
		final InitialContext ctx = new InitialContext();
		String key = InitParametersInjector.get(InitParametersInjector.JNDI_CONNECTIVITY_CONFIGURATION);
		if (key != null) {
			return ctx.lookup(key);
		}
		return null;
	}


}
