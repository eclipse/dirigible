/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.oauth.filters;

import static org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.oauth.utils.OAuthUtils;
import org.slf4j.Logger;

public abstract class AbstractOAuthFilter implements Filter {

	/**
	 * The name of the cookie, where is stored the initial request path
	 */
	public static final String INITIAL_REQUEST_PATH_COOKIE = "initialRequestPath";
	private static final String SLASH = "/";
	private static final boolean IS_OAUTH_AUTHENTICATION_ENABLED = Configuration.isOAuthAuthenticationEnabled();

	protected abstract Logger getLogger();

	public void init(FilterConfig filterConfig) throws ServletException {
		// Do nothing
	}

	public void destroy() {
		// Do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (IS_OAUTH_AUTHENTICATION_ENABLED) {
			filter(request, response, chain);
		}
		chain.doFilter(request, response);
		
	}

	/**
	 * Perform OAuth related filtering
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	protected abstract void filter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;

	/**
	 * Authenticate.
	 * @throws IOException 
	 * 
	 */
	protected void authenticate(ServletRequest request, ServletResponse response) throws IOException {
		String authenticationUrl = OAuthUtils.getAuthenticationUrl();
		setRequestPathCookie(request, response);
		((HttpServletResponse) response).sendRedirect(authenticationUrl);
	}

	protected void setRequestPathCookie(ServletRequest request, ServletResponse response) {
		boolean found = false;
		Cookie[] cookies = ((HttpServletRequest) request).getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(INITIAL_REQUEST_PATH_COOKIE) && cookie.getValue() != null && !cookie.getValue().equals("")) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			String contextPath = ((HttpServletRequest) request).getContextPath();
			String pathInfo = ((HttpServletRequest) request).getPathInfo();
			String requestPath = contextPath + "/services/v4" + pathInfo;
			Cookie cookie = new Cookie(INITIAL_REQUEST_PATH_COOKIE, requestPath);
			cookie.setPath("/");
			((HttpServletResponse) response).addCookie(cookie);
		}
	}

	protected void removeRequestPathCookie(ServletRequest request, ServletResponse response) {
		((HttpServletResponse) response).addCookie(new Cookie(INITIAL_REQUEST_PATH_COOKIE, null));
	}

	/**
	 * Unauthorized.
	 *
	 */
	@SuppressWarnings("deprecation")
	protected void unauthorized(ServletRequest request, ServletResponse response, String message) throws IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		String path = httpServletRequest.getPathInfo() != null ? httpServletRequest.getPathInfo() : SLASH;
		String error = String.format("Unauthorized access is forbidden: %s", path, message);

		getLogger().warn(error);

		error = escapeHtml4(error);
		error = escapeEcmaScript(error);
		httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, error);
	}

	/**
	 * Forbidden.
	 *
	 */
	@SuppressWarnings("deprecation")
	protected void forbidden(ServletRequest request, ServletResponse response, String message) throws IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		String path = httpServletRequest.getPathInfo() != null ? httpServletRequest.getPathInfo() : SLASH;
		String error = String.format("Requested URI [%s] is forbidden: %s", path, message);

		getLogger().warn(error);

		error = escapeHtml4(error);
		error = escapeEcmaScript(error);
		httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, error);
	}
}
