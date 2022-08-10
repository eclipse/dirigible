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
 * The ScriptingDependencyException.
 */
public class ScriptingDependencyException extends ScriptingException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7175996091072301851L;

	/**
	 * Instantiates a new scripting dependency exception.
	 */
	public ScriptingDependencyException() {
		super();
	}

	/**
	 * Instantiates a new scripting dependency exception.
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
	public ScriptingDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new scripting dependency exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ScriptingDependencyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new scripting dependency exception.
	 *
	 * @param message
	 *            the message
	 */
	public ScriptingDependencyException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new scripting dependency exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public ScriptingDependencyException(Throwable cause) {
		super(cause);
	}

}
