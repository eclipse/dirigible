/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.mobile.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;

/**
 * Wizard for mobile templates
 */
public class MobileTemplateWizard extends TemplateWizard {

	private static final String CREATE_MOBILE_APP = Messages.MobileTemplateWizard_CREATE_MOBILE_APP;
	private final MobileTemplateModel model;
	private final MobileTemplateTypePage typesPage;
	private final MobileTemplateTargetLocationPage targetLocationPage;

	/**
	 * Constructor
	 * 
	 * @param resource
	 */
	public MobileTemplateWizard(IResource resource) {
		setWindowTitle(CREATE_MOBILE_APP);
		model = new MobileTemplateModel();
		model.setSourceResource(resource);
		typesPage = new MobileTemplateTypePage(model);
		targetLocationPage = new MobileTemplateTargetLocationPage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(targetLocationPage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		return new MobileTemplateGenerator(model);
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
