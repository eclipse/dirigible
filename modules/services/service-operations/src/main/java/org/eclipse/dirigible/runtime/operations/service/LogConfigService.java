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
package org.eclipse.dirigible.runtime.operations.service;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
 * Front facing REST service serving the Log Configurations.
 */
@Path("/ops/logconfig")
@RolesAllowed({ "Operator" })
@Api(value = "Operations - Log Configuration", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class LogConfigService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(LogConfigService.class);

	private LogsProcessor processor = new LogsProcessor();
	
	@Context
	private HttpServletResponse response;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return LogConfigService.class;
	}
	
	/**
	 * List all loggers with their severity level.
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
	public Response listLoggers() throws URISyntaxException, DecoderException, IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		return Response.ok().entity(processor.listLoggers()).build();
	}

	/**
	 * Get severity.
	 *
	 * @param loggerName
	 *            the loggerName
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws DecoderException
	 *             the decoder exception
	 * @throws IOException the I/O error
	 */
	@GET
	@Path("severity/{loggerName}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getSeverity(@PathParam("loggerName") String loggerName) throws URISyntaxException, DecoderException, IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		if (loggerName == null) {
			loggerName = "ROOT";
		}

		return Response.ok().entity(processor.getSeverity(loggerName)).build();
	}
	
	/**
	 * Set severity.
	 *
	 * @param loggerName
	 *            the loggerName
	 * @param logLevel
	 *            the logLevel
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws DecoderException
	 *             the decoder exception
	 * @throws IOException the I/O error
	 */
	@POST
	@Path("severity/{loggerName}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response setSeverity(@PathParam("loggerName") String loggerName, String logLevel) throws URISyntaxException, DecoderException, IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		if (loggerName == null) {
			loggerName = "ROOT";
		}

		return Response.ok().entity(processor.setSeverity(loggerName, logLevel)).build();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
