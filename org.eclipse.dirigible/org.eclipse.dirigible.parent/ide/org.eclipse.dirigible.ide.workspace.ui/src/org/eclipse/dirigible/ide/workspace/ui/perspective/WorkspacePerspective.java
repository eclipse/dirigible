/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.ui.perspective;

import org.eclipse.dirigible.ide.workspace.ui.view.WebViewerView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class WorkspacePerspective implements IPerspectiveFactory {

	public static final String PERSPECTIVE_ID = "workspace"; //$NON-NLS-1$

	public static final String WORKSPACE_EXPLORER_VIEW_ID = "org.eclipse.dirigible.ide.workspace.ui.view.WorkspaceExplorerView"; //$NON-NLS-1$

	public static final String PROPERTY_SHEET_VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

	// private static final String CONTENT_OUTLINE_VIEW_ID =
	// "org.eclipse.ui.views.ContentOutline";

	public static final String LOGS_VIEW_ID = "org.eclipse.dirigible.ide.workspace.ui.view.LogsView";

	public static final String LOG_CONSOLE_VIEW_ID = "org.eclipse.dirigible.ide.workspace.ui.view.LogConsoleView";

	public static final String WEB_VIEWER_VIEW_ID = WebViewerView.class.getName();

	public static final String SECURITY_MANAGER_VIEW_ID = "org.eclipse.dirigible.ide.services.security.manager.views.SecurityManagerView"; //$NON-NLS-1$

	public static final String CHEAT_SHEET_VIEW_ID = "org.eclipse.ui.cheatsheets.views.CheatSheetView"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		// Left
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, //$NON-NLS-1$
				0.40f, editorArea);
		left.addView(WORKSPACE_EXPLORER_VIEW_ID);
		layout.getViewLayout(WORKSPACE_EXPLORER_VIEW_ID).setCloseable(false);

		// TODO - Throws exceptions that the view has been already registered?!
		// // Right
		// IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
		// 0.85f, editorArea);
		// right.addView(CHEAT_SHEET_VIEW_ID);

		// Logs
		IFolderLayout logs = layout.createFolder("logs", //$NON-NLS-1$
				IPageLayout.BOTTOM, 0.75f, editorArea);
		logs.addView(LOG_CONSOLE_VIEW_ID);
		logs.addView(LOGS_VIEW_ID);

		// Bottom
		IFolderLayout bottom = layout.createFolder("bottom", //$NON-NLS-1$
				IPageLayout.BOTTOM, 0.35f, editorArea);
		bottom.addView(WEB_VIEWER_VIEW_ID);
		layout.getViewLayout(WEB_VIEWER_VIEW_ID).setCloseable(false);
		bottom.addView(PROPERTY_SHEET_VIEW_ID);
		// bottom.addView(CONTENT_OUTLINE_VIEW_ID);
		bottom.addView(SECURITY_MANAGER_VIEW_ID);

		layout.addShowViewShortcut(WORKSPACE_EXPLORER_VIEW_ID);
		layout.addShowViewShortcut(PROPERTY_SHEET_VIEW_ID);
		layout.addShowViewShortcut(WEB_VIEWER_VIEW_ID);
		layout.addShowViewShortcut(CHEAT_SHEET_VIEW_ID);
		// layout.addShowViewShortcut(CONTENT_OUTLINE_VIEW_ID);
		layout.addPerspectiveShortcut(PERSPECTIVE_ID);
	}

}
