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

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.dirigible.repository.logging.Logger;

public class GenericListViewContentProvider implements ITreeContentProvider {

	private static final long serialVersionUID = 1309704265765047023L;

	private static final Logger logger = Logger
			.getLogger(GenericListViewContentProvider.class);

	private GenericListView genericListView;

	public GenericListViewContentProvider(GenericListView genericListView) {
		this.genericListView = genericListView;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		//
	}

	public void dispose() {
		//
	}

	public Object[] getElements(Object parent) {
		List<GenericLocationMetadata> list = null;
		GenericLocationMetadata[] elements = new GenericLocationMetadata[] {};
		try {
			list = genericListView.getGenericListManager().getLocationsList();
			elements = list.toArray(new GenericLocationMetadata[] {});
		} catch (Exception e) {
			logger.error(GenericListView.GENERIC_VIEW_ERROR, e);
			MessageDialog.openError(this.genericListView.getViewer()
					.getControl().getShell(),
					GenericListView.GENERIC_VIEW_ERROR, e.getMessage());
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
