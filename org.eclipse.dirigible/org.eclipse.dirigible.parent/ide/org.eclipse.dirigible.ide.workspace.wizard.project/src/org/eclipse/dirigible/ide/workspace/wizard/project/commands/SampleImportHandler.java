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

package org.eclipse.dirigible.ide.workspace.wizard.project.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.dirigible.ide.workspace.ui.commands.AbstractWorkspaceHandler;
import org.eclipse.dirigible.ide.workspace.wizard.project.sample.SampleProjectWizard;

public class SampleImportHandler extends AbstractWorkspaceHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Wizard wizard = new SampleProjectWizard();
		WizardDialog dialog = new WizardDialog(null, wizard);
		dialog.open();

		refreshWorkspace();

		return null;
	}
}
