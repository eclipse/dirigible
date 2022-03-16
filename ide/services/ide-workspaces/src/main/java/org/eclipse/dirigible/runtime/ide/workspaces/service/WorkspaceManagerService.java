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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.DecoderException;
import org.apache.cxf.jaxrs.common.openapi.DelegatingServletConfig;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.api.v3.utils.UrlFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceSelectionTargetPair;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceSourceTargetPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing RPC service serving the Workspace actions.
 */
@Path("/ide/workspace")
@RolesAllowed({"Developer"})
@Api(value = "IDE - Workspace Manager", authorizations = {@Authorization(value = "basicAuth", scopes = {})})
@ApiResponses({@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error")})
public class WorkspaceManagerService extends AbstractRestService implements IRestService {

    private static final String ERROR_PATH_DOES_NOT_EXISTS = "Path does not exists.";

    private static final String ERROR_INVALID_PROJECT_NAME = "Invalid project name";

    private static final String ERROR_TARGET_PATH_POINTS_TO_A_NON_EXISTING_FOLDER = "Target path points to a non-existing folder";

    private static final String ERROR_TARGET_WORKSPACE_IS_EMPTY = "Target workspace is empty";

    private static final String ERROR_TARGET_PATH_IS_EMPTY = "Target path is empty";

    private static final String ERROR_SOURCE_WORKSPACE_IS_EMPTY = "Source workspace is empty";

    private static final String ERROR_SOURCE_PATH_IS_EMPTY = "Source path is empty";

    private static final String ERROR_SOURCE_AND_TARGET_PATHS_HAVE_TO_BE_PRESENT_IN_THE_BODY_OF_THE_REQUEST = "Source and Target paths and workspaces have to be present in the body of the request";

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceManagerService.class);

    private WorkspaceProcessor processor = new WorkspaceProcessor();

    @Context
    private HttpServletResponse response;

    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
     */
    @Override
    public Class<? extends IRestService> getType() {
        return WorkspaceManagerService.class;
    }

    /**
     * Copy.
     *
     * @param currentWorkspace the current workspace
     * @param content          the content
     * @param request          the request
     * @return the response
     * @throws URISyntaxException           the URI syntax exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws DecoderException             the decoder exception
     */
    @POST
    @Path("{workspace}/copy")
    public Response copy(@PathParam("workspace") String currentWorkspace, WorkspaceSourceTargetPair content, @Context HttpServletRequest request)
            throws URISyntaxException, UnsupportedEncodingException, DecoderException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        if ((content.getSource() == null) || (content.getTarget() == null) || (content.getSourceWorkspace() == null) || (content.getTargetWorkspace() == null)) {
            return createErrorResponseBadRequest(ERROR_SOURCE_AND_TARGET_PATHS_HAVE_TO_BE_PRESENT_IN_THE_BODY_OF_THE_REQUEST);
        }

        RepositoryPath sourcePath = new RepositoryPath(UrlFacade.decode(content.getSource()));
        if (sourcePath.getSegments().length == 0) {
            return createErrorResponseBadRequest(ERROR_SOURCE_PATH_IS_EMPTY);
        }

        RepositoryPath targetPath = new RepositoryPath(UrlFacade.decode(content.getTarget()));
        if (targetPath.getSegments().length == 0) {
            return createErrorResponseBadRequest(ERROR_TARGET_PATH_IS_EMPTY);
        }

        String sourceWorkspace = content.getSourceWorkspace();
        if (sourceWorkspace.length() == 0) {
            return createErrorResponseBadRequest(ERROR_SOURCE_WORKSPACE_IS_EMPTY);
        }
        String targetWorkspace = content.getTargetWorkspace();
        if (targetWorkspace.length() == 0) {
            return createErrorResponseBadRequest(ERROR_TARGET_WORKSPACE_IS_EMPTY);
        }

        String sourceProject = sourcePath.getSegments()[0];
        String targetProject = targetPath.getSegments()[0];
        if (sourcePath.getSegments().length == 1) {
            // a project is selected as a source
            processor.copyProject(sourceWorkspace, targetWorkspace, sourceProject, targetProject);
            return Response.created(processor.getURI(targetWorkspace, targetProject, null)).build();
        }

        String targetFilePath = targetPath.constructPathFrom(1);
        if (targetFilePath.equals(targetPath.build())) {
            targetFilePath = IRepository.SEPARATOR;
        }
        if (!processor.existsFolder(targetWorkspace, targetProject, targetFilePath)) {
            return createErrorResponseBadRequest(ERROR_TARGET_PATH_POINTS_TO_A_NON_EXISTING_FOLDER);
        }

        String sourceFilePath = sourcePath.constructPathFrom(1);
        if (processor.existsFile(sourceWorkspace, sourceProject, sourceFilePath)) {
            processor.copyFile(sourceWorkspace, targetWorkspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
        } else {
            processor.copyFolder(sourceWorkspace, targetWorkspace, sourceProject, sourceFilePath, targetProject,
                    targetFilePath + IRepositoryStructure.SEPARATOR, sourcePath.getLastSegment());
        }

        return Response.created(processor.getURI(targetWorkspace, null, content.getTarget())).build();
    }

    /**
     * @param currentWorkspace
     * @param content
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws DecoderException
     */
    @POST
    @Path("{workspace}/copySelection")
    public Response copySelection(@PathParam("workspace") String currentWorkspace, WorkspaceSelectionTargetPair content, @Context HttpServletRequest request)
            throws URISyntaxException, UnsupportedEncodingException, DecoderException {
        String user = UserFacade.getName();
        ArrayList<WorkspaceSelectionTargetPair.SelectedNode> sourceSelection = content.getSource();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        if ((content.getSource() == null) || (content.getTarget() == null) || (content.getSourceWorkspace() == null) || (content.getTargetWorkspace() == null)) {
            return createErrorResponseBadRequest(ERROR_SOURCE_AND_TARGET_PATHS_HAVE_TO_BE_PRESENT_IN_THE_BODY_OF_THE_REQUEST);
        }

        if (sourceSelection.size() == 0) {
            return createErrorResponseBadRequest(ERROR_SOURCE_PATH_IS_EMPTY);
        }

        RepositoryPath targetPath = new RepositoryPath(UrlFacade.decode(content.getTarget()));
        System.out.println("INIT TARGET PATH " + targetPath.toString());
        if (targetPath.getSegments().length == 0) {
            return createErrorResponseBadRequest(ERROR_TARGET_PATH_IS_EMPTY);
        }

        String sourceWorkspace = content.getSourceWorkspace();
        if (sourceWorkspace.length() == 0) {
            return createErrorResponseBadRequest(ERROR_SOURCE_WORKSPACE_IS_EMPTY);
        }
        String targetWorkspace = content.getTargetWorkspace();
        if (targetWorkspace.length() == 0) {
            return createErrorResponseBadRequest(ERROR_TARGET_WORKSPACE_IS_EMPTY);
        }

        String targetProject = targetPath.getSegments()[1];
        WorkspaceSelectionTargetPair.SelectedNode nodeToCopy;

        for (int i = 0; i < sourceSelection.size(); i++) {

            nodeToCopy = sourceSelection.get(i);
            RepositoryPath sourcePath = new RepositoryPath(UrlFacade.decode(sourceSelection.get(i).getPath()));
            String sourceProject = sourcePath.getSegments()[1];

            if (sourcePath.getSegments().length == 1) {
                // a project is selected as a source
                processor.copyProject(sourceWorkspace, targetWorkspace, sourceProject, targetProject);
                return Response.created(processor.getURI(targetWorkspace, targetProject, null)).build();
            }

            String targetFilePath = targetPath.constructPathFrom(2);
            String relativePath = sourceSelection.get(i).getRelativePath();
            if (targetFilePath.equals(targetPath.build())) {
                targetFilePath = IRepository.SEPARATOR;
            }
            targetFilePath = targetFilePath.concat(IRepository.SEPARATOR).concat(nodeToCopy.getInternalPath()).replaceAll("^/+", "");
            System.out.println("SOURCE file " + sourcePath.toString());
            System.out.println("DESTINATION file " + targetFilePath);
            System.out.println("RELATIVE PATH" + relativePath);

            String fileOrFolder = sourceSelection.get(i).getNodeType();
            String conflictResolution = sourceSelection.get(i).getResolution();
            String relativePathToTargetFile = Paths.get(targetFilePath).getParent().toString();
            String fileOrFolderName = Paths.get(targetFilePath).getFileName().toString();

            switch (fileOrFolder) {
                case "folder":
                    if (processor.existsFile(targetWorkspace, targetProject, targetFilePath)) {
                        switch (conflictResolution) {
                            case "replace":
                                processor.deleteFile(targetWorkspace, targetProject, targetFilePath);
                                processor.createFolder(targetWorkspace, targetProject, targetFilePath);
                                break;
                            case "skip":
                                String skipPath = sourceSelection.get(i).getPath().concat(IRepository.SEPARATOR);
                                content.skipByPath(skipPath);
                                break;
                            default:
                                processor.copyFolder(sourceWorkspace, targetWorkspace, sourceProject, sourcePath.toString(), targetProject, relativePathToTargetFile, fileOrFolderName);

                        }
                    } else
                    if (!processor.existsFolder(targetWorkspace, targetProject, targetFilePath)) {
                        processor.createFolder(targetWorkspace, targetProject, targetFilePath);
                    }
                    break;
                case "file":
                    if (processor.existsFile(sourceWorkspace, sourceProject, relativePath)) {
                        switch (conflictResolution) {
                            case "replace":
                                System.out.println("COPY with REPLACE " + sourceWorkspace + "/" + sourceProject + "/" + relativePath + " -> " + targetWorkspace.concat(IRepository.SEPARATOR).concat(targetProject).concat(IRepository.SEPARATOR).concat(targetFilePath));
                                if (processor.existsFile(targetWorkspace, targetProject, targetFilePath))
                                    processor.deleteFile(targetWorkspace, targetProject, targetFilePath);
                                else if (processor.existsFolder(targetWorkspace, targetProject, targetFilePath))
                                    processor.deleteFolder(targetWorkspace, targetProject, targetFilePath);
                                processor.copyFile(sourceWorkspace, targetWorkspace, sourceProject, relativePath, targetProject, relativePathToTargetFile);
                                break;
                            case "skip":
                                System.out.println("SKIP COPY " + sourceWorkspace + "/" + sourceProject + "/" + relativePath + " -> " + targetWorkspace.concat(IRepository.SEPARATOR).concat(targetProject).concat(IRepository.SEPARATOR).concat(targetFilePath));
                                break;
                            default:
                                System.out.println("DEFAULT or NON-CONFLICT COPY (rename)" + sourceWorkspace + "/" + sourceProject + "/" + relativePath + " -> " + targetWorkspace.concat(IRepository.SEPARATOR).concat(targetProject).concat(IRepository.SEPARATOR).concat(targetFilePath));
                                processor.copyFile(sourceWorkspace, targetWorkspace, sourceProject, relativePath, targetProject, relativePathToTargetFile);

                        }
                    }
                    else
                        System.out.println("File " + relativePath + " in " + sourceWorkspace + "/" + sourceProject + " doesn't exist.");
                    break;
                default:
                    System.out.println("UNKNOWN NODE TYPE");
            }
//            if (!processor.existsFolder(targetWorkspace, targetProject, targetFilePath)) {
//                return createErrorResponseBadRequest(ERROR_TARGET_PATH_POINTS_TO_A_NON_EXISTING_FOLDER);
//            }
//
//            String sourceFilePath = sourcePath.constructPathFrom(1);
//            if (processor.existsFile(sourceWorkspace, sourceProject, sourceFilePath)) {
//                processor.copyFile(sourceWorkspace, targetWorkspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
//            } else {
//                processor.copyFolder(sourceWorkspace, targetWorkspace, sourceProject, sourceFilePath, targetProject,
//                        targetFilePath + IRepositoryStructure.SEPARATOR, sourcePath.getLastSegment());
//            }
        }
        return Response.created(processor.getURI(targetWorkspace, null, content.getTarget())).build();
    }

    /**
     * Move.
     *
     * @param workspace the workspace
     * @param content   the content
     * @param request   the request
     * @return the response
     * @throws URISyntaxException           the URI syntax exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws DecoderException             the decoder exception
     */
    @POST
    @Path("{workspace}/move")
    public Response move(@PathParam("workspace") String workspace, WorkspaceSourceTargetPair content, @Context HttpServletRequest request)
            throws URISyntaxException, UnsupportedEncodingException, DecoderException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        if ((content.getSource() == null) || (content.getTarget() == null)) {
            return createErrorResponseBadRequest(ERROR_SOURCE_AND_TARGET_PATHS_HAVE_TO_BE_PRESENT_IN_THE_BODY_OF_THE_REQUEST);
        }

        RepositoryPath sourcePath = new RepositoryPath(UrlFacade.decode(content.getSource()));
        if (sourcePath.getSegments().length == 0) {
            return createErrorResponseBadRequest(ERROR_SOURCE_PATH_IS_EMPTY);
        }

        RepositoryPath targetPath = new RepositoryPath(UrlFacade.decode(content.getTarget()));
        if (targetPath.getSegments().length == 0) {
            return createErrorResponseBadRequest(ERROR_TARGET_PATH_IS_EMPTY);
        }

        String sourceProject = sourcePath.getSegments()[0];
        String targetProject = targetPath.getSegments()[0];
        if (sourcePath.getSegments().length == 1) {
            // a project is selected as a source
            processor.moveProject(workspace, sourceProject, targetProject);
            return Response.created(processor.getURI(workspace, targetProject, null)).build();
        }

        String sourceFilePath = sourcePath.constructPathFrom(1);
        String targetFilePath = targetPath.constructPathFrom(1);
        if (processor.existsFile(workspace, sourceProject, sourceFilePath)) {
            processor.moveFile(workspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
        } else if (processor.existsFolder(workspace, sourceProject, sourceFilePath)) {
            processor.moveFolder(workspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
        } else {
            return createErrorResponseNotFound(ERROR_PATH_DOES_NOT_EXISTS);
        }

        return Response.created(processor.getURI(workspace, null, content.getTarget())).build();
    }

    /**
     * Rename.
     *
     * @param workspace the workspace
     * @param content   the content
     * @param request   the request
     * @return the response
     * @throws URISyntaxException           the URI syntax exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws DecoderException             the decoder exception
     */
    @POST
    @Path("{workspace}/rename")
    public Response rename(@PathParam("workspace") String workspace, WorkspaceSourceTargetPair content, @Context HttpServletRequest request)
            throws URISyntaxException, UnsupportedEncodingException, DecoderException {
        return move(workspace, content, request);
    }

    /**
     * Link project.
     *
     * @param workspace the workspace
     * @param content   the content
     * @param request   the request
     * @return the response
     * @throws URISyntaxException the URI syntax exception
     * @throws DecoderException   the decoder exception
     * @throws IOException        IO error
     */
    @POST
    @Path("{workspace}/linkProject")
    public Response link(@PathParam("workspace") String workspace, WorkspaceSourceTargetPair content, @Context HttpServletRequest request)
            throws URISyntaxException, DecoderException, IOException {
        String user = UserFacade.getName();
        if (user == null) {
            return createErrorResponseForbidden(NO_LOGGED_IN_USER);
        }

        if ((content.getSource() == null) || (content.getTarget() == null)) {
            return createErrorResponseBadRequest(ERROR_SOURCE_AND_TARGET_PATHS_HAVE_TO_BE_PRESENT_IN_THE_BODY_OF_THE_REQUEST);
        }

        RepositoryPath sourcePath = new RepositoryPath(UrlFacade.decode(content.getSource()));
        if (sourcePath.getSegments().length == 0) {
            return createErrorResponseBadRequest(ERROR_SOURCE_PATH_IS_EMPTY);
        }

        RepositoryPath targetPath = new RepositoryPath(UrlFacade.decode(content.getTarget()));
        if (targetPath.getSegments().length == 0) {
            return createErrorResponseBadRequest(ERROR_TARGET_PATH_IS_EMPTY);
        }

        String sourceProject = sourcePath.getSegments()[0];
        String targetProject = targetPath.getPath();
        if (sourcePath.getSegments().length == 1) {
            // a project is selected as a source
            processor.linkProject(workspace, sourceProject, targetProject);
            return Response.created(processor.getURI(workspace, sourceProject, null)).build();
        }
        return createErrorResponseBadRequest(ERROR_INVALID_PROJECT_NAME);
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
