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
import org.eclipse.dirigible.ide.template.ui.common.ContentForEntityGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeDiscriminator;
import org.eclipse.dirigible.ide.template.ui.common.table.ContentForEntityModel;
import org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlForEntityTemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlForEntityTemplateModel;
import org.eclipse.dirigible.repository.api.IRepository;

public class WebContentEntityGenerationWorker extends ContentForEntityGenerationWorker {

	private static final TemplateTypeDiscriminator typeDiscriminator = new WebContentTemplateTypeDiscriminator();
	private static final ContentForEntityModel model = new HtmlForEntityTemplateModel();
	private static final HtmlForEntityTemplateGenerator generator = new HtmlForEntityTemplateGenerator(model);

	public WebContentEntityGenerationWorker(IRepository repository, IWorkspace workspace) {
		super(repository, workspace);
	}

	@Override
	protected TemplateGenerator getTemplateGenerator() {
		return this.generator;
	}

	@Override
	protected TemplateTypeDiscriminator getTypeDiscriminator() {
		return this.typeDiscriminator;
	}

	@Override
	protected GenerationModel getTemplateModel() {
		return this.model;
	}
}
