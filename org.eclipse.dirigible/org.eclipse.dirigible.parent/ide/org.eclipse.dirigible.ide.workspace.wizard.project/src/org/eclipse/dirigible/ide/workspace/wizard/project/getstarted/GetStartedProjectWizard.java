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

package org.eclipse.dirigible.ide.workspace.wizard.project.getstarted;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.workspace.wizard.project.create.Messages;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

public class GetStartedProjectWizard extends Wizard {
	
	private static final Logger logger = Logger.getLogger(GetStartedProjectWizard.class);

	private static final String COULD_NOT_CREATE_PROJECT = Messages.NewProjectWizard_COULD_NOT_CREATE_PROJECT;

	private static final String OPERATION_FAILED = Messages.NewProjectWizard_OPERATION_FAILED;

	private static final String PROJECT_S_CREATED_SUCCESSFULLY = Messages.NewProjectWizard_PROJECT_S_CREATED_SUCCESSFULLY;

	private static final String WINDOW_TITLE = "Get Started Project Wizard";

	private final GetStartedProjectWizardModel model;

	private final GetStartedProjectWizardWelcomePage welcomePage;
	
	private final GetStartedProjectWizardMainPage mainPage;
	
	private final GetStartedProjectWizardDBTablePage dbTablePage;
	
	private final GetStartedProjectWizardJSServicePage jsServicePage;
	
	private final GetStartedProjectWizardHTMLPage htmlPage;
	
	private final GetStartedProjectWizardSummaryPage summaryPage;

	public GetStartedProjectWizard() {
		setWindowTitle(WINDOW_TITLE);

		model = new GetStartedProjectWizardModel();
		
		model.setProjectName(CommonParameters.getUserName() + "_" + model.getProjectName());
		
		welcomePage = new GetStartedProjectWizardWelcomePage();
		mainPage = new GetStartedProjectWizardMainPage(model);
		dbTablePage = new GetStartedProjectWizardDBTablePage();
		jsServicePage = new GetStartedProjectWizardJSServicePage();
		htmlPage = new GetStartedProjectWizardHTMLPage();
		summaryPage = new GetStartedProjectWizardSummaryPage();
	}

	@Override
	public void addPages() {
		addPage(welcomePage);
		addPage(mainPage);
		addPage(dbTablePage);
		addPage(jsServicePage);
		addPage(htmlPage);
		addPage(summaryPage);
	}

	public boolean performFinish() {
		logger.info(String.format(PROJECT_S_CREATED_SUCCESSFULLY,
				mainPage.getProjectName()));
		boolean result = this.onFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(String.format(
					StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED,
					mainPage.getProjectName()));
		}
		return result;
	}

	public void showErrorDialog(String title, String message) {
		logger.error(message);
		MessageDialog.openError(null, title, message);
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

}
