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

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.dirigible.repository.ext.security.SecurityException;
import org.eclipse.dirigible.repository.ext.security.SecurityLocationMetadata;
import org.eclipse.dirigible.repository.logging.Logger;

public class SecurityViewContentProvider implements ITreeContentProvider {

	private static final long serialVersionUID = 1309704265765047023L;

	private static final Logger logger = Logger
			.getLogger(SecurityViewContentProvider.class);

	private SecurityManagerView securityManagerView;

	public SecurityViewContentProvider(SecurityManagerView securityManagerView) {
		this.securityManagerView = securityManagerView;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		//
	}

	public void dispose() {
		//
	}

	public Object[] getElements(Object parent) {
		List<SecurityLocationMetadata> list = null;
		SecurityLocationMetadata[] elements = new SecurityLocationMetadata[] {};
		try {
			list = securityManagerView.getSecurityManager().getAccessList();
			elements = list.toArray(new SecurityLocationMetadata[] {});
		} catch (SecurityException e) {
			logger.error(SecurityManagerView.SECURITY_ERROR, e);
			MessageDialog.openError(this.securityManagerView.getViewer()
					.getControl().getShell(),
					SecurityManagerView.SECURITY_ERROR, e.getMessage());
		}
		return elements;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
}
