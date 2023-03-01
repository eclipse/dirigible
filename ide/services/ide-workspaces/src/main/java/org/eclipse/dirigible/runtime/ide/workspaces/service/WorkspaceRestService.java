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
package org.eclipse.dirigible.runtime.ide.workspaces.service;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Workspace content.
 */
@Path("/ide/workspaces")
@RolesAllowed({ "Developer" })
@Api(value = "IDE - Workspace", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden") })
public class WorkspaceRestService extends AbstractRestService implements IRestService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(WorkspaceRestService.class);

	/** The processor. */
	private WorkspaceProcessor processor = new WorkspaceProcessor();

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
		return WorkspaceRestService.class;
	}

	// Workspace

	/**
	 * List workspaces.
	 *
	 * @param request
	 *            the request
	 * @return the response
	 */
	@GET
	@Path("/")
	public Response listWorkspaces(@Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		List<IWorkspace> workspaces = processor.listWorkspaces();
		List<String> workspacesNames = new ArrayList<String>();
		for (IWorkspace workspace : workspaces) {
			workspacesNames.add(workspace.getName());
		}
		return Response.ok().entity(new Gson().toJson(workspacesNames)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Gets the workspace.
	 *
	 * @param workspace
	 *            the workspace
	 * @param request
	 *            the request
	 * @return the workspace
	 */
	@GET
	@Path("{workspace}")
	public Response getWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return createErrorResponseNotFound(error);
		}

		IWorkspace workspaceObject = processor.getWorkspace(workspace);
		if (!workspaceObject.exists()) {
			return createErrorResponseNotFound(workspace);
		}
		return Response.ok().entity(processor.renderWorkspaceTree(workspaceObject)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Creates the workspace.
	 *
	 * @param workspace
	 *            the workspace
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@POST
	@Path("{workspace}")
	public Response createWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) throws URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (processor.existsWorkspace(workspace)) {
			return Response.notModified().build();
		}

		IWorkspace workspaceObject = processor.createWorkspace(workspace);
		if (!workspaceObject.exists()) {
			return createErrorResponseNotFound(workspace);
		}
		return Response.created(processor.getURI(workspace, null, null)).entity(processor.renderWorkspaceTree(workspaceObject))
				.type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Delete workspace.
	 *
	 * @param workspace
	 *            the workspace
	 * @param request
	 *            the request
	 * @return the response
	 */
	@DELETE
	@Path("{workspace}")
	public Response deleteWorkspace(@PathParam("workspace") String workspace, @Context HttpServletRequest request) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(workspace)) {
			return Response.ok().build();
		}

		processor.deleteWorkspace(workspace);
		return Response.noContent().build();
	}

	// Project

	/**
	 * Gets the project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param request
	 *            the request
	 * @return the project
	 */
	@GET
	@Path("{workspace}/{project}")
	public Response getProject(@PathParam("workspace") String workspace, @PathParam("project") String project, @Context HttpServletRequest request) {
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

		IProject projectObject = processor.getProject(workspace, project);
		if (!projectObject.exists()) {
			return createErrorResponseNotFound(project);
		}
		return Response.ok().entity(processor.renderProjectTree(workspace, projectObject)).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Creates the project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@POST
	@Path("{workspace}/{project}")
	public Response createProject(@PathParam("workspace") String workspace, @PathParam("project") String project, @Context HttpServletRequest request)
			throws URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return createErrorResponseNotFound(error);
		}

		if (processor.existsProject(workspace, project)) {
			return Response.notModified().build();
		}

		IProject projectObject = processor.createProject(workspace, project);
		if (!projectObject.exists()) {
			return createErrorResponseNotFound(project);
		}
		return Response.created(processor.getURI(workspace, project, null)).entity(processor.renderProjectTree(workspace, projectObject))
				.type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Delete project.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param request
	 *            the request
	 * @return the response
	 * @throws IOException in case of exception
	 */
	@DELETE
	@Path("{workspace}/{project}")
	public Response deleteProject(@PathParam("workspace") String workspace, @PathParam("project") String project,
			@Context HttpServletRequest request) throws IOException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return createErrorResponseNotFound(error);
		}

		if (!processor.existsProject(workspace, project)) {
			return Response.notModified().build();
		}

		processor.deleteProject(workspace, project);
		return Response.noContent().build();
	}

	// Folders and Files

	/**
	 * Gets the file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param request
	 *            the request
	 * @return the file
	 */
	@GET
	@Path("{workspace}/{project}/{path:.*}")
	public Response getFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			@Context HttpServletRequest request) {
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

		IFile file = processor.getFile(workspace, project, path);
		if (!file.exists()) {
			IFolder folder = processor.getFolder(workspace, project, path);
			if (!folder.exists()) {
				return createErrorResponseNotFound(path);
			}
			return Response.ok().entity(processor.renderFolderTree(workspace, folder)).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		String headerContentType = request.getHeader("describe");
		if ((headerContentType != null) && ContentTypeHelper.APPLICATION_JSON.equals(headerContentType)) {
			return Response.ok().entity(processor.renderFileDescription(workspace, file)).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		if (file.isBinary()) {
			return Response.ok().entity(file.getContent()).type(file.getContentType()).build();
		}
		return Response.ok(new String(file.getContent(), StandardCharsets.UTF_8)).type(file.getContentType()).build();
	}

	/**
	 * Creates the file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@POST
	@Path("{workspace}/{project}/{path:.*}")
	@Consumes({ "application/octet-stream" })
	public Response createFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
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

		if (path.endsWith(IRepositoryStructure.SEPARATOR)) {
			IFolder folder = processor.getFolder(workspace, project, path);
			if (folder.exists()) {
				String error = format("Folder {0} already exists in Project {1} in Workspace {2}.", path, project, workspace);
				return createErrorResponseBadRequest(error);
			}

			folder = processor.createFolder(workspace, project, path);
			return Response.created(processor.getURI(workspace, project, path)).build();
		}

		IFile file = processor.getFile(workspace, project, path);
		if (file.exists()) {
			String error = format("File {0} already exists in Project {1} in Workspace {2}.", path, project, workspace);
			return createErrorResponseBadRequest(error);
		}

		if (request.getHeader("Content-Transfer-Encoding") != null && "base64".equals(request.getHeader("Content-Transfer-Encoding"))) {
			content = Base64.decodeBase64(content);
		}
		file = processor.createFile(workspace, project, path, content, request.getContentType());
		return Response.created(processor.getURI(workspace, project, path)).build();
	}

	/**
	 * Creates the file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@POST
	@Path("{workspace}/{project}/{path:.*}")
	public Response createFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			String content, @Context HttpServletRequest request) throws URISyntaxException {
		return createFile(workspace, project, path, content.getBytes(StandardCharsets.UTF_8), request);
	}

	/**
	 * Update file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@PUT
	@Path("{workspace}/{project}/{path:.*}")
	public Response updateFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
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

		IFile file = processor.getFile(workspace, project, path);
		if (!file.exists()) {
			String error = format("File {0} does not exists in Project {1} in Workspace {2}.", path, project, workspace);
			return createErrorResponseBadRequest(error);
		}

		file = processor.updateFile(workspace, project, path, content);
		return Response.noContent().build();
	}

	/**
	 * Update file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@PUT
	@Path("{workspace}/{project}/{path:.*}")
	public Response updateFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			String content, @Context HttpServletRequest request) throws URISyntaxException {
		return updateFile(workspace, project, path, content.getBytes(StandardCharsets.UTF_8), request);
	}

	/**
	 * Delete file.
	 *
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@DELETE
	@Path("{workspace}/{project}/{path:.*}")
	public Response deleteFile(@PathParam("workspace") String workspace, @PathParam("project") String project, @PathParam("path") String path,
			byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
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

		IFolder folder = processor.getFolder(workspace, project, path);
		if (!folder.exists()) {
			IFile file = processor.getFile(workspace, project, path);
			if (!file.exists()) {
				return Response.notModified().build();
			}

			processor.deleteFile(workspace, project, path);
			return Response.noContent().build();
		}
		processor.deleteFolder(workspace, project, path);
		return Response.noContent().build();
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
