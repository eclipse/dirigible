/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.ide.bpm.service;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.eclipse.dirigible.runtime.ide.bpm.processor.BpmProcessor;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the BPM related resources and operations
 */
@Singleton
@Path("/ide/bpm")
@Api(value = "IDE - BPM", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class BpmRestService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(BpmRestService.class);

	@Inject
	private BpmProcessor processor;
	
	@Inject
	private WorkspaceProcessor workspaceProcessor;

	@Context
	private HttpServletResponse response;

	/**
	 * Get the BPM model source
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return the response
	 * @throws JsonProcessingException exception
	 */
	@GET
	@Path("models/{workspace}/{project}/{path:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Returns the model source in JSON")
	@ApiResponses({ @ApiResponse(code = 200, message = "Model Source", response = String.class),
			@ApiResponse(code = 404, message = "Model with the requested workspace: [{workspace}], project: [{project}] and path: [{path}] does not exist") })
	public Response getModel(@ApiParam(value = "Workspace", required = true) @PathParam("workspace") String workspace, 
			@ApiParam(value = "Project", required = true) @PathParam("project") String project, 
			@ApiParam(value = "Path", required = true) @PathParam("path") String path) throws JsonProcessingException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		String model = processor.getModel(workspace, project, path);
		
		if (model == null) {
			String error = format("Model in workspace: {0} and project {1} with path {2} does not exist.", workspace, project, path);
			return createErrorResponseNotFound(error);
		}
		return Response.ok().entity(model).build();
	}
	
	/**
	 * Save the BPM model source
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param payload the payload
	 * @return the response
	 * @throws URISyntaxException in case of an error
	 * @throws IOException exception
	 */
	@POST
	@Path("models/{workspace}/{project}/{path:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Returns the URI of the stored model source")
	@ApiResponses({ @ApiResponse(code = 200, message = "Model Source", response = String.class),
			@ApiResponse(code = 404, message = "Model with the requested workspace: [{workspace}], project: [{project}] and path: [{path}] does not exist") })
	public Response saveModel(@ApiParam(value = "Workspace", required = true) @PathParam("workspace") String workspace, 
			@ApiParam(value = "Project", required = true) @PathParam("project") String project, 
			@ApiParam(value = "Path", required = true) @PathParam("path") String path, 
			@ApiParam(value = "Model Payload", required = true) @FormParam("json_xml") String payload) throws URISyntaxException, IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}
		
		processor.saveModel(workspace, project, path, payload);
		
		return Response.ok().location(workspaceProcessor.getURI(workspace, project, path)).build();
	}

	
	/**
	 * Get the Stencil-Set
	 *
	 * @return the response
	 * @throws IOException 
	 */
	@GET
	@Path("stencil-sets")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation("Returns the stensil sets")
	@ApiResponses({ @ApiResponse(code = 200, message = "Stencil Sets", response = String.class),
			@ApiResponse(code = 404, message = "Stencil Sets definition does not exist") })
	public Response getStencilSet() throws IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		String stencilSets = processor.getStencilSet();
		
		if (stencilSets == null) {
			String error = "Stencil Sets definition does not exist.";
			throw new RepositoryNotFoundException(error);
		}
		return Response.ok().entity(stencilSets).build();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return BpmRestService.class;
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
