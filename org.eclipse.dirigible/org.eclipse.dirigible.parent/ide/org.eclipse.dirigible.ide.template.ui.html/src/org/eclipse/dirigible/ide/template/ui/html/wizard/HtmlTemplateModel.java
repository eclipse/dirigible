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

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.repository.api.ICommonConstants;

@SuppressWarnings("javadoc")
public class HtmlTemplateModel extends GenerationModel {

	private static final String TARGET_LOCATION_IS_NOT_ALLOWED = Messages.HtmlTemplateModel_TARGET_LOCATION_IS_NOT_ALLOWED;

	public String pageTitle;

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	@Override
	protected String getArtifactType() {
		return ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

	@Override
	protected String getTargetLocationErrorMessage() {
		return TARGET_LOCATION_IS_NOT_ALLOWED;
	}
}
