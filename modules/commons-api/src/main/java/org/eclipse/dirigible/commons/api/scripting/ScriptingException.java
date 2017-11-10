/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.api.scripting;

public class ScriptingException extends Exception {

	private static final long serialVersionUID = 375339390660073390L;

	public ScriptingException() {
		super();
	}

	public ScriptingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScriptingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptingException(String message) {
		super(message);
	}

	public ScriptingException(Throwable cause) {
		super(cause);
	}

}
