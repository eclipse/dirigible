/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.dirigible.ide.editor.text.editor.TextEditor;
import org.eclipse.dirigible.ide.shared.editor.EditorUtil;
import org.eclipse.dirigible.ide.workspace.dual.EditorInputFactory;
import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

public class OpenHandler extends AbstractHandler {

	private static final String OPEN_FAILURE2 = Messages.OpenHandler_OPEN_FAILURE2;

	// private static final String VIEW_THEM_VIA_REGISTRY_AFTER_ACTIVATION =
	// Messages.OpenHandler_VIEW_THEM_VIA_REGISTRY_AFTER_ACTIVATION;

	// private static final String OPEN_FAILURE = Messages.OpenHandler_OPEN_FAILURE;

	private static final String BINARY_FILES_ARE_NOT_SUPPORTED = Messages.OpenHandler_BINARY_FILES_ARE_NOT_SUPPORTED;

	private static final Logger logger = Logger.getLogger(OpenHandler.class);

	private static final String COULD_NOT_OPEN_ONE_OR_MORE_FILES = Messages.OpenHandler_COULD_NOT_OPEN_ONE_OR_MORE_FILES;

	// private static final String SOURCE_CODE_EDITOR_ID = "org.eclipse.dirigible.ide.editor.ace.AceEditor";

	private static final String SOURCE_CODE_EDITOR_ID = "org.eclipse.dirigible.ide.editor.orion.OrionEditor"; //$NON-NLS-1$

	// private static final String TEXT_EDITOR_RCP_ID = "org.eclipse.jdt.ui.CompilationUnitEditor"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean successful = true;
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Object element : structuredSelection.toArray()) {
				if (element instanceof IFile) {
					successful &= (openEditorFor(element) != null);
				} else {
					successful &= true;
					// WorkspaceViewerUtils.expandElement(element);
				}
			}
		}
		if (!successful) {
			logger.error(COULD_NOT_OPEN_ONE_OR_MORE_FILES);
			MessageDialog.openError(null, OPEN_FAILURE2, COULD_NOT_OPEN_ONE_OR_MORE_FILES);
		}
		return null;
	}

	public static IEditorPart open(Object element, int row) {
		OpenHandler handler = new OpenHandler();

		if (element instanceof IFile) {
			return handler.openEditorForResource((IFile) element, row);
		}
		return null;
	}

	public IEditorPart openEditorFor(Object element) {
		if (element instanceof IFile) {
			return openEditorForResource((IFile) element, 0);
		}
		return null;
	}

	private IEditorPart openEditorForResource(IFile file, int row) {
		// String editorId = null;
		String editorId = EditorUtil.getEditorIdForExtension(file.getFileExtension());
		String contentType = ContentTypeHelper.getContentType(file.getFileExtension());
		if (editorId == null) {
			if ((contentType != null) && contentType.contains("text")) { //$NON-NLS-1$
				editorId = SOURCE_CODE_EDITOR_ID;
			} else {
				logger.error(BINARY_FILES_ARE_NOT_SUPPORTED);
				// MessageDialog.openError(null, OPEN_FAILURE,
				// BINARY_FILES_ARE_NOT_SUPPORTED
				// + VIEW_THEM_VIA_REGISTRY_AFTER_ACTIVATION);
				// return null;
				editorId = TextEditor.ID;
			}
		}
		// SourceFileEditorInput input = new SourceFileEditorInput(file);
		// input.setRow(row);
		// breakpointsSupported(file, contentType, input);
		// readonlyEnabled(file, contentType, input);

		FileEditorInput input = EditorInputFactory.createInput(file, row, contentType);

		return openEditor(editorId, input);
	}

	private IEditorPart openEditor(String id, IEditorInput input) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			String targetEditorId = id;
			Object descriptor = findSourceCodeEditor(id, workbench);
			if (descriptor == null) {
				// descriptor = findSourceCodeEditor(TEXT_EDITOR_RCP_ID, workbench);
				// targetEditorId = TEXT_EDITOR_RCP_ID;
				descriptor = findSourceCodeEditor(TextEditor.ID, workbench);
				targetEditorId = TextEditor.ID;
			} else {
				targetEditorId = id;
			}

			IEditorPart editorPart = null;
			editorPart = page.openEditor(input, targetEditorId);
			return editorPart;
		} catch (PartInitException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private Object findSourceCodeEditor(String id, IWorkbench workbench) {
		Object descriptor = workbench.getEditorRegistry().findEditor(id);
		return descriptor;
	}

}
