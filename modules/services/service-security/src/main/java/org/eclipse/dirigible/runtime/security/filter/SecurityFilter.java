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

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.api.v3.utils.EscapeFacade;
import org.eclipse.dirigible.commons.config.Configuration;
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
@WebFilter(urlPatterns = {
		
		"/services/v3/js/*",
		"/services/v3/rhino/*",
		"/services/v3/nashorn/*",
		"/services/v3/v8/*",
		"/services/v3/public/*",
		"/services/v3/web/*",
		"/services/v3/wiki/*",
		"/services/v3/command/*",
		
		"/public/v3/js/*",
		"/public/v3/rhino/*",
		"/public/v3/nashorn/*",
		"/public/v3/v8/*",
		"/public/v3/public/*",
		"/public/v3/web/*",
		"/public/v3/wiki/*",
		"/public/v3/command/*",
		
		"/services/v4/js/*",
		"/services/v4/rhino/*",
		"/services/v4/nashorn/*",
		"/services/v4/v8/*",
		"/services/v4/public/*",
		"/services/v4/web/*",
		"/services/v4/wiki/*",
		"/services/v4/command/*",
		
		"/public/v4/js/*",
		"/public/v4/rhino/*",
		"/public/v4/nashorn/*",
		"/public/v4/v8/*",
		"/public/v4/public/*",
		"/public/v4/web/*",
		"/public/v4/wiki/*",
		"/public/v4/command/*",
		
		"/odata/v2/*"
		
	}, filterName = "SecurityFilter", description = "Check all the URIs for access permissions")
public class SecurityFilter implements Filter {

	/** The Constant PATH_WEB_RESOURCES. */
	private static final String PATH_WEB_RESOURCES = "/web/resources";


	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	/** The security core service. */
	private static ISecurityCoreService securityCoreService = new SecurityCoreService();

	/** The Constant SECURED_PREFIXES. */
	private static final Set<String> SECURED_PREFIXES = new HashSet<String>();

	/**
	 * Inits the.
	 *
	 * @param filterConfig the filter config
	 * @throws ServletException the servlet exception
	 */
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

	/**
	 * Do filter.
	 *
	 * @param request the request
	 * @param response the response
	 * @param chain the chain
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
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
			if (!path.startsWith(PATH_WEB_RESOURCES)) {
				for (String prefix : SECURED_PREFIXES) {
					if (path.startsWith(prefix)) {
	 					path = path.substring(prefix.length());
						break;
					}
				}
				String method = httpServletRequest.getMethod();
	
				boolean isInRole = false;
				Principal principal = httpServletRequest.getUserPrincipal();
				
				List<AccessDefinition> accessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, ISecurityCoreService.CONSTRAINT_SCOPE_HTTP, path, method);
				if (!accessDefinitions.isEmpty()) {
					
					if (principal == null && !Configuration.isJwtModeEnabled()) {
						// white list check
						for (AccessDefinition accessDefinition : accessDefinitions) {
							if (ISecurityCoreService.ROLE_PUBLIC.equalsIgnoreCase(accessDefinition.getRole())) {
								isInRole = true;
								break;
							}
						}
						
						if (!isInRole) {
							forbidden(path, "No logged in user", httpServletResponse);
							return;
						}
					} else {
						for (AccessDefinition accessDefinition : accessDefinitions) {
							if (ISecurityCoreService.ROLE_PUBLIC.equalsIgnoreCase(accessDefinition.getRole()) || HttpRequestFacade.isUserInRole(accessDefinition.getRole())) {
								isInRole = true;
								break;
							}
						}
						if (!isInRole) {
							forbidden(path, "The logged in user does not have any of the required roles for the requested URI", httpServletResponse);
							return;
						}
					}
				} else {
					if (!Configuration.isAnonymousModeEnabled() && principal == null && !Configuration.isJwtModeEnabled()) {
						forbidden(path, "No logged in user and no white list constraints", httpServletResponse);
						return;
					}
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
		if (logger.isWarnEnabled()) {logger.warn(error);}
		error = EscapeFacade.escapeHtml4(error);
		error = EscapeFacade.escapeJavascript(error);
		response.sendError(HttpServletResponse.SC_FORBIDDEN, error);
	}

	/**
	 * Destroy.
	 */
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// Not Used
	}

}
