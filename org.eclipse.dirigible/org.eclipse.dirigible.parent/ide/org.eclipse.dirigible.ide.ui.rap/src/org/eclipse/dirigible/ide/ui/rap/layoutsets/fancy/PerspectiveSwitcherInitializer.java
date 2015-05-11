/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.dirigible.ide.ui.rap.layoutsets.fancy;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

import org.eclipse.dirigible.ide.ui.rap.shared.LayoutSetConstants;

public class PerspectiveSwitcherInitializer implements ILayoutSetInitializer {

	public void initializeLayoutSet(final LayoutSet layoutSet) {
		layoutSet.addImagePath(LayoutSetConstants.PERSP_CLOSE,
				LayoutSetConstants.IMAGE_PATH_FANCY + "close.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.PERSP_LEFT_ACTIVE,
				LayoutSetConstants.IMAGE_PATH_FANCY
						+ "perspective_left_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.PERSP_RIGHT_ACTIVE,
				LayoutSetConstants.IMAGE_PATH_FANCY
						+ "perspective_right_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.PERSP_BG,
				LayoutSetConstants.IMAGE_PATH_FANCY + "perspective_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.PERSP_BG_ACTIVE,
				LayoutSetConstants.IMAGE_PATH_FANCY
						+ "perspective_bg_active.png"); //$NON-NLS-1$
		FormData fdButton = new FormData();
		fdButton.top = new FormAttachment(0, 4);
		layoutSet.addPosition(LayoutSetConstants.PERSP_BUTTON_POS, fdButton);
	}
}
