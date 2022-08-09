/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.theme.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.utils.EscapeFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Theme related content.
 */
@Path("/core/theme")
@Api(value = "Core - Theme", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class ThemeRestService extends AbstractRestService implements IRestService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ThemeRestService.class);

	/** The Constant NAME_PARAM. */
	private static final String NAME_PARAM = "name"; //$NON-NLS-1$

	/** The Constant DEFAULT_THEME. */
	private static final String DEFAULT_THEME = "fiori"; //$NON-NLS-1$

	/** The Constant INIT_PARAM_DEFAULT_THEME. */
	private static final String INIT_PARAM_DEFAULT_THEME = "DIRIGIBLE_THEME_DEFAULT"; //$NON-NLS-1$

	/** The Constant COOKIE_THEME. */
	private static final String COOKIE_THEME = "dirigible-theme"; //$NON-NLS-1$

	/** The repository. */
	private IRepository repository = null;
	
	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}

	/** The response. */
	@Context
	private HttpServletResponse response;

	/** The Constant THEMES_PATH. */
	private static final String THEMES_PATH = "/theme-";

	/**
	 * Gets the theme.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the theme
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/")
	public Response getTheme(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
		String cookieValue = getCurrentTheme(request, response);
		return Response.ok().entity(cookieValue).type(ContentTypeHelper.TEXT_PLAIN).build();
	}

	/**
	 * Gets the current theme.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the current theme
	 */
	private String getCurrentTheme(HttpServletRequest request, HttpServletResponse response) {
		String env = Configuration.get(INIT_PARAM_DEFAULT_THEME);
		String cookieValue = (env == null) ? DEFAULT_THEME : env;
		String themеName = request.getParameter(NAME_PARAM);
		themеName = EscapeFacade.escapeHtml4(themеName);
		themеName = EscapeFacade.escapeJavascript(themеName);
		if ((null != themеName) && !themеName.isEmpty()) {
			themеName = themеName.trim();
			// if there is valid theme name, then force the setting of the cookie
			setCookieUser(response, themеName);
			cookieValue = themеName;
		} else {
			// parameter not present, so look up in the cookies
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				String cookieName = COOKIE_THEME;
				for (Cookie cookie : cookies) {
					if (cookieName.equals(cookie.getName())) {
						cookieValue = cookie.getValue();
						// cookie exists, hence use it
						break;
					}
				}
			}
		}
		cookieValue = EscapeFacade.escapeHtml4(cookieValue);
		cookieValue = EscapeFacade.escapeJavascript(cookieValue);
		return cookieValue.trim();
	}

	/**
	 * Sets the cookie user.
	 *
	 * @param resp
	 *            the resp
	 * @param themeName
	 *            the theme name
	 */
	private void setCookieUser(HttpServletResponse resp, String themeName) {
		Cookie cookie = new Cookie(COOKIE_THEME, themeName);
		cookie.setMaxAge(30 * 24 * 60 * 60);
		cookie.setPath("/");
		resp.addCookie(cookie);
	}

	/**
	 * Gets the style.
	 *
	 * @param path
	 *            the path
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the style
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/{path:.*}")
	public Response getStyle(@PathParam("path") String path, @Context HttpServletRequest request, @Context HttpServletResponse response)
			throws IOException {

		String cookieValue = getCurrentTheme(request, response);

		if ((path == null) || "".equals(path)) {
			return Response.ok().entity(cookieValue.trim()).type(ContentTypeHelper.TEXT_PLAIN).build();
		}

		String repositoryPath = IRepositoryStructure.PATH_REGISTRY_PUBLIC + THEMES_PATH + cookieValue + IRepository.SEPARATOR + path;
		final IResource resource = getRepository().getResource(repositoryPath);
		if (resource.exists()) {
			return Response.ok().entity(resource.getContent()).type(ContentTypeHelper.TEXT_CSS).build();
		}

		// try from the classloader
		try {
			InputStream bundled = ThemeRestService.class.getResourceAsStream("/META-INF/dirigible" + THEMES_PATH + cookieValue + IRepository.SEPARATOR + path);
			if (bundled != null) {
				try {
					return Response.ok().entity(IOUtils.toByteArray(bundled)).type(ContentTypeHelper.TEXT_CSS)
							.build();
				} finally {
					if (bundled != null) {
						bundled.close();
					}
				}
			}
		} catch (IOException e) {
			throw new RepositoryException(e);
		}

		final String message = String.format("There is no resource at the specified path: %s", repositoryPath);
		logger.error(message);
		return createErrorResponseNotFound(message);
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return ThemeRestService.class;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}
}
