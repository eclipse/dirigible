/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.api.v3.utils.EscapeFacade;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.core.security.verifier.AccessVerifier;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Security Filter.
 */
@WebFilter(urlPatterns = { "/services/v3/js/*", "/services/v3/rhino/*", "/services/v3/nashorn/*", "/services/v3/v8/*", "/services/v3/public/*",
		"/services/v3/web/*", "/services/v3/wiki/*", "/services/v3/command/*" }, filterName = "SecurityFilter", description = "Check all the URIs for access permissions")
public class SecurityFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	private static ISecurityCoreService securityCoreService = StaticInjector.getInjector().getInstance(SecurityCoreService.class);

	private static final Set<String> SECURED_PREFIXES = new HashSet<String>();

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SECURED_PREFIXES.add("/js");
		SECURED_PREFIXES.add("/rhino");
		SECURED_PREFIXES.add("/nashorn");
		SECURED_PREFIXES.add("/v8");
		SECURED_PREFIXES.add("/public");
		SECURED_PREFIXES.add("/web");
		SECURED_PREFIXES.add("/wiki");
		SECURED_PREFIXES.add("/command");
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
	 * javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		try {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;

			String path = httpServletRequest.getPathInfo() != null ? httpServletRequest.getPathInfo() : IRepositoryStructure.SEPARATOR;
			for (String prefix : SECURED_PREFIXES) {
				if (path.startsWith(prefix)) {
					path = path.substring(prefix.length());
					break;
				}
			}
			String method = httpServletRequest.getMethod();

			List<AccessDefinition> accessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, ISecurityCoreService.CONSTRAINT_SCOPE_HTTP, path, method);
			if (!accessDefinitions.isEmpty()) {
				Principal principal = httpServletRequest.getUserPrincipal();
				if (principal == null) {
					forbidden(path, "No logged in user", httpServletResponse);
					return;
				}
				boolean isInRole = false;
				for (AccessDefinition accessDefinition : accessDefinitions) {
					if (httpServletRequest.isUserInRole(accessDefinition.getRole())) {
						isInRole = true;
						break;
					}
				}
				if (!isInRole) {
					forbidden(path, "The logged in user does not have any of the required roles for the requested URI", httpServletResponse);
					return;
				}
			}
		} catch (IllegalArgumentException e) {
			throw new ServletException(e);
		} catch (AccessException e) {
			throw new ServletException(e);
		}

		chain.doFilter(request, response);
	}

	/**
	 * Forbidden.
	 *
	 * @param uri
	 *            the uri
	 * @param message
	 *            the message
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void forbidden(String uri, String message, HttpServletResponse response) throws IOException {
		String error = String.format("Requested URI [%s] is forbidden: %s", uri, message);
		logger.warn(error);
		error = EscapeFacade.escapeHtml4(error);
		error = EscapeFacade.escapeJavascript(error);
		response.sendError(HttpServletResponse.SC_FORBIDDEN, error);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// Not Used
	}

}
