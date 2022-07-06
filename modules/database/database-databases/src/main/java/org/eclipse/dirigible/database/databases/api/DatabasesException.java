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
package org.eclipse.dirigible.database.databases.api;

/**
 * The Databases Exception.
 */
public class DatabasesException extends Exception {

	private static final long serialVersionUID = 5585765264282669358L;

	/**
	 * Instantiates a new databases exception.
	 */
	public DatabasesException() {
		super();
	}

	/**
	 * Instantiates a new databases exception.
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
	public DatabasesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new databases exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public DatabasesException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new databases exception.
	 *
	 * @param message
	 *            the message
	 */
	public DatabasesException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new databases exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public DatabasesException(Throwable cause) {
		super(cause);
	}

}
