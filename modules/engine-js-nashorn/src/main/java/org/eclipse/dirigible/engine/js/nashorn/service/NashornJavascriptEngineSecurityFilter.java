package org.eclipse.dirigible.engine.js.nashorn.service;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.engine.api.security.AbstractSecurityFilter;

@WebFilter(urlPatterns = "/services/v3/nashorn/*", filterName = "NashornJavascriptEngineSecurityFilter", description = "Filter all the Nashorn Javascript URIs")
public class NashornJavascriptEngineSecurityFilter extends AbstractSecurityFilter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(request, response, chain);
	}

	@Override
	protected String getPrefix() {
		return "/nashorn";
	}
}
