package org.eclipse.dirigible.engine.js.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.runtime.core.filter.AbstractHttpContextFilter;

@WebFilter(urlPatterns = "/services/v3/js/*", filterName = "JavascriptEngineHttpContextFilter", description = "Set the HTTP Request and Response to the Context for all the Javascript URIs")
public class JavascriptEngineHttpContextFilter extends AbstractHttpContextFilter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		super.doFilter(request, response, chain);
	}

}
