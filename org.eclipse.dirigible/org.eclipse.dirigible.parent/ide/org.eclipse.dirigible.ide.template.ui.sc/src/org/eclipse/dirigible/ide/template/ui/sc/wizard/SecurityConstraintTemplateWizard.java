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

package org.eclipse.dirigible.ide.template.ui.sc.wizard;

import org.eclipse.core.resources.IResource;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;

public class SecurityConstraintTemplateWizard extends TemplateWizard {

	private static final String CREATE_ACCESS_FILE = Messages.SecurityConstraintTemplateWizard_CREATE_ACCESS_FILE;
	private final SecurityConstraintTemplateModel model;
	private final SecurityConstraintTemplateTypePage typesPage;
	private final SecurityConstraintTemplateTargetLocationPage targetLocationPage;

	public SecurityConstraintTemplateWizard(IResource resource) {
		setWindowTitle(CREATE_ACCESS_FILE);

		model = new SecurityConstraintTemplateModel();
		model.setSourceResource(resource);
		typesPage = new SecurityConstraintTemplateTypePage(model);
		targetLocationPage = new SecurityConstraintTemplateTargetLocationPage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(targetLocationPage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		SecurityConstraintTemplateGenerator generator = new SecurityConstraintTemplateGenerator(
				model);
		return generator;
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String openEditorForFileWithExtension() {
		return CommonParameters.SECURITY_EXTENSION;
	}

	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(String.format(
					StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED, model.getFileName()));
		}
		return result;
	}

}
