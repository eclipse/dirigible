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
package org.eclipse.dirigible.engine.js.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Javascript backend services.
 */
@Path("/js")
@Api(value = "JavaScript Engine", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class JavascriptRestService extends AbstractRestService implements IRestService {


	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(JavascriptRestService.class.getCanonicalName());
	
	/** The Constant DIRIGIBLE_JAVASCRIPT_HANDLER_CLASS_NAME. */
	private static final String DIRIGIBLE_JAVASCRIPT_HANDLER_CLASS_NAME = "DIRIGIBLE_JAVASCRIPT_HANDLER_CLASS_NAME";

	/** The Constant DEFAULT_DIRIGIBLE_JAVASCRIPT_HANDLER_CLASS_NAME. */
	private static final String DEFAULT_DIRIGIBLE_JAVASCRIPT_HANDLER_CLASS_NAME = "org.eclipse.dirigible.graalium.handler.GraaliumJavascriptHandler";
	
	/** The Constant HTTP_PATH_MATCHER. */
	private static final String HTTP_PATH_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}";
	
	/** The Constant HTTP_PATH_WITH_PARAM_MATCHER. */
	private static final String HTTP_PATH_WITH_PARAM_MATCHER = "/{projectName}/{projectFilePath:.*\\.js|.*\\.mjs}/{projectFilePathParam:.*}";

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
		try {
			if (!isValid(projectName) || !isValid(projectFilePath)) {
				return Response.status(Response.Status.FORBIDDEN).build();
			}

			getJavascriptHandler().handleRequest(projectName, projectFilePath, projectFilePathParam, null, debug);
			return Response.ok().build();
		} catch (RepositoryNotFoundException e) {
			String message = e.getMessage() + ". Try to publish the service before execution.";
			throw new RepositoryNotFoundException(message, e);
		}
	}

	/**
	 * Gets the javascript handler.
	 *
	 * @return the javascript handler
	 */
	private JavascriptHandler getJavascriptHandler() {
		String javascriptHandlerClassName = Configuration.get(DIRIGIBLE_JAVASCRIPT_HANDLER_CLASS_NAME, DEFAULT_DIRIGIBLE_JAVASCRIPT_HANDLER_CLASS_NAME);
		if (javascriptHandlerClassName == null) {
			return new DefaultJavascriptHandler();
		}

		try {
			return (JavascriptHandler) Class.forName(javascriptHandlerClassName).getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			throw new RuntimeException("Could not use " + javascriptHandlerClassName, e);
		}
	}

	/**
	 * Gets the dirigible working directory.
	 *
	 * @return the dirigible working directory
	 */
	private java.nio.file.Path getDirigibleWorkingDirectory() {
		IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		String publicRegistryPath = repository.getInternalResourcePath(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		return java.nio.file.Path.of(publicRegistryPath);
	}

	/**
	 * Checks if is valid.
	 *
	 * @param inputPath the input path
	 * @return true, if is valid
	 */
	public boolean isValid(String inputPath) {
		String registryPath = getDirigibleWorkingDirectory().toString();
		String normalizedInputPath = java.nio.file.Path.of(inputPath).normalize().toString();
		File file = new File(registryPath, normalizedInputPath);
		try {
			return file.getCanonicalPath().startsWith(registryPath);
		} catch (IOException e) {
			return false;
		}
	}

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
		return JavascriptRestService.class;
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
