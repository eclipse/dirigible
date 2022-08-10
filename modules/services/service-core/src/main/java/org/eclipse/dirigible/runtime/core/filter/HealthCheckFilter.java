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
package org.eclipse.dirigible.runtime.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.health.HealthStatus;

/**
 * The HTTP Context Filter.
 */
@WebFilter(urlPatterns = {"/services/v3/*", "/public/v3/*", "/services/v4/*", "/public/v4/*"}, filterName = "HealthCheckFilter", description = "Check the health status of the Dirigible instance")
public class HealthCheckFilter implements Filter {

	/**
	 * Inits the.
	 *
	 * @param filterConfig the filter config
	 * @throws ServletException the servlet exception
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Not used
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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		boolean isResources = httpRequest.getPathInfo().startsWith("/web/resources");
		boolean isHealthCheck = httpRequest.getPathInfo().startsWith("/healthcheck");
		boolean isOps = httpRequest.getPathInfo().startsWith("/ops");
		boolean isWebJars = httpRequest.getPathInfo().startsWith("/webjars");
		if (!isResources && !isHealthCheck && !isOps && !isWebJars) {
			HealthStatus healthStatus = HealthStatus.getInstance();
			if (healthStatus.getStatus().equals(HealthStatus.Status.Ready)) {
				chain.doFilter(request, response);
				return;
			}
			httpResponse.sendRedirect("/index-busy.html");
			return;
		}
		chain.doFilter(request, response);
	}

	/**
	 * Destroy.
	 */
	@Override
	public void destroy() {
		// Not used
	}

}
