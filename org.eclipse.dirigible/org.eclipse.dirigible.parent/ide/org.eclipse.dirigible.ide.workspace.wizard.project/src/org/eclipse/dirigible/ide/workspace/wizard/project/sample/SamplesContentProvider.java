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

package org.eclipse.dirigible.ide.workspace.wizard.project.sample;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SamplesContentProvider implements ITreeContentProvider {
	private static final long serialVersionUID = -217525942777347650L;

	@Override
	public void dispose() {
		//
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] children = new Object[] {};
		if (parentElement instanceof SamplesCategory) {
			SamplesCategory category = (SamplesCategory) parentElement;
			if (category.getCategories().size() > 0) {
				children = category.getCategories().toArray();
			} else {
				children = category.getSamples().toArray();
			}

		}
		return children;
	}

	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof SamplesModel) {
			parent = ((SamplesModel) element).getParent();
		}
		return parent;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
