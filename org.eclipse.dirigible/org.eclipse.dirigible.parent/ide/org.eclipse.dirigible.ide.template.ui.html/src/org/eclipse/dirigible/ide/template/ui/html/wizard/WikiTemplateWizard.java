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

public class WikiTemplateWizard extends TemplateWizard {

	private static final String CREATE_WIKI_PAGE = Messages.WikiTemplateWizard_CREATE_WIKI_PAGE;
	private final WikiTemplateModel model;
	private final WikiTemplateTypePage typesPage;
	private final WikiTemplateTargetLocationPage targetLocationPage;
	private final WikiTemplateTitlePage titlePage;

	public WikiTemplateWizard(IResource resource) {
		setWindowTitle(CREATE_WIKI_PAGE);
		model = new WikiTemplateModel();
		model.setSourceResource(resource);
		typesPage = new WikiTemplateTypePage(model);
		titlePage = new WikiTemplateTitlePage(model);
		targetLocationPage = new WikiTemplateTargetLocationPage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(titlePage);
		addPage(targetLocationPage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		WikiTemplateGenerator generator = new WikiTemplateGenerator(model);
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
