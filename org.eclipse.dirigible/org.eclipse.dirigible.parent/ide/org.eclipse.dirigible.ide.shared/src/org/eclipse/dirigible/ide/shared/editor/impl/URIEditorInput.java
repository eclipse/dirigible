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

package org.eclipse.dirigible.ide.shared.editor.impl;

import java.net.URI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IURIEditorInput;

public class URIEditorInput implements IURIEditorInput {

	private final URI uri;

	private final String name;

	private final String toolTipText;

	public URIEditorInput(URI uri) {
		this.uri = uri;
		this.name = extractNameFromUri(uri);
		this.toolTipText = uri.toString();
	}

	public URIEditorInput(URI uri, String name) {
		this.uri = uri;
		this.name = name;
		this.toolTipText = uri.toString();
	}

	public URIEditorInput(URI uri, String name, String toolTipText) {
		this.uri = uri;
		this.name = name;
		this.toolTipText = toolTipText;
	}

	private static String extractNameFromUri(URI uri) {
		String location = uri.toString();
		int lastSlashIndex = location.lastIndexOf('/');
		if (lastSlashIndex != -1 && lastSlashIndex != location.length() - 1) {
			return location.substring(lastSlashIndex + 1);
		} else {
			return location;
		}
	}

	public boolean exists() {
		return true; // XXX: Not clean...
	}

	public ImageDescriptor getImageDescriptor() {
		return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return toolTipText;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public URI getURI() {
		return uri;
	}

}
