package org.eclipse.dirigible.engine.js.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.runtime.core.filter.AbstractSecurityFilter;

@WebFilter(urlPatterns = "/services/v3/js/*", filterName = "JavascriptEngineSecurityFilter", description = "Check all the Javascript URIs for access permissions")
public class JavascriptEngineSecurityFilter extends AbstractSecurityFilter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		super.doFilter(request, response, chain);
	}

	@Override
	protected String getPrefix() {
		return "/js";
	}
}
