/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.tc.wizard;

import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTargetLocationPage;
import org.eclipse.dirigible.repository.ext.utils.CommonUtils;

public class TestCaseTemplateTargetLocationPage extends TemplateTargetLocationPage {

	private static final String SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME = Messages.TestCaseTemplateTargetLocationPage_SELECT_THE_TARGET_LOCATION_AND_THE_TARGET_FILE_NAME;

	private static final String TARGET_LOCATION = Messages.TestCaseTemplateTargetLocationPage_TARGET_LOCATION;

	private static final long serialVersionUID = 5413819137031452222L;

	private static final String PAGE_NAME = "org.eclipse.dirigible.ide.template.ui.tc.wizard.TestCaseTemplateTargetLocationPage"; //$NON-NLS-1$

	private TestCaseTemplateModel model;

	protected TestCaseTemplateTargetLocationPage(TestCaseTemplateModel model) {
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
		String defaultName = (preset == null) ? "test_case.js" : CommonUtils.getFileNameNoExtension(preset) + ".js"; //$NON-NLS-1$
		return defaultName;
	}

}
