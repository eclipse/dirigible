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

package org.eclipse.dirigible.ide.workspace.ui.viewer;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ReservedFolderFilter extends ViewerFilter {

	private static final long serialVersionUID = 4273811741225537399L;
	private String folderName;

	public ReservedFolderFilter(String folderName) {
		this.folderName = folderName;

	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IProject) {
			return true;
		}
		if (element instanceof IFolder) {
			IFolder iFolder = (IFolder) element;
			if (iFolder.getParent() instanceof IProject) {
				return folderName.equals(iFolder.getName());
			}
			return true;
		}
		return false;
	}

}
