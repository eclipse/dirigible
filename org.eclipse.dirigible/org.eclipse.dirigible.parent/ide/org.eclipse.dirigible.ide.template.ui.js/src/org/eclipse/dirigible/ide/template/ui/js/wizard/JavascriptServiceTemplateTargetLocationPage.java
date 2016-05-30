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
import org.eclipse.dirigible.ide.ui.common.validation.IValidationStatus;
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
		String ext = model.getTemplate().getExtension();
		String name = model.getTemplate().getLocation();
		String jsOrLibName = (name.endsWith("guid-generator.js")) ? "library" : "service";

		// String jsOrLibExt = ("/org/eclipse/dirigible/ide/template/ui/js/templates/guid-generator.jslib" //$NON-NLS-1$
		// .equals(model.getTemplate().getLocation())) ? "jslib" : "js"; //$NON-NLS-1$ //$NON-NLS-2$
		String jsOrLibExt = ext;
		// if ("/org/eclipse/dirigible/ide/template/ui/js/templates/ruby-service.rb" //$NON-NLS-1$
		// .equals(model.getTemplate().getLocation())) {
		// jsOrLibExt = "rb"; //$NON-NLS-1$
		// } else if ("/org/eclipse/dirigible/ide/template/ui/js/templates/groovy-service.groovy" //$NON-NLS-1$
		// .equals(model.getTemplate().getLocation())) {
		// jsOrLibExt = "groovy"; //$NON-NLS-1$
		// } else if ("/org/eclipse/dirigible/ide/template/ui/js/templates/sql-service.sql" //$NON-NLS-1$
		// .equals(model.getTemplate().getLocation())) {
		// jsOrLibExt = "sql"; //$NON-NLS-1$
		// } else if ("/org/eclipse/dirigible/ide/template/ui/js/templates/terminal-command.command" //$NON-NLS-1$
		// .equals(model.getTemplate().getLocation())) {
		// jsOrLibExt = "command"; //$NON-NLS-1$
		// } else if ("/org/eclipse/dirigible/ide/template/ui/js/templates/java-service.java_" //$NON-NLS-1$
		// .equals(model.getTemplate().getLocation())) {
		// jsOrLibExt = "java"; //$NON-NLS-1$
		// }
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
