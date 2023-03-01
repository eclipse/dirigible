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
package org.eclipse.dirigible.runtime.databases.service;

import static java.text.MessageFormat.format;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
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
 * Front facing REST service serving the raw data.
 */
@Path("/ide/metadata/export")
@Api(value = "IDE - Data - Metadata", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class DatabaseMetadataRestService extends AbstractRestService implements IRestService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseMetadataRestService.class);

	/** The processor. */
	private DatabaseProcessor processor = new DatabaseProcessor();

	/** The response. */
	@Context
	private HttpServletResponse response;
	
	/**
	 * Execute artifact metadata export.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param schema
	 * 			  the schema name
	 * @param artifact
	 * 			  the artifact name
	 * @param request
	 *            the request
	 * @return the response
	 * @throws SQLException in case of an error
	 */
	@GET
	@Path("{type}/{name}/{schema}/{artifact}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation("Executes a query operation on the datasource {name}, {type}, {schema} and {artifact} and returns the result in a CSV format")
	@ApiResponses({ @ApiResponse(code = 200, message = "File has been generated successfully", response = String.class),
			@ApiResponse(code = 404, message = "Datasource with {name} for the requested database {type} does not exist") })
	public Response exportArtifactMetadata(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "DataSource Name", required = true) @PathParam("name") String name,
			@ApiParam(value = "Schema Name", required = true) @PathParam("schema") String schema,
			@ApiParam(value = "Artifact Name", required = true) @PathParam("artifact") String artifact, @Context HttpServletRequest request) throws SQLException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return createErrorResponseNotFound(error);
		}
		
		String result = processor.exportArtifactMetadata(type, name, schema, artifact);
		return Response.ok().header("Content-Disposition",  "attachment; filename=\"" + schema + "." + artifact + "-" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".table\"").entity(result).build();
	}
	
	/**
	 * Execute schema metadata export.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param schema
	 * 			  the schema name
	 * @param request
	 *            the request
	 * @return the response
	 * @throws SQLException in case of an error
	 */
	@GET
	@Path("{type}/{name}/{schema}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation("Executes a query operation on the datasource {name}, {type} and {schema} and returns the result in a CSV format")
	@ApiResponses({ @ApiResponse(code = 200, message = "File has been generated successfully", response = String.class),
			@ApiResponse(code = 404, message = "Datasource with {name} for the requested database {type} does not exist") })
	public Response exportSchemaMetadata(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "DataSource Name", required = true) @PathParam("name") String name,
			@ApiParam(value = "Schema Name", required = true) @PathParam("schema") String schema,
			@Context HttpServletRequest request) throws SQLException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return createErrorResponseNotFound(error);
		}
		
		String result = processor.exportSchemaMetadata(type, name, schema);
		return Response.ok().header("Content-Disposition",  "attachment; filename=\"" + schema + "-" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".schema\"").entity(result).build();
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
		return DatabaseMetadataRestService.class;
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
