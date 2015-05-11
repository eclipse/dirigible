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

package org.eclipse.dirigible.ide.workspace.ui.shared;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TextContentProvider implements IContentProvider {

	private String text;

	public TextContentProvider() {
		this(""); //$NON-NLS-1$
	}

	public TextContentProvider(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public IValidationStatus validate() {
		return ValidationStatus.createOk();
	}

	public InputStream getContent() {
		return new ByteArrayInputStream(text.getBytes());
	}

}
