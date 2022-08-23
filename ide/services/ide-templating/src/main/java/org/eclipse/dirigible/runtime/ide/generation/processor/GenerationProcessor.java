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
package org.eclipse.dirigible.runtime.ide.generation.processor;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.utils.EscapeFacade;
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
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModel;
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModelComposition;
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModelEntity;
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModelPerspective;
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModelProperty;
import org.eclipse.dirigible.runtime.ide.generation.model.entity.EntityDataModelSidebarItem;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateMetadata;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateMetadataSource;
import org.eclipse.dirigible.runtime.ide.generation.model.template.GenerationTemplateModelParameters;
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
			GenerationTemplateMetadata metadataObject = GsonHelper.GSON.fromJson(metadata.toString(), GenerationTemplateMetadata.class);
			
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

	/**
	 * Generate a full-stack application based on the provided entity data model.
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
		
		if (model.getName().endsWith(".model")) {
			IWorkspace workspaceObject = getWorkspacesCoreService().getWorkspace(workspace);
			IProject projectObject = workspaceObject.getProject(project);
			List<IFile> generatedFiles = new ArrayList<IFile>();
			addStandardParameters(workspace, project, path, parameters.getParameters());
			
			String wrapper = generateWrapper(parameters);
			Object metadata = ScriptEngineExecutorsManager.executeServiceCode(IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT, wrapper, null);
			if (metadata != null) {
				GenerationTemplateMetadata metadataObject = GsonHelper.GSON.fromJson(metadata.toString(), GenerationTemplateMetadata.class);
				
				EntityDataModel entityDataModel = GsonHelper.GSON.fromJson(new String(model.getContent(), StandardCharsets.UTF_8), EntityDataModel.class);
				
				List<Map<String, Object>> models = mapModels(entityDataModel, parameters, workspace, project, path);
				parameters.getParameters().put("models", models);
				
				Map<String, Map<String, Object>> perspectives = mapPerspectives(entityDataModel, parameters, workspace, project, path);
				parameters.getParameters().put("perspectives", models);
				
				Map<String, Map<String, Object>> sidebar = mapSidebar(entityDataModel, parameters, workspace, project, path);
				parameters.getParameters().put("sidebar", models);
				
				distributeByLayoutType(models, perspectives, sidebar, parameters);
				
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
		} else {
			return generateFile(workspace, project, path, parameters);
		}
	}

	/**
	 * Distribute by layout type.
	 *
	 * @param models the models
	 * @param perspectives the perspectives
	 * @param sidebar the sidebar
	 * @param parameters the parameters
	 */
	private void distributeByLayoutType(List<Map<String, Object>> models, Map<String, Map<String, Object>> perspectives, 
			Map<String, Map<String, Object>> sidebar, GenerationTemplateModelParameters parameters) {
		
		List<Map<String, Object>> uiPrimaryModels = new ArrayList<>();
		List<Map<String, Object>> uiReportModels = new ArrayList<>();
		List<Map<String, Object>> uiManageModels = new ArrayList<>();
		List<Map<String, Object>> uiListModels = new ArrayList<>();
		List<Map<String, Object>> uiManageMasterModels = new ArrayList<>();
		List<Map<String, Object>> uiListMasterModels = new ArrayList<>();
		List<Map<String, Object>> uiManageDetailsModels = new ArrayList<>();
		List<Map<String, Object>> uiListDetailsModels = new ArrayList<>();
		
		List<Map<String, Object>> uiReportTableModels = new ArrayList<>();
		List<Map<String, Object>> uiReportBarsModels = new ArrayList<>();
		List<Map<String, Object>> uiReportLinesModels = new ArrayList<>();
		List<Map<String, Object>> uiReportPieModels = new ArrayList<>();
		
		List<Map<String, Object>> uiPerspectives = new ArrayList<>();
		Set<String> perspectiveCheck = new HashSet<String>();
		
		for (Map<String, Object> model : models) {
			Object layoutType = model.get("layoutType");
			Boolean isPrimary = model.get("type") != null ? "PRIMARY".equals(model.get("type").toString()) : false;
			Boolean isReport = model.get("type") != null ? "REPORT".equals(model.get("type").toString()) : false;
			if (isPrimary) {
				uiPrimaryModels.add(model);
			} else if(isReport) {
				uiReportModels.add(model);
			}
			if ("MANAGE".equals(layoutType) && isPrimary) {
				uiManageModels.add(model);
			} else if ("LIST".equals(layoutType) && isPrimary) {
				uiListModels.add(model);
			} else if ("MANAGE_MASTER".equals(layoutType) && isPrimary) {
				uiManageMasterModels.add(model);
			} else if ("LIST_MASTER".equals(layoutType) && isPrimary) {
				uiListMasterModels.add(model);
			} else if ("MANAGE_DETAILS".equals(layoutType) && !isPrimary && !isReport) {
				uiManageDetailsModels.add(model);
			} else if ("LIST_DETAILS".equals(layoutType) && !isPrimary && !isReport) {
				uiListDetailsModels.add(model);
			} else if ("REPORT_TABLE".equals(layoutType) && isReport) {
				uiReportTableModels.add(model);
			} else if ("REPORT_BAR".equals(layoutType) && isReport) {
				uiReportBarsModels.add(model);
			} else if ("REPORT_LINE".equals(layoutType) && isReport) {
				uiReportLinesModels.add(model);
			} else if ("REPORT_PIE".equals(layoutType) && isReport) {
				uiReportPieModels.add(model);
			}

			String perspectiveName = model.get("perspectiveName").toString();
			if (perspectiveName != null && !perspectiveCheck.contains(perspectiveName)) {
				Map<String, Object> uiPerspective = new HashMap<String, Object>();
				
				Map<String, Object> perspectiveParameters = perspectives.get(perspectiveName);
				if (perspectiveParameters != null) {
					uiPerspective.put("perspectiveName", perspectiveParameters.get("name"));
					uiPerspective.put("perspectiveLabel", perspectiveParameters.get("label"));
					uiPerspective.put("perspectiveIcon", perspectiveParameters.get("icon"));
					uiPerspective.put("perspectiveOrder", perspectiveParameters.get("order"));
				} else {
					uiPerspective.put("perspectiveName", perspectiveName);
					uiPerspective.put("perspectiveLabel", perspectiveName);
					uiPerspective.put("perspectiveIcon", model.get("perspectiveIcon"));
					uiPerspective.put("perspectiveOrder", model.get("perspectiveOrder"));
				}
				
				uiPerspective.put("launchpadName", model.get("launchpadName"));
				uiPerspective.put("extensionName", model.get("extensionName"));
				uiPerspective.put("brand", model.get("brand"));
				uiPerspective.put(GenerationParameters.PARAMETER_WORKSPACE_NAME, model.get(GenerationParameters.PARAMETER_WORKSPACE_NAME));
				uiPerspective.put(GenerationParameters.PARAMETER_PROJECT_NAME, model.get(GenerationParameters.PARAMETER_PROJECT_NAME));
				uiPerspective.put(GenerationParameters.PARAMETER_FILE_NAME, model.get(GenerationParameters.PARAMETER_FILE_NAME));
				uiPerspective.put(GenerationParameters.PARAMETER_FILE_NAME_EXT, model.get(GenerationParameters.PARAMETER_FILE_NAME_EXT));
				uiPerspective.put(GenerationParameters.PARAMETER_FILE_NAME_BASE, model.get(GenerationParameters.PARAMETER_FILE_NAME_BASE));
				uiPerspective.put(GenerationParameters.PARAMETER_FILE_PATH, model.get(GenerationParameters.PARAMETER_FILE_PATH));
				uiPerspective.put(GenerationParameters.PARAMETER_PACKAGE_PATH, model.get(GenerationParameters.PARAMETER_PACKAGE_PATH));
				uiPerspective.putAll(parameters.getParameters());

				uiPerspectives.add(uiPerspective);
				perspectiveCheck.add(perspectiveName);
			}
		}
		parameters.getParameters().put("uiPrimaryModels", uiPrimaryModels);
		parameters.getParameters().put("uiReportModels", uiReportModels);
		parameters.getParameters().put("uiManageModels", uiManageModels);
		parameters.getParameters().put("uiListModels", uiListModels);
		parameters.getParameters().put("uiManageMasterModels", uiManageMasterModels);
		parameters.getParameters().put("uiListMasterModels", uiListMasterModels);
		parameters.getParameters().put("uiManageDetailsModels", uiManageDetailsModels);
		parameters.getParameters().put("uiListDetailsModels", uiListDetailsModels);
		
		parameters.getParameters().put("uiReportTableModels", uiReportTableModels);
		parameters.getParameters().put("uiReportBarsModels", uiReportBarsModels);
		parameters.getParameters().put("uiReportLinesModels", uiReportLinesModels);
		parameters.getParameters().put("uiReportPieModels", uiReportPieModels);
		
		parameters.getParameters().put("uiPerspectives", uiPerspectives);
	}

	/**
	 * Map models.
	 *
	 * @param entityDataModel the entity data model
	 * @param parameters the parameters
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the list
	 */
	private List<Map<String, Object>> mapModels(EntityDataModel entityDataModel, GenerationTemplateModelParameters parameters, String workspace, String project, String path) {
		List<Map<String, Object>> models = new ArrayList<>();
		for (EntityDataModelEntity entity : entityDataModel.getModel().getEntities()) {
			Map<String, Object> model = new HashMap<String, Object>();
			RepositoryPath localPath = new RepositoryPath().append(path).getParentPath().append(entity.getName());
			model.putAll(parameters.getParameters());
			addStandardParameters(workspace, project, localPath.build(), model);
			model.put("name", entity.getName());
			model.put("type", entity.getType());
			model.put("dataName", entity.getDataName());
			model.put("dataCount", entity.getDataCount());
			model.put("dataQuery", entity.getDataQuery());
			model.put("layoutType", entity.getLayoutType());
			model.put("title", entity.getTitle());
			model.put("tooltip", entity.getTooltip());
			model.put("icon", entity.getIcon());
			model.put("menuKey", entity.getMenuKey());
			model.put("menuLabel", entity.getMenuLabel());
			model.put("menuIndex", entity.getMenuIndex());
			model.put("perspectiveName", entity.getPerspectiveName());
			model.put("perspectiveIcon", entity.getPerspectiveIcon());
			model.put("perspectiveOrder", entity.getPerspectiveOrder());
			model.put("feedUrl", entity.getFeedUrl());
			model.put("feedUsername", entity.getFeedUsername());
			model.put("feedPassword", entity.getFeedPassword());
			model.put("feedSchedule", entity.getFeedSchedule());
			model.put("feedPath", entity.getFeedPath());
			model.put("roleRead", entity.getRoleRead());
			model.put("roleWrite", entity.getRoleWrite());
			model.put("projectionReferencedModel", entity.getProjectionReferencedModel());
			model.put("projectionReferencedEntity", entity.getProjectionReferencedEntity());
			List<Map<String, Object>> propertiesModels = new ArrayList<>();
			for (EntityDataModelProperty property : entity.getProperties()) {
				Map<String, Object> propertyModel = new HashMap<String, Object>();
				propertyModel.put("name", property.getName());
				propertyModel.put("isCalculatedProperty", property.getIsCalculatedProperty());
				propertyModel.put("calculatedPropertyExpression", EscapeFacade.unescapeHtml4(property.getCalculatedPropertyExpression()));

				propertyModel.put("dataName", property.getDataName());
				propertyModel.put("dataPrimaryKey", property.getDataPrimaryKey());
				propertyModel.put("dataAutoIncrement", property.getDataAutoIncrement());
				propertyModel.put("dataDefaultValue", property.getDataDefaultValue());
				propertyModel.put("dataLength", property.getDataLength());
				propertyModel.put("dataNullable", property.getDataNullable());
				propertyModel.put("dataNotNull", property.getDataNullable() == null ? null : !property.getDataNullable());
				propertyModel.put("dataPrecision", property.getDataPrecision());
				propertyModel.put("dataScale", property.getDataScale());
				propertyModel.put("dataType", property.getDataType());
				propertyModel.put("dataUnique", property.getDataUnique());
				
				propertyModel.put("relationshipType", property.getRelationshipType());
				propertyModel.put("relationshipCardinality", property.getRelationshipCardinality());
				propertyModel.put("relationshipName", property.getRelationshipName());
				propertyModel.put("relationshipEntityName", property.getRelationshipEntityName());
				propertyModel.put("relationshipEntityPerspectiveName", property.getRelationshipEntityPerspectiveName());
				
				propertyModel.put("widgetLength", property.getWidgetLength());
				propertyModel.put("widgetPattern", property.getWidgetPattern());
				propertyModel.put("widgetService", property.getWidgetService());
				propertyModel.put("widgetType", property.getWidgetType());
				propertyModel.put("widgetLabel", property.getWidgetLabel());
				propertyModel.put("widgetIsMajor", property.getWidgetIsMajor());
				propertyModel.put("widgetSection", property.getWidgetSection());
				propertyModel.put("widgetShortLabel", property.getWidgetShortLabel());
				propertyModel.put("widgetFormat", property.getWidgetFormat());
				propertyModel.put("widgetDropDownKey", property.getWidgetDropDownKey());
				propertyModel.put("widgetDropDownValue", property.getWidgetDropDownValue());

				propertyModel.put("feedPropertyName", property.getFeedPropertyName());

				propertyModel.put("roleRead", property.getRoleRead());
				propertyModel.put("roleWrite", property.getRoleWrite());

				propertiesModels.add(propertyModel);
			}
			model.put("properties", propertiesModels);
			
			List<Map<String, Object>> compositionsModels = new ArrayList<>();
			for (EntityDataModelComposition composition : entity.getCompositions()) {
				Map<String, Object> compositionModel = new HashMap<String, Object>();
				compositionModel.put("entityName", composition.getEntityName());
				compositionModel.put("entityProperty", composition.getEntityProperty());
				compositionModel.put("localProperty", composition.getLocalProperty());
				
				compositionsModels.add(compositionModel);
			}
			model.put("compositions", compositionsModels);
			
			models.add(model);
		}
		return models;
	}

	/**
	 * Map perspectives.
	 *
	 * @param entityDataModel the entity data model
	 * @param parameters the parameters
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the map
	 */
	private Map<String, Map<String, Object>> mapPerspectives(EntityDataModel entityDataModel, GenerationTemplateModelParameters parameters, String workspace, String project, String path) {
		Map<String, Map<String, Object>> perspectives = new HashMap<>();
		for (EntityDataModelPerspective perspective : entityDataModel.getModel().getPerspectives()) {
			Map<String, Object> model = new HashMap<String, Object>();
			RepositoryPath localPath = new RepositoryPath().append(path).getParentPath().append(perspective.getName());
			model.putAll(parameters.getParameters());
			addStandardParameters(workspace, project, localPath.build(), model);
			model.put("name", perspective.getName());
			model.put("label", perspective.getLabel());
			model.put("icon", perspective.getIcon());
			model.put("order", perspective.getOrder());
			perspectives.put(perspective.getName(), model);
		}
		return perspectives;
	}
	
	/**
	 * Map sidebar.
	 *
	 * @param entityDataModel the entity data model
	 * @param parameters the parameters
	 * @param workspace the workspace
	 * @param project the project
	 * @param path the path
	 * @return the map
	 */
	private Map<String, Map<String, Object>> mapSidebar(EntityDataModel entityDataModel, GenerationTemplateModelParameters parameters, String workspace, String project, String path) {
		Map<String, Map<String, Object>> sidebar = new HashMap<>();
		for (EntityDataModelSidebarItem item : entityDataModel.getModel().getSidebar()) {
			Map<String, Object> model = new HashMap<String, Object>();
			RepositoryPath localPath = new RepositoryPath().append(path).getParentPath().append(item.getPath());
			model.putAll(parameters.getParameters());
			addStandardParameters(workspace, project, localPath.build(), model);
			model.put("path", item.getPath());
			model.put("label", item.getLabel());
			model.put("icon", item.getIcon());
			model.put("url", item.getUrl());
			sidebar.put(item.getPath(), model);
		}
		return sidebar;
	}

}
