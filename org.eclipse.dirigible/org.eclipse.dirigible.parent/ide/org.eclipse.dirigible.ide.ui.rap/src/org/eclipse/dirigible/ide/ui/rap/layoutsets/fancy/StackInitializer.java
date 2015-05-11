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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

import org.eclipse.dirigible.ide.ui.rap.shared.LayoutSetConstants;

@SuppressWarnings("deprecation")
public class StackInitializer implements ILayoutSetInitializer {

	public void initializeLayoutSet(final LayoutSet layoutSet) {
		String path = LayoutSetConstants.IMAGE_PATH_FANCY;
		layoutSet.addImagePath(LayoutSetConstants.STACK_CONF_ACTIVE, path
				+ "stack_tab_conf_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_CONF_INACTIVE, path
				+ "stack_tab_conf_inactive.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_CONF_BG_ACTIVE, path
				+ "stack_tab_active_confarea_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_TAB_INACTIVE_BG_ACTIVE,
				path + "stack_tab_inactive_bg_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_CONF_BG_INACTIVE, path
				+ "stack_tab_inactive_confarea_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(
				LayoutSetConstants.STACK_TAB_INACTIVE_RIGHT_ACTIVE, path
						+ "stack_tab_inactive_right_active.png"); //$NON-NLS-1$
		String separatorActive = LayoutSetConstants.STACK_TAB_INACTIVE_SEPARATOR_ACTIVE;
		layoutSet.addImagePath(separatorActive, path
				+ "stack_tab_inactive_separator_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_TAB_OVERFLOW_ACTIVE,
				path + "stack_tab_overflow_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_BORDER_LEFT, path
				+ "stack_border_left.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_BORDER_RIGHT, path
				+ "stack_border_right.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_BORDER_TOP, path
				+ "stack_border_top.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_BORDER_BOTTOM, path
				+ "stack_border_bottom.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_BORDER_LEFT_ACTIVE,
				path + "stack_border_left_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_BORDER_RIGHT_AVTIVE,
				path + "stack_border_right_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_BORDER_BOTTOM_ACTIVE,
				path + "stack_border_bottom_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_INACTIVE_CORNER, path
				+ "stack_inactive_corner.png"); //$NON-NLS-1$
		String cornerActive = LayoutSetConstants.STACK_TAB_INACTIVE_CORNER_ACTIVE;
		layoutSet.addImagePath(cornerActive, path
				+ "stack_inactive_corner_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_VIEW_TOOLBAR_BG, path
				+ "viewtoolbar_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_VIEW_MENU_ICON, path
				+ "viewMenu.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_VIEW_PULLDOWN, path
				+ "viewPulldown.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_TAB_BG_ACTIVE, path
				+ "stack_tab_bg_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_TABBAR_LEFT_ACTIVE,
				path + "stack_tabbar_left_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_TABBAR_RIGHT_ACTIVE,
				path + "stack_tabbar_right_active.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_TABBAR_LEFT_INACTIVE,
				path + "stack_tabbar_left_inactive.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_TABBAR_RIGHT_INACTIVE,
				path + "stack_tabbar_right_inactive.png"); //$NON-NLS-1$
		layoutSet.addImagePath(LayoutSetConstants.STACK_TOP_STANDALONE_ACTIVE,
				path + "stack_border_top_active_standalone.png"); //$NON-NLS-1$
		layoutSet.addImagePath(
				LayoutSetConstants.STACK_TOP_STANDALONE_INACTIVE, path
						+ "stack_border_top_standalone.png"); //$NON-NLS-1$

		FormData fdConfButton = new FormData();
		fdConfButton.top = new FormAttachment(0, 3);
		fdConfButton.right = new FormAttachment(100, -5);
		layoutSet.addPosition(LayoutSetConstants.STACK_CONF_POSITION,
				fdConfButton);
		FormData fdOverflow = new FormData();
		fdOverflow.top = new FormAttachment(0, 6);
		fdOverflow.right = new FormAttachment(100, -30);
		layoutSet.addPosition(LayoutSetConstants.STACK_OVERFLOW_POSITION,
				fdOverflow);
		layoutSet.addColor(LayoutSetConstants.STACK_BUTTON_ACTIVE,
				Graphics.getColor(255, 255, 255));
		layoutSet.addColor(LayoutSetConstants.STACK_BUTTON_INACTIVE,
				Graphics.getColor(255, 255, 255));
		FormData fdTabBg = new FormData();
		fdTabBg.width = 7;
		fdTabBg.height = 3;
		layoutSet.addPosition(LayoutSetConstants.STACK_TABBG_POS, fdTabBg);
		FormData fdButton = new FormData();
		fdButton.top = new FormAttachment(0, 0);
		layoutSet.addPosition(LayoutSetConstants.STACK_BUTTON_TOP, fdButton);
		FormData fdConfPos = new FormData();
		fdConfPos.right = new FormAttachment(100, -6);
		layoutSet.addPosition(LayoutSetConstants.STACK_CONF_POS, fdConfPos);
	}
}
