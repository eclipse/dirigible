/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.api.scripting;

/**
 * The ScriptingException.
 */
public class ScriptingException extends RuntimeException {

	private static final long serialVersionUID = 375339390660073390L;

	/**
	 * Instantiates a new scripting exception.
	 */
	public ScriptingException() {
		super();
	}

	/**
	 * Instantiates a new scripting exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param enableSuppression
	 *            the enable suppression
	 * @param writableStackTrace
	 *            the writable stack trace
	 */
	public ScriptingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new scripting exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ScriptingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new scripting exception.
	 *
	 * @param message
	 *            the message
	 */
	public ScriptingException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new scripting exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public ScriptingException(Throwable cause) {
		super(cause);
	}

}
