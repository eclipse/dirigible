package org.eclipse.dirigible.engine.js.rhino.service;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.engine.api.security.AbstractSecurityFilter;

@WebFilter(urlPatterns = "/services/v3/rhino/*", filterName = "RhinoJavascriptEngineSecurityFilter", description = "Filter all the Rhino Javascript URIs")
public class RhinoJavascriptEngineSecurityFilter extends AbstractSecurityFilter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(request, response, chain);
	}

	@Override
	protected String getPrefix() {
		return "/rhino";
	}
}
