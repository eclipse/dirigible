/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.air.init;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for MasterRepositorySynchronizerServlet
 */
@WebServlet(name = "MasterRepositorySynchronizerServlet", urlPatterns = "/services/master-sync", loadOnStartup = 4)
public class MasterRepositorySynchronizerServlet extends org.eclipse.dirigible.runtime.content.MasterRepositorySynchronizerServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		registerInitRegister();
	}

}
