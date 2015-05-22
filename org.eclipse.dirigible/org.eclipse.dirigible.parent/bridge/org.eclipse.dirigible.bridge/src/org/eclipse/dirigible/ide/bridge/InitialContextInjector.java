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

public class InitialContextInjector implements IInjector {
	
	private static final Logger logger = LoggerFactory.getLogger(InitialContextInjector.class);
	
	public static final String INITIAL_CONTEXT = "InitialContext"; //$NON-NLS-1$
	
	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		InitialContext initialContext = (InitialContext) req.getSession().getAttribute(INITIAL_CONTEXT);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
				req.getSession().setAttribute(INITIAL_CONTEXT, initialContext);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public void injectOnStart(ServletConfig servletConfig) {
		InitialContext initialContext = (InitialContext) System.getProperties().get(INITIAL_CONTEXT);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
				System.getProperties().put(INITIAL_CONTEXT, initialContext);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
}
