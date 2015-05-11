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

package org.eclipse.dirigible.ide.workspace.ui.wizard.folder;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.repository.logging.Logger;

public class NewFolderWizard extends Wizard {

	private static final String COULD_NOT_CREATE_FOLDER = Messages.NewFolderWizard_COULD_NOT_CREATE_FOLDER;

	private static final String OPERATION_FAILED = Messages.NewFolderWizard_OPERATION_FAILED;

	private static final String FOLDER_S_CREATED_SUCCESSFULLY = Messages.NewFolderWizard_FOLDER_S_CREATED_SUCCESSFULLY;

	private static final Logger logger = Logger
			.getLogger(NewFolderWizard.class);

	private static final String WINDOW_TITLE = Messages.NewFolderWizard_WINDOW_TITLE;

	private final NewFolderWizardMainPage mainPage;

	private final NewFolderWizardModel model;

	public NewFolderWizard() {
		this(null);
	}

	public NewFolderWizard(IContainer selection) {
		setWindowTitle(WINDOW_TITLE);

		model = new NewFolderWizardModel();
		if (selection != null) {
			model.setParentLocation(selection.getFullPath().toString());
		}
		mainPage = new NewFolderWizardMainPage(model);
	}

	@Override
	public void addPages() {
		addPage(mainPage);
	}

	public boolean performFinish() {
		logger.info(String.format(FOLDER_S_CREATED_SUCCESSFULLY,
				model.getFolderName()));
		boolean result = this.onFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(String.format(
					StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED,
					model.getFolderName()));
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
		} catch (CoreException e) {
			this.showErrorDialog(OPERATION_FAILED, COULD_NOT_CREATE_FOLDER);
			return false;
		}
	}

}
