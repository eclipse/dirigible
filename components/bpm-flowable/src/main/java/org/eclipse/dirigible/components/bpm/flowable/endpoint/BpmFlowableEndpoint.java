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
package org.eclipse.dirigible.components.bpm.flowable.endpoint;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.bpm.flowable.provider.BpmProviderFlowable;
import org.eclipse.dirigible.components.bpm.flowable.service.BpmService;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Front facing REST service serving the BPM related resources and operations.
 */
@CrossOrigin
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_IDE + "bpm")
public class BpmFlowableEndpoint extends BaseEndpoint {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(BpmFlowableEndpoint.class);
	
	/** The bpm provider flowable. */
	@Autowired
	private BpmProviderFlowable bpmProviderFlowable;
	
	/** The bpm service. */
	@Autowired
	private BpmService bpmService;
	
	/** The workspace service. */
	@Autowired
	private WorkspaceService workspaceService;

	
	/**
	 * Gets the bpm provider flowable.
	 *
	 * @return the bpm provider flowable
	 */
	public BpmProviderFlowable getBpmProviderFlowable() {
		return bpmProviderFlowable;
	}
	
	/**
	 * Gets the bpm service.
	 *
	 * @return the bpm service
	 */
	public BpmService getBpmService() {
		return bpmService;
	}
	
	/**
	 * Gets the workspace service.
	 *
	 * @return the workspace service
	 */
	public WorkspaceService getWorkspaceService() {
		return workspaceService;
	}
	
	/**
	 * Get the BPM model source.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the response
	 * @throws JsonProcessingException exception
	 */
	@GetMapping(value = "/models/{workspace}/{project}/{*path}", produces = "application/json")
	public ResponseEntity<ObjectNode> getModel(
			@PathVariable("workspace") String workspace, 
			@PathVariable("project") String project, 
			@PathVariable("path") String path) throws JsonProcessingException {

		path = sanitizePath(path);

		ObjectNode model = getBpmService().getModel(workspace, project, path);
		
		if (model == null) {
			String error = format("Model in workspace: {0} and project {1} with path {2} does not exist.", workspace, project, path);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}
		return ResponseEntity.ok(model);
	}

	private String sanitizePath(String path) {
		if (path.indexOf("?") > 0) {
			path = path.substring(0, path.indexOf("?"));
		} else if (path.indexOf("&") > 0) {
			path = path.substring(0, path.indexOf("&"));
		} else if (path.indexOf("/") == 0) {
			path = path.substring(1);
		}
		return path;
	}
	
	/**
	 * Save the BPM model source.
	 *
	 * @param workspace            the workspace
	 * @param project            the project
	 * @param path            the path
	 * @param payload the payload
	 * @return the response
	 * @throws URISyntaxException in case of an error
	 * @throws IOException exception
	 */
	@PostMapping(value = "/models/{workspace}/{project}/{*path}", produces = "application/json")
	public ResponseEntity<URI> saveModel(
			@PathVariable("workspace") String workspace, 
			@PathVariable("project") String project, 
			@PathVariable("path") String path, 
			@RequestParam("json_xml") String payload) throws URISyntaxException, IOException {
		
		path = sanitizePath(path);
		
		getBpmService().saveModel(workspace, project, path, payload);
		
		return ResponseEntity.ok(getWorkspaceService().getURI(workspace, project, path));
	}

	
	/**
	 * Get the Stencil-Set.
	 *
	 * @return the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GetMapping(value = "/stencil-sets", produces = "application/json")
	public ResponseEntity<JsonNode> getStencilSet() throws IOException {

		JsonNode stencilSets = getBpmService().getStencilSet();
		
		if (stencilSets == null) {
			String error = "Stencil Sets definition does not exist.";
			throw new RepositoryNotFoundException(error);
		}
		return ResponseEntity.ok(stencilSets);
	}

	/**
	 * Gets the processes keys.
	 *
	 * @return the processes keys
	 */
	@GetMapping(value = "/bpm-processes/keys")
	public ResponseEntity<List<String>> getProcessesKeys() {
		ProcessEngine processEngine = ((ProcessEngine) getBpmProviderFlowable().getProcessEngine());

		List<String> keys = processEngine
				.getRepositoryService()
				.createProcessDefinitionQuery()
				.list()
				.stream()
				.map(ProcessDefinition::getKey)
				.collect(Collectors.toList());

		return ResponseEntity.ok(keys);
	}

	/**
	 * Gets the process image.
	 *
	 * @param processDefinitionKey the process definition key
	 * @return the process image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GetMapping(value = "/bpm-processes/{processDefinitionKey}/image", produces = "image/png")
	public ResponseEntity<byte[]> getProcessImage(
			@PathVariable("processDefinitionKey") String processDefinitionKey
	) throws IOException {
		ProcessEngine processEngine = ((ProcessEngine) getBpmProviderFlowable().getProcessEngine());
		RepositoryService repositoryService = processEngine.getRepositoryService();

		ProcessDefinition process = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey)
				.latestVersion()
				.singleResult();

		String deploymentId = process.getDeploymentId();
		String diagramResourceName = process.getDiagramResourceName();

		byte[] imageBytes = repositoryService
				.getResourceAsStream(deploymentId, diagramResourceName)
				.readAllBytes();

		return ResponseEntity.ok(imageBytes);
	}

}
