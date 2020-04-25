/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils;
import org.eclipse.dirigible.cf.utils.JwtUtils;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = {
		"/services/v3/*",
		"/public/v3/*",
		"/websockets/v3/*",
		"/services/v4/*",
		"/public/v4/*",
		"/websockets/v4/*"
}, filterName = "XSUAA Security Filter", description = "Check all URIs for the permissions")
public class XsuaaFilter extends AbstractXsuaaFilter {

	private static final Logger logger = LoggerFactory.getLogger(XsuaaFilter.class);

	private static final String UNAUTHORIZED_MESSAGE = "No logged in user";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String jwt = JwtUtils.getJwt(request);
		if (!CloudFoundryUtils.isValidJwt(jwt)) {
			unauthorized(request, response, UNAUTHORIZED_MESSAGE);
			return;
		}

		try {
			ThreadContextFacade.setUp();
			ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), request);
			ThreadContextFacade.set(HttpServletResponse.class.getCanonicalName(), response);

			UserFacade.setName(JwtUtils.getClaim(jwt).getUserName());
		} catch (ContextException e) {
			logger.info("Error while setting userName from XSUAA Filter.", e);
		}
		chain.doFilter(request, response);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
