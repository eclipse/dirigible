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

package org.eclipse.dirigible.ide.workspace.ui.commands;

import org.eclipse.jface.wizard.Wizard;

/**
 * Wizard via which the user inserts data into DB tables, providing a list of
 * .dsv files to import from
 * 
 */
public class UploadDataWizard extends Wizard {

	private static final String WINDOW_TITLE = Messages.UploadDataWizard_WINDOW_TITLE;

	private UploadDataWizardPage uploadDataWizardPage;

	private UploadDataHandler uploadDataHandler;

	public UploadDataWizard(UploadDataHandler uploadDataHandler) {
		setWindowTitle(WINDOW_TITLE);
		this.uploadDataHandler = uploadDataHandler;
		uploadDataWizardPage = new UploadDataWizardPage();
	}

	@Override
	public boolean performFinish() {
		uploadDataHandler
				.insertIntoDbAsync(uploadDataWizardPage.getFilePaths());
		return true;
	}

	@Override
	public void addPages() {
		addPage(uploadDataWizardPage);
	}

}
