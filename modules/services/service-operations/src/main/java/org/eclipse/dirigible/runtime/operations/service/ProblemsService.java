/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.operations.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.runtime.operations.processor.ProblemsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Front facing REST service serving the Problems.
 */
@Path("/ops/problems")
@RolesAllowed({ "Operator" })
@Api(value = "Operations - Problems", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class ProblemsService extends AbstractRestService implements IRestService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemsService.class);

    private ProblemsProcessor processor = new ProblemsProcessor();

    @Context
    private HttpServletResponse response;

    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
     */
    @Override
    public Class<? extends IRestService> getType() {
        return ProblemsService.class;
    }

    /**
     * List all the problems currently registered.
     *
     * @return the response
     * @throws ProblemsException the scheduler exception
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProblems()
            throws ProblemsException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        return Response.ok().entity(processor.list()).build();
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
