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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.ide.workspace.domain.Workspace;
import org.eclipse.dirigible.components.ide.workspace.json.WorkspaceDescriptor;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_IDE + "workspaces")
public class WorkspaceEndpoint {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(WorkspaceEndpoint.class);
	
	/** The websocket service. */
    @Autowired
    private WorkspaceService workspaceService;
    
    /**
	 * List workspaces.
	 *
	 * @return the list of workspaces names
	 */
	@GetMapping
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
	@GetMapping("{workspace}")
	public ResponseEntity<WorkspaceDescriptor> getWorkspace(@PathVariable("workspace") String workspace) {
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
	@PostMapping
	public ResponseEntity<URI> createWorkspace(@RequestBody String workspace) throws URISyntaxException {
		if (workspaceService.existsWorkspace(workspace)) {
			return new ResponseEntity(HttpStatus.NOT_MODIFIED);
		}

		Workspace workspaceObject = workspaceService.createWorkspace(workspace);
		if (!workspaceObject.exists()) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, workspace);
		}
		
		return ResponseEntity.created(workspaceService.getURI(workspace, null, null)).build();
	}
	
	/**
	 * Delete workspace.
	 *
	 * @param workspace the workspace
	 * @return the response
	 */
	@DeleteMapping
	public ResponseEntity<String> deleteWorkspace(@RequestBody String workspace) {
		if (!workspaceService.existsWorkspace(workspace)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, workspace);
		}

		workspaceService.deleteWorkspace(workspace);
		return ResponseEntity.noContent().build();
	}

}
