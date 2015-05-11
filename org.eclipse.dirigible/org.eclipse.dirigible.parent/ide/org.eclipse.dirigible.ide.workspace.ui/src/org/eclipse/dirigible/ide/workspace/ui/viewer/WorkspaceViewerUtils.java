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

package org.eclipse.dirigible.ide.workspace.ui.viewer;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import org.eclipse.dirigible.ide.workspace.ui.view.WorkspaceExplorerView;

public class WorkspaceViewerUtils {
	
	public static void expandElement(Object element) {
		if (element instanceof IFolder
				|| element instanceof IProject) {
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart view = page.findView(WorkspaceExplorerView.VIEW_ID);
			if (view != null
					&& view instanceof WorkspaceExplorerView) {
				TreeViewer treeViewer = ((WorkspaceExplorerView)view).getViewer().getViewer();
				if (element instanceof IFolder
						|| element instanceof IProject) {
					treeViewer.setExpandedState(element, true);
				}
			}
		}
	}
	
	public static void doubleClickedElement(Object element) {
		if (element instanceof IFolder
				|| element instanceof IProject) {
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart view = page.findView(WorkspaceExplorerView.VIEW_ID);
			if (view != null
					&& view instanceof WorkspaceExplorerView) {
				TreeViewer treeViewer = ((WorkspaceExplorerView)view).getViewer().getViewer();
				if (element instanceof IFolder
						|| element instanceof IProject) {
					treeViewer.setExpandedState(element, 
							(!treeViewer.getExpandedState(element)));
				}
			}
		}
	}

	public static void selectElement(final Object element) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(WorkspaceExplorerView.VIEW_ID);
		if (view != null
				&& view instanceof WorkspaceExplorerView) {
			TreeViewer treeViewer = ((WorkspaceExplorerView)view).getViewer().getViewer();
			treeViewer.setSelection(new StructuredSelection(element));
		}
	
	}
	
}
