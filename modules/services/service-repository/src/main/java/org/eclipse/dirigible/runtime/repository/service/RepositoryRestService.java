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
package org.eclipse.dirigible.runtime.repository.service;

import static java.text.MessageFormat.format;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.repository.processor.RepositoryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the raw Repository content.
 */
@Path("/core/repository")
@Api(value = "Core - Repository", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class RepositoryRestService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryRestService.class);

	private RepositoryProcessor processor = new RepositoryProcessor();

	@Context
	private HttpServletResponse response;

	/**
	 * Gets the resource.
	 *
	 * @param path
	 *            the path
	 * @return the resource
	 */
	@GET
	@Path("/{path:.*}")
	public Response getRepositoryResource(@PathParam("path") String path) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		IResource resource = processor.getResource(path);
		if (!resource.exists()) {
			ICollection collection = processor.getCollection(path);
			if (!collection.exists()) {
				return createErrorResponseNotFound(path);
			}
			return Response.ok().entity(processor.renderRepository(collection)).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		if (resource.isBinary()) {
			return Response.ok().entity(resource.getContent()).type(resource.getContentType()).build();
		}
		return Response.ok(new String(resource.getContent(), StandardCharsets.UTF_8)).type(resource.getContentType()).build();
	}

	/**
	 * Creates the resource.
	 *
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
	@Path("/{path:.*}")
	public Response createResource(@PathParam("path") String path, byte[] content, @Context HttpServletRequest request) throws URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (path.endsWith(IRepositoryStructure.SEPARATOR)) {
			ICollection collection = processor.createCollection(path);
			return Response.created(processor.getURI(UrlFacade.escape(collection.getPath() + IRepositoryStructure.SEPARATOR))).build();
		}

		IResource resource = processor.getResource(path);
		if (resource.exists()) {
			String message = format("Resource at location {0} already exists", path);
			return createErrorResponseBadRequest(message);
		}
		resource = processor.createResource(path, content, request.getContentType());
		return Response.created(processor.getURI(UrlFacade.escape(resource.getPath()))).build();
	}

	/**
	 * Update resource.
	 *
	 * @param path
	 *            the path
	 * @param content
	 *            the content
	 * @return the response
	 */
	@PUT
	@Path("/{path:.*}")
	public Response updateResource(@PathParam("path") String path, byte[] content) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		IResource resource = processor.getResource(path);
		if (!resource.exists()) {
			String message = format("Resource at location {0} does not exist", path);
			return createErrorResponseNotFound(message);
		}
		resource = processor.updateResource(path, content);
		return Response.noContent().build();
	}

	/**
	 * Delete resource.
	 *
	 * @param path
	 *            the path
	 * @return the response
	 */
	@DELETE
	@Path("/{path:.*}")
	public Response deleteResource(@PathParam("path") String path) {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (path.endsWith(IRepositoryStructure.SEPARATOR)) {
			processor.deleteCollection(path);
			Response.noContent().build();
		}

		IResource resource = processor.getResource(path);
		if (!resource.exists()) {
			ICollection collection = processor.getCollection(path);
			if (collection.exists()) {
				processor.deleteCollection(path);
				return Response.noContent().build();
			}
			String message = format("Collection or Resource at location {0} does not exist", path);
			return createErrorResponseNotFound(message);
		}
		processor.deleteResource(path);
		return Response.noContent().build();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return RepositoryRestService.class;
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
