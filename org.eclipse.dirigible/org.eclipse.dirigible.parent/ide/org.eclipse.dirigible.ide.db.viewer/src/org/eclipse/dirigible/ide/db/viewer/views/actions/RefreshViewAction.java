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

package org.eclipse.dirigible.ide.db.viewer.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.dirigible.ide.db.viewer.views.DatabaseViewContentProvider;
import org.eclipse.dirigible.ide.db.viewer.views.TreeObject;

public class RefreshViewAction extends Action {

	private static final String REFRESH_DATABASE_BROWSER = Messages.RefreshViewAction_REFRESH_DATABASE_BROWSER;

	private static final String REFRESH = Messages.RefreshViewAction_REFRESH;

	private static final long serialVersionUID = -6466267290652926585L;
	private TreeViewer viewer;
	private TreeObject treeObject;

	public RefreshViewAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText(REFRESH);
		setToolTipText(REFRESH_DATABASE_BROWSER);
	}

	public RefreshViewAction(TreeViewer viewer, TreeObject treeObject) {
		this.viewer = viewer;
		this.treeObject = treeObject;
		setText(REFRESH);
		setToolTipText(REFRESH_DATABASE_BROWSER);
	}

	public void run() {
		if (DatabaseViewContentProvider.class.isInstance(viewer
				.getContentProvider())) {
			((DatabaseViewContentProvider) viewer.getContentProvider())
					.requestRefreshContent();
		}
		viewer.refresh();
		if (treeObject != null) {
			viewer.expandToLevel(treeObject, 100);
		}
	}
}
