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
import org.eclipse.dirigible.ide.template.ui.common.TemplateTargetLocationPage;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;

public class HtmlTemplateTargetLocationPage extends TemplateTargetLocationPage {

	private static final String EXT_HTML = ".html"; //$NON-NLS-1$

	private static final String PAGE_NAME_HTML = "page_name.html"; //$NON-NLS-1$

	private static final String INDEX_HTML = "index.html"; //$NON-NLS-1$

	private static final String SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME = Messages.HtmlTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;

	private static final String TARGET_LOCATION = Messages.HtmlTemplateTargetLocationPage_TARGET_LOCATION;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.html.wizard.HtmlTemplateTargetLocationPage"; //$NON-NLS-1$

	private HtmlTemplateModel model;

	protected HtmlTemplateTargetLocationPage(HtmlTemplateModel model) {
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
		if ((getModel().getFileName() == null) || "".equals(getModel().getFileName())) { //$NON-NLS-1$
			String pageName = PAGE_NAME_HTML;
			if (model.getTemplate().getLocation().endsWith(INDEX_HTML)) {
				pageName = INDEX_HTML;
			}
			return pageName;
		}
		return (preset == null) ? getModel().getFileName() : CommonUtils.getFileNameNoExtension(preset) + EXT_HTML;

	}

	@Override
	protected boolean isForcedFileName() {
		return true;
	}

	@Override
	protected String getArtifactContainerName() {
		return ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

}
