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

public interface IInjector {

	/**
	 * Inject one or more objects into the current request session
	 * 
	 * @param servletConfig
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException;
	
	/**
	 * Inject one or more object in System's Properties at application startup
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	public abstract void injectOnStart(ServletConfig servletConfig) throws ServletException, IOException;

}