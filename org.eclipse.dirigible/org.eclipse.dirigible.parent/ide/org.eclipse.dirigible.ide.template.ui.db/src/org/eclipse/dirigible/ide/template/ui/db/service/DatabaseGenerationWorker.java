/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.TemplateType;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypesEnumerator;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.db.wizard.ColumnDefinition;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateModel;
import org.eclipse.dirigible.ide.template.ui.db.wizard.DataStructureTemplateTypeDiscriminator;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DatabaseGenerationWorker extends AbstractGenerationWorker {

	private static final String PARAM_ROWS = "rows";
	private static final String PARAM_QUERY = "query";
	private static final String PARAM_COLUMNS = "columns";

	public DatabaseGenerationWorker(IRepository repository, IWorkspace workspace) {
		super(repository, workspace);
	}

	@Override
	public String generate(String parameters, HttpServletRequest request) throws GenerationException {
		try {
			JsonElement parametersElement = new JsonParser().parse(parameters);
			JsonObject parametersObject = parametersElement.getAsJsonObject();

			DataStructureTemplateModel model = new DataStructureTemplateModel();
			DataStructureTemplateGenerator generator = new DataStructureTemplateGenerator(model);

			TemplateType[] templates = TemplateTypesEnumerator.prepareTemplateTypes(DataStructureTemplateTypeDiscriminator.getTemplatesPath(),
					DataStructureTemplateTypeDiscriminator.getCategory(), request);

			setParametersToModel(parametersObject, model, templates);

			generator.generate(request);

		} catch (Exception e) {
			throw new GenerationException(e);
		}
		return GENERATION_PASSED_SUCCESSFULLY;
	}

	protected void setParametersToModel(JsonObject parametersObject, DataStructureTemplateModel model, TemplateType[] templates)
			throws GenerationException {

		// template type
		if (parametersObject.has(PARAM_TEMPLATE_TYPE)) {
			String templateType = parametersObject.get(PARAM_TEMPLATE_TYPE).getAsString();
			for (TemplateType template : templates) {
				if (template.getLocation().substring(DataStructureTemplateTypeDiscriminator.getTemplatesPath().length())
						.indexOf(templateType + ICommonConstants.SEPARATOR) == 0) {
					model.setTemplate(template);
					break;
				}
			}
			if (model.getTemplate() == null) {
				throw new GenerationException(String.format(TEMPLATE_S_DOES_NOT_EXIST_IN_THIS_INSTANCE, templateType));
			}
		} else {
			throw new GenerationException(String.format(MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED, PARAM_TEMPLATE_TYPE));
		}

		// file name
		if (parametersObject.has(PARAM_FILE_NAME)) {
			model.setFileName(parametersObject.get(PARAM_FILE_NAME).getAsString());
		} else {
			throw new GenerationException(String.format(MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED, PARAM_FILE_NAME));
		}

		// project name
		if (parametersObject.has(PARAM_PROJECT_NAME)) {
			String projectName = parametersObject.get(PARAM_PROJECT_NAME).getAsString();
			IProject project = getWorkspace().getRoot().getProject(projectName);
			if (project.exists()) {
				model.setTargetContainer(project.getFullPath().toString());
			}
		} else {
			throw new GenerationException(String.format(MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED, PARAM_PROJECT_NAME));
		}

		// package name
		if (parametersObject.has(PARAM_PACKAGE_NAME)) {
			model.setPackageName(
					ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES + IRepository.SEPARATOR + parametersObject.get(PARAM_PACKAGE_NAME).getAsString());
		} else {
			throw new GenerationException(String.format(MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED, PARAM_PACKAGE_NAME));
		}

		// columns
		if (parametersObject.has(PARAM_COLUMNS)) {
			List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
			JsonArray columns = parametersObject.get(PARAM_COLUMNS).getAsJsonArray();
			Iterator<JsonElement> iter = columns.iterator();
			while (iter.hasNext()) {
				JsonElement columnElement = iter.next();
				if (columnElement.isJsonObject()) {
					JsonObject columnObject = columnElement.getAsJsonObject();
					ColumnDefinition columnDefinition = new ColumnDefinition();
					columnDefinition.setName(columnObject.get("name").getAsString());
					columnDefinition.setType(columnObject.get("type").getAsString());
					columnDefinition.setLength(columnObject.get("length").getAsInt());
					columnDefinition.setPrimaryKey(columnObject.get("primaryKey").getAsBoolean());
					columnDefinition.setNotNull(columnObject.get("notNull").getAsBoolean());
					columnDefinition.setDefaultValue(columnObject.get("defaultValue").getAsString());
					columnDefinitions.add(columnDefinition);
				}
			}

			model.setColumnDefinitions(columnDefinitions.toArray(new ColumnDefinition[] {}));
		} else {
			checkIfRequired(model, PARAM_COLUMNS);
		}

		// query
		if (parametersObject.has(PARAM_QUERY)) {
			model.setQuery(parametersObject.get(PARAM_QUERY).getAsString());
		} else {
			checkIfRequired(model, PARAM_QUERY);
		}

		// query
		if (parametersObject.has(PARAM_ROWS)) {
			String rows = parametersObject.get(PARAM_ROWS).getAsString();
			model.setDsvSampleRows(rows.split((rows.indexOf("\n") >= 0) ? "\n" : "\r"));
		} else {
			checkIfRequired(model, PARAM_ROWS);
		}
	}

	@Override
	public String enumerateTemplates(HttpServletRequest request) throws GenerationException {
		try {
			TemplateType[] templates = TemplateTypesEnumerator.prepareTemplateTypes(DataStructureTemplateTypeDiscriminator.getTemplatesPath(),
					DataStructureTemplateTypeDiscriminator.getCategory(), request);
			String result = new Gson().toJson(templates);
			return result;
		} catch (Exception e) {
			throw new GenerationException(e);
		}
	}

}
