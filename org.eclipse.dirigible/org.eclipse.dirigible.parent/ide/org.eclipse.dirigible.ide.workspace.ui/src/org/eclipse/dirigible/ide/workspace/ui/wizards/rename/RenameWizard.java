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

package org.eclipse.dirigible.ide.workspace.ui.wizards.rename;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import org.eclipse.dirigible.repository.logging.Logger;

public class RenameWizard extends Wizard {

	private static final String CHECK_LOGS_FOR_MORE_INFO = Messages.RenameWizard_CHECK_LOGS_FOR_MORE_INFO;

	private static final String COULD_NOT_COMPLETE_WIZARD_DUE_TO_THE_FOLLOWING_ERROR = Messages.RenameWizard_COULD_NOT_COMPLETE_WIZARD_DUE_TO_THE_FOLLOWING_ERROR;

	private static final String OPERATION_ERROR = Messages.RenameWizard_OPERATION_ERROR;

	private static final String COULD_NOT_COMPLETE_RESOURCE_RENAME = Messages.RenameWizard_COULD_NOT_COMPLETE_RESOURCE_RENAME;

	private static final Logger logger = Logger.getLogger(RenameWizard.class);

	private static final String RENAME_WIZARD_TITLE = Messages.RenameWizard_RENAME_WIZARD_TITLE;

	private final RenameWizardModel model;

	private final RenameWizardNamingPage namingPage;

	public RenameWizard(IResource resource) {
		setWindowTitle(RENAME_WIZARD_TITLE);
		model = new RenameWizardModel(resource);
		namingPage = new RenameWizardNamingPage(model);
	}

	@Override
	public void addPages() {
		addPage(namingPage);
	}

	public boolean performFinish() {
		return onFinish();
	}

	public void showErrorDialog(String title, String message) {
		logger.error(message);
		MessageDialog.openError(null, title, message);
	}

	public String getText() {
		if (namingPage != null) {
			return namingPage.getText();
		}
		return null;
	}

	public boolean onFinish() {
		try {
			model.persist();
			return true;
		} catch (IOException ex) {
			logger.error(COULD_NOT_COMPLETE_RESOURCE_RENAME, ex);
			this.showErrorDialog(
					OPERATION_ERROR,
					COULD_NOT_COMPLETE_WIZARD_DUE_TO_THE_FOLLOWING_ERROR
							+ ex.getMessage() + ". " + CHECK_LOGS_FOR_MORE_INFO); //$NON-NLS-1$
			return false;
		}
	}
}
