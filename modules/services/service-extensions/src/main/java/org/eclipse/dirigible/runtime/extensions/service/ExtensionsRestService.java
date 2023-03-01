/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.extensions.service;

import static java.text.MessageFormat.format;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.runtime.extensions.processor.ExtensionPoint;
import org.eclipse.dirigible.runtime.extensions.processor.ExtensionsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the raw repository content.
 */
@Path("/core/extensions")
@Api(value = "Core - Extensions", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found") })
public class ExtensionsRestService extends AbstractRestService implements IRestService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ExtensionsRestService.class);

	/** The processor. */
	private ExtensionsProcessor processor = new ExtensionsProcessor();

	/** The response. */
	@Context
	private HttpServletResponse response;

	/**
	 * List extension points.
	 *
	 * @return the response
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("List all the Extension Points with their Extensions")
	@ApiResponses({ @ApiResponse(code = 200, message = "List of Extension Points", response = ExtensionPoint.class, responseContainer = "List") })
	public Response listExtensionPoints() throws ExtensionsException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.renderExtensionPoints()).build();
	}

	/**
	 * Gets the extension point.
	 *
	 * @param name
	 *            the name
	 * @return the extension point
	 * @throws ExtensionsException
	 *             the extensions exception
	 */
	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Returns the Extension Point with their Extensions requested by its name")
	@ApiResponses({ @ApiResponse(code = 200, message = "The Extension Point", response = ExtensionPoint.class),
			@ApiResponse(code = 404, message = "Extension Point with the requested name does not exist") })
	public Response getExtensionPoint(@ApiParam(value = "Name of the ExtensionPoint", required = true) @PathParam("name") String name)
			throws ExtensionsException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		String json = processor.renderExtensionPoint(name);
		if (json == null) {
			String error = format("ExtensionPoint with name [{0}] does not exist", name);
			return createErrorResponseNotFound(error);
		}
		return Response.ok().entity(json).build();
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
		return ExtensionsRestService.class;
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
