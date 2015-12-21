/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.viewer.views.actions;

import org.eclipse.dirigible.ide.db.viewer.views.ISQLConsole;
import org.eclipse.dirigible.ide.db.viewer.views.TableDefinition;
import org.eclipse.dirigible.ide.db.viewer.views.TreeObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ViewTableContentAction extends Action {

	private static final String DATABASE_VIEW = Messages.ViewTableContentAction_DATABASE_VIEW;

	private static final String CANNOT_OPEN_SQL_VIEW = Messages.ViewTableContentAction_CANNOT_OPEN_SQL_VIEW;

	private static final String WILL_SHOW_TABLE_CONTENT = Messages.ViewTableContentAction_WILL_SHOW_TABLE_CONTENT;

	private static final String SHOW_CONTENT = Messages.ViewTableContentAction_SHOW_CONTENT;

	private static final long serialVersionUID = 5194043886090203855L;

	private TreeViewer viewer;

	private String consoleId;

	public ViewTableContentAction(TreeViewer viewer, String consoleId) {
		this.viewer = viewer;
		this.consoleId = consoleId;
		setText(SHOW_CONTENT);
		setToolTipText(WILL_SHOW_TABLE_CONTENT);
	}

	@Override
	public void run() {
		try {
			ISelection selection = viewer.getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (TreeObject.class.isInstance(obj)) {
				TableDefinition tableDefinition = ((TreeObject) obj).getTableDefinition();
				if (tableDefinition != null) {
					ISQLConsole view = (ISQLConsole) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(consoleId);
					executeSelectStatement(tableDefinition, view);
				}
			}

		} catch (PartInitException e) {
			showMessage(CANNOT_OPEN_SQL_VIEW);
		}
	}

	protected void executeSelectStatement(TableDefinition tableDefinition, ISQLConsole view) {
		String script = tableDefinition.getContentScript();
		view.setQuery(script);
		view.executeStatement(true);
	}

	protected void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), DATABASE_VIEW, message);
	}

}
