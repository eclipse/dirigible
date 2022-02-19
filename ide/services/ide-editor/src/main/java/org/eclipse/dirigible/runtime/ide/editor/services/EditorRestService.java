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
package org.eclipse.dirigible.runtime.ide.editor.services;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Front facing REST service serving the Editor content.
 */
@Path("/ide/editor")
@Api(value = "IDE - Editor", authorizations = {@Authorization(value = "basicAuth", scopes = {})})
@ApiResponses({@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden")})
public class EditorRestService extends AbstractRestService implements IRestService {

    private static final Logger logger = LoggerFactory.getLogger(EditorRestService.class);

    private final IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    private static final String PRETTIER_CONFIG_FILE_NAME = ".prettierrc.json";
    private static final String PRETTIER_CONFIG_CONTENT_TYPE = "application/json";

    @Context
    private HttpServletResponse response;

    /**
     * Finds all prettier config file paths inside a project
     *
     * @param workspaceName the workspace
     * @param projectName   the project to search the config file in
     * @param filePath      the config file name with extension
     * @return the found prettier config paths
     */
    @GET
    @Path("/prettier/config/{workspaceName}/{projectName}/{path:.*}")
    public Response findPrettierConfig(
            @PathParam("workspaceName") String workspaceName,
            @PathParam("projectName") String projectName,
            @PathParam("path") String filePath,
            @Context HttpServletRequest request) {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        String projectPath = joinPathParts(IRepository.SEPARATOR, "users", user, workspaceName, projectName);
        String queryPath = joinPathParts(IRepository.SEPARATOR, "prettier", "config", workspaceName, projectName, filePath);

        RepositoryPath sourceFilePath = new RepositoryPath(projectPath + IRepository.SEPARATOR + filePath);
        RepositoryPath sourceFolder = sourceFilePath.getParentPath();
        String sourceFileName = sourceFilePath.getLastSegment();
        ICollection lookupDirectory = repository.getCollection(sourceFolder.getPath());

        if (!isPathNormalized(queryPath) || !isResourceInDirectory(lookupDirectory, sourceFileName)) {
            return createPrettierConfigNotFoundErrorResponse("There is no resource at specified path", queryPath);
        }

        while (isSubPath(projectPath, lookupDirectory.getPath())) {
            String content = getConfigFileContent(lookupDirectory, PRETTIER_CONFIG_FILE_NAME);
            if(content != null && !content.isEmpty()) {
                return Response.ok(content).type(PRETTIER_CONFIG_CONTENT_TYPE).build();
            }

            lookupDirectory = lookupDirectory.getParent();
        }

        return createPrettierConfigNotFoundErrorResponse("There is no config file for resource", queryPath);

    }

    private Response createPrettierConfigNotFoundErrorResponse(String reason, String queryPath) {
        String errorMessage = String.format("%s: %s", reason, queryPath);
        logger.error(errorMessage);
        return createErrorResponseNotFound(errorMessage);
    }

    private boolean isPathNormalized(String queryPath) {
        return Objects.equals(FilenameUtils.normalize(queryPath), queryPath);
    }

    private boolean isResourceInDirectory(ICollection directory, String resourceName) {
        return directory.getResource(resourceName).exists();
    }

    private String joinPathParts(String delimiter, String... args) {
        return delimiter + String.join(delimiter, args);
    }

    private boolean isSubPath(String superPath, String subPath) {
        return subPath.startsWith(superPath);
    }

    private String getConfigFileContent(ICollection directory, String configFileName) {
        IResource config = directory.getResource(configFileName);
        if (config.exists()) {
            return new String(config.getContent(), StandardCharsets.UTF_8);
        }
        return null;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public Class<? extends IRestService> getType() {
        return EditorRestService.class;
    }
}
