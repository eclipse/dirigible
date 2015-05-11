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

package org.eclipse.dirigible.ide.debug.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import org.eclipse.dirigible.ide.workspace.ui.perspective.WorkspacePerspective;

public class DebugPerspective implements IPerspectiveFactory {

	private static final String PERSPECTIVE_ID = "debug"; //$NON-NLS-1$

	private static final String DEBUG_VIEW_ID = "org.eclipse.dirigible.ide.debug.ui.DebugView"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		// Left
		IFolderLayout left = layout.createFolder(
				"left", IPageLayout.LEFT, 0.20f, editorArea); //$NON-NLS-1$
		left.addView(WorkspacePerspective.WORKSPACE_EXPLORER_VIEW_ID);
		layout.getViewLayout(WorkspacePerspective.WORKSPACE_EXPLORER_VIEW_ID).setCloseable(false);
		
		// Top
		IFolderLayout top = layout.createFolder(
						"top", IPageLayout.TOP, 0.20f, editorArea); //$NON-NLS-1$
				
		top.addView(DEBUG_VIEW_ID);
		layout.getViewLayout(DEBUG_VIEW_ID).setCloseable(false);

		layout.addShowViewShortcut(DEBUG_VIEW_ID);
		layout.addPerspectiveShortcut(PERSPECTIVE_ID);
		
		// Bottom
			IFolderLayout bottom = layout.createFolder("bottom", //$NON-NLS-1$
					IPageLayout.BOTTOM, 0.80f, editorArea);
			bottom.addView(WorkspacePerspective.WEB_VIEWER_VIEW_ID);
			// bottom.addView(CONTENT_OUTLINE_VIEW_ID);
			bottom.addView(WorkspacePerspective.LOGS_VIEW_ID);

	}

}
