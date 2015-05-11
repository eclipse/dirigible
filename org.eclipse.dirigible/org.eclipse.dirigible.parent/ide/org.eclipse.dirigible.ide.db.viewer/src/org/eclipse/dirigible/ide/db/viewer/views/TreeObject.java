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

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {

	private String name;
	private TreeParent parent;
	private TableDefinition tableDefinition = null;

	public TreeObject(String name) {
		this(name, null);
	}

	public TreeObject(String name, TableDefinition tableDefinition) {
		this.name = name;
		this.tableDefinition = tableDefinition;
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class key) {
		return null;
	}

	public TableDefinition getTableDefinition() {
		return tableDefinition;
	}

	@Override
	public int hashCode() {
		return name.hashCode()
				+ (tableDefinition != null ? tableDefinition.hashCode() : 0);
	}

	@Override
	public boolean equals(Object arg0) {
		if (!TreeObject.class.isInstance(arg0)) {
			return false;
		}
		TreeObject to = (TreeObject) arg0;
		if (to.getName() != null) {
			if (to.getName().equals(getName())) {
				if (to.getTableDefinition() == getTableDefinition()) {
					return true;
				} else {
					if (to.getTableDefinition() != null) {
						return to.getTableDefinition().equals(
								getTableDefinition());
					}
				}
			}
		}
		return super.equals(arg0);
	}

}
