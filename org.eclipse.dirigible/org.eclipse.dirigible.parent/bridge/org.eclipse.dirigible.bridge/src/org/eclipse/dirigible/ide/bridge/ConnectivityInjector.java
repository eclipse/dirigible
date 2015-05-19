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

public class ConnectivityInjector implements Injector {
	
	public static final String JAVA_COMP_ENV_CONNECTIVITY_CONFIGURATION = "java:comp/env/connectivity/Configuration"; //$NON-NLS-1$
	public static final String CONNECTIVITY_CONFIGURATION = "ConnectivityConfiguration"; //$NON-NLS-1$
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectivityInjector.class);
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.ide.bridge.Injector#inject(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void inject(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Object connectivityConfiguration = req.getSession().getAttribute(CONNECTIVITY_CONFIGURATION);
		if (connectivityConfiguration == null) {
			try {
				connectivityConfiguration = lookupConnectivityConfiguration();
				req.getSession().setAttribute(CONNECTIVITY_CONFIGURATION, connectivityConfiguration);
				System.getProperties().put(CONNECTIVITY_CONFIGURATION, connectivityConfiguration);
			} catch (Exception e) {
				logger.error(DirigibleBridge.class.getCanonicalName(), e);
			}
		}
	}
	
	/**
	 * Retrieve the Connectivity Configuration from the target server environment
	 * 
	 * @return
	 * @throws NamingException
	 */
	private Object lookupConnectivityConfiguration() throws NamingException {
		final InitialContext ctx = new InitialContext();
		return ctx.lookup(JAVA_COMP_ENV_CONNECTIVITY_CONFIGURATION);
	}


}
