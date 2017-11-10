/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.api.context;

public class InvalidStateException extends RuntimeException {

	private static final long serialVersionUID = 5039208772641246649L;

	public InvalidStateException() {
		super();
	}

	public InvalidStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidStateException(String message) {
		super(message);
	}

	public InvalidStateException(Throwable cause) {
		super(cause);
	}

}
