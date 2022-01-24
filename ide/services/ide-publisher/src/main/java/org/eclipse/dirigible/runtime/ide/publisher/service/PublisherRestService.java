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
package org.eclipse.dirigible.runtime.ide.publisher.service;

import static java.text.MessageFormat.format;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.publisher.processor.PublisherProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the publishing requests.
 */
@Path("/ide/publisher")
@RolesAllowed({ "Developer" })
@Api(value = "IDE - Publish", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class PublisherRestService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(PublisherRestService.class);

	private PublisherProcessor processor = new PublisherProcessor();

	@Context
	private HttpServletResponse response;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return PublisherRestService.class;
	}

	/**
	 * Request publishing.
	 *
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * @param request
	 *            the request
	 * @return the response
	 * @throws PublisherException
	 *             the publisher exception
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@POST
	@Path("request/{workspace}/{path:.*}")
	@ApiOperation("Publish Workspace Resources")
	@ApiResponses({ @ApiResponse(code = 200, message = "Execution Result") })
	public Response requestPublishing(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Resource Path") @PathParam("path") String path, @Context HttpServletRequest request)
			throws PublisherException, URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return createErrorResponseNotFound(error);
		}

		long id = processor.requestPublishing(user, workspace, path);

		return Response.created(new URI("ide/publisher/" + id)).build();
	}
	
	/**
	 * Request unpublishing.
	 *
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * @param request
	 *            the request
	 * @return the response
	 * @throws PublisherException
	 *             the publisher exception
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@DELETE
	@Path("request/{workspace}/{path:.*}")
	@ApiOperation("Unpublish Workspace Resources")
	@ApiResponses({ @ApiResponse(code = 200, message = "Execution Result") })
	public Response requestUnpublishing(@ApiParam(value = "Name of the Workspace", required = true) @PathParam("workspace") String workspace,
			@ApiParam(value = "Resource Path") @PathParam("path") String path, @Context HttpServletRequest request)
			throws PublisherException, URISyntaxException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if (!processor.existsWorkspace(user, workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			return createErrorResponseNotFound(error);
		}

		long id = processor.requestUnpublishing(user, workspace, path);

		return Response.created(new URI("ide/publisher/" + id)).build();
	}

	/**
	 * Gets the request.
	 *
	 * @param id
	 *            the id
	 * @param request
	 *            the request
	 * @return the request
	 * @throws PublisherException
	 *             the publisher exception
	 */
	@GET
	@Path("request/{id}")
	public Response getRequest(@PathParam("id") long id, @Context HttpServletRequest request) throws PublisherException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		PublishRequestDefinition publishRequestDefinition = processor.getPublishingRequest(id);
		if (publishRequestDefinition != null) {
			return Response.ok().entity(publishRequestDefinition).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		String message = "Publishing request does not exist or has already been processed.";
		return createErrorResponseNotFound(message);
	}

	/**
	 * List log.
	 *
	 * @param request
	 *            the request
	 * @return the response
	 * @throws PublisherException
	 *             the publisher exception
	 */
	@GET
	@Path("log")
	public Response listLog(@Context HttpServletRequest request) throws PublisherException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		List<PublishLogDefinition> publishLogDefinitions = processor.listPublishingLog();
		return Response.ok().entity(publishLogDefinitions).type(ContentTypeHelper.APPLICATION_JSON).build();
	}

	/**
	 * Clear log.
	 *
	 * @param request
	 *            the request
	 * @return the response
	 * @throws PublisherException
	 *             the publisher exception
	 */
	@DELETE
	@Path("log")
	public Response clearLog(@Context HttpServletRequest request) throws PublisherException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		processor.clearPublishingLog();
		return Response.noContent().build();
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
