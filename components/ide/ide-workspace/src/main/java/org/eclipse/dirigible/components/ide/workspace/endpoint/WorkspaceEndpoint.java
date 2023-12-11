/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.workspace.endpoint;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;

import jakarta.validation.Valid;

import org.apache.commons.codec.DecoderException;
import org.eclipse.dirigible.components.api.utils.UrlFacade;
import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.ide.workspace.domain.WorkspaceSelectionTargetPair;
import org.eclipse.dirigible.components.ide.workspace.domain.WorkspaceSourceTargetPair;
import org.eclipse.dirigible.components.ide.workspace.service.PublisherService;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class WorkspaceEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_IDE + "workspace")
public class WorkspaceEndpoint {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceEndpoint.class);

    /** The workspace service. */
    @Autowired
    private WorkspaceService workspaceService;

    /** The publisher service. */
    @Autowired
    private PublisherService publisherService;

    /**
     * Copy.
     *
     * @param currentWorkspace the current workspace
     * @param content the content
     * @return the response
     * @throws URISyntaxException the URI syntax exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws DecoderException the decoder exception
     */
    @PostMapping("{workspace}/copy")
    public ResponseEntity<URI> copy(@PathVariable("workspace") String currentWorkspace,
            @Valid @RequestBody WorkspaceSourceTargetPair content)
            throws URISyntaxException, UnsupportedEncodingException, DecoderException {
        if ((content.getSource() == null) || (content.getTarget() == null) || (content.getSourceWorkspace() == null)
                || (content.getTargetWorkspace() == null)) {
            String error = "Source and Target paths and workspaces have to be present in the body of the request";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        RepositoryPath sourcePath = new RepositoryPath(UrlFacade.decode(content.getSource()));
        if (sourcePath.getSegments().length == 0) {
            String error = "Source path is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        RepositoryPath targetPath = new RepositoryPath(UrlFacade.decode(content.getTarget()));
        if (targetPath.getSegments().length == 0) {
            String error = "Target path is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String sourceWorkspace = content.getSourceWorkspace();
        if (sourceWorkspace.length() == 0) {
            String error = "Source workspace is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String targetWorkspace = content.getTargetWorkspace();
        if (targetWorkspace.length() == 0) {
            String error = "Target workspace is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String sourceProject = sourcePath.getSegments()[0];
        String targetProject = targetPath.getSegments()[0];
        if (sourcePath.getSegments().length == 1) {
            // a project is selected as a source
            workspaceService.copyProject(sourceWorkspace, targetWorkspace, sourceProject, targetProject);
            return ResponseEntity.created(workspaceService.getURI(targetWorkspace, targetProject, null))
                                 .build();
        }

        String targetFilePath = targetPath.constructPathFrom(1);
        if (targetFilePath.equals(targetPath.build())) {
            targetFilePath = IRepository.SEPARATOR;
        }
        if (!workspaceService.existsFolder(targetWorkspace, targetProject, targetFilePath)) {
            String error = "Target path points to a non-existing folder";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String sourceFilePath = sourcePath.constructPathFrom(1);
        if (workspaceService.existsFile(sourceWorkspace, sourceProject, sourceFilePath)) {
            workspaceService.copyFile(sourceWorkspace, targetWorkspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
        } else {
            workspaceService.copyFolder(sourceWorkspace, targetWorkspace, sourceProject, sourceFilePath, targetProject,
                    targetFilePath + IRepositoryStructure.SEPARATOR, sourcePath.getLastSegment());
        }

        return ResponseEntity.created(workspaceService.getURI(targetWorkspace, null, content.getTarget()))
                             .build();
    }

    /**
     * Copy selection of nodes.
     *
     * @param currentWorkspace the current workspace
     * @param content the content
     * @return the response
     * @throws Exception the exception
     */
    @PostMapping("{workspace}/copySelection")
    public ResponseEntity<URI> copySelection(@PathVariable("workspace") String currentWorkspace,
            @Valid @RequestBody WorkspaceSelectionTargetPair content) throws Exception {
        ArrayList<WorkspaceSelectionTargetPair.SelectedNode> sourceSelection = content.getSource();

        if ((content.getSource() == null) || (content.getTarget() == null) || (content.getSourceWorkspace() == null)
                || (content.getTargetWorkspace() == null)) {
            String error = "Source and Target paths and workspaces have to be present in the body of the request";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        if (sourceSelection.size() == 0) {
            String error = "Source path is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        RepositoryPath targetPath = new RepositoryPath(UrlFacade.decode(content.getTarget()));
        if (targetPath.getSegments().length == 0) {
            String error = "Target path is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String sourceWorkspace = content.getSourceWorkspace();
        if (sourceWorkspace.length() == 0) {
            String error = "Source workspace is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String targetWorkspace = content.getTargetWorkspace();
        if (targetWorkspace.length() == 0) {
            String error = "Target workspace is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String targetProject = targetPath.getSegments()[1];
        WorkspaceSelectionTargetPair.SelectedNode nodeToCopy;

        for (int i = 0; i < sourceSelection.size(); i++) {

            nodeToCopy = sourceSelection.get(i);
            RepositoryPath sourcePath = new RepositoryPath(UrlFacade.decode(sourceSelection.get(i)
                                                                                           .getPath()));
            String sourceProject = sourcePath.getSegments()[1];

            if (sourcePath.getSegments().length == 1) {
                // a project is selected as a source
                workspaceService.copyProject(sourceWorkspace, targetWorkspace, sourceProject, targetProject);
                return ResponseEntity.created(workspaceService.getURI(targetWorkspace, targetProject, null))
                                     .build();
            }

            String targetFilePath = targetPath.constructPathFrom(2);
            String relativePath = sourceSelection.get(i)
                                                 .getRelativePath();
            if (targetFilePath.equals(targetPath.build())) {
                targetFilePath = IRepository.SEPARATOR;
            }
            targetFilePath = targetFilePath.concat(IRepository.SEPARATOR)
                                           .concat(nodeToCopy.getInternalPath())
                                           .replaceAll("^/+", "");

            String fileOrFolder = sourceSelection.get(i)
                                                 .getNodeType();
            String conflictResolution = sourceSelection.get(i)
                                                       .getResolution();
            String relativePathToTargetFile = Paths.get(targetFilePath)
                                                   .getParent()
                                                   .toString();
            String fileOrFolderName = Paths.get(targetFilePath)
                                           .getFileName()
                                           .toString();
            String skipPath;

            switch (fileOrFolder) {
                case "folder":
                    if (workspaceService.existsFile(targetWorkspace, targetProject, targetFilePath)) {
                        switch (conflictResolution) {
                            case "replace":
                                workspaceService.deleteFile(targetWorkspace, targetProject, targetFilePath);
                                workspaceService.createFolder(targetWorkspace, targetProject, targetFilePath);
                                break;
                            case "skip":
                                skipPath = sourceSelection.get(i)
                                                          .getPath()
                                                          .concat(IRepository.SEPARATOR);
                                content.skipByPath(skipPath);
                                break;
                            default:
                                workspaceService.copyFolder(sourceWorkspace, targetWorkspace, sourceProject, relativePath, targetProject,
                                        relativePathToTargetFile.concat(IRepository.SEPARATOR), fileOrFolderName);
                                skipPath = sourceSelection.get(i)
                                                          .getPath()
                                                          .concat(IRepository.SEPARATOR);
                                content.skipByPath(skipPath);

                        }
                    } else if (!workspaceService.existsFolder(targetWorkspace, targetProject, targetFilePath)) {
                        workspaceService.createFolder(targetWorkspace, targetProject, targetFilePath);
                    }
                    break;
                case "file":
                    if (workspaceService.existsFile(sourceWorkspace, sourceProject, relativePath)) {
                        switch (conflictResolution) {
                            case "replace":
                                if (workspaceService.existsFile(targetWorkspace, targetProject, targetFilePath))
                                    workspaceService.deleteFile(targetWorkspace, targetProject, targetFilePath);
                                else if (workspaceService.existsFolder(targetWorkspace, targetProject, targetFilePath))
                                    workspaceService.deleteFolder(targetWorkspace, targetProject, targetFilePath);
                                workspaceService.copyFile(sourceWorkspace, targetWorkspace, sourceProject, relativePath, targetProject,
                                        relativePathToTargetFile);
                                break;
                            case "skip":
                                break;
                            default:
                                workspaceService.copyFile(sourceWorkspace, targetWorkspace, sourceProject, relativePath, targetProject,
                                        relativePathToTargetFile);

                        }
                    }
                    break;
                default:
                    throw new InputMismatchException();
            }
        }
        return ResponseEntity.created(workspaceService.getURI(targetWorkspace, null, content.getTarget()))
                             .build();
    }

    /**
     * Move.
     *
     * @param workspace the workspace
     * @param content the content
     * @return the response
     * @throws URISyntaxException the URI syntax exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws DecoderException the decoder exception
     */
    @PostMapping("{workspace}/move")
    public ResponseEntity<URI> move(@PathVariable("workspace") String workspace, @Valid @RequestBody WorkspaceSourceTargetPair content)
            throws URISyntaxException, UnsupportedEncodingException, DecoderException {
        if ((content.getSource() == null) || (content.getTarget() == null) || (content.getSourceWorkspace() == null)
                || (content.getTargetWorkspace() == null)) {
            String error = "Source and Target paths and workspaces have to be present in the body of the request";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        RepositoryPath sourcePath = new RepositoryPath(UrlFacade.decode(content.getSource()));
        if (sourcePath.getSegments().length == 0) {
            String error = "Source path is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        RepositoryPath targetPath = new RepositoryPath(UrlFacade.decode(content.getTarget()));
        if (targetPath.getSegments().length == 0) {
            String error = "Target path is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String sourceProject = sourcePath.getSegments()[0];
        String targetProject = targetPath.getSegments()[0];
        if (sourcePath.getSegments().length == 1) {
            // a project is selected as a source
            workspaceService.moveProject(workspace, sourceProject, targetProject);
            return ResponseEntity.created(workspaceService.getURI(workspace, targetProject, null))
                                 .build();
        }

        String sourceFilePath = sourcePath.constructPathFrom(1);
        String targetFilePath = targetPath.constructPathFrom(1);
        if (workspaceService.existsFile(workspace, sourceProject, sourceFilePath)) {
            workspaceService.moveFile(workspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
        } else if (workspaceService.existsFolder(workspace, sourceProject, sourceFilePath)) {
            workspaceService.moveFolder(workspace, sourceProject, sourceFilePath, targetProject, targetFilePath);
        } else {

            String error = "Path does not exists.";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
        }
        publisherService.unpublish(sourcePath.getPath());
        publisherService.publish(workspace, targetPath.getPath(), "");

        return ResponseEntity.created(workspaceService.getURI(workspace, null, content.getTarget()))
                             .build();
    }

    /**
     * Rename.
     *
     * @param workspace the workspace
     * @param content the content
     * @return the response
     * @throws URISyntaxException the URI syntax exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws DecoderException the decoder exception
     */
    @PostMapping("{workspace}/rename")
    public ResponseEntity<URI> rename(@PathVariable("workspace") String workspace, @Valid @RequestBody WorkspaceSourceTargetPair content)
            throws URISyntaxException, UnsupportedEncodingException, DecoderException {
        return move(workspace, content);
    }

    /**
     * Link project.
     *
     * @param workspace the workspace
     * @param content the content
     * @return the response
     * @throws URISyntaxException the URI syntax exception
     * @throws DecoderException the decoder exception
     * @throws IOException IO error
     */
    @PostMapping("{workspace}/linkProject")
    public ResponseEntity<URI> link(@PathVariable("workspace") String workspace, @Valid @RequestBody WorkspaceSourceTargetPair content)
            throws URISyntaxException, DecoderException, IOException {
        if ((content.getSource() == null) || (content.getTarget() == null) || (content.getSourceWorkspace() == null)
                || (content.getTargetWorkspace() == null)) {
            String error = "Source and Target paths and workspaces have to be present in the body of the request";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        RepositoryPath sourcePath = new RepositoryPath(UrlFacade.decode(content.getSource()));
        if (sourcePath.getSegments().length == 0) {
            String error = "Source path is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        RepositoryPath targetPath = new RepositoryPath(UrlFacade.decode(content.getTarget()));
        if (targetPath.getSegments().length == 0) {
            String error = "Target path is empty";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }

        String sourceProject = sourcePath.getSegments()[0];
        String targetProject = targetPath.getPath();
        if (sourcePath.getSegments().length == 1) {
            // a project is selected as a source
            workspaceService.linkProject(workspace, sourceProject, targetProject);
            return ResponseEntity.created(workspaceService.getURI(workspace, sourceProject, null))
                                 .build();
        }
        String error = "Invalid project name";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
    }

}
