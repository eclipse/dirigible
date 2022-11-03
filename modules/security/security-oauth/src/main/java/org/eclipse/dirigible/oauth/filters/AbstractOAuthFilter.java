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

/**
 * The Class AbstractOAuthFilter.
 */
public abstract class AbstractOAuthFilter implements Filter {

	public static final String INITIAL_REQUEST_PATH_COOKIE = "initialRequestPath";

	/** The Constant SLASH. */
	private static final String SLASH = "/";

	/** The Constant IS_OAUTH_AUTHENTICATION_ENABLED. */
	private static final boolean IS_OAUTH_AUTHENTICATION_ENABLED = Configuration.isOAuthAuthenticationEnabled();

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	protected abstract Logger getLogger();

	/**
	 * Inits the.
	 *
	 * @param filterConfig the filter config
	 * @throws ServletException the servlet exception
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		// Do nothing
	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		// Do nothing
	}

	/**
	 * Do filter.
	 *
	 * @param request the request
	 * @param response the response
	 * @param chain the chain
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (IS_OAUTH_AUTHENTICATION_ENABLED) {
			filter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * Perform OAuth related filtering.
	 *
	 * @param request the request
	 * @param response the response
	 * @param chain the chain
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	protected abstract void filter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;

	/**
	 * Authenticate.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void authenticate(ServletRequest request, ServletResponse response) throws IOException {
		String authenticationUrl = OAuthUtils.getAuthenticationUrl();
		setRequestPathCookie(request, response);
		((HttpServletResponse) response).sendRedirect(authenticationUrl);
	}

	/**
	 * Sets the request path cookie.
	 *
	 * @param request the request
	 * @param response the response
	 */
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

	/**
	 * Unauthorized.
	 *
	 * @param request the request
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
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
	 * @param request the request
	 * @param response the response
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
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
