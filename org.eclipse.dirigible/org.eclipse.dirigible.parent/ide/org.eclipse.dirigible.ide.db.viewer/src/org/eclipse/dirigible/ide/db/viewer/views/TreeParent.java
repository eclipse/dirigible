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

package org.eclipse.dirigible.ide.db.viewer.views;

import java.util.ArrayList;

public class TreeParent extends TreeObject {
	private ArrayList<TreeObject> children;
	private IDbConnectionFactory connectionFactory;

	public TreeParent(String name, IDbConnectionFactory connectionFactory) {
		super(name);
		this.connectionFactory = connectionFactory;
		children = new ArrayList<TreeObject>();
	}

	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}

	public TreeObject[] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public IDbConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	public int hashCode() {
		int toCode = super.hashCode();
		if (children != null && children.size() > 0) {
			for (TreeObject to : children) {
				toCode += (to != null ? to.hashCode() : 0);
			}
		}
		return toCode;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!TreeParent.class.isInstance(arg0)) {
			return false;
		}
		TreeParent tp = (TreeParent) arg0;
		if (super.equals(arg0)) {
			boolean compareChildren = compareChildren(tp.getChildren(),
					getChildren());
			if (!compareChildren) {
				// uncomment to debug
				// System.out.println(tp.getName() + " vs " + getName() +" : "+
				// compareChildren);
			}
			return compareChildren;
		}
		return false;
	}

	private boolean compareChildren(TreeObject[] childrenHome,
			TreeObject[] childrenAway) {
		if (childrenAway != null && childrenHome != null) {
			if (childrenAway.length == childrenHome.length) {
				boolean f = false;
				for (TreeObject treeObjectA : childrenAway) {
					f = false;
					for (TreeObject treeObjectH : childrenHome) {
						if (TreeParent.class.isInstance(treeObjectH)
								&& TreeParent.class.isInstance(treeObjectA)) {
							f = treeObjectA.getName().equals(
									treeObjectH.getName());
						} else {
							f = treeObjectH != null
									&& treeObjectH.equals(treeObjectA);
						}
						if (f) {
							break;
						}
					}
					if (!f) {
						return false;
					}
				}
				return true;
			}
		} else {
			return childrenHome == childrenAway;
		}
		return false;
	}
}