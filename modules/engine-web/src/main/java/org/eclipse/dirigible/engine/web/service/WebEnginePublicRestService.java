/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.web.service;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.service.IRestService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Front facing REST service serving the public raw web content from the registry/public space
 */
@Singleton
@Path("/public")
@Api("Core - Web Engine Public")
public class WebEnginePublicRestService extends AbstractWebEngineRestService {

	@Override
	@GET
	@Path("/{path:.*}")
	@ApiOperation("Get Resource Content")
	@ApiResponses({ @ApiResponse(code = 200, message = "Get the content fo the resource", response = byte[].class),
			@ApiResponse(code = 404, message = "No such resource") })
	public Response getResource(@ApiParam(value = "Path of the Resource", required = true) @PathParam("path") String path) {
		return super.getResource(path);
	}

	@Override
	public Class<? extends IRestService> getType() {
		return WebEnginePublicRestService.class;
	}
}
