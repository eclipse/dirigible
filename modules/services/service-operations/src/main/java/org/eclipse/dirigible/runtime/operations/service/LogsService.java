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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.DecoderException;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.runtime.operations.processor.LogsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Logs.
 */
@Path("/ops/logs")
@RolesAllowed({ "Operator" })
@Api(value = "Operations - Logs", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class LogsService extends AbstractRestService implements IRestService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(LogsService.class);

	/** The processor. */
	private LogsProcessor processor = new LogsProcessor();
	
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
		return LogsService.class;
	}

	/**
	 * List all the log files in the logs folder.
	 *
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws DecoderException
	 *             the decoder exception
	 * @throws IOException the I/O error
	 */
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listLogs()
			throws URISyntaxException, DecoderException, IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.list()).build();
	}
	
	/**
	 * Search.
	 *
	 * @param file
	 *            the file
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws DecoderException
	 *             the decoder exception
	 * @throws IOException the I/O error
	 */
	@GET
	@Path("{file}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response list(@PathParam("file") String file) throws URISyntaxException, DecoderException, IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.get(file)).build();
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
