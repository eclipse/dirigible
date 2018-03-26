/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.ide.generation.processor;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModel;
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModelEntity;
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModelProperty;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateMetadata;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateMetadataSource;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateModelParameters;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateParameters;
import org.eclipse.dirigible.runtime.ide.workspaces.processor.WorkspaceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

/**
 * Processing the Generation Service incoming requests.
 */
public class GenerationProcessor extends WorkspaceProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(GenerationProcessor.class);

	private static final String ACTION_COPY = "copy";
	
	private static final String ACTION_GENERATE = "generate";
	
	private static final String MUSTACHE_DEFAULT_START_SYMBOL = "{{";
	
	private static final String MUSTACHE_DEFAULT_END_SYMBOL = "}}";
	
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
			GenerationTemplateMetadata metadataObject = GsonHelper.GSON.fromJson(metadata.toString(), GenerationTemplateMetadata.class);
			
			for (GenerationTemplateMetadataSource source : metadataObject.getSources()) {
				String sourcePath = new RepositoryPath().append(IRepositoryStructure.PATH_REGISTRY_PUBLIC).append(source.getLocation()).build();
				IResource sourceResource = projectObject.getRepository().getResource(sourcePath);
				if (sourceResource.exists()) {
					byte[] input = sourceResource.getContent();
					logger.trace("Generating using template from the Registry: " + sourcePath);
					generateWithTemplateIterable(parameters, projectObject, generatedFiles, source, input);
				} else {
					InputStream in = GenerationProcessor.class.getResourceAsStream(source.getLocation());
					try {
						if (in != null) {
							byte[] input = IOUtils.toByteArray(in);
							logger.trace("Generating using built-in template: " + source.getLocation());
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

	private void generateWithTemplateIterable(GenerationTemplateParameters parameters, IProject projectObject,
			List<IFile> generatedFiles, GenerationTemplateMetadataSource source, byte[] input)
			throws ScriptingException, IOException {
		if (source.getCollection() != null) {
			List<Map<String, Object>> elements = (List<Map<String, Object>>) parameters.getParameters().get(source.getCollection());
			if (elements == null) {
				throw new ScriptingException(format("Invalid template definition file: [{0}]. Multiplicity element is set, but no actual parameter provided.", parameters.getTemplate()));
			}
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
		if (action != null) {
			if (ACTION_GENERATE.equals(action)) {
				String sm = MUSTACHE_DEFAULT_START_SYMBOL;
				String em = MUSTACHE_DEFAULT_END_SYMBOL;
				if (source.getStart() != null && source.getEnd() != null) {
					sm = source.getStart();
					em = source.getEnd();
				}
				output = generateContent(parameters, source.getLocation(), input, sm, em);
			} else if (ACTION_COPY.equals(action)) {
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
			.append("');JSON.stringify(template.getTemplate());").toString();
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
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] generateContent(Map<String, Object> parameters, String location,
			byte[] input, String sm, String em) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
		DefaultMustacheFactory defaultMustacheFactory = new DefaultMustacheFactory();
		Mustache mustache = defaultMustacheFactory.compile(new InputStreamReader(new ByteArrayInputStream(input), StandardCharsets.UTF_8), location, sm, em);
		mustache.execute(writer, parameters);
		writer.flush();
		return baos.toByteArray();
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
	private String generateName(Map<String, Object> parameters, String location,
			String input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
		DefaultMustacheFactory defaultMustacheFactory = new DefaultMustacheFactory();
		Mustache mustache = defaultMustacheFactory.compile(new StringReader(input), location);
		mustache.execute(writer, parameters);
		writer.flush();
		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}

	/**
	 * Generate a full-stack application based on the provided entity data model
	 * 
	 * @param model the model
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @param parameters the parameters
	 * @return the list
	 * @throws ScriptingException the scripting exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<IFile> generateModel(IFile model, String workspace, String project, String path,
			GenerationTemplateModelParameters parameters) throws ScriptingException, IOException {
		IWorkspace workspaceObject = getWorkspacesCoreService().getWorkspace(workspace);
		IProject projectObject = workspaceObject.getProject(project);
		List<IFile> generatedFiles = new ArrayList<IFile>();
		addStandardParameters(workspace, project, path, parameters.getParameters());
		
		String wrapper = generateWrapper(parameters);
		Object metadata = ScriptEngineExecutorsManager.executeServiceCode(IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT, wrapper, null);
		if (metadata != null) {
			GenerationTemplateMetadata metadataObject = GsonHelper.GSON.fromJson(metadata.toString(), GenerationTemplateMetadata.class);
			EntityDataModel entityDataModel = GsonHelper.GSON.fromJson(new String(model.getContent(), StandardCharsets.UTF_8), EntityDataModel.class);
			
			List<Map<String, Object>> dataModels = mapDataModels(entityDataModel, parameters, workspace, project, path);
			parameters.getParameters().put("dataModels", dataModels);
			List<Map<String, Object>> uiModels = mapUiModels(entityDataModel, workspace, project, path);
			parameters.getParameters().put("uiModels", uiModels);
			List<Map<String, Object>> uiManageModels = new ArrayList<>();
			List<Map<String, Object>> uiListModels = new ArrayList<>();
			List<Map<String, Object>> uiDisplayModels = new ArrayList<>();
			distributeByLayoutType(uiModels, parameters, uiManageModels, uiListModels, uiDisplayModels);
			parameters.getParameters().put("uiManageModels", uiManageModels);
			parameters.getParameters().put("uiListModels", uiListModels);
			parameters.getParameters().put("uiDisplayModels", uiDisplayModels);
			for (GenerationTemplateMetadataSource source : metadataObject.getSources()) {
				String sourcePath = new RepositoryPath().append(IRepositoryStructure.PATH_REGISTRY_PUBLIC).append(source.getLocation()).build();
				IResource sourceResource = projectObject.getRepository().getResource(sourcePath);
				if (sourceResource.exists()) {
					byte[] input = sourceResource.getContent();
					logger.trace("Generating using template from the Registry: " + sourcePath);
					generateWithTemplateIterable(parameters, projectObject, generatedFiles, source, input);
				} else {
					InputStream in = GenerationProcessor.class.getResourceAsStream(source.getLocation());
					try {
						if (in != null) {
							byte[] input = IOUtils.toByteArray(in);
							logger.trace("Generating using built-in template: " + source.getLocation());
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

	private void distributeByLayoutType(List<Map<String, Object>> uiModels, GenerationTemplateModelParameters parameters, List<Map<String, Object>> uiManageModels,
			List<Map<String, Object>> uiListModels, List<Map<String, Object>> uiDisplayModels) {
		uiManageModels.add(parameters.getParameters());
		uiListModels.add(parameters.getParameters());
		uiDisplayModels.add(parameters.getParameters());
		for (Map<String, Object> uiModel : uiModels) {
			Object layoutType = uiModel.get("layoutType");
			if ("MANAGE".equals(layoutType)) {
				uiManageModels.add(uiModel);
			} else if ("LIST".equals(layoutType)) {
				uiListModels.add(uiModel);
			} else if ("DISPLAY".equals(layoutType)) {
				uiDisplayModels.add(uiModel);
			}
		}
	}

	private List<Map<String, Object>> mapDataModels(EntityDataModel entityDataModel, GenerationTemplateModelParameters parameters, String workspace, String project, String path) {
		List<Map<String, Object>> dataModels = new ArrayList<>();
		for (EntityDataModelEntity entity : entityDataModel.getModel().getEntities()) {
			Map<String, Object> dataModel = new HashMap<String, Object>();
			RepositoryPath localPath = new RepositoryPath().append(path).getParentPath().append(entity.getName());
			addStandardParameters(workspace, project, localPath.build(), dataModel);
			dataModel.putAll(parameters.getParameters());
			dataModel.put("name", entity.getName());
			dataModel.put("dataName", entity.getDataName());
			List<Map<String, Object>> propertiesModels = new ArrayList<>();
			for (EntityDataModelProperty property : entity.getProperties()) {
				Map<String, Object> propertyModel = new HashMap<String, Object>();
				propertyModel.put("name", property.getName());
				propertyModel.put("dataName", property.getDataName());
				propertyModel.put("dataPrimaryKey", property.getDataPrimaryKey());
				propertyModel.put("dataIdentity", property.getDataAutoIncrement());
				propertyModel.put("dataDefaultValue", property.getDataDefaultValue());
				propertyModel.put("dataLength", property.getDataLength());
				propertyModel.put("dataNullable", !property.getDataNotNull());
				propertyModel.put("dataPrecision", property.getDataPrecision());
				propertyModel.put("dataScale", property.getDataScale());
				propertyModel.put("dataType", property.getDataType());
				propertyModel.put("dataUnique", property.getDataUnique());
				propertiesModels.add(propertyModel);
			}
			dataModel.put("properties", propertiesModels);
			dataModels.add(dataModel);
		}
		return dataModels;
	}
	
	private List<Map<String, Object>> mapUiModels(EntityDataModel entityDataModel, String workspace, String project, String path) {
		List<Map<String, Object>> dataModels = new ArrayList<>();
		for (EntityDataModelEntity entity : entityDataModel.getModel().getEntities()) {
			Map<String, Object> uiModel = new HashMap<String, Object>();
			RepositoryPath localPath = new RepositoryPath().append(path).getParentPath().append(entity.getName());
			addStandardParameters(workspace, project, localPath.build(), uiModel);
			uiModel.put("name", entity.getName());
			uiModel.put("layoutType", entity.getLayoutType());
			uiModel.put("menuKey", entity.getMenuKey());
			uiModel.put("menuLabel", entity.getMenuLabel());
			List<Map<String, Object>> propertiesModels = new ArrayList<>();
			for (EntityDataModelProperty property : entity.getProperties()) {
				Map<String, Object> propertyModel = new HashMap<String, Object>();
				propertyModel.put("name", property.getName());
				propertyModel.put("widgetLength", property.getWidgetLength());
				propertyModel.put("widgetPattern", property.getWidgetPattern());
				propertyModel.put("widgetService", property.getWidgetService());
				propertyModel.put("widgetType", property.getWidgetType());
				propertiesModels.add(propertyModel);
			}
			uiModel.put("properties", propertiesModels);
			dataModels.add(uiModel);
		}
		return dataModels;
	}


}
