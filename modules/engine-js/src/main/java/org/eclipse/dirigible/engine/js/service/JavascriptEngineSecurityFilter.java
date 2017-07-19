package org.eclipse.dirigible.engine.js.service;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.engine.api.security.AbstractSecurityFilter;

@WebFilter(urlPatterns = "/services/v3/js/*", filterName = "JavascriptEngineSecurityFilter", description = "Filter all the Javascript URIs")
public class JavascriptEngineSecurityFilter extends AbstractSecurityFilter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(request, response, chain);
	}

	@Override
	protected String getPrefix() {
		return "/js";
	}
}
