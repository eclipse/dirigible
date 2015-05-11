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

package org.eclipse.dirigible.ide.services.security.manager.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import org.eclipse.dirigible.repository.ext.security.SecurityLocationMetadata;

public class SecurityViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private static final long serialVersionUID = 2686057886720931696L;

	public String getColumnText(Object obj, int index) {
		if (obj instanceof SecurityLocationMetadata) {
			SecurityLocationMetadata securityLocationMetadata = (SecurityLocationMetadata) obj;
			if (index == 0) {
				return securityLocationMetadata.getLocation();
			} else if (index == 1) {
				return enumerateRoles(securityLocationMetadata.getRoles());
			}
		}
		return getText(obj);
	}

	private String enumerateRoles(List<String> roles) {
		StringBuilder buff = new StringBuilder();
		int i = 0;
		for (Iterator<String> iterator = roles.iterator(); iterator.hasNext();) {
			if (i++ > 0) {
				buff.append(","); //$NON-NLS-1$
			}
			String role = (String) iterator.next();
			buff.append(role);
		}
		return buff.toString();
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
				.getImage(ISharedImages.IMG_ELCL_STOP);
	}
}
