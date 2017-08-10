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

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;

@WebFilter(urlPatterns = "/services/v3/*", filterName = "HttpContextFilter", description = "Set the HTTP Request and Response to the Context for all URIs")
public class HttpContextFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Not used
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
		// Not used
	}

}
