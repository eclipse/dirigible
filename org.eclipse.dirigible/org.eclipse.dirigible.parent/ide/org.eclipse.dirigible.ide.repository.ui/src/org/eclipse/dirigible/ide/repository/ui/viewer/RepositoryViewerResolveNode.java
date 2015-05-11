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

package org.eclipse.dirigible.ide.repository.ui.viewer;

public class RepositoryViewerResolveNode {

	private static final String CLICK_TO_RESOLVE = Messages.RepositoryViewerResolveNode_CLICK_TO_RESOLVE;

	private final Object parent;

	private final String text;

	public RepositoryViewerResolveNode() {
		this((Object) null);
	}

	public RepositoryViewerResolveNode(Object parent) {
		this(parent, CLICK_TO_RESOLVE); // TODO: I18N
	}

	public RepositoryViewerResolveNode(String text) {
		this(null, text);
	}

	public RepositoryViewerResolveNode(Object parent, String text) {
		this.parent = parent;
		this.text = text;
	}

	public Object getParent() {
		return parent;
	}

	public String getText() {
		return text;
	}

}
