/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.tc.wizard;

import org.eclipse.core.resources.IResource;

import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateWizard;

public class TestCaseTemplateWizard extends TemplateWizard {

	private static final String CREATE_TEST_CASE = Messages.TestCaseTemplateWizard_CREATE_TEST_CASE;
	private final TestCaseTemplateModel model;
	private final TestCaseTemplateTypePage typesPage;
	private final TestCaseTemplateTargetLocationPage targetLocationPage;

	public TestCaseTemplateWizard(IResource resource) {
		setWindowTitle(CREATE_TEST_CASE);

		model = new TestCaseTemplateModel();
		model.setSourceResource(resource);
		typesPage = new TestCaseTemplateTypePage(model);
		targetLocationPage = new TestCaseTemplateTargetLocationPage(model);
	}

	@Override
	public void addPages() {
		addPage(typesPage);
		addPage(targetLocationPage);
	}

	@Override
	public TemplateGenerator getTemplateGenerator() {
		TestCaseTemplateGenerator generator = new TestCaseTemplateGenerator(
				model);
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
			StatusLineManagerUtil.setInfoMessage(String.format(
					StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED,
					model.getFileName()));
		}
		return result;
	}

}
