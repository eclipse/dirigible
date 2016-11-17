/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.is.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;

public class IntegrationServiceTemplateWizard extends TemplateWizard {

	private static final String CREATE_INTEGRATION_SERVICE = Messages.IntegrationServiceTemplateWizard_CREATE_INTEGRATION_SERVICE;
	private final IntegrationServiceTemplateModel model;
	private final IntegrationServiceTemplateTypePage typesPage;
	private final IntegrationServiceTemplateTargetLocationPage targetLocationPage;
	private final IntegrationServiceTemplateServiceParametersPage serviceEndpointPage;

	public IntegrationServiceTemplateWizard(IResource resource) {
		setWindowTitle(CREATE_INTEGRATION_SERVICE);
		model = new IntegrationServiceTemplateModel();
		model.setSourceResource(resource);
		typesPage = new IntegrationServiceTemplateTypePage(model);
		serviceEndpointPage = new IntegrationServiceTemplateServiceParametersPage(model);
		targetLocationPage = new IntegrationServiceTemplateTargetLocationPage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(targetLocationPage);
		addPage(serviceEndpointPage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		IntegrationServiceTemplateGenerator generator = new IntegrationServiceTemplateGenerator(model);
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
