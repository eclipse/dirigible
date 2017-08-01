package org.eclipse.dirigible.runtime.core.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSecurityFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSecurityFilter.class);

	private static ISecurityCoreService securityCoreService = StaticInjector.getInjector().getInstance(SecurityCoreService.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		try {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;

			String uri = httpServletRequest.getPathInfo() != null ? httpServletRequest.getPathInfo() : IRepositoryStructure.SEPARATOR;
			String prefix = getPrefix();
			if (uri.startsWith(prefix)) {
				uri = uri.substring(prefix.length());
			}
			String method = httpServletRequest.getMethod();

			List<AccessDefinition> accessDefinitions = AccessVerifier.getMatchingAccessDefinitions(securityCoreService, uri, method);
			if (!accessDefinitions.isEmpty()) {
				Principal principal = httpServletRequest.getUserPrincipal();
				if (principal == null) {
					forbidden(uri, "No logged in user", httpServletResponse);
					return;
				}
				boolean isInRole = false;
				for (AccessDefinition accessDefinition : accessDefinitions) {
					if (httpServletRequest.isUserInRole(accessDefinition.getRole())) {
						isInRole = true;
						break;
					}
				}
				if (!isInRole) {
					forbidden(uri, "The loogged in user does not have any of the required roles for the requested URI", httpServletResponse);
					return;
				}
			}
		} catch (IllegalArgumentException e) {
			throw new ServletException(e);
		} catch (AccessException e) {
			throw new ServletException(e);
		}

		chain.doFilter(request, response);
	}

	private void forbidden(String uri, String message, HttpServletResponse response) throws IOException {
		String error = String.format("Requested URI [%s] is forbidden: %s", uri, message);
		logger.warn(error);
		response.sendError(HttpServletResponse.SC_FORBIDDEN, error);
	}

	protected abstract String getPrefix();

	@Override
	public void destroy() {
	}

}
