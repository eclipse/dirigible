/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.filter;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.runtime.registry.PathUtils;

public abstract class AbstractRegistrySecureFilter implements Filter {

	private static final String EMPTY = ""; //$NON-NLS-1$

	private static final String Q = "?"; //$NON-NLS-1$

	private static final String ACCEPT = "Accept"; //$NON-NLS-1$

	private static final String GET_METHOD = "GET"; //$NON-NLS-1$

	private static final String HTTP_METHOD_CANNOT_BE_REDIRECTED_AUTOMATICALLY = "AbstractRegistrySecureFilter.2"; //$NON-NLS-1$

	private static final String JSON = "json"; //$NON-NLS-1$

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		// SAML standard redirect
		String location = PathUtils.extractPath(request);
		String queryString = request.getQueryString();
		if (isLocationSecured(location) != null) {
			// SAML do not redirect in case of explicitly requested
			// application/json without logged-in user
			String acceptHeader = request.getHeader(ACCEPT);
			if ((acceptHeader != null) && acceptHeader.contains(JSON)) {
				if (request.getUserPrincipal() == null) {
					((HttpServletResponse) res).sendError(HttpURLConnection.HTTP_FORBIDDEN,
							String.format(Messages.getString(HTTP_METHOD_CANNOT_BE_REDIRECTED_AUTOMATICALLY), request.getMethod(),
									req.getServletContext().getContextPath() + getSecuredMapping()));
					return;
				}
			}
			if (GET_METHOD.equalsIgnoreCase(request.getMethod())) {
				((HttpServletResponse) res).sendRedirect(
						req.getServletContext().getContextPath() + getSecuredMapping() + location + (queryString != null ? Q + queryString : EMPTY));
			} else {
				((HttpServletResponse) res).sendError(HttpURLConnection.HTTP_FORBIDDEN,
						String.format(Messages.getString(HTTP_METHOD_CANNOT_BE_REDIRECTED_AUTOMATICALLY), request.getMethod(),
								req.getServletContext().getContextPath() + getSecuredMapping()));
			}
			return;
		}
		chain.doFilter(req, res);
	}

	protected abstract String getSecuredMapping();

	protected String isLocationSecured(String location) throws ServletException {
		return SecuredLocationVerifier.isLocationSecured(location);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		//
	}

	@Override
	public void destroy() {
		//
	}

}
