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
package org.eclipse.dirigible.runtime.ide.bpm.processor;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.service.WorkspacesCoreService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.common.engine.impl.util.io.InputStreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Processing the BPM UI Service incoming requests.
 */
public class BpmProcessor {

	private static final Logger logger = LoggerFactory.getLogger(BpmProcessor.class);

	private WorkspacesCoreService workspacesCoreService = new WorkspacesCoreService();

	public String getModel(String workspace, String project, String path) throws JsonProcessingException {
		BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
		IFile file = workspacesCoreService.getWorkspace(workspace).getProject(project).getFile(path);
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
			String json = objectMapper.writeValueAsString(rootNode);
			return json;
		} else {
			throw new RepositoryNotFoundException(format("The requested BPMN file does not exist in workspace: [{0}], project: [{1}] and path: [{2}]", workspace, project, path));
		}
	}
	
	public void saveModel(String workspace, String project, String path, String payload) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode modelNode = objectMapper.readTree(payload);
        BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
		BpmnModel bpmnModel = bpmnJsonConverter.convertToBpmnModel(modelNode);
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] bytes = bpmnXMLConverter.convertToXML(bpmnModel);
        IFile file = workspacesCoreService.getWorkspace(workspace).getProject(project).getFile(path);
        if (!file.exists()) {
        	file.create();
        }
        file.setContent(bytes);
	}
	
	public String getStencilSet() throws IOException {
		InputStream in = BpmProcessor.class.getResourceAsStream("/stencilset_bpmn.json");
		try {
			byte[] content = IOUtils.toByteArray(in);
			return new String(content, StandardCharsets.UTF_8);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

}
