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

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTargetLocationPage;
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;

/**
 * Wizard page for IntegrationService template target location
 */
public class IntegrationServiceTemplateTargetLocationPage extends TemplateTargetLocationPage {

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.is.wizard.IntegrationServiceTemplateTargetLocationPage"; //$NON-NLS-1$

	private static final String SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME = Messages.IntegrationServiceTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;
	private static final String TARGET_LOCATION = Messages.IntegrationServiceTemplateTargetLocationPage_TARGET_LOCATION;

	private static final String FILE_JOB_NAME = "job_name.job"; //$NON-NLS-1$
	private static final String FILE_FLOW_NAME = "flow_name.flow"; //$NON-NLS-1$
	private static final String FILE_LISTENER_NAME = "listener_name.listener"; //$NON-NLS-1$

	private static final String EXT_JOB = ".job"; //$NON-NLS-1$
	private static final String EXT_FLOW = ".flow"; //$NON-NLS-1$
	private static final String EXT_LISTENER = ".listener"; //$NON-NLS-1$

	private IntegrationServiceTemplateModel model;

	protected IntegrationServiceTemplateTargetLocationPage(IntegrationServiceTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(TARGET_LOCATION);
		setDescription(SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME);
	}

	@Override
	protected void checkPageStatus() {
		if ((getModel().getTargetLocation() == null) || "".equals(getModel().getTargetLocation())) { //$NON-NLS-1$
			setPageComplete(false);
			return;
		}
		if ((getModel().getFileName() == null) || "".equals(getModel().getFileName())) { //$NON-NLS-1$
			setPageComplete(false);
			return;
		}
		IValidationStatus status = model.validateLocation();
		if (status.hasErrors()) {
			setErrorMessage(status.getMessage());
			setPageComplete(false);
		} else if (status.hasWarnings()) {
			setErrorMessage(status.getMessage());
			setPageComplete(true);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getDefaultFileName(String preset) {
		String defaultName = FILE_FLOW_NAME;
		if (model.getTemplate().getLocation().endsWith(EXT_FLOW)) {
			defaultName = (preset == null) ? FILE_FLOW_NAME : CommonUtils.getFileNameNoExtension(preset) + EXT_FLOW;
		} else if (model.getTemplate().getLocation().endsWith(EXT_JOB)) {
			defaultName = (preset == null) ? FILE_JOB_NAME : CommonUtils.getFileNameNoExtension(preset) + EXT_JOB;
		} else if (model.getTemplate().getLocation().endsWith(EXT_LISTENER)) {
			defaultName = (preset == null) ? FILE_LISTENER_NAME : CommonUtils.getFileNameNoExtension(preset) + EXT_LISTENER;
		}
		return defaultName;
	}

	@Override
	protected boolean isForcedFileName() {
		return true;
	}

	@Override
	protected String getArtifactContainerName() {
		return ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
	}

}
