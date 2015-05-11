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

package org.eclipse.dirigible.ide.db.viewer.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class DatabasePerspective implements IPerspectiveFactory {

	private static final String BOTTOM_NAME = "bottom"; //$NON-NLS-1$

	private static final String LEFT_NAME = "left"; //$NON-NLS-1$

	private static final String PERSPECTIVE_ID = "database"; //$NON-NLS-1$

	// private static final String PROPERTY_SHEET_VIEW_ID =
	// "org.eclipse.ui.views.PropertySheet";

	private static final String DATABASE_VIEW_ID = "org.eclipse.dirigible.ide.db.viewer.views.DatabaseViewer"; //$NON-NLS-1$

	private static final String SQL_CONSOLE_VIEW_ID = "org.eclipse.dirigible.ide.db.viewer.views.SQLConsole"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		// Left
		final IFolderLayout left = layout.createFolder(LEFT_NAME,
				IPageLayout.LEFT, 0.35f, editorArea);
		left.addView(DATABASE_VIEW_ID);
		layout.getViewLayout(DATABASE_VIEW_ID).setCloseable(false);

		// // Right
		// final IFolderLayout right = layout.createFolder("right",
		// IPageLayout.RIGHT, 0.20f, DATABASE_VIEW_ID);
		// right.addView(SQL_CONSOLE_VIEW_ID);
		// layout.getViewLayout(SQL_CONSOLE_VIEW_ID).setCloseable(false);

		// Bottom
		final IFolderLayout bottom = layout.createFolder(BOTTOM_NAME,
				IPageLayout.BOTTOM, 0.60f, editorArea);
		bottom.addView(SQL_CONSOLE_VIEW_ID);
		layout.getViewLayout(SQL_CONSOLE_VIEW_ID).setCloseable(false);
		// bottom.addView(PROPERTY_SHEET_VIEW_ID);

		layout.addShowViewShortcut(DATABASE_VIEW_ID);
		layout.addShowViewShortcut(SQL_CONSOLE_VIEW_ID);
		// layout.addShowViewShortcut(PROPERTY_SHEET_VIEW_ID);
		layout.addPerspectiveShortcut(PERSPECTIVE_ID);

	}

}
