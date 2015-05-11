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

package org.eclipse.dirigible.ide.editor.text.editor;

@SuppressWarnings("serial")
public class ContentProviderException extends Exception {

	public ContentProviderException() {
		super();
	}

	public ContentProviderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContentProviderException(String message) {
		super(message);
	}

	public ContentProviderException(Throwable cause) {
		super(cause);
	}

}
