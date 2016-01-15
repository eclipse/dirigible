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

import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTargetLocationPage;
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
import org.eclipse.dirigible.repository.api.ICommonConstants;

public class HtmlForEntityTemplateTargetLocationPage extends TemplateTargetLocationPage {

	private static final String EXT_HTML = ".html";

	private static final String PAGE_NAME_HTML = "page_name.html";

	private static final String SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME = Messages.HtmlForEntityTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlForEntityTemplateTargetLocationPage"; //$NON-NLS-1$

	private HtmlForEntityTemplateModel model;

	protected HtmlForEntityTemplateTargetLocationPage(HtmlForEntityTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(Messages.HtmlForEntityTemplateTargetLocationPage_TARGET_LOCATION);
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
		return (preset == null) ? PAGE_NAME_HTML : CommonUtils.getFileNameNoExtension(preset) + EXT_HTML;
	}

	@Override
	protected String getArtifactContainerName() {
		return ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

}
