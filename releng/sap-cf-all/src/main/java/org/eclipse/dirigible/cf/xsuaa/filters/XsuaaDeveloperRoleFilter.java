/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cf.xsuaa.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.cf.utils.CloudFoundryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = {
		"/services/v3/ide/*",
        "/websockets/v3/ide/*",
        "/services/v4/ide/*",
        "/websockets/v4/ide/*"
,}, filterName = "XSUAA Developer Role Security Filter", description = "Check all URIs for the Developer role permission")
public class XsuaaDeveloperRoleFilter extends AbstractXsuaaFilter {

	private static final Logger logger = LoggerFactory.getLogger(XsuaaDeveloperRoleFilter.class);

	private static final String FORBIDDEN_MESSAGE = "The logged in user does not have any of the required roles for the requested URI";

	private static final String ROLE = "Developer";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!CloudFoundryUtils.isInRole(request, ROLE)) {
			forbidden(request, response, FORBIDDEN_MESSAGE);
			return;
		}

		chain.doFilter(request, response);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
