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

package org.eclipse.dirigible.ide.editor.text.input;

import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Use the {@link ContentEditorInput} class when passing input to the text
 * editor that is read only and is a result of some code processing (and not a
 * living resource).
 * 
 */
public class ContentEditorInput implements IEditorInput {

	private final String name;

	private final String toolTipText;

	private final byte[] content;

	public ContentEditorInput(String name, String toolTipText, byte[] content) {
		this.name = name;
		this.toolTipText = toolTipText;
		this.content = Arrays.copyOf(content, content.length);
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public boolean exists() {
		return true;
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

	public byte[] getContent() {
		return content; // NOPMD
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ContentEditorInput)) {
			return false;
		}
		final ContentEditorInput other = (ContentEditorInput) obj;
		return getName().equals(other.getName());
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}
