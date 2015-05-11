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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InitParametersInjector implements Injector {
	
	public static final String RUNTIME_URL = "runtimeUrl"; //$NON-NLS-1$
	public static final String SERVICES_URL = "servicesUrl"; //$NON-NLS-1$
	public static final String ENABLE_ROLES = "enableRoles"; //$NON-NLS-1$
	public static final String LOG_IN_SYSTEM_OUTPUT = "logInSystemOutput"; //$NON-NLS-1$
	
	@Override
	public void inject(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String parameter = servletConfig.getInitParameter(RUNTIME_URL);
		req.getSession().setAttribute(RUNTIME_URL, parameter);
		System.getProperties().put(RUNTIME_URL, parameter);
		
		parameter = servletConfig.getInitParameter(SERVICES_URL);
		req.getSession().setAttribute(SERVICES_URL, parameter);
		System.getProperties().put(SERVICES_URL, parameter);
		
		parameter = servletConfig.getInitParameter(ENABLE_ROLES);
		req.getSession().setAttribute(ENABLE_ROLES, parameter);
		System.getProperties().put(ENABLE_ROLES, parameter);
		
		parameter = servletConfig.getInitParameter(LOG_IN_SYSTEM_OUTPUT);
		req.getSession().setAttribute(LOG_IN_SYSTEM_OUTPUT, parameter);
		System.getProperties().put(LOG_IN_SYSTEM_OUTPUT, parameter);
		
	}

}
