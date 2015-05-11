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

package org.eclipse.dirigible.ide.repository.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import org.eclipse.dirigible.ide.repository.ui.view.RepositoryView;
import org.eclipse.dirigible.ide.repository.ui.view.ResourceHistoryView;

public class RepositoryPerspective implements IPerspectiveFactory {

	private static final String PERSPECTIVE_ID = "repository"; //$NON-NLS-1$

	private static final String PROPERTY_SHEET_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

	public void createInitialLayout(IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		// Left
		final IFolderLayout left = layout.createFolder("left", //$NON-NLS-1$
				IPageLayout.LEFT, 0.35f, editorArea);
		left.addView(RepositoryView.ID);
		layout.getViewLayout(RepositoryView.ID).setCloseable(false);

		// Bottom
		final IFolderLayout bottom = layout.createFolder("bottom", //$NON-NLS-1$
				IPageLayout.BOTTOM, 0.60f, editorArea);
		bottom.addView(ResourceHistoryView.ID);
		bottom.addView(PROPERTY_SHEET_VIEW_ID);

		layout.addShowViewShortcut(RepositoryView.ID);
		layout.addShowViewShortcut(PROPERTY_SHEET_VIEW_ID);
		layout.addPerspectiveShortcut(PERSPECTIVE_ID);
	}

}
