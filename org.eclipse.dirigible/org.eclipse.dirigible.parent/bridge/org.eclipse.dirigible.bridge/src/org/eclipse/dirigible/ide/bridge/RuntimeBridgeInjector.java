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

import java.beans.PropertyChangeSupport;
import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeBridgeInjector implements IInjector {
	
	public static final String DIRIGIBLE_RUNTIME_BRIDGE = "dirigible.runtime.bridge"; //$NON-NLS-1$
	
	private static final Logger logger = LoggerFactory.getLogger(RuntimeBridgeInjector.class);
	
	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		PropertyChangeSupport sessionRuntimeBridge = (PropertyChangeSupport) req.getSession().getAttribute(DIRIGIBLE_RUNTIME_BRIDGE);
		if (sessionRuntimeBridge == null) {
			PropertyChangeSupport runtimeBridge = lookupRuntimeBridge();
			try {
				req.getSession().setAttribute(DIRIGIBLE_RUNTIME_BRIDGE, runtimeBridge);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public void injectOnStart(ServletConfig servletConfig)
			throws ServletException, IOException {
		// do nothing	
	}
	
	/**
	 * Retrieve the RuntimeBridge(PropertyChangeSupport) from the target server environment
	 * 
	 * @return
	 * @throws NamingException
	 */
	private PropertyChangeSupport lookupRuntimeBridge() {
		return (PropertyChangeSupport) System.getProperties().get(DIRIGIBLE_RUNTIME_BRIDGE);
	}

}
