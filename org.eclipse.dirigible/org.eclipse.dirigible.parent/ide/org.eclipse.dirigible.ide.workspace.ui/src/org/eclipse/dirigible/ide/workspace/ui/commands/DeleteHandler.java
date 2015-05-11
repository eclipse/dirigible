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

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteHandler extends AbstractHandler {

	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_SELECTED_ITEMS_D = Messages.DeleteHandler_ARE_YOU_SURE_YOU_WANT_TO_DELETE_SELECTED_ITEMS_D;
	private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_SELECTED_ITEM = Messages.DeleteHandler_ARE_YOU_SURE_YOU_WANT_TO_DELETE_SELECTED_ITEM;
	private static final String SOME_OR_ALL_OF_THE_FILES_COULD_NOT_BE_DELETED = Messages.DeleteHandler_SOME_OR_ALL_OF_THE_FILES_COULD_NOT_BE_DELETED;
	private static final String DELETE_ERROR = Messages.DeleteHandler_DELETE_ERROR;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			execute((IStructuredSelection) selection);
		}
		return null;
	}

	public void execute(IStructuredSelection selection) {
		Comparator<IResource> comparator = new ResourceComparator();
		SortedSet<IResource> resources = new TreeSet<IResource>(comparator);
		for (Object element : selection.toArray()) {
			if (element instanceof IResource) {
				resources.add((IResource) element);
			}
		}
		execute(resources);
	}

	private void closeFileInEditor(String fileName) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = page.getEditorReferences();
		for (IEditorReference editorRef : editors) {
			IEditorPart editorPart = editorRef.getEditor(false);
			if (editorPart != null && editorPart.getTitle().equals(fileName)) {
				page.closeEditor(editorPart, false);
				return;
			}

		}
	}

	/*
	 * We require that all resources are sorted in such a way that we first
	 * delete the leaf-most elements and then handle elements closer to the
	 * root.
	 */
	private void execute(SortedSet<IResource> resources) {
		if (resources.size() == 0) {
			return;
		}
		if (!confirmDelete(resources.size())) {
			return;
		}
		Throwable throwable = null;
		for (IResource resource : resources) {
			try {
				resource.delete(false, null);
				closeFileInEditor(resource.getName());
			} catch (CoreException ex) {
				if (throwable == null) {
					throwable = ex;
				}
			}
		}
		if (throwable != null) {
			MessageDialog.openWarning(null, DELETE_ERROR,
					SOME_OR_ALL_OF_THE_FILES_COULD_NOT_BE_DELETED);
		}
	}

	private static boolean confirmDelete(int count) {
		String message = ""; //$NON-NLS-1$
		if (count == 1) {
			message = ARE_YOU_SURE_YOU_WANT_TO_DELETE_SELECTED_ITEM;
		} else {
			message = String.format(
					ARE_YOU_SURE_YOU_WANT_TO_DELETE_SELECTED_ITEMS_D, count);
		}
		return MessageDialog.openConfirm(null, Messages.DeleteHandler_CONFIRM_DELETE, message);
	}

	private class ResourceComparator implements Comparator<IResource> {

		@Override
		public int compare(IResource o1, IResource o2) {
			int segmentCount1 = o1.getFullPath().segmentCount();
			int segmentCount2 = o2.getFullPath().segmentCount();
			if (segmentCount1 == segmentCount2) {
				String path1 = o1.getFullPath().toString();
				String path2 = o2.getFullPath().toString();
				return path2.compareTo(path1);
			} else {
				return segmentCount2 - segmentCount1;
			}
		}

	}

}
