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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.dirigible.ide.editor.text.editor.TextEditor;
import org.eclipse.dirigible.ide.workspace.ui.wizards.rename.RenameWizard;
import org.eclipse.dirigible.repository.logging.Logger;

public class RenameCommandHandler extends AbstractHandler {

	private static final String RENAME_OPERATION_CANCELLED_CAN_ONLY_RENAME_INSTANCES_OF_I_RESOURCE = Messages.RenameCommandHandler_RENAME_OPERATION_CANCELLED_CAN_ONLY_RENAME_INSTANCES_OF_I_RESOURCE;
	private static final String RENAME_OPERATION_CANCELLED_CAN_ONLY_RENAME_A_SINGLE_RESOURCE_AT_A_TIME = Messages.RenameCommandHandler_RENAME_OPERATION_CANCELLED_CAN_ONLY_RENAME_A_SINGLE_RESOURCE_AT_A_TIME;
	private static final Logger log = Logger
			.getLogger(RenameCommandHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			renameSelection((IStructuredSelection) selection);
		}
		return null;

	}

	private void renameFileInEditor(String oldFileName, String newFileName) {
		IEditorReference[] editors = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();
		for (IEditorReference editorRef : editors) {
			IEditorPart editorPart = editorRef.getEditor(false);
			if (editorPart != null && editorPart.getTitle().equals(oldFileName)) {
				// org.eclipse.dirigible.ide.editor.js.
//				if (editorPart instanceof JavaScriptEditor) {
//					((JavaScriptEditor) editorPart).setPartName(newFileName);
//					return;
//				}
				if (editorPart instanceof TextEditor) {
					((TextEditor) editorPart).setPartName(newFileName);
					return;
				}
			}

		}
	}

	private void renameSelection(IStructuredSelection selection) {
		if (selection.size() != 1) {
			log.warn(RENAME_OPERATION_CANCELLED_CAN_ONLY_RENAME_A_SINGLE_RESOURCE_AT_A_TIME);
			return;
		}
		final Object element = selection.getFirstElement();
		if (!(element instanceof IResource)) {
			log.warn(RENAME_OPERATION_CANCELLED_CAN_ONLY_RENAME_INSTANCES_OF_I_RESOURCE);
			return;
		}
		String newValue = renameResource((IResource) element);
		if (newValue != null) {
			renameFileInEditor(((IResource) element).getName(), newValue);
		}
	}

	private String renameResource(IResource resource) {
		final RenameWizard wizard = new RenameWizard(resource);
		final Dialog dialog = new WizardDialog(null, wizard);
		if (dialog.open() == Window.CANCEL) {
			return null;
		}
		;
		return wizard.getText();
	}

}
