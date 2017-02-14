/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.js.service;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateType;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeDiscriminator;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.js.wizard.JavascriptServiceTemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.js.wizard.JavascriptServiceTemplateModel;
import org.eclipse.dirigible.repository.api.IRepository;

import com.google.gson.JsonObject;

public class ScriptingGenerationWorker extends AbstractGenerationWorker {

	private static final String PARAM_TABLE_TYPE = "tableType";
	private static final String PARAM_TABLE_NAME = "tableName";

	private static final JavascriptServiceTemplateModel model = new JavascriptServiceTemplateModel();
	private static final JavascriptServiceTemplateGenerator generator = new JavascriptServiceTemplateGenerator(model);
	private static final TemplateTypeDiscriminator typeDiscriminator = new ScriptingServiceTemplateTypeDiscriminator();

	public ScriptingGenerationWorker(IRepository repository, IWorkspace workspace) {
		super(repository, workspace);
	}

	@Override
	protected void readAndSetExtraParametersToModel(JsonObject parametersObject, GenerationModel model, TemplateType[] templates)
			throws GenerationException {
		JavascriptServiceTemplateModel jsModel = (JavascriptServiceTemplateModel) model;
		// table name
		if (parametersObject.has(PARAM_TABLE_NAME)) {
			jsModel.setTableName(parametersObject.get(PARAM_TABLE_NAME).getAsString());
		} else {
			checkIfRequired(model, PARAM_TABLE_NAME);
		}

		// table type
		if (parametersObject.has(PARAM_TABLE_TYPE)) {
			jsModel.setTableType(parametersObject.get(PARAM_TABLE_TYPE).getAsString());
		} else {
			checkIfRequired(model, PARAM_TABLE_TYPE);
		}
	}

	@Override
	protected TemplateTypeDiscriminator getTypeDiscriminator() {
		return this.typeDiscriminator;
	}

	@Override
	protected GenerationModel getTemplateModel() {
		return this.model;
	}

	@Override
	protected TemplateGenerator getTemplateGenerator() {
		return this.generator;
	}
}
