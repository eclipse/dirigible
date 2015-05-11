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

package org.eclipse.dirigible.ide.workspace.ui.view.newmenu;

public class NewMenuException extends RuntimeException {
	private static final long serialVersionUID = 564186555025736228L;

	public NewMenuException() {
		super();
	}

	public NewMenuException(String message) {
		super(message);
	}

	public NewMenuException(Throwable ex) {
		super(ex);
	}

	public NewMenuException(String message, Throwable ex) {
		super(message, ex);
	}
}
