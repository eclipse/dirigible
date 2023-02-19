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
package org.eclipse.dirigible.components.bpm.flowable.service;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.components.ide.workspace.domain.File;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.util.io.InputStreamSource;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Processing the BPM UI Service incoming requests.
 */
@Service
public class BpmService {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(BpmService.class);

	/** The workspace service. */
	private WorkspaceService workspaceService;
	
	@Autowired
	public BpmService(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}
	
	public WorkspaceService getWorkspaceService() {
		return workspaceService;
	}

	/**
	 * Gets the model.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the model
	 * @throws JsonProcessingException the json processing exception
	 */
	public ObjectNode getModel(String workspace, String project, String path) throws JsonProcessingException {
		BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
		File file = getWorkspaceService().getWorkspace(workspace).getProject(project).getFile(path);
		if (file.exists()) {
			BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(new InputStreamSource(new ByteArrayInputStream(file.getContent())), true, true);
			BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
			ObjectNode objectNode = bpmnJsonConverter.convertToJson(bpmnModel);
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.set("model", objectNode);
			rootNode.set("modelId", JsonNodeFactory.instance.textNode(workspace + IRepository.SEPARATOR + project + IRepository.SEPARATOR + path));
			rootNode.set("name", JsonNodeFactory.instance.textNode(bpmnModel.getProcesses().get(0).getName()));
			rootNode.set("key", JsonNodeFactory.instance.textNode(bpmnModel.getProcesses().get(0).getId()));
			rootNode.set("description", JsonNodeFactory.instance.textNode(bpmnModel.getProcesses().get(0).getDocumentation()));
			rootNode.set("lastUpdated", JsonNodeFactory.instance.textNode(file.getInformation().getModifiedAt() + ""));
			rootNode.set("lastUpdatedBy", JsonNodeFactory.instance.textNode(file.getInformation().getModifiedBy()));
//			String json = objectMapper.writeValueAsString(rootNode);
//			return json;
			return rootNode;
		} else {
			throw new RepositoryNotFoundException(format("The requested BPMN file does not exist in workspace: [{0}], project: [{1}] and path: [{2}]", workspace, project, path));
		}
	}
	
	/**
	 * Save model.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param payload the payload
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void saveModel(String workspace, String project, String path, String payload) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode modelNode = objectMapper.readTree(payload);
        BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
		BpmnModel bpmnModel = bpmnJsonConverter.convertToBpmnModel(modelNode);
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] bytes = bpmnXMLConverter.convertToXML(bpmnModel);
        File file = getWorkspaceService().getWorkspace(workspace).getProject(project).getFile(path);
        if (!file.exists()) {
        	file.create();
        }
        file.setContent(bytes);
	}
	
	/**
	 * Gets the stencil set.
	 *
	 * @return the stencil set
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JsonNode getStencilSet() throws IOException {
		InputStream in = BpmService.class.getResourceAsStream("/stencilset_bpmn.json");
		try {
			byte[] content = IOUtils.toByteArray(in);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode modelNode = objectMapper.readTree(content);
//			return new String(content, StandardCharsets.UTF_8);
			return modelNode;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

}
