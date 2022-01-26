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
package org.eclipse.dirigible.runtime.ide.generation.service;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateModelParameters;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateParameters;
import org.eclipse.dirigible.runtime.ide.generation.processor.GenerationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Generation content.
 */
@Path("/ide/generate")
@RolesAllowed({ "Developer" })
@Api(value = "IDE - Generation", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
	@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class GenerationRestService extends AbstractRestService implements IRestService {
	
	private static final Logger logger = LoggerFactory.getLogger(GenerationRestService.class);

	private GenerationProcessor processor = new GenerationProcessor();
	
	@Context
	private HttpServletResponse response;

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return GenerationRestService.class;
	}

	/**
	 * Generate file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param parameters the parameters
	 * @param request the request
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 * @throws ScriptingException the scripting exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@POST
	@Path("/file/{workspace}/{project}/{path:.*}")
	public Response generateFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			GenerationTemplateParameters parameters, @Context HttpServletRequest request) throws URISyntaxException, ScriptingException, IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return createErrorResponseNotFound(error);
		}

		if (!processor.existsProject(workspace, project)) {
			processor.createProject(workspace, project);
//			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
//			return createErrorResponseNotFound(error);
//			return Response.status(Status.NOT_FOUND).entity(error).build();
		}

		IFile file = processor.getFile(workspace, project, path);
		if (file.exists()) {
			String error = format("File {0} already exists in Project {1} in Workspace {2}.", path, project, workspace);
			return createErrorResponseBadRequest(error);
		}

		List<IFile> files = processor.generateFile(workspace, project, path, parameters);
		return Response.created(processor.getURI(workspace, project, path)).build();
	}
	
	/**
	 * Generate file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param parameters the parameters
	 * @param request the request
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 * @throws ScriptingException the scripting exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@POST
	@Path("/model/{workspace}/{project}/{path:.*}")
	public Response generateModel(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			GenerationTemplateModelParameters parameters, @Context HttpServletRequest request) throws URISyntaxException, ScriptingException, IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return createErrorResponseNotFound(error);
		}

		if (!processor.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			return createErrorResponseNotFound(error);
		}

		IFile model = processor.getFile(workspace, project, parameters.getModel());
		if (!model.exists()) {
			String error = format("Model file {0} does not exist in Project {1} in Workspace {2}.", parameters.getModel(), project, workspace);
			return createErrorResponseBadRequest(error);
		}

		List<IFile> files = processor.generateModel(model, workspace, project, path, parameters);
		return Response.created(processor.getURI(workspace, project, path)).build();
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
