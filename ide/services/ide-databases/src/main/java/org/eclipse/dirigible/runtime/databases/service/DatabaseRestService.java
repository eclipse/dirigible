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
package org.eclipse.dirigible.runtime.databases.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.database.databases.api.DatabasesException;
import org.eclipse.dirigible.database.databases.definition.DatabaseDefinition;
import org.eclipse.dirigible.runtime.databases.processor.DatabaseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the defined database management.
 */
@Path("/ide/database")
@Api(value = "IDE - Database", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class DatabaseRestService extends AbstractRestService implements IRestService {
	
	private static final Logger logger = LoggerFactory.getLogger(DatabaseRestService.class);

	private DatabaseProcessor processor = new DatabaseProcessor();

	@Context
	private HttpServletResponse response;
	
	/**
	 * List defined database.
	 *
	 * @return the response
	 * @throws DatabasesException 
	 */
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("List all the defined databases")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of the defined databases", response = String.class, responseContainer = "List") })
	public Response listDefinedDatabases() throws DatabasesException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		List<DatabaseDefinition> databases = processor.getDefinedDatabases();
		return Response.ok().entity(databases).build();
	}
	
	/**
	 * Get a defined database.
	 *
	 * @return the response
	 * @throws DatabasesException 
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Get a defined database by {id}")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get a defined database by {id}", response = DatabaseDefinition.class) })
	public Response getDefinedDatabases(@ApiParam(value = "Database definition id", required = true) @PathParam("id") long id) throws DatabasesException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		DatabaseDefinition database = processor.getDefinedDatabase(id);
		return Response.ok().entity(database).build();
	}
	
	/**
	 * Create a defined database.
	 *
	 * @return the response
	 * @throws DatabasesException 
	 */
	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Create a defined database")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Create a defined database", response = DatabaseDefinition.class) })
	public Response createDefinedDatabases(@ApiParam(value = "Database definition", required = true) DatabaseDefinition definition) throws DatabasesException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		if (definition == null) {
			return createErrorResponseBadRequest("Database definition not provided");
		}

		DatabaseDefinition database = processor.createDefinedDatabase(definition);
		return Response.ok().entity(database).build();
	}
	
	/**
	 * Delete a defined database.
	 *
	 * @param id the id
	 * @return the response
	 * @throws DatabasesException 
	 */
	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Delete a defined database by {id}")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Delete a defined database by {id}") })
	public Response removeDefinedDatabases(@ApiParam(value = "Database definition id", required = true) @PathParam("id") long id) throws DatabasesException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		processor.removeDefinedDatabase(id);
		return Response.ok().build();
	}
	
	/**
	 * Update a defined database.
	 * 
	 * @param id the id
	 * @param definition the definition
	 * @return the response
	 * @throws DatabasesException 
	 */
	@PUT
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Create a defined database")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Create a defined database") })
	public Response updateDefinedDatabases(@ApiParam(value = "Database definition id", required = true) @PathParam("id") long id, 
			@ApiParam(value = "Database definition", required = true) DatabaseDefinition definition) throws DatabasesException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		if (definition == null) {
			return createErrorResponseBadRequest("Database definition not provided");
		}

		if (definition.getId() != id) {
			logger.warn("The id provided in the body of the database definition update operation differs from the one requested via the URI");
			definition.setId(id);
		}
		processor.updateDefinedDatabase(definition);
		return Response.ok().build();
	}
	

	@Override
	public Class<? extends IRestService> getType() {
		return DatabaseRestService.class;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
