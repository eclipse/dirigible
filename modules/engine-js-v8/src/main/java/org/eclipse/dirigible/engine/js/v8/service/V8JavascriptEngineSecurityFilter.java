package org.eclipse.dirigible.engine.js.v8.service;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.engine.api.security.AbstractSecurityFilter;

@WebFilter(urlPatterns = "/services/v3/v8/*", filterName = "V8JavascriptEngineSecurityFilter", description = "Filter all the V8 Javascript URIs")
public class V8JavascriptEngineSecurityFilter extends AbstractSecurityFilter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(request, response, chain);
	}

	@Override
	protected String getPrefix() {
		return "/v8";
	}
}
