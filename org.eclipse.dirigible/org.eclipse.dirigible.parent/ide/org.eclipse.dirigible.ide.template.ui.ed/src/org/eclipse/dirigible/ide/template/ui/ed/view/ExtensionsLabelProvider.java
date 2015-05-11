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

package org.eclipse.dirigible.ide.template.ui.ed.view;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.eclipse.dirigible.ide.common.image.ImageUtils;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionDefinition;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionPointDefinition;

public class ExtensionsLabelProvider extends LabelProvider {
	private static final long serialVersionUID = 1L;

	private static final Image EXTENSION_DEFINITION_ICON = ImageUtils.createImage(ImageUtils
			.getIconURL("org.eclipse.dirigible.ide.repository.ui", //$NON-NLS-1$
					"/resources/icons/", "icon-extension.png")); //$NON-NLS-1$ //$NON-NLS-1$

	private static final Image EXTENSION_POINT_DEFINITION_ICON = ImageUtils.createImage(ImageUtils
			.getIconURL("org.eclipse.dirigible.ide.repository.ui", //$NON-NLS-1$
					"/resources/icons/", "icon-extension-point.png")); //$NON-NLS-1$ //$NON-NLS-1$

	@Override
	public String getText(Object element) {
		String text = null;
		if (element instanceof ExtensionPointDefinition) {
			text = ((ExtensionPointDefinition) element).getLocation();
		} else if (element instanceof ExtensionDefinition) {
			text = ((ExtensionDefinition) element).getLocation();
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {
		Image image = null;
		if (element instanceof ExtensionPointDefinition) {
			image = EXTENSION_POINT_DEFINITION_ICON;
		} else if (element instanceof ExtensionDefinition) {
			image = EXTENSION_DEFINITION_ICON;
		}
		return image;
	}

}
