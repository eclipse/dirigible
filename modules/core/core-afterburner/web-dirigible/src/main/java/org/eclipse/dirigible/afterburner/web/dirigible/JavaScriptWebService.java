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
package org.eclipse.dirigible.afterburner.web.dirigible;

import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/js/v2")
public class JavaScriptWebService extends AbstractRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptWebService.class.getCanonicalName());
    private static final String HTTP_PATH_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}";
    private static final String HTTP_PATH_WITH_PARAM_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}/{projectFilePathParam}";
    private final JavaScriptWebHandler requestHandler = new JavaScriptWebHandler();

    @GET
    @Path(HTTP_PATH_MATCHER)
    public Response get(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @GET
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response get(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @POST
    @Path(HTTP_PATH_MATCHER)
    public Response post(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @POST
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response post(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @PUT
    @Path(HTTP_PATH_MATCHER)
    public Response put(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @PUT
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response put(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @PATCH
    @Path(HTTP_PATH_MATCHER)
    public Response patch(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @PATCH
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response patch(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @DELETE
    @Path(HTTP_PATH_MATCHER)
    public Response delete(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @DELETE
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response delete(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    @HEAD
    @Path(HTTP_PATH_MATCHER)
    public Response head(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath
    ) {
        return executeJavaScript(projectName, projectFilePath);
    }

    @HEAD
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response head(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam);
    }

    private Response executeJavaScript(String projectName, String projectFilePath) {
        return executeJavaScript(projectName, projectFilePath, "");
    }

    private Response executeJavaScript(String projectName, String projectFilePath, String projectFilePathParam) {
        try {
            requestHandler.handleJavaScriptRequest(projectName, projectFilePath, projectFilePathParam);
            return Response.ok().build();
        } catch (Exception e) {
            String message = e.getMessage();
            LOGGER.error(message, e);
            createErrorResponseInternalServerError(message);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public Class<? extends IRestService> getType() {
        return JavaScriptWebService.class;
    }
}
