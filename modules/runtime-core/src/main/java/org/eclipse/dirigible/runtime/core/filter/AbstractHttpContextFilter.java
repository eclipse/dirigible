package org.eclipse.dirigible.runtime.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpContextFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AbstractHttpContextFilter.class);

	private static ISecurityCoreService securityCoreService = StaticInjector.getInjector().getInstance(SecurityCoreService.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		try {
			ThreadContextFacade.setUp();
			try {
				ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), request);
				ThreadContextFacade.set(HttpServletResponse.class.getCanonicalName(), response);

				chain.doFilter(request, response);

			} finally {
				ThreadContextFacade.tearDown();
			}
		} catch (ContextException e) {
			throw new ServletException(e);
		}

	}

	@Override
	public void destroy() {
	}

}
