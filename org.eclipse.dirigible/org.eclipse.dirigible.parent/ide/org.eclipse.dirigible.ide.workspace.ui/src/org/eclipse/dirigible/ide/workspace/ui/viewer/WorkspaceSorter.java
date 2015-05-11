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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class WorkspaceSorter extends ViewerSorter {

	private static final long serialVersionUID = -6643939337586499946L;

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if ((e2 instanceof IContainer) && (e1 instanceof IResource)
				&& !(e1 instanceof IContainer)) {
			return 1;
		}
		if ((e1 instanceof IContainer) && (e2 instanceof IResource)
				&& !(e2 instanceof IContainer)) {
			return -1;
		}
		if ((e1 instanceof IResource) && (e2 instanceof IResource)) {
			final IResource resource1 = (IResource) e1;
			final IResource resource2 = (IResource) e2;
			final String path1 = resource1.getFullPath().toString();
			final String path2 = resource2.getFullPath().toString();
			return path1.compareTo(path2);
		}
		return 0;
	}

}
