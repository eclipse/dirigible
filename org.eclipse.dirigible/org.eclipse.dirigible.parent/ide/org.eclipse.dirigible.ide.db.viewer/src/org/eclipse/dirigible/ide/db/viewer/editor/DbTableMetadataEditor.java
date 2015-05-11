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

package org.eclipse.dirigible.ide.db.viewer.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public class DbTableMetadataEditor extends MultiPageEditorPart {

	private static final String TABLE_DETAILS = Messages.DbTableMetadataEditor_TABLE_DETAILS;

	private static final long serialVersionUID = -1881524156909611914L;

	private TableDetailsEditorPage mainEditorPage;

	public DbTableMetadataEditor() {
		super();
	}

	public void doSave(final IProgressMonitor monitor) {
		mainEditorPage.setDirty(false);
	}

	public void doSaveAs() {
		//
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter) {
		Object result = super.getAdapter(adapter);
		return result;
	}

	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		setPartName(((DbEditorInput) input).getTableName());
	}

	protected void createPages() {
		mainEditorPage = new TableDetailsEditorPage();
		int index;
		try {
			index = addPage(mainEditorPage, getEditorInput());
			setPageText(index, TABLE_DETAILS);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
