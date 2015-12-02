/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.filter;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.security.SecurityManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.registry.Messages;
import org.eclipse.dirigible.runtime.registry.PathUtils;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;

public class RegistrySecureRolesFilter extends AbstractRegistrySecureFilter {

	private static final String YOU_DO_NOT_HAVE_REQUIRED_ROLE_S_TO_ACCESS_THIS_LOCATION = Messages
			.getString("RegistrySecureRolesFilter.YOU_DO_NOT_HAVE_REQUIRED_ROLE_S_TO_ACCESS_THIS_LOCATION"); //$NON-NLS-1$
	private static final Logger logger = Logger.getLogger(RegistrySecureRolesFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		String location = PathUtils.extractPath(request);
		if (isLocationSecured(location)) {
			if (!isUserInRole(req, location)) {
				((HttpServletResponse) res).sendError(HttpServletResponse.SC_FORBIDDEN, YOU_DO_NOT_HAVE_REQUIRED_ROLE_S_TO_ACCESS_THIS_LOCATION);
			}
		}
		chain.doFilter(req, res);

	}

	private boolean isUserInRole(ServletRequest req, String location) {
		try {
			if (req instanceof HttpServletRequest) {
				HttpServletRequest request = (HttpServletRequest) req;
				Principal principal = request.getUserPrincipal();
				if (principal != null) {
					SecurityManager securityManager = SecurityManager.getInstance(RepositoryFacade.getInstance().getRepository(request),
							DataSourceFacade.getInstance().getDataSource(request));
					List<String> roles = securityManager.getRolesForLocation(location);
					for (String role : roles) {
						if (request.isUserInRole(role)) {
							return true;
						}
					}
				}
			}
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	protected String getSecuredMapping() {
		return null;
	}

}
