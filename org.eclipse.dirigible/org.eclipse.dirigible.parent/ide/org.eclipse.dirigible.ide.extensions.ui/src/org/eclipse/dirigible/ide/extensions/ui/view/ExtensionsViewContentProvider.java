/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.extensions.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.repository.ext.extensions.EExtensionException;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ExtensionsViewContentProvider implements ITreeContentProvider {

	private static final long serialVersionUID = 1309704265765047023L;

	private static final Logger logger = Logger.getLogger(ExtensionsViewContentProvider.class);

	private ExtensionManager extensionManager;

	public ExtensionsViewContentProvider(ExtensionManager extensionManager) {
		this.extensionManager = extensionManager;
	}

	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		//
	}

	@Override
	public void dispose() {
		//
	}

	@Override
	public Object[] getElements(Object parent) {
		try {
			return toSimpleTreeNode(extensionManager.getExtensionPoints(), null);
		} catch (EExtensionException e) {
			logger.error("Failed to get extension points", e);
		}
		return null;
	}

	private Object[] toSimpleTreeNode(String[] nodes, SimpleTreeNode parent) {
		List<SimpleTreeNode> result = new ArrayList<SimpleTreeNode>();
		if (nodes != null) {
			for (String node : nodes) {
				result.add(new SimpleTreeNode(node, parent));
			}
		}
		return result.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		try {
			return toSimpleTreeNode(extensionManager.getExtensions(parentElement.toString()), (SimpleTreeNode) parentElement);
		} catch (EExtensionException e) {
			logger.error("Failed to get extensions", e);
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return ((SimpleTreeNode) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((SimpleTreeNode) element).isRootElement();
	}

}
