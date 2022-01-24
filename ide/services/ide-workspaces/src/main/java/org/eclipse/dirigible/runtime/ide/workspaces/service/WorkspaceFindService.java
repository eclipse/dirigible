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
package org.eclipse.dirigible.runtime.ide.workspaces.service;

import java.io.UnsupportedEncodingException;
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

import org.apache.commons.codec.DecoderException;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing RPC service serving the Workspace actions.
 */
@Path("/ide/workspace-find")
@RolesAllowed({ "Developer" })
@Api(value = "IDE - Workspace Find", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class WorkspaceFindService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(WorkspaceFindService.class);

	private WorkspaceProcessor processor = new WorkspaceProcessor();

	@Context
	private HttpServletResponse response;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return WorkspaceFindService.class;
	}
	
	/**
	 * Find.
	 *
	 * @param pattern
	 *            the content
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 * @throws DecoderException
	 *             the decoder exception
	 */
	@POST
	@Path("/")
	public Response find(String pattern, @Context HttpServletRequest request)
			throws URISyntaxException, UnsupportedEncodingException, DecoderException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if ((pattern == null) || pattern.isEmpty()) {
			Response.ok().entity("No find pattern provided in the request body").build();
		}

		List<IFile> files = processor.find(pattern);

		return Response.ok().entity(processor.renderFileDescriptions(files)).build();
	}

	/**
	 * Find.
	 *
	 * @param workspace
	 *            the workspace
	 * @param pattern
	 *            the content
	 * @param request
	 *            the request
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 * @throws DecoderException
	 *             the decoder exception
	 */
	@POST
	@Path("{workspace}")
	public Response find(@PathParam("workspace") String workspace, String pattern, @Context HttpServletRequest request)
			throws URISyntaxException, UnsupportedEncodingException, DecoderException {
		String user = UserFacade.getName();
		if (user == null) {
			return createErrorResponseForbidden(NO_LOGGED_IN_USER);
		}

		if ((pattern == null) || pattern.isEmpty()) {
			Response.ok().entity("No find pattern provided in the request body").build();
		}

		List<IFile> files = processor.find(workspace, pattern);

		return Response.ok().entity(processor.renderFileDescriptions(files)).build();
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
