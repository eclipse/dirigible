/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.service;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateType;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeDiscriminator;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlTemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlTemplateModel;
import org.eclipse.dirigible.repository.api.IRepository;

import com.google.gson.JsonObject;

public class WebContentGenerationWorker extends AbstractGenerationWorker {

	private static final String PARAM_PAGE_TITLE = "pageTitle";

	private static final HtmlTemplateModel model = new HtmlTemplateModel();
	private static final HtmlTemplateGenerator generator = new HtmlTemplateGenerator(model);
	private static final TemplateTypeDiscriminator typeDiscriminator = new WebContentEntityTemplateTypeDiscriminator();

	public WebContentGenerationWorker(IRepository repository, IWorkspace workspace) {
		super(repository, workspace);
	}

	@Override
	protected void readAndSetExtraParametersToModel(JsonObject parametersObject, GenerationModel model, TemplateType[] templates)
			throws GenerationException {
		HtmlTemplateModel htmlModel = (HtmlTemplateModel) model;
		// page title
		if (parametersObject.has(PARAM_PAGE_TITLE)) {
			htmlModel.setPageTitle(parametersObject.get(PARAM_PAGE_TITLE).getAsString());
		} else {
			checkIfRequired(htmlModel, PARAM_PAGE_TITLE);
		}
	}

	@Override
	protected GenerationModel getTemplateModel() {
		return this.model;
	}

	@Override
	protected TemplateGenerator getTemplateGenerator() {
		return this.generator;
	}

	@Override
	protected TemplateTypeDiscriminator getTypeDiscriminator() {
		return this.typeDiscriminator;
	}
}
