package org.eclipse.dirigible.services.spring.wrappers;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class WrapRequestFilter extends OncePerRequestFilter {

	private static final String[] PATHS = new String[] { "/services/v4", "/public/v4" };

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		ServletContextFacadeRequestWrapper wrapper = new ServletContextFacadeRequestWrapper(request);
		String path = getMatchingContextPathForRequest(request);
		if (path != null) {
			wrapper.setContextPath(request.getContextPath() + path);
			String newPath = request.getServletPath().substring(path.length());
			if (newPath.length() == 0) {
				newPath = "/";
			}
			wrapper.setServletPath(newPath);
		}
		filterChain.doFilter(wrapper, response);
	}

	public String getMatchingContextPathForRequest(HttpServletRequest request) {
		for (String path : PATHS) {
			if (request.getServletPath().startsWith(path)) {
				return path;
			}
		}
		return null;
	}

}
