/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.logging.Logger;

public class SandboxFilter implements Filter {

	private static final Logger logger = Logger.getLogger(SandboxFilter.class);

	public static final String SANDBOX_CONTEXT = "sandbox"; //$NON-NLS-1$
	public static final String DEBUG_CONTEXT = "debug"; //$NON-NLS-1$

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		logger.trace("SandboxFilter doFilter req.getServletPath(): " + req.getServletPath());

		if (req.getServletPath() != null && (req.getServletPath().contains(SANDBOX_CONTEXT))) {
			req.setAttribute(SANDBOX_CONTEXT, true);
			logger.trace("setAttribute(SANDBOX_CONTEXT, true)");
		}
		if (req.getServletPath() != null && req.getServletPath().contains(DEBUG_CONTEXT)) {
			req.setAttribute(DEBUG_CONTEXT, true);
			logger.trace("setAttribute(DEBUG_CONTEXT, true)");
		}
		
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.setHeader("cache-control", "private, max-age=0, no-cache");
		
		chain.doFilter(request, response);
	}

}
