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

import org.eclipse.equinox.servletbridge.BridgeServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhanced Bridge Servlet which retrieves specific parameters and objects 
 * from the target environment and provide them to RAP OSGi environment thru the Session object  
 *
 */
public class DirigibleBridge extends BridgeServlet {
	
	private static final long serialVersionUID = -8043662807856187626L;
	
	private static final Logger logger = LoggerFactory.getLogger(DirigibleBridge.class);
	
	static Class<Injector>[] injectorClasses;
	static {
		injectorClasses = new Class[]{
			InitParametersInjector.class,
			
			InitialContextInjector.class,
			DatabaseInjector.class,
			ProxyParametersInjector.class,
			LocalParametersInjector.class,
			MailInjector.class,
			RuntimeBridgeInjector.class,
			ConnectivityInjector.class
		};
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletConfig servletConfig = getServletConfig();
		
		for (Class injectorClass : injectorClasses) {
			try {
				Injector injector = (Injector) injectorClass.newInstance();
				injector.inject(servletConfig, req, resp);
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		super.service(req, resp);
	}
	

}
