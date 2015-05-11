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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;

import org.eclipse.dirigible.ide.repository.ui.viewer.ArtifactLabelProvider;

public class WorkspaceLabelProvider extends ArtifactLabelProvider {

	private static final long serialVersionUID = 6141865080631032831L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IResource) {
			IResource resource = (IResource) element;
			return resource.getName();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof IProject) {
			return createImage(TYPE_PROJECT_ICON_URL);
		}
		if (element instanceof IFolder) {
			return getCollectionImageByName(((IFolder) element).getName());
		}
		if (element instanceof IFile) {
			return getResourceImage(((IFile) element).getName());
		}
		return null;
	}

//	private static String getExtension(String filename) {
//		if (filename == null) {
//			return ""; //$NON-NLS-1$
//		}
//		int dotIndex = filename.lastIndexOf("."); //$NON-NLS-1$
//		if (dotIndex != -1) {
//			return filename.substring(dotIndex + 1);
//		} else {
//			return ""; //$NON-NLS-1$
//		}
//	}

}
