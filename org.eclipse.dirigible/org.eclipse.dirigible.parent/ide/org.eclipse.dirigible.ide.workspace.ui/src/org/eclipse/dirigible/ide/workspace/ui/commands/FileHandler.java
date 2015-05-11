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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.dirigible.ide.workspace.ui.wizard.file.NewFileWizard;

public class FileHandler extends AbstractWorkspaceHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IContainer selectedContainer = getSelectedContainer(event);
		if (selectedContainer == null) {
			Object lastSelectedElement = getLastSelectedWorkspaceElement();
			if (lastSelectedElement != null && lastSelectedElement instanceof IContainer) {
				selectedContainer = ((IContainer) lastSelectedElement);
			}
		}
		return execute(selectedContainer);
	}

	public Object execute(IContainer container) {
		Wizard wizard = new NewFileWizard(container);
		WizardDialog dialog = new WizardDialog(null, wizard);
		dialog.open();
		return null;
	}

	private IContainer getSelectedContainer(ExecutionEvent event) {
		IContainer container = null;
		if (event != null) {
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if (selection instanceof IStructuredSelection) {
				container = getContainer((IStructuredSelection) selection);
			}
		}
		return container;
	}

	private IContainer getContainer(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IContainer) {
			return (IContainer) element;
		}
		return null;
	}

}
