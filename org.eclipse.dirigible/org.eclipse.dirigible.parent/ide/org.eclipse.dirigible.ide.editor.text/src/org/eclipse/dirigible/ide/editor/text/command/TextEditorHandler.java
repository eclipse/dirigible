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

package org.eclipse.dirigible.ide.editor.text.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.dirigible.repository.logging.Logger;

public class TextEditorHandler extends AbstractHandler {

	private static final String COULD_NOT_OPEN_EDITOR_FOR_SOME_OR_ALL_OF_THE_SELECTED_ITEMS = Messages.TextEditorHandler_COULD_NOT_OPEN_EDITOR_FOR_SOME_OR_ALL_OF_THE_SELECTED_ITEMS;

	private static final Logger logger = Logger
			.getLogger(TextEditorHandler.class);

	private static final String EDITOR_ID = "org.eclipse.dirigible.ide.editor.text.editor.TextEditor"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		if (selection instanceof IStructuredSelection && window != null) {
			execute(window.getActivePage(), (IStructuredSelection) selection);
		}
		return null;
	}

	protected String getEditorId() {
		return EDITOR_ID;
	}

	private void execute(IWorkbenchPage page, IStructuredSelection selection) {
		Throwable error = null;
		for (Object element : selection.toArray()) {
			if (element instanceof IFile) {
				try {
					execute(page, (IFile) element);
				} catch (PartInitException ex) {
					if (error == null) {
						error = ex;
					}
				}

			}
		}
		if (error != null) {
			logger.error(
					COULD_NOT_OPEN_EDITOR_FOR_SOME_OR_ALL_OF_THE_SELECTED_ITEMS,
					error);
			MessageDialog
					.openError(null,
							Messages.TextEditorHandler_OPERATION_ERROR,
							COULD_NOT_OPEN_EDITOR_FOR_SOME_OR_ALL_OF_THE_SELECTED_ITEMS);
		}

	}

	private void execute(IWorkbenchPage page, IFile file)
			throws PartInitException {
		IEditorInput input = new FileEditorInput(file);
		page.openEditor(input, getEditorId());
	}

}
