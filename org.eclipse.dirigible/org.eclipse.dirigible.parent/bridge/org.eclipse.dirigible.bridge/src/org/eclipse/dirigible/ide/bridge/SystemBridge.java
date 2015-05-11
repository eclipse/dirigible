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

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemBridge extends HttpServlet {
	
	private static final String INITIAL_CONTEXT = "InitialContext"; //$NON-NLS-1$
	
	private static final long serialVersionUID = -8043662807856187626L;
	
	private static final Logger logger = LoggerFactory.getLogger(SystemBridge.class);
	
	public static Properties ENV_PROPERTIES = new Properties();
	
	@Override
	public void init() throws ServletException {
		
		ENV_PROPERTIES.putAll(System.getProperties());
		
		for (Object property : ENV_PROPERTIES.keySet()) {
			logger.info("SYSTEM_" + property + ": " + ENV_PROPERTIES.getProperty(property.toString()));
		}
		
		try {
			System.getProperties().put(INITIAL_CONTEXT, new InitialContext());
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
		}
		
		super.init();
	}	

}
