/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.ext.utils.RequestUtils;

/**
 * Operational Service exposes some utility functions.
 */
public class OperationalServlet extends HttpServlet {

	private static final String LOGGED_OUT = "logged out"; //$NON-NLS-1$

	private static final long serialVersionUID = -1668088260723897990L;

	private static final String PARAMETER_USER = "user"; //$NON-NLS-1$
	private static final String PARAMETER_LOGOUT = "logout"; //$NON-NLS-1$

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (req.getParameter(PARAMETER_USER) != null) {
			String userName = RequestUtils.getUser(req);
			resp.getWriter().write(userName);
			resp.getWriter().flush();
			resp.getWriter().close();
		} else if (req.getParameter(PARAMETER_LOGOUT) != null) {
			resp.setHeader("Cache-Control", "no-cache, no-store"); //$NON-NLS-1$ //$NON-NLS-2$
			resp.setHeader("Pragma", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
			req.getSession().invalidate();
			resp.getWriter().write(LOGGED_OUT);
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

}
