/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.healthcheck.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.components.base.healthcheck.status.HealthCheckStatus;
import org.springframework.stereotype.Component;

/**
 * The HTTP Context Filter.
 */
@Component
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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String path = getRequestPath(httpRequest);
		boolean isResources = isResourcesRequest(path);
		boolean isHealthCheck = isHealtCheckRequest(path);
		boolean isOps = isOpsRequest(path);
		boolean isWebJars = isWebJarsRequest(path);
		boolean isTheme = isThemeRequest(path);
		if (!isResources && !isHealthCheck && !isOps && !isWebJars && !isTheme) {
			HealthCheckStatus healthStatus = HealthCheckStatus.getInstance();
			if (healthStatus.getStatus()
							.equals(HealthCheckStatus.Status.Ready)) {
				chain.doFilter(request, response);
				return;
			}
			httpResponse.sendRedirect("/index-busy.html");
			return;
		}
		chain.doFilter(request, response);
	}

	private boolean isResourcesRequest(String path) {
		return path.startsWith("/web/resources") || path.startsWith("/js/resources");
	}

	private boolean isHealtCheckRequest(String path) {
		return path.startsWith("/core/healthcheck") || path.startsWith("/index-busy.html");
	}

	private boolean isOpsRequest(String path) {
		return path.startsWith("/ops");
	}

	private boolean isWebJarsRequest(String path) {
		return path.startsWith("/webjars");
	}

	private boolean isThemeRequest(String path) {
		return path.startsWith("/web/theme/") || path.startsWith("/js/theme/");
	}

	private String getRequestPath(HttpServletRequest httpRequest) {
		String path = httpRequest.getPathInfo();
		if (path == null) {
			path = httpRequest.getServletPath();
			path = path	.replace("/services", "")
						.replace("/public", "");
		}
		return path;
	}

	/**
	 * Destroy.
	 */
	@Override
	public void destroy() {
		// Not used
	}

}
