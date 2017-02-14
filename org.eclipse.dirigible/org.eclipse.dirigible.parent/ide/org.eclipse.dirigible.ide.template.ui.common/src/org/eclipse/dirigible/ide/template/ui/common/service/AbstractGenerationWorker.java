/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common.service;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dirigible.ide.common.status.LogProgressMonitor;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateParameterMetadata;
import org.eclipse.dirigible.ide.template.ui.common.TemplateType;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeDiscriminator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypesEnumerator;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorker;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class AbstractGenerationWorker implements IGenerationWorker {

	protected static final String PARAM_PACKAGE_NAME = "packageName";
	protected static final String PARAM_PROJECT_NAME = "projectName";
	protected static final String PARAM_FILE_NAME = "fileName";
	protected static final String PARAM_TEMPLATE_TYPE = "templateType";

	protected static final String GENERATION_PASSED_SUCCESSFULLY = "Generation passed successfully.";
	protected static final String MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED = "Mandatory parameter [%s] has not been provided";
	protected static final String TEMPLATE_S_DOES_NOT_EXIST_IN_THIS_INSTANCE = "Template %s does not exist in this instance";

	private IRepository repository;

	private IWorkspace workspace;

	public AbstractGenerationWorker(IRepository repository, IWorkspace workspace) {
		this.repository = repository;
		this.workspace = workspace;
	}

	@Override
	public String generate(String parameters, HttpServletRequest request) throws GenerationException {
		try {
			JsonElement parametersElement = new JsonParser().parse(parameters);
			JsonObject parametersObject = parametersElement.getAsJsonObject();

			TemplateType[] templates = TemplateTypesEnumerator.prepareTemplateTypes(getTypeDiscriminator().getTemplatesPath(),
					getTypeDiscriminator().getCategory(), request);

			setParametersToModel(parametersObject, getTemplateModel(), templates);

			getTemplateGenerator().generate(request);

		} catch (Exception e) {
			throw new GenerationException(e);
		}
		return GENERATION_PASSED_SUCCESSFULLY;
	}

	protected void setParametersToModel(JsonObject parametersObject, GenerationModel model, TemplateType[] templates) throws GenerationException {
		// template type
		if (parametersObject.has(PARAM_TEMPLATE_TYPE)) {
			String templateType = parametersObject.get(PARAM_TEMPLATE_TYPE).getAsString();
			for (TemplateType template : templates) {
				if (template.getLocation().substring(getTypeDiscriminator().getTemplatesPath().length())
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
			if (!project.exists()) {
				try {
					project.create(new LogProgressMonitor());
				} catch (CoreException e) {
					throw new GenerationException(e);
				}
			}
			model.setTargetContainer(project.getFullPath().toString());
		} else {
			throw new GenerationException(String.format(MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED, PARAM_PROJECT_NAME));
		}

		// package name
		if (parametersObject.has(PARAM_PACKAGE_NAME)) {
			String packageName = parametersObject.get(PARAM_PACKAGE_NAME).getAsString();
			model.setProjectPackageName(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES + IRepository.SEPARATOR + packageName);
			model.setPackageName(packageName);
		} else {
			throw new GenerationException(String.format(MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED, PARAM_PACKAGE_NAME));
		}

		readAndSetExtraParametersToModel(parametersObject, model, templates);
	}

	protected abstract void readAndSetExtraParametersToModel(JsonObject parametersObject, GenerationModel model, TemplateType[] templates)
			throws GenerationException;

	protected abstract GenerationModel getTemplateModel();

	protected abstract TemplateGenerator getTemplateGenerator();

	protected abstract TemplateTypeDiscriminator getTypeDiscriminator();

	@Override
	public String getTemplates(HttpServletRequest request) throws GenerationException {
		try {
			TemplateType[] templates = TemplateTypesEnumerator.prepareTemplateTypes(getTypeDiscriminator().getTemplatesPath(),
					getTypeDiscriminator().getCategory(), request);
			String result = new Gson().toJson(templates);
			return result;
		} catch (Exception e) {
			throw new GenerationException(e);
		}
	}

	public IRepository getRepository() {
		return this.repository;
	}

	public IWorkspace getWorkspace() {
		return this.workspace;
	}

	protected boolean isBuiltInParameter(String param) {
		return (PARAM_PACKAGE_NAME.equals(param) || PARAM_PROJECT_NAME.equals(param) || PARAM_FILE_NAME.equals(param)
				|| PARAM_TEMPLATE_TYPE.equals(param));
	}

	public static void checkIfRequired(GenerationModel model, String param) throws GenerationException {
		TemplateParameterMetadata[] requiredParameters = model.getTemplate().getTemplateMetadata().getParameters();
		for (TemplateParameterMetadata requiredParameter : requiredParameters) {
			if ((requiredParameter != null) && param.equals(requiredParameter.getName()) && requiredParameter.isRequired()) {
				throw new GenerationException(String.format(MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED, param));
			}
		}
	}
}
