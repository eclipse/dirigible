/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.ed.wizard;

import org.eclipse.core.resources.IResource;

import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;

public class ExtensionDefinitionTemplateWizard extends TemplateWizard {

	private static final String CREATE_EXTENSION_FILE = Messages.ExtensionDefinitionTemplateWizard_CREATE_EXTENSION_FILE;
	private final ExtensionDefinitionTemplateModel model;
	private final ExtensionDefinitionTemplateTypePage typesPage;
	private final ExtensionDefinitionTemplateTargetLocationPage targetLocationPage;

	public ExtensionDefinitionTemplateWizard(IResource resource) {
		setWindowTitle(CREATE_EXTENSION_FILE);

		model = new ExtensionDefinitionTemplateModel();
		model.setSourceResource(resource);
		typesPage = new ExtensionDefinitionTemplateTypePage(model);
		targetLocationPage = new ExtensionDefinitionTemplateTargetLocationPage(
				model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(targetLocationPage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		ExtensionDefinitionTemplateGenerator generator = new ExtensionDefinitionTemplateGenerator(
				model);
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
			StatusLineManagerUtil.setInfoMessage(String.format(
					StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED,
					model.getFileName()));
		}
		return result;
	}

}
