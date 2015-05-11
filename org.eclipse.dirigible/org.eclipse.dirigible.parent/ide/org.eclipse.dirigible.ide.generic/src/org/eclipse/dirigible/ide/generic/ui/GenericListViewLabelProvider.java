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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class GenericListViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private static final long serialVersionUID = 2686057886720931696L;

	public String getColumnText(Object obj, int index) {
		if (obj instanceof GenericLocationMetadata) {
			GenericLocationMetadata genericLocationMetadata = (GenericLocationMetadata) obj;
			if (index == 0) {
				return genericLocationMetadata.getName();
			} else if (index == 1) {
				return genericLocationMetadata.getLocation();
			}
		}
		return getText(obj);
	}

	public Image getColumnImage(Object obj, int index) {
		if (index == 0) {
			return getImage(obj);
		} else {
			return null;
		}
	}

	public Image getImage(Object obj) {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_DEF_VIEW);
	}
}
