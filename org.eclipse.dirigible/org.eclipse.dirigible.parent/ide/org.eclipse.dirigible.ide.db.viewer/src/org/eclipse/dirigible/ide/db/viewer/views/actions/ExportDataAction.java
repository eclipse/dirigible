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

package org.eclipse.dirigible.ide.db.viewer.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;

import org.eclipse.dirigible.ide.db.export.DataDownloadDialog;
import org.eclipse.dirigible.ide.db.export.DataExportWrapper;
import org.eclipse.dirigible.ide.db.viewer.views.TableDefinition;
import org.eclipse.dirigible.ide.db.viewer.views.TreeObject;

public class ExportDataAction extends Action {

	private static final long serialVersionUID = 1L;
	private static final String EXPORT_DATA = Messages.ExportDataAction_EXPORT_DATA;
	private static final String EXPORT_DATA_TOOLTIP = Messages.ExportDataAction_EXPORT_DATA_AS_DSV_FILE;
	
	private TreeViewer viewer;
	private String tableName;
	
	public ExportDataAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText(EXPORT_DATA);
		setToolTipText(EXPORT_DATA_TOOLTIP);
	}
	
	public void runWithEvent(Event event) {
		ISelection selection = viewer.getSelection();
		Object selectedElement = ((IStructuredSelection) selection).getFirstElement();
		
		if (TreeObject.class.isInstance(selectedElement)) {
			
			if (((TreeObject) selectedElement).getTableDefinition() != null) {
				
				TableDefinition tableDefinition = ((TreeObject) selectedElement).getTableDefinition();
				
				String tablePath = tableDefinition.getFqn();
				tableName = tablePath.substring(tablePath.lastIndexOf(".")+2, tablePath	.length()-1);
				
				DataDownloadDialog dataDownloadDialog = new DataDownloadDialog(
						event.display.getActiveShell());
				dataDownloadDialog.setURL(DataExportWrapper.getUrl(tableName));
				dataDownloadDialog.open();
			}
		}
	}
}
