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
package org.eclipse.dirigible.runtime.core.services;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.health.HealthStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front-facing service providing the version information
 *
 */
@Path("/healthcheck")
@RolesAllowed({ "Everyone" })
@Api(value = "Version", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class HealthCheckRestService implements IRestService {

	@Override
	public Class<? extends IRestService> getType() {
		return HealthCheckRestService.class;
	}

	@GET
	@Path("")
	@Produces({ "application/json" })
	public HealthStatus getStatus() {
		return HealthStatus.getInstance();
	}
}
