/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common;

public class GenerationException extends Exception {

	private static final long serialVersionUID = 1L;

	public GenerationException() {
		super();
	}

	public GenerationException(String message) {
		super(message);
	}

	public GenerationException(Throwable ex) {
		super(ex);
	}

	public GenerationException(String message, Throwable ex) {
		super(message, ex);
	}

}
