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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * This is a filter that allows only {@link IContainer} elements to be displayed
 * in a viewer.
 * 
 */
public class WorkspaceContainerFilter extends ViewerFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2262191725453283162L;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return (element instanceof IContainer);
	}

}
