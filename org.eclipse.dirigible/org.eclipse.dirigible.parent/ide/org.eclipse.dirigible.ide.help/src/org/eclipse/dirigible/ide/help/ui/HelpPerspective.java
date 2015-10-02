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

package org.eclipse.dirigible.ide.help.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class HelpPerspective implements IPerspectiveFactory {

	private static final String PERSPECTIVE_ID = "help"; //$NON-NLS-1$

	private static final String HELP_VIEW_ID = "org.eclipse.dirigible.ide.help.ui.HelpView"; //$NON-NLS-1$
	private static final String SAMPLES_VIEW_ID = "org.eclipse.dirigible.ide.help.ui.SamplesView"; //$NON-NLS-1$
	private static final String FORUM_VIEW_ID = "org.eclipse.dirigible.ide.help.ui.ForumView"; //$NON-NLS-1$
	private static final String ISSUES_VIEW_ID = "org.eclipse.dirigible.ide.help.ui.IssuesView"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		// Left
		IFolderLayout left = layout.createFolder(
				"left", IPageLayout.LEFT, 0.60f, editorArea); //$NON-NLS-1$
		left.addView(HELP_VIEW_ID);

		
		// Issues -> Bugzilla and Forum -> FUDForum, sameorigin issue. To be discussed and redesigned...
		
		// Bottom
		IFolderLayout bottom = layout.createFolder("bottom", //$NON-NLS-1$
				IPageLayout.BOTTOM, 0.35f, editorArea);
//		bottom.addView(ISSUES_VIEW_ID);
//		bottom.addView(FORUM_VIEW_ID);
		bottom.addView(SAMPLES_VIEW_ID);


		layout.addShowViewShortcut(HELP_VIEW_ID);
		layout.addShowViewShortcut(SAMPLES_VIEW_ID);
//		layout.addShowViewShortcut(FORUM_VIEW_ID);
//		layout.addShowViewShortcut(ISSUES_VIEW_ID);
		
		
		layout.getViewLayout(HELP_VIEW_ID).setCloseable(false);
		layout.getViewLayout(SAMPLES_VIEW_ID).setCloseable(false);
//		layout.getViewLayout(FORUM_VIEW_ID).setCloseable(false);
//		layout.getViewLayout(ISSUES_VIEW_ID).setCloseable(false);
		
		layout.addPerspectiveShortcut(PERSPECTIVE_ID);
	}
	

}
