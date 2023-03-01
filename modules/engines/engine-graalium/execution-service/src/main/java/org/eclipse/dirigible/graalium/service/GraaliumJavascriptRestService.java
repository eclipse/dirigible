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
package org.eclipse.dirigible.graalium.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.graalium.handler.GraaliumJavascriptHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraaliumJavascriptRestService.
 */
@Path("/graalium")
public class GraaliumJavascriptRestService extends AbstractRestService implements IRestService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(GraaliumJavascriptRestService.class.getCanonicalName());
    
    /** The Constant HTTP_PATH_MATCHER. */
    private static final String HTTP_PATH_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}";
    
    /** The Constant HTTP_PATH_WITH_PARAM_MATCHER. */
    private static final String HTTP_PATH_WITH_PARAM_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}/{projectFilePathParam:.*}";
    
    /** The JavaScript handler. */
    private final GraaliumJavascriptHandler javascriptHandler = new GraaliumJavascriptHandler();

    /**
     * Gets the.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param debug the debug
     * @return the response
     */
    @GET
    @Path(HTTP_PATH_MATCHER)
    public Response get(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    /**
     * Gets the.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the response
     */
    @GET
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response get(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    /**
     * Post.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param debug the debug
     * @return the response
     */
    @POST
    @Path(HTTP_PATH_MATCHER)
    public Response post(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    /**
     * Post.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the response
     */
    @POST
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response post(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    /**
     * Put.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param debug the debug
     * @return the response
     */
    @PUT
    @Path(HTTP_PATH_MATCHER)
    public Response put(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    /**
     * Put.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the response
     */
    @PUT
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response put(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    /**
     * Patch.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param debug the debug
     * @return the response
     */
    @PATCH
    @Path(HTTP_PATH_MATCHER)
    public Response patch(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    /**
     * Patch.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the response
     */
    @PATCH
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response patch(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    /**
     * Delete.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param debug the debug
     * @return the response
     */
    @DELETE
    @Path(HTTP_PATH_MATCHER)
    public Response delete(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    /**
     * Delete.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the response
     */
    @DELETE
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response delete(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    /**
     * Head.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param debug the debug
     * @return the response
     */
    @HEAD
    @Path(HTTP_PATH_MATCHER)
    public Response head(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, debug != null);
    }

    /**
     * Head.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the response
     */
    @HEAD
    @Path(HTTP_PATH_WITH_PARAM_MATCHER)
    public Response head(
            @PathParam("projectName") String projectName,
            @PathParam("projectFilePath") String projectFilePath,
            @PathParam("projectFilePathParam") String projectFilePathParam,
            @QueryParam("debug") String debug
    ) {
        return executeJavaScript(projectName, projectFilePath, projectFilePathParam, debug != null);
    }

    /**
     * Execute java script.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param debug the debug
     * @return the response
     */
    private Response executeJavaScript(String projectName, String projectFilePath, boolean debug) {
        return executeJavaScript(projectName, projectFilePath, "", debug);
    }

    /**
     * Execute java script.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the response
     */
    private Response executeJavaScript(String projectName, String projectFilePath, String projectFilePathParam, boolean debug) {
        javascriptHandler.handleRequest(projectName, projectFilePath, projectFilePathParam, null, debug);
        return Response.ok().build();
    }

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return GraaliumJavascriptRestService.class;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}
}
