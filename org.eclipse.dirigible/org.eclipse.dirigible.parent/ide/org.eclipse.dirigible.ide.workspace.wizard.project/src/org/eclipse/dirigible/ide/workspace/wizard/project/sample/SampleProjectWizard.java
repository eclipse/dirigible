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

package org.eclipse.dirigible.ide.workspace.wizard.project.sample;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;


public class SampleProjectWizard extends Wizard {

	private static final String SampleProjectWizard_WINDOW_TITLE = Messages.SampleProjectWizard_WINDOW_TITLE;
	private static final String COULD_NOT_CREATE_PROJECT = Messages.SampleProjectWizard_COULD_NOT_CREATE_PROJECT;
	private static final String OPERATION_FAILED = Messages.SampleProjectWizard_OPERATION_FAILED;
	private static final String PROJECT_S_CREATED_SUCCESSFULLY = Messages.SampleProjectWizard_PROJECT_S_CREATED_SUCCESSFULLY;
	private final SampleProjectWizardModel model;
	private final SampleProjectWizardGitTemplatePage samplesPage;
	
	private static final Logger logger = Logger
			.getLogger(SampleProjectWizard.class);
	
	public SampleProjectWizard() {
		setWindowTitle(SampleProjectWizard_WINDOW_TITLE);

		model = new SampleProjectWizardModel();
		samplesPage = new SampleProjectWizardGitTemplatePage(model);
	}

	@Override
	public void addPages() {
		addPage(samplesPage);
	}
	
	@Override
	public boolean performFinish() {
		String projectName = model.getTemplate().getName();
		logger.info(String.format(PROJECT_S_CREATED_SUCCESSFULLY,
				projectName));
		boolean result = this.onFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(String.format(
					StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED,
					projectName));
		}
		return result;
	}
	
	public boolean onFinish() {
		try {
			model.execute();
			return true;
		} catch (CoreException ex) {
			logger.error(ex.getMessage(), ex);
			this.showErrorDialog(OPERATION_FAILED, String.format(COULD_NOT_CREATE_PROJECT, ex.getMessage()));
			return false;
		}
	}
	
	public void showErrorDialog(String title, String message) {
		logger.error(message);
		MessageDialog.openError(null, title, message);
	}
}
