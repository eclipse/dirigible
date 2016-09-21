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

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateParameterMetadata;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorker;

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
	public abstract String generate(String parameters, HttpServletRequest request) throws Exception;

	@Override
	public abstract String getTemplates(HttpServletRequest request) throws Exception;

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

	protected void checkIfRequired(GenerationModel model, String param) throws GenerationException {
		TemplateParameterMetadata[] requiredParameters = model.getTemplate().getTemplateMetadata().getParameters();
		for (TemplateParameterMetadata requiredParameter : requiredParameters) {
			if ((requiredParameter != null) && param.equals(requiredParameter.getName()) && requiredParameter.isRequired()) {
				throw new GenerationException(String.format(MANDATORY_PARAMETER_S_HAS_NOT_BEEN_PROVIDED, param));
			}
		}
	}
}
