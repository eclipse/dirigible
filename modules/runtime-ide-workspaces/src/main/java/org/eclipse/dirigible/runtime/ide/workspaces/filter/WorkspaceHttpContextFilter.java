package org.eclipse.dirigible.runtime.ide.workspaces.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.dirigible.runtime.core.filter.AbstractHttpContextFilter;

@WebFilter(urlPatterns = "/services/v3/ide/workspaces/*", filterName = "WorkspaceHttpContextFilter", description = "Set the HTTP Request and Response to the Context for all the Workspace URIs")
public class WorkspaceHttpContextFilter extends AbstractHttpContextFilter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(request, response, chain);
	}

}
