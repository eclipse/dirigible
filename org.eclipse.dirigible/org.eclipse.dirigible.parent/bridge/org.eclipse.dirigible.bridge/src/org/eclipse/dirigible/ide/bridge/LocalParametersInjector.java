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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalParametersInjector implements IInjector {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalParametersInjector.class); 
	
	public static final String HC_LOCAL_HTTP_PORT = "HC_LOCAL_HTTP_PORT"; //$NON-NLS-1$
	public static final String HC_APPLICATION_URL = "HC_APPLICATION_URL"; //$NON-NLS-1$
	public static final String HC_APPLICATION = "HC_APPLICATION"; //$NON-NLS-1$
	public static final String HC_ACCOUNT = "HC_ACCOUNT"; //$NON-NLS-1$
	public static final String HC_REGION = "HC_REGION"; //$NON-NLS-1$
	public static final String HC_HOST = "HC_HOST"; //$NON-NLS-1$
	
	
	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String parameterHC_HOST = DirigibleBridge.ENV_PROPERTIES.getProperty(HC_HOST);
		req.getSession().setAttribute(HC_HOST, parameterHC_HOST);
		logger.debug("HC_HOST:" + parameterHC_HOST);
		String parameterHC_REGION = DirigibleBridge.ENV_PROPERTIES.getProperty(HC_REGION);
		req.getSession().setAttribute(HC_REGION, parameterHC_REGION);
		logger.debug("HC_REGION:" + parameterHC_REGION);
		String parameterHC_ACCOUNT = DirigibleBridge.ENV_PROPERTIES.getProperty(HC_ACCOUNT);
		req.getSession().setAttribute(HC_ACCOUNT, parameterHC_ACCOUNT);
		logger.debug("HC_ACCOUNT:" + parameterHC_ACCOUNT);
		String parameterHC_APPLICATION = DirigibleBridge.ENV_PROPERTIES.getProperty(HC_APPLICATION);
		req.getSession().setAttribute(HC_APPLICATION, parameterHC_APPLICATION);
		logger.debug("HC_APPLICATION:" + parameterHC_APPLICATION);
		String parameterHC_APPLICATION_URL = DirigibleBridge.ENV_PROPERTIES.getProperty(HC_APPLICATION_URL);
		req.getSession().setAttribute(HC_APPLICATION_URL, parameterHC_APPLICATION_URL);
		logger.debug("HC_APPLICATION_URL:" + parameterHC_APPLICATION_URL);
		String parameterHC_LOCAL_HTTP_PORT = DirigibleBridge.ENV_PROPERTIES.getProperty(HC_LOCAL_HTTP_PORT);
		req.getSession().setAttribute(HC_LOCAL_HTTP_PORT, parameterHC_LOCAL_HTTP_PORT);
		logger.debug("HC_LOCAL_HTTP_PORT:" + parameterHC_LOCAL_HTTP_PORT);
		
	}

	@Override
	public void injectOnStart(ServletConfig servletConfig)
			throws ServletException, IOException {
		// do nothing		
	}	
}
