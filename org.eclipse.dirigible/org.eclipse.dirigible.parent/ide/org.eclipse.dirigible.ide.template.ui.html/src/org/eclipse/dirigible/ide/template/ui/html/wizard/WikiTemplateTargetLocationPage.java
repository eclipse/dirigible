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

public class WikiTemplateTargetLocationPage extends TemplateTargetLocationPage {

	private static final long serialVersionUID = 11784306852854208L;

	private static final String SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME = Messages.HtmlTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;

	private static final String TARGET_LOCATION = Messages.HtmlTemplateTargetLocationPage_TARGET_LOCATION;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.html.wizard.WikiTemplateTargetLocationPage"; //$NON-NLS-1$

	private WikiTemplateModel model;

	protected WikiTemplateTargetLocationPage(WikiTemplateModel model) {
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
	protected boolean isForcedFileName() {
		return true;
	}

	@Override
	protected String getDefaultFileName(String preset) {
		String templateLocation = model.getTemplateLocation();
		String defaultName = null;
		String defaultExt = null;
		if (templateLocation.endsWith("confluence")) {
			defaultExt = ".confluence";
			defaultName = (preset == null) ? "wiki_page.confluence" : CommonUtils.getFileNameNoExtension(preset) + defaultExt;
		} else if (templateLocation.endsWith("md")) {
			defaultExt = ".md";
			defaultName = (preset == null) ? "wiki_page.md" : CommonUtils.getFileNameNoExtension(preset) + defaultExt;
		} else if (templateLocation.endsWith("textile")) {
			defaultExt = ".textile";
			defaultName = (preset == null) ? "wiki_page.textile" : CommonUtils.getFileNameNoExtension(preset) + defaultExt;
		} else if (templateLocation.endsWith("tracwiki")) {
			defaultExt = ".tracwiki";
			defaultName = (preset == null) ? "wiki_page.tracwiki" : CommonUtils.getFileNameNoExtension(preset) + defaultExt;
		} else if (templateLocation.endsWith("twiki")) {
			defaultExt = ".twiki";
			defaultName = (preset == null) ? "wiki_page.twiki" : CommonUtils.getFileNameNoExtension(preset) + defaultExt;
		} else {
			defaultExt = ".html";
		}
		// return defaultName;
		return (preset == null) ? defaultName : CommonUtils.getFileNameNoExtension(preset) + defaultExt;
	}

	@Override
	protected String getArtifactContainerName() {
		return ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;
	}

}
