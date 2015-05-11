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

package org.eclipse.dirigible.ide.workspace.ui.wizard.file;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.repository.logging.Logger;

public class NewFileWizard extends Wizard {

	private static final String COULD_NOT_SAVE_FILE = Messages.NewFileWizard_COULD_NOT_SAVE_FILE;

	private static final String OPERATION_FAILED = Messages.NewFileWizard_OPERATION_FAILED;

	private static final String FILE_S_CREATED_SUCCESSFULLY = Messages.NewFileWizard_FILE_S_CREATED_SUCCESSFULLY;

	private static final Logger logger = Logger.getLogger(NewFileWizard.class);

	private static final String WINDOW_TITLE = Messages.NewFileWizard_WINDOW_TITLE;

	private final NewFileWizardMainPage mainPage;

	private final NewFileWizardModel model;

	public NewFileWizard() {
		this(null);
	}

	public NewFileWizard(IContainer selection) {
		setWindowTitle(WINDOW_TITLE);

		model = new NewFileWizardModel();
		if (selection != null) {
			model.setParentLocation(selection.getFullPath().toString());
		}

		mainPage = new NewFileWizardMainPage(model);
	}

	@Override
	public void addPages() {
		addPage(mainPage);
	}

	public boolean performFinish() {
		logger.info(String.format(FILE_S_CREATED_SUCCESSFULLY,
				model.getFileName()));
		boolean result = onFinish();
		if (result) {
			StatusLineManagerUtil.setInfoMessage(String.format(
					StatusLineManagerUtil.ARTIFACT_HAS_BEEN_CREATED,
					model.getFileName()));
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
			logger.error(e.getMessage(), e);
			showErrorDialog(OPERATION_FAILED, COULD_NOT_SAVE_FILE);
			return false;
		}
	}

}
