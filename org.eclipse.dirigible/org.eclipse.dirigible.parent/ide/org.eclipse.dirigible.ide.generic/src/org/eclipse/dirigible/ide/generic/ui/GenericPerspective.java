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

package org.eclipse.dirigible.ide.generic.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class GenericPerspective implements IPerspectiveFactory {

	private static final String PERSPECTIVE_ID = "help"; //$NON-NLS-1$

	public static final String GENERIC_VIEW_ID = "org.eclipse.dirigible.ide.generic.ui.GenericView"; //$NON-NLS-1$
	public static final String GENERIC_LIST_VIEW_ID = "org.eclipse.dirigible.ide.generic.ui.GenericListView"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

//		// Left
		IFolderLayout left = layout.createFolder(
				"left", IPageLayout.LEFT, 0.60f, editorArea); //$NON-NLS-1$
		left.addView(GENERIC_LIST_VIEW_ID);

		
		
		// Bottom
		IFolderLayout bottom = layout.createFolder("bottom", //$NON-NLS-1$
				IPageLayout.BOTTOM, 0.65f, editorArea);
		bottom.addView(GENERIC_VIEW_ID);
		bottom.addPlaceholder(GENERIC_VIEW_ID + ":*");

		layout.addShowViewShortcut(GENERIC_LIST_VIEW_ID);
//		layout.addShowViewShortcut(GENERIC_VIEW_ID);
		
		layout.getViewLayout(GENERIC_LIST_VIEW_ID).setCloseable(false);
//		layout.getViewLayout(GENERIC_VIEW_ID).setCloseable(false);
		
		layout.addPerspectiveShortcut(PERSPECTIVE_ID);
	}
	

}
