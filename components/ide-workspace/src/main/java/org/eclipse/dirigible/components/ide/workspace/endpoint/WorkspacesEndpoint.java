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
package org.eclipse.dirigible.components.ide.workspace.endpoint;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.ide.workspace.domain.File;
import org.eclipse.dirigible.components.ide.workspace.domain.Folder;
import org.eclipse.dirigible.components.ide.workspace.domain.Project;
import org.eclipse.dirigible.components.ide.workspace.domain.Workspace;
import org.eclipse.dirigible.components.ide.workspace.json.ProjectDescriptor;
import org.eclipse.dirigible.components.ide.workspace.json.WorkspaceDescriptor;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class WorkspaceEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_IDE + "workspaces")
public class WorkspacesEndpoint {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(WorkspacesEndpoint.class);
	
	/** The workspace service. */
    @Autowired
    private WorkspaceService workspaceService;
    

    // Workspace
    
    /**
	 * List workspaces.
	 *
	 * @return the list of workspaces names
	 */
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<String>> listWorkspaces() {
		List<Workspace> workspaces = workspaceService.listWorkspaces();
		List<String> workspacesNames = new ArrayList<String>();
		for (Workspace workspace : workspaces) {
			workspacesNames.add(workspace.getName());
		}
		return ResponseEntity.ok(workspacesNames);
	}
	
	/**
	 * Gets the workspace.
	 *
	 * @param workspace the workspace
	 * @return the workspace
	 */
	@GetMapping(value = "/{workspace}", produces = "application/json")
	public ResponseEntity<WorkspaceDescriptor> getWorkspace(
			@PathVariable("workspace") String workspace) {
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		Workspace workspaceObject = workspaceService.getWorkspace(workspace);
		return ResponseEntity.ok(workspaceService.renderWorkspaceTree(workspaceObject));
	}
	
	/**
	 * Creates the workspace.
	 *
	 * @param workspace the workspace
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PostMapping("/{workspace}")
	public ResponseEntity<URI> createWorkspace(
			@PathVariable("workspace") String workspace) throws URISyntaxException {
		if (workspaceService.existsWorkspace(workspace)) {
			return new ResponseEntity<URI>(workspaceService.getURI(workspace, null, null), HttpStatus.NOT_MODIFIED);
		}

		Workspace workspaceObject = workspaceService.createWorkspace(workspace);
		if (!workspaceObject.exists()) {
			String error = format("Failed to create workspace {0}", workspace);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
		
		return ResponseEntity.created(workspaceService.getURI(workspace, null, null)).build();
	}
	
	/**
	 * Delete workspace.
	 *
	 * @param workspace the workspace
	 * @return the response
	 */
	@DeleteMapping("/{workspace}")
	public ResponseEntity<String> deleteWorkspace(
			@PathVariable("workspace") String workspace) {
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Failed to delete workspace {0}, because it does not exist", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		workspaceService.deleteWorkspace(workspace);
		return ResponseEntity.noContent().build();
	}
	
	// Project

	/**
	 * Gets the project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the project
	 */
	@GetMapping(value = "/{workspace}/{project}", produces = "application/json")
	public ResponseEntity<ProjectDescriptor> getProject(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project) {

		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (!workspaceService.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in workspace {1}.", project, workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		Project projectObject = workspaceService.getProject(workspace, project);
		if (!projectObject.exists()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, project);
		}
		
		return ResponseEntity.ok(workspaceService.renderProjectTree(workspace, projectObject));
	}

	/**
	 * Creates the project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PostMapping("/{workspace}/{project}")
	public ResponseEntity<URI> createProject(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project)
			throws URISyntaxException {
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (workspaceService.existsProject(workspace, project)) {
			return new ResponseEntity<URI>(workspaceService.getURI(workspace, project, null), HttpStatus.NOT_MODIFIED);
		}

		Project projectObject = workspaceService.createProject(workspace, project);
		if (!projectObject.exists()) {
			String error = format("Failed to create project {0} in workspace {1}", project, workspace);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
		
//		publisherService.publish(workspace, project);
		
		return ResponseEntity.created(workspaceService.getURI(workspace, project, null)).build();
	}

	/**
	 * Delete project.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @return the response
	 * @throws IOException in case of exception
	 */
	@DeleteMapping("/{workspace}/{project}")
	public ResponseEntity<String> deleteProject(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project) throws IOException {
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (!workspaceService.existsProject(workspace, project)) {
			String error = format("Failed to delete project {0} in workspace {1}, because it does not exist", project, workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}
		
//		publisherService.unpublish(project);

		workspaceService.deleteProject(workspace, project);
		return ResponseEntity.noContent().build();
	}
	
	// Folders and Files

	/**
	 * Gets the file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param headerContentType the header content type
	 * @return the file
	 */
	@GetMapping("/{workspace}/{project}/{*path}")
	public ResponseEntity<?> getFile(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project,
			@PathVariable("path") String path,
			@Nullable @RequestHeader("describe") String headerContentType) {
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (!workspaceService.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in Workspace {1}.", project, workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		File file = workspaceService.getFile(workspace, project, path);
		if (!file.exists()) {
			Folder folder = workspaceService.getFolder(workspace, project, path);
			if (!folder.exists()) {
				String error = format("Path {0} in project {1} in workspace {1} does not exist", path, project, workspace);
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
			}
			final HttpHeaders httpHeaders= new HttpHeaders();
		    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity(workspaceService.renderFolderTree(workspace, folder), httpHeaders, HttpStatus.OK);
		}
		if ((headerContentType != null) && ContentTypeHelper.APPLICATION_JSON.equals(headerContentType)) {
			final HttpHeaders httpHeaders= new HttpHeaders();
		    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity(workspaceService.renderFileDescription(workspace, file), httpHeaders, HttpStatus.OK);
		}
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.valueOf(file.getContentType()));
		if (file.isBinary()) {
			return new ResponseEntity(file.getContent(), httpHeaders, HttpStatus.OK);
		}
		return new ResponseEntity(new String(file.getContent(), StandardCharsets.UTF_8), httpHeaders, HttpStatus.OK);
	}

	/**
	 * Creates the file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param content the content
	 * @param headerContentTransferEncoding the header content transfer encoding
	 * @param headerContentType the header content type
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PostMapping(value = "/{workspace}/{project}/{*path}", consumes = "application/octet-stream")
	public ResponseEntity<?> createFile(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project,
			@PathVariable("path") String path,
			@Valid @RequestBody byte[] content,
			@Nullable @RequestHeader("Content-Transfer-Encoding") String headerContentTransferEncoding, 
			@Nullable @RequestHeader("Content-Type") String headerContentType) throws URISyntaxException {
		
		if (content == null) {
			content = new byte[] {};
		}
		
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (!workspaceService.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in workspace {1}.", project, workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (path.endsWith(IRepositoryStructure.SEPARATOR)) {
			Folder folder = workspaceService.getFolder(workspace, project, path);
			if (folder.exists()) {
				String error = format("Folder {0} already exists in project {1} in workspace {2}.", path, project, workspace);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
			}

			folder = workspaceService.createFolder(workspace, project, path);
			return ResponseEntity.created(workspaceService.getURI(workspace, project, path)).build();
		}

		File file = workspaceService.getFile(workspace, project, path);
		if (file.exists()) {
			String error = format("File {0} already exists in project {1} in workspace {2}.", path, project, workspace);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
		}

		if (headerContentTransferEncoding != null && "base64".equals(headerContentTransferEncoding)) {
			content = Base64.decodeBase64(content);
		}
		file = workspaceService.createFile(workspace, project, path, content, headerContentType);
		
//		publisherService.publish(workspace, project + "/" + path);
		
		return ResponseEntity.created(workspaceService.getURI(workspace, project, path)).build();
	}

	/**
	 * Creates the file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param content the content
	 * @param headerContentTransferEncoding the header content transfer encoding
	 * @param headerContentType the header content type
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PostMapping(value = "/{workspace}/{project}/{*path}", consumes = {"text/plain"})
	public ResponseEntity<?> createFile(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project,
			@PathVariable("path") String path,
			@Nullable @RequestBody String content,
			@Nullable @RequestHeader("Content-Transfer-Encoding") String headerContentTransferEncoding, 
			@Nullable @RequestHeader("Content-Type") String headerContentType) throws URISyntaxException {
		
		if (content == null) {
			content = "";
		}
		
		return createFile(workspace, project, path, content.getBytes(StandardCharsets.UTF_8), headerContentTransferEncoding, headerContentType);
	}

	/**
	 * Update file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param content the content
	 * @return the response
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	@PutMapping(value = "/{workspace}/{project}/{*path}", consumes = "application/octet-stream")
	public ResponseEntity<URI> updateFile(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project,
			@PathVariable("path") String path,
			@Nullable @RequestBody byte[] content) throws URISyntaxException {
		
		if (content == null) {
			content = new byte[] {};
		}
		
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (!workspaceService.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in workspace {1}.", project, workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		File file = workspaceService.getFile(workspace, project, path);
		if (!file.exists()) {
			String error = format("File {0} already exists in project {1} in workspace {2}.", path, project, workspace);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
		}

		file = workspaceService.updateFile(workspace, project, path, content);
		
//		publisherService.publish(workspace, project + "/" + path);
		
		return ResponseEntity.ok(workspaceService.getURI(workspace, project, path));
	}

	/**
	 * Update file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param content the content
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 */
	@PutMapping(value = "/{workspace}/{project}/{*path}", consumes = {"text/plain"})
	public ResponseEntity<URI>  updateFile(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project,
			@PathVariable("path") String path,
			@Nullable @RequestBody String content) throws URISyntaxException {
		
		if (content == null) {
			content = "";
		}
		
		return updateFile(workspace, project, path, content.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Delete file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the response
	 * @throws URISyntaxException the URI syntax exception
	 */
	@DeleteMapping("/{workspace}/{project}/{*path}")
	public ResponseEntity<?> deleteFile(
			@PathVariable("workspace") String workspace,
			@PathVariable("project") String project,
			@PathVariable("path") String path) throws URISyntaxException {
		if (!workspaceService.existsWorkspace(workspace)) {
			String error = format("Workspace {0} does not exist.", workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		if (!workspaceService.existsProject(workspace, project)) {
			String error = format("Project {0} does not exist in workspace {1}.", project, workspace);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}

		Folder folder = workspaceService.getFolder(workspace, project, path);
		if (!folder.exists()) {
			File file = workspaceService.getFile(workspace, project, path);
			if (!file.exists()) {
				String error = format("File {0} already exists in project {1} in workspace {2}.", path, project, workspace);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
			}

			workspaceService.deleteFile(workspace, project, path);
			return ResponseEntity.noContent().build();
		}
		workspaceService.deleteFolder(workspace, project, path);
		
//		publisherService.unpublish(project + "/" + path);
		
		return ResponseEntity.noContent().build();
	}

	
	
}