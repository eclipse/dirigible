/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.config;

public class TestModeException extends Exception {
	
	private static final long serialVersionUID = -7766343853084847849L;
	
	public TestModeException() {
		super();
	}

	public TestModeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TestModeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TestModeException(String message) {
		super(message);
	}

	public TestModeException(Throwable cause) {
		super(cause);
	}

}
