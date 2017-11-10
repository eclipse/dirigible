/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.extensions.api;

public class ExtensionsException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public ExtensionsException() {
		super();
	}

	public ExtensionsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExtensionsException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExtensionsException(String message) {
		super(message);
	}

	public ExtensionsException(Throwable cause) {
		super(cause);
	}

}
