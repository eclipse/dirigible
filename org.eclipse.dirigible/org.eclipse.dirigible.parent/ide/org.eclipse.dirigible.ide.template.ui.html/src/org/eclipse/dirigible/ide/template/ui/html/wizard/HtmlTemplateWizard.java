/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;

public class HtmlTemplateWizard extends TemplateWizard {

	private static final String CREATE_USER_INTERFACE = Messages.HtmlTemplateWizard_CREATE_USER_INTERFACE;
	private final HtmlTemplateModel model;
	private final HtmlTemplateTypePage typesPage;
	private final HtmlTemplateTargetLocationPage targetLocationPage;
	private final HtmlTemplateTitlePage titlePage;

	public HtmlTemplateWizard(IResource resource) {
		setWindowTitle(CREATE_USER_INTERFACE);
		model = new HtmlTemplateModel();
		model.setSourceResource(resource);
		typesPage = new HtmlTemplateTypePage(model);
		titlePage = new HtmlTemplateTitlePage(model);
		targetLocationPage = new HtmlTemplateTargetLocationPage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(titlePage);
		addPage(targetLocationPage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		HtmlTemplateGenerator generator = new HtmlTemplateGenerator(model);
		return generator;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(String.format(StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED, model.getFileName()));
		}
		return result;
	}
}
