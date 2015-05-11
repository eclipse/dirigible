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

package org.eclipse.dirigible.ide.registry.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class RegistryPerspective implements IPerspectiveFactory {

	private static final String PERSPECTIVE_ID = "registry"; //$NON-NLS-1$

	private static final String REGISTRY_VIEW_ID = "org.eclipse.dirigible.ide.registry.ui.RegistryView"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		// Left
		IFolderLayout left = layout.createFolder(
				"left", IPageLayout.LEFT, 0.40f, editorArea); //$NON-NLS-1$
		left.addView(REGISTRY_VIEW_ID);
		layout.getViewLayout(REGISTRY_VIEW_ID).setCloseable(false);

		layout.addShowViewShortcut(REGISTRY_VIEW_ID);
		layout.addPerspectiveShortcut(PERSPECTIVE_ID);

	}

}
