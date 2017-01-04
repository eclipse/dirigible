/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.extensions.ui.view;

import org.eclipse.dirigible.ide.common.image.ImageUtils;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionDefinition;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionPointDefinition;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ExtensionsViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final long serialVersionUID = 2686057886720931696L;
	
	
	private static final Image EXTENSION_DEFINITION_ICON = ImageUtils.createImage(ImageUtils
			.getIconURL("org.eclipse.dirigible.ide.repository.ui", //$NON-NLS-1$
					"/resources/icons/", "icon-extension.png")); //$NON-NLS-1$ //$NON-NLS-1$

	private static final Image EXTENSION_POINT_DEFINITION_ICON = ImageUtils.createImage(ImageUtils
			.getIconURL("org.eclipse.dirigible.ide.repository.ui", //$NON-NLS-1$
					"/resources/icons/", "icon-extension-point.png")); //$NON-NLS-1$ //$NON-NLS-1$

	@Override
	public String getColumnText(Object obj, int index) {
		return obj.toString();
	}

	@Override
	public Image getColumnImage(Object obj, int index) {
		if (index == 0) {
			return getImage(obj);
		}
		return null;
	}

	@Override
	public Image getImage(Object element) {
		Image image = null;
		SimpleTreeNode node = (SimpleTreeNode) element;
		if (node.getParent() == null) {
			image = EXTENSION_POINT_DEFINITION_ICON;
		} else {
			image = EXTENSION_DEFINITION_ICON;
		}
		return image;
	}
}
