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
package org.eclipse.dirigible.runtime.operations.service;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.runtime.operations.processor.SecurityProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Jobs.
 */
@Path("/ops/security")
@RolesAllowed({ "Operator" })
@Api(value = "Operations - Access", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class SecurityService extends AbstractRestService implements IRestService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

	/** The processor. */
	private SecurityProcessor processor = new SecurityProcessor();
	
	/** The response. */
	@Context
	private HttpServletResponse response;

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
		return SecurityService.class;
	}

	/**
	 * List all the access definitions currently registered.
	 *
	 * @return the response
	 * @throws AccessException the scheduler exception
	 */
	@GET
	@Path("/access")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAccess()
			throws AccessException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.listAccess()).build();
	}
	
	/**
	 * List all the roles definitions currently registered.
	 *
	 * @return the response
	 * @throws AccessException the scheduler exception
	 */
	@GET
	@Path("/roles")
	@Produces({ "application/json" })
	public Response listRoles()
			throws AccessException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.listRoles()).build();
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
