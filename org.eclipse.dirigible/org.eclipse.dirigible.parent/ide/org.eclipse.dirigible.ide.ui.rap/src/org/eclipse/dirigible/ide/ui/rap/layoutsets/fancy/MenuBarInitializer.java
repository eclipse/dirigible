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

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;

import org.eclipse.dirigible.ide.ui.rap.shared.LayoutSetConstants;

@SuppressWarnings("deprecation")
public class MenuBarInitializer implements ILayoutSetInitializer {

	public void initializeLayoutSet(final LayoutSet layoutSet) {
		String path = LayoutSetConstants.IMAGE_PATH_FANCY;
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_ARROW, path
				+ "menu_arrow.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_TOP_BG, path
				+ "popup_top_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_BOTTOM_BG, path
				+ "popup_bottom_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_LEFT_BG, path
				+ "popup_left_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_RIGHT_BG, path
				+ "popup_right_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_CORNER_LEFT, path
				+ "popup_corner_left.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_CORNER_RIGHT, path
				+ "popup_corner_right.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_SECOND_LAYER_CHEFRON,
				path + "popup_secondLayer.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.MENUBAR_BG, path
				+ "menubar_bg.png"); //$NON-NLS-1$

		layoutSet.addColor(LayoutSetConstants.MENUBAR_POPUP,
				Graphics.getColor(244, 244, 244));
		layoutSet.addColor(LayoutSetConstants.MENUBAR_POPUP_BUTTON,
				Graphics.getColor(91, 91, 91));
	}
}
