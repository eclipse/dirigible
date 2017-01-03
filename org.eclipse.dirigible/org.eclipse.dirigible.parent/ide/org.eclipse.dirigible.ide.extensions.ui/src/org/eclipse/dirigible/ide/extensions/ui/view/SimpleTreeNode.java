package org.eclipse.dirigible.ide.extensions.ui.view;

class SimpleTreeNode {

	private String name;
	private SimpleTreeNode parent;

	public SimpleTreeNode(String name, SimpleTreeNode parent) {
		this.name = name;
		this.parent = parent;
	}

	@Override
	public String toString() {
		return name;
	}

	public SimpleTreeNode getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public boolean isRootElement() {
		return getParent() == null;
	}

}
