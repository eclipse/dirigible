package org.eclipse.dirigible.engine.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.runtime.core.filter.AbstractSecurityFilter;

@WebFilter(urlPatterns = "/services/v3/public/*", filterName = "WebEnginePublicSecurityFilter", description = "Filter all the public Web URIs")
public class WebEnginePublicSecurityFilter extends AbstractSecurityFilter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(request, response, chain);
	}

	@Override
	protected String getPrefix() {
		return "/public";
	}
}
