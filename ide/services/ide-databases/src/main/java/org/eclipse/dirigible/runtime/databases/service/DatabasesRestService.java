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

import static java.text.MessageFormat.format;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.database.api.metadata.DatabaseMetadata;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
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
 * Front facing REST service serving the raw databases content.
 */
@Path("/ide/databases")
@Api(value = "IDE - Databases", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class DatabasesRestService extends AbstractRestService implements IRestService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabasesRestService.class);

	/** The processor. */
	private DatabaseProcessor processor = new DatabaseProcessor();

	/** The response. */
	@Context
	private HttpServletResponse response;

	/**
	 * List database types.
	 *
	 * @return the response
	 */
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("List all the databases types")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of Databases Types", response = String.class, responseContainer = "List") })
	public Response listDatabaseTypes() {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		List<String> databaseTypes = processor.getDatabaseTypes();
		return Response.ok().entity(databaseTypes).build();
	}

	/**
	 * List data sources.
	 *
	 * @param type
	 *            the type
	 * @return the response
	 */
	@GET
	@Path("{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Returns all the available data sources for the given database {type}")
	@ApiResponses({ @ApiResponse(code = 200, message = "List of Data Sources", response = String.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "Data Sources for the requested database {type} does not exist") })
	public Response listDataSources(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		Set<String> list = processor.getDataSources(type);
		if (list == null) {
			String error = format("Database Type {0} not known.", type);
			return createErrorResponseNotFound(error);
		}
		return Response.ok().entity(list).build();

	}

	/**
	 * List artifacts.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @return the response
	 * @throws SQLException
	 *             the SQL exception
	 */
	@GET
	@Path("{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Returns the metadata of the given data source with {name} and {type}")
	@ApiResponses({ @ApiResponse(code = 200, message = "Database Metadata", response = DatabaseMetadata.class),
			@ApiResponse(code = 404, message = "Database Metadata for the requested database {type} does not exist") })
	public Response listArtifacts(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "DataSource Name", required = true) @PathParam("name") String name) throws SQLException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		DataSource dataSource = processor.getDataSource(type, name);
		if (dataSource == null) {
			String error = format("DataSource {0} of Type {1} not known.", name, type);
			return createErrorResponseNotFound(error);
		}
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return Response.ok().entity(metadata).build();

	}
	
	/**
	 * List artifacts.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the datasource name
	 * @param schema
	 * 			  the schema name
	 * @param artifact
	 * 			  the artifact name
	 * @param kind
	 * 			  the artifact kind
	 * @return the response
	 * @throws SQLException
	 *             the SQL exception
	 */
	@GET
	@Path("{type}/{name}/{schema}/{artifact}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Returns the metadata of the given data source with {name} and {type}")
	@ApiResponses({ @ApiResponse(code = 200, message = "Database Metadata", response = DatabaseMetadata.class),
			@ApiResponse(code = 404, message = "Database Metadata for the requested database {type} does not exist") })
	public Response describeArtifact(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "DataSource Name", required = true) @PathParam("name") String name,
			@ApiParam(value = "Schema Name", required = true) @PathParam("schema") String schema,
			@ApiParam(value = "Artifact Name", required = true) @PathParam("artifact") String artifact, @QueryParam("kind") String kind) throws SQLException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		DataSource dataSource = processor.getDataSource(type, name);
		if (dataSource == null) {
			String error = format("DataSource {0} of Type {1} not known.", name, type);
			return createErrorResponseNotFound(error);
		}
		String metadata = processor.describeArtifact(dataSource, schema, artifact, kind);
		return Response.ok().entity(metadata).build();

	}

	

	/**
	 * Execute query.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param sql
	 *            the sql
	 * @param request
	 *            the request
	 * @return the response
	 */
	@POST
	@Path("{type}/{name}/query")
	@ApiOperation("Executes a query operation on the datasource {name} and {type} and returns the result in a tabular format")
	@ApiResponses({ @ApiResponse(code = 200, message = "Datasource updated successfully", response = String.class),
			@ApiResponse(code = 404, message = "Datasource with {name} for the requested database {type} does not exist") })
	public Response executeQuery(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "DataSource Name", required = true) @PathParam("name") String name, byte[] sql, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return createErrorResponseNotFound(error);
		}

		String accept = request.getHeader("Accept");
		if (ContentTypeHelper.TEXT_PLAIN.equals(accept)) {
			String result = processor.executeQuery(type, name, new String(sql, StandardCharsets.UTF_8), false, false);
			return Response.ok().entity(result).type(MediaType.TEXT_PLAIN).build();
		}  else if (ContentTypeHelper.TEXT_CSV.equals(accept)) {
			String result = processor.executeQuery(type, name, new String(sql, StandardCharsets.UTF_8), false, true);
			return Response.ok().entity(result).type(MediaType.TEXT_PLAIN).build();
		}
		String result = processor.executeQuery(type, name, new String(sql, StandardCharsets.UTF_8), true, false);
		return Response.ok().entity(result).type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Execute update.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param sql
	 *            the sql
	 * @param request
	 *            the request
	 * @return the response
	 */
	@POST
	@Path("{type}/{name}/update")
	@ApiOperation("Executes an update operation on the datasource {name} and {type} and returns the result in a tabular format")
	@ApiResponses({ @ApiResponse(code = 200, message = "Datasource updated successfully", response = String.class),
			@ApiResponse(code = 404, message = "Datasource with {name} for the requested database {type} does not exist") })
	public Response executeUpdate(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "DataSource Name", required = true) @PathParam("name") String name, byte[] sql, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return createErrorResponseNotFound(error);
		}

		String accept = request.getHeader("Accept");
		if (ContentTypeHelper.TEXT_PLAIN.equals(accept)) {
			String result = processor.executeUpdate(type, name, new String(sql, StandardCharsets.UTF_8), false, false);
			return Response.ok().entity(result).type(MediaType.TEXT_PLAIN).build();
		}  else if (ContentTypeHelper.TEXT_CSV.equals(accept)) {
			String result = processor.executeUpdate(type, name, new String(sql, StandardCharsets.UTF_8), false, true);
			return Response.ok().entity(result).type(MediaType.TEXT_PLAIN).build();
		}
		String result = processor.executeUpdate(type, name, new String(sql, StandardCharsets.UTF_8), true, false);
		return Response.ok().entity(result).type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Execute procedure.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param sql
	 *            the sql
	 * @param request
	 *            the request
	 * @return the response
	 */
	@POST
	@Path("{type}/{name}/procedure")
	@ApiOperation("Executes an update operation on the datasource {name} and {type} and returns the result in a tabular format")
	@ApiResponses({ @ApiResponse(code = 200, message = "Datasource updated successfully", response = String.class),
			@ApiResponse(code = 404, message = "Datasource with {name} for the requested database {type} does not exist") })
	public Response executeProcedure(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "DataSource Name", required = true) @PathParam("name") String name, byte[] sql, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return createErrorResponseNotFound(error);
		}

		String accept = request.getHeader("Accept");
		if (ContentTypeHelper.TEXT_PLAIN.equals(accept)) {
			String result = processor.executeProcedure(type, name, new String(sql, StandardCharsets.UTF_8), false, false);
			return Response.ok().entity(result).type(MediaType.TEXT_PLAIN).build();
		}  else if (ContentTypeHelper.TEXT_CSV.equals(accept)) {
			String result = processor.executeProcedure(type, name, new String(sql, StandardCharsets.UTF_8), false, true);
			return Response.ok().entity(result).type(MediaType.TEXT_PLAIN).build();
		}
		String result = processor.executeProcedure(type, name, new String(sql, StandardCharsets.UTF_8), true, false);
		return Response.ok().entity(result).type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Execute.
	 *
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @param sql
	 *            the sql
	 * @param request
	 *            the request
	 * @return the response
	 */
	@POST
	@Path("{type}/{name}/execute")
	@ApiOperation("Executes a query or update operation on the datasource {name} and {type} and returns the result in a tabular format")
	@ApiResponses({ @ApiResponse(code = 200, message = "Datasource updated successfully", response = String.class),
			@ApiResponse(code = 404, message = "Datasource with {name} for the requested database {type} does not exist") })
	public Response execute(@ApiParam(value = "Database Type", required = true) @PathParam("type") String type,
			@ApiParam(value = "DataSource Name", required = true) @PathParam("name") String name, byte[] sql, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsDatabase(type, name)) {
			String error = format("Datasource {0} does not exist as {1}.", name, type);
			return createErrorResponseNotFound(error);
		}

		String accept = request.getHeader("Accept");
		if (ContentTypeHelper.TEXT_PLAIN.equals(accept)) {
			String result = processor.execute(type, name, new String(sql, StandardCharsets.UTF_8), false, false);
			return Response.ok().entity(result).type(MediaType.TEXT_PLAIN).build();
		} else if (ContentTypeHelper.TEXT_CSV.equals(accept)) {
			String result = processor.execute(type, name, new String(sql, StandardCharsets.UTF_8), false, true);
			return Response.ok().entity(result).type(MediaType.TEXT_PLAIN).build();
		}
		String result = processor.execute(type, name, new String(sql, StandardCharsets.UTF_8), true, false);
		return Response.ok().entity(result).type(MediaType.APPLICATION_JSON).build();
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
		return DatabasesRestService.class;
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
