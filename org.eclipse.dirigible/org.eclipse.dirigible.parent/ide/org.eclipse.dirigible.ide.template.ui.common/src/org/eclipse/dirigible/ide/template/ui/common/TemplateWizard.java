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

package org.eclipse.dirigible.ide.template.ui.common;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import org.eclipse.dirigible.ide.workspace.ui.commands.OpenHandler;
import org.eclipse.dirigible.repository.logging.Logger;

public abstract class TemplateWizard extends Wizard {

	private static final String GENERATION_FAILED = Messages.TemplateWizard_GENERATION_FAILED;

	private static final Logger logger = Logger.getLogger(TemplateWizard.class);

	protected abstract GenerationModel getModel();

	@Override
	public boolean canFinish() {
		final IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == null || !currentPage.isPageComplete()) {
			return false;
		}
		if (getModel().validate().hasErrors()) {
			return false;
		}

		return followingPagesAreComplete(currentPage);
	}

	private boolean followingPagesAreComplete(IWizardPage page) {
		IWizardPage currentPage = page.getNextPage();
		while (currentPage != null) {
			if (!currentPage.isPageComplete()) {
				return false;
			}
			currentPage = currentPage.getNextPage();
		}
		return true;
	}

	@Override
	public boolean performFinish() {
		try {
			TemplateGenerator generator = getTemplateGenerator();
			generator.generate();
			openFiles(generator.getGeneratedFiles());
			return true;
		} catch (Exception ex) {
			logger.error(GENERATION_FAILED, ex);
			MessageDialog.openError(null, GENERATION_FAILED, ex.getMessage());
			return false;
		}
	}

	private void openFiles(List<IFile> generatedFiles) {
		if (generatedFiles.size() > 0) {
			if (generatedFiles.size() > 1) {
				if (openEditorForFileWithExtension() != null) {
					for (IFile iFile : generatedFiles) {
						if (iFile.getName().endsWith(
								openEditorForFileWithExtension())) {
							OpenHandler.open(iFile, 0);
						}
					}
				}
			} else {
				OpenHandler.open(generatedFiles.get(0), 0);
			}
		}

	}

	protected String openEditorForFileWithExtension() {
		return null;
	}

	public abstract TemplateGenerator getTemplateGenerator();

}
