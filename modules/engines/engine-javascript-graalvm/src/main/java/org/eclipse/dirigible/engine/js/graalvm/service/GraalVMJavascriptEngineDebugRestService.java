/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.graalvm.service;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.engine.js.graalvm.debugger.GraalVMJavascriptDebugProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the GraalVM based Javascript backend services.
 */
@Singleton
@Path("/ide/debug/graalvm")
@Api(value = "JavaScript Engine Debugger - GraalVM", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class GraalVMJavascriptEngineDebugRestService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(GraalVMJavascriptEngineDebugRestService.class);

	@Context
	private HttpServletResponse response;

	/**
	 * Enable debugging.
	 *
	 * @return result of the execution of the service
	 */
	@GET
	@Path("/enable")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Enable debugging")
	@ApiResponses({ @ApiResponse(code = 200, message = "Execution Result") })
	public Response enable() {
		String user = UserFacade.getName();
		if (user == null) {
			sendErrorForbidden(response, NO_LOGGED_IN_USER);
			return Response.status(Status.FORBIDDEN).build();
		}
		try {
			GraalVMJavascriptDebugProcessor.addUserSession(user);
			return Response.ok().build();
		} catch (Throwable e) {
			String message = e.getMessage();
			logger.error(message, e);
			sendErrorInternalServerError(response, message);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
		}
	}
	
	/**
	 * Disable debugging.
	 *
	 * @return result of the execution of the service
	 */
	@GET
	@Path("/disable")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Disable debugging")
	@ApiResponses({ @ApiResponse(code = 200, message = "Execution Result") })
	public Response disable() {
		String user = UserFacade.getName();
		if (user == null) {
			sendErrorForbidden(response, NO_LOGGED_IN_USER);
			return Response.status(Status.FORBIDDEN).build();
		}
		try {
			GraalVMJavascriptDebugProcessor.clear();
			return Response.ok().build();
		} catch (Throwable e) {
			String message = e.getMessage();
			logger.error(message, e);
			sendErrorInternalServerError(response, message);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return GraalVMJavascriptEngineDebugRestService.class;
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
