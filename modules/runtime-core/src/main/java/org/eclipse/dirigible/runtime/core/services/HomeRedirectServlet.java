/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.core.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

@WebServlet(name = "HomeRedirectServlet", urlPatterns = { "/home" })
public class HomeRedirectServlet extends HttpServlet {

	private static final String DIRIGIBLE_HOME_URL = "DIRIGIBLE_HOME_URL";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if ((pathInfo == null) || "".equals(pathInfo) || IRepositoryStructure.SEPARATOR.equals(pathInfo)) {
			String homeUrl = Configuration.get(DIRIGIBLE_HOME_URL);
			response.sendRedirect(homeUrl);
		}
	}

}
