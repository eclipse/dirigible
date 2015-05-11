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

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.dirigible.ide.workspace.dual.DownloadProjectWrapper;
import org.eclipse.dirigible.ide.workspace.wizard.project.export.DownloadDialog;

public class DownloadProjectHandler extends AbstractHandler {
	
	public static final String PROJECT_NAME_SEPARATOR = "$"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelection(event);

		if (selection.size() == 1) {
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IProject) {
				IProject project = (IProject) firstElement;
				DownloadDialog downloadDialog = new DownloadDialog(
						HandlerUtil.getActiveShell(event));
				downloadDialog.setURL(DownloadProjectWrapper
						.getUrl(project.getName()));
				downloadDialog.open();
			}
		} else if (selection.size() > 1) {
			StringBuffer projectNames = new StringBuffer();
			Iterator<?> iterator = selection.iterator();
			boolean dot = false;
			while (iterator.hasNext()) {
				Object element = iterator.next();
				if (element instanceof IProject) {
					IProject project = (IProject) element;
					if (dot) {
						projectNames
								.append(PROJECT_NAME_SEPARATOR);
					}
					projectNames.append(project.getName());
					dot = true;
				}
			}
			DownloadDialog downloadDialog = new DownloadDialog(
					HandlerUtil.getActiveShell(event));
			downloadDialog.setURL(DownloadProjectWrapper
					.getUrl(projectNames.toString()));
			downloadDialog.open();
		}
		return null;
	}

}
