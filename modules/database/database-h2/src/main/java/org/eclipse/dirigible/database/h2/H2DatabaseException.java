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
package org.eclipse.dirigible.database.h2;

import org.eclipse.dirigible.database.api.DatabaseException;

/**
 * The H2 Database Exception.
 */
public class H2DatabaseException extends DatabaseException {

	private static final long serialVersionUID = -2161860568272479874L;

	/**
	 * Instantiates a new H2 database exception.
	 */
	public H2DatabaseException() {
		super();
	}

	/**
	 * Instantiates a new H2 database exception.
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
	public H2DatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new H2 database exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public H2DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new H2 database exception.
	 *
	 * @param message
	 *            the message
	 */
	public H2DatabaseException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new H2 database exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public H2DatabaseException(Throwable cause) {
		super(cause);
	}

}
