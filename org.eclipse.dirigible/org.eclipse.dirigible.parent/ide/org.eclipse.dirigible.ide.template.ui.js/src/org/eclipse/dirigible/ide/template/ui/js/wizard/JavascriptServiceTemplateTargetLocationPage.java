/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.js.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTargetLocationPage;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;

public class JavascriptServiceTemplateTargetLocationPage extends TemplateTargetLocationPage {

	private static final long serialVersionUID = 1L;

	private static final String SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME = Messages.JavascriptServiceTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;

	private static final String TARGET_LOCATION = Messages.JavascriptServiceTemplateTargetLocationPage_TARGET_LOCATION;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.js.wizard.JavascriptServiceTemplateTargetLocationPage"; //$NON-NLS-1$

	private JavascriptServiceTemplateModel model;

	protected JavascriptServiceTemplateTargetLocationPage(JavascriptServiceTemplateModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(TARGET_LOCATION);
		setDescription(SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME);
	}

	@Override
	protected GenerationModel getModel() {
		return model;
	}

	@Override
	protected String getDefaultFileName(String preset) {
		String ext = model.getTemplate().getExtension();
		String name = model.getTemplate().getLocation();
		String jsOrLibName = (name.endsWith("guid-generator.js")) ? "library" : "service";

		String jsOrLibExt = ext;
		return (preset == null) ? jsOrLibName + "_name." + jsOrLibExt : CommonUtils.getFileNameNoExtension(preset) + "." + jsOrLibExt; //$NON-NLS-1$
	}

	@Override
	protected boolean isForcedFileName() {
		return true;
	}

	@Override
	protected String getArtifactContainerName() {
		return ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}
}
