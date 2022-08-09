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
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Front facing REST service serving the Problems.
 */
@Path("/ops/problems")
@RolesAllowed({ "Operator" })
@Api(value = "Operations - Problems", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class ProblemsService extends AbstractRestService implements IRestService {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ProblemsService.class);

    /** The processor. */
    private ProblemsProcessor processor = new ProblemsProcessor();

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

    /**
     * List all the problems currently registered.
     *
     * @param condition the condition
     * @param limit the limit
     * @return the response
     * @throws ProblemsException the scheduler exception
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchProblemsBatch(@QueryParam("condition") String condition, @QueryParam("limit") int limit)
            throws ProblemsException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        return Response.ok().entity(processor.fetchProblemsBatch(condition, limit)).build();
    }

    /**
     * Updates the status of all selected problems.
     *
     * @param status the status
     * @param selectedIds the selected ids
     * @return the complete list of problems after the update
     * @throws ProblemsException the scheduler exception
     */
    @POST
    @Path("/update/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStatus(@PathParam("status") String status, List<Long> selectedIds)
            throws ProblemsException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        processor.updateStatus(selectedIds, status);
        return Response.ok().build();
    }

    /**
     * Deletes all problems by their status.
     * s
     *
     * @param status the status
     * @return the response
     * @throws ProblemsException the scheduler exception
     */
    @DELETE
    @Path("/delete/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProblemsByStatus(@PathParam("status") String status)
            throws ProblemsException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        processor.deleteProblemsByStatus(status);
        return Response.ok().build();
    }

    /**
     * Deletes all problems.
     *s
     * @return the response
     * @throws ProblemsException the scheduler exception
     */
    @DELETE
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearProblems()
            throws ProblemsException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        processor.clear();
        return Response.ok().build();
    }

    /**
     * Deletes all selected problems.
     * s
     *
     * @param selectedIds the selected ids
     * @return the response
     * @throws ProblemsException the scheduler exception
     */
    @POST
    @Path("/delete/selected")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMultipleProblems(List<Long> selectedIds)
            throws ProblemsException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        processor.deleteMultipleProblemsById(selectedIds);
        return Response.ok().build();
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
