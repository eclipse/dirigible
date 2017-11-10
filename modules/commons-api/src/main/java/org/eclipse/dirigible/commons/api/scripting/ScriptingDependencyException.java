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

public class ScriptingDependencyException extends ScriptingException {

	private static final long serialVersionUID = -7175996091072301851L;

	public ScriptingDependencyException() {
		super();
	}

	public ScriptingDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScriptingDependencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptingDependencyException(String message) {
		super(message);
	}

	public ScriptingDependencyException(Throwable cause) {
		super(cause);
	}

}
