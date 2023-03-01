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
package org.eclipse.dirigible.runtime.ide.generation.processor;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.generation.api.GenerationEnginesManager;
import org.eclipse.dirigible.core.generation.api.GenerationException;
import org.eclipse.dirigible.core.generation.api.IGenerationEngine;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateMetadata;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateMetadataSource;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateParameters;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Processing the Generation Service incoming requests.
 */
public class GenerationProcessor extends WorkspaceProcessor {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(GenerationProcessor.class);
	

	/**
	 * Generate file.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param parameters the parameters
	 * @return the list
	 * @throws ScriptingException the scripting exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<IFile> generateFile(String workspace, String project, String path, GenerationTemplateParameters parameters) throws ScriptingException, IOException {
		IWorkspace workspaceObject = getWorkspacesCoreService().getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		List<IFile> generatedFiles = new ArrayList<IFile>();
		addStandardParameters(workspace, project, path, parameters.getParameters());
		
		String wrapper = generateWrapper(parameters);
		Object metadata = ScriptEngineExecutorsManager.executeServiceCode(IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT, wrapper, null);
		if (metadata != null) {
			GenerationTemplateMetadata metadataObject = GsonHelper.fromJson(metadata.toString(), GenerationTemplateMetadata.class);
			
			for (GenerationTemplateMetadataSource source : metadataObject.getSources()) {
				String sourcePath = new RepositoryPath().append(IRepositoryStructure.PATH_REGISTRY_PUBLIC).append(source.getLocation()).build();
				IResource sourceResource = projectObject.getRepository().getResource(sourcePath);
				if (sourceResource.exists()) {
					byte[] input = sourceResource.getContent();
					if (logger.isTraceEnabled()) {logger.trace("Generating using template from the Registry: " + sourcePath);}
					generateWithTemplateIterable(parameters, projectObject, generatedFiles, source, input);
				} else {
					InputStream in = GenerationProcessor.class.getResourceAsStream("/META-INF/dirigible" + source.getLocation());
					try {
						if (in != null) {
							byte[] input = IOUtils.toByteArray(in);
							if (logger.isTraceEnabled()) {logger.trace("Generating using built-in template: " + source.getLocation());}
							generateWithTemplateIterable(parameters, projectObject, generatedFiles, source, input);
						} else {
							throw new ScriptingException(
									format("Invalid source location of [{0}] in template definition file: [{1}] or the resource does not exist",
											source.getLocation(), parameters.getTemplate()));
						} 
					} finally {
						if (in != null) {
							in.close();
						}
					}
				}
			}
			return generatedFiles;
			
		}
		
		throw new ScriptingException(format("Invalid template definition file: [{0}]", parameters.getTemplate()));
	}

	/**
	 * Generate with template iterable.
	 *
	 * @param parameters the parameters
	 * @param projectObject the project object
	 * @param generatedFiles the generated files
	 * @param source the source
	 * @param input the input
	 * @throws ScriptingException the scripting exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void generateWithTemplateIterable(GenerationTemplateParameters parameters, IProject projectObject,
			List<IFile> generatedFiles, GenerationTemplateMetadataSource source, byte[] input)
			throws ScriptingException, IOException {
		if (source.getCollection() != null) {
			List<Map<String, Object>> elements = (List<Map<String, Object>>) parameters.getParameters().get(source.getCollection());
			if (elements == null) {
				throw new ScriptingException(format("Invalid template definition file: [{0}]. Multiplicity element is set, but no actual parameter provided.", parameters.getTemplate()));
			}
			//addStandardParameters(workspace, project, path, elements);
			for (Map<String, Object> elementParameters : elements) {
				generateWithTemplate(elementParameters, projectObject, generatedFiles, source, input);
			}
		} else {
			generateWithTemplate(parameters.getParameters(), projectObject, generatedFiles, source, input);
		}
	}

	/**
	 * Generate with template.
	 *
	 * @param parameters the parameters
	 * @param projectObject the project object
	 * @param generatedFiles the generated files
	 * @param source the source
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ScriptingException the scripting exception
	 */
	private void generateWithTemplate(Map<String, Object> parameters, IProject projectObject,
			List<IFile> generatedFiles, GenerationTemplateMetadataSource source, byte[] input) throws IOException, ScriptingException {
		byte[] output = null;
		String action = source.getAction();
		parameters.put(GenerationParameters.PARAMETER_ENGINE, source.getEngine());
		parameters.put(GenerationParameters.PARAMETER_HANDLER, source.getHandler());
		if (action != null) {
			if (IGenerationEngine.ACTION_GENERATE.equals(action)) {
				String sm = null;//MUSTACHE_DEFAULT_START_SYMBOL;
				String em = null;//MUSTACHE_DEFAULT_END_SYMBOL;
				if (source.getStart() != null && source.getEnd() != null) {
					sm = source.getStart();
					em = source.getEnd();
				}
				String engine = IGenerationEngine.GENERATION_ENGINE_DEFAULT;
				if (source.getEngine() != null) {
					engine = source.getEngine();
				}
				output = generateContent(parameters, source.getLocation(), input, sm, em, engine);
			} else if (IGenerationEngine.ACTION_COPY.equals(action)) {
				output = input;
			} else {
				throw new ScriptingException(format("Invalid action in template definition: [{0}]", action));
			}
		} else {
			output = input;
		}
		
		String generatedFileName; 
		String rename = source.getRename();
		if (rename != null) {
			generatedFileName = generateName(parameters, source.getLocation() + "-name", rename);
		} else {
			generatedFileName = new RepositoryPath().append(source.getLocation()).getLastSegment();
		}
		String generatedFilePath = new RepositoryPath().append(parameters.get("packagePath").toString()).append(generatedFileName).build();
		String contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(generatedFileName));
		boolean isBinary = ContentTypeHelper.isBinary(contentType);
		IFile fileObject = projectObject.createFile(generatedFilePath, output, isBinary, contentType);
		generatedFiles.add(fileObject);
	}

	/**
	 * Generate wrapper.
	 *
	 * @param parameters the parameters
	 * @return the string
	 */
	private String generateWrapper(GenerationTemplateParameters parameters) {
		String wrapper = new StringBuilder()
			.append("var template = require('")
			.append(parameters.getTemplate())
			.append("');JSON.stringify(template.getTemplate(")
			.append(new Gson().toJson(parameters.getParameters()) + "));").toString();
		return wrapper;
	}

	/**
	 * Adds the standard parameters.
	 *
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param parameters the parameters
	 */
	private void addStandardParameters(String workspace, String project, String path,
			Map<String, Object> parameters) {
		RepositoryPath filePath = new RepositoryPath().append(path);
		RepositoryPath packagePath = new RepositoryPath().append(path).getParentPath();
		String fileName = filePath.getLastSegment();
		String fileNameExt = FilenameUtils.getExtension(fileName);
		String fileNameBase = FilenameUtils.getBaseName(fileName);
		
		parameters.put(GenerationParameters.PARAMETER_WORKSPACE_NAME, workspace);
		parameters.put(GenerationParameters.PARAMETER_PROJECT_NAME, project);
		parameters.put(GenerationParameters.PARAMETER_FILE_NAME, fileName);
		parameters.put(GenerationParameters.PARAMETER_FILE_NAME_EXT, fileNameExt);
		parameters.put(GenerationParameters.PARAMETER_FILE_NAME_BASE, fileNameBase);
		parameters.put(GenerationParameters.PARAMETER_FILE_PATH, filePath.build());
		parameters.put(GenerationParameters.PARAMETER_PACKAGE_PATH, packagePath.build());
	}

	/**
	 * Generate content.
	 *
	 * @param parameters the parameters
	 * @param location the location
	 * @param input the input
	 * @param sm the sm
	 * @param em the em
	 * @param engine the engine type
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] generateContent(Map<String, Object> parameters, String location,
			byte[] input, String sm, String em, String engine) throws IOException {
		List<IGenerationEngine> generationEngines = GenerationEnginesManager.getGenerationEngines();
		for (IGenerationEngine next : generationEngines) {
			if (next.getName().equals(engine)) {
				return next.generate(parameters, location, input, sm, em);
			}
		}
		throw new GenerationException("Generation Engine not available: " + engine);
	}
	
	/**
	 * Generate name.
	 *
	 * @param parameters the parameters
	 * @param location the location
	 * @param input the input
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String generateName(Map<String, Object> parameters, String location, String input) throws IOException {
		List<IGenerationEngine> generationEngines = GenerationEnginesManager.getGenerationEngines();
		for (IGenerationEngine next : generationEngines) {
			if (next.getName().equals(IGenerationEngine.GENERATION_ENGINE_DEFAULT)) {
				return new String(next.generate(parameters, location, input.getBytes()));
			}
		}
		throw new GenerationException("Generation Engine not available: " + IGenerationEngine.GENERATION_ENGINE_DEFAULT);
	}

}
