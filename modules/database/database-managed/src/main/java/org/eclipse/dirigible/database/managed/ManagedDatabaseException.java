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
package org.eclipse.dirigible.database.managed;

import org.eclipse.dirigible.database.api.DatabaseException;

/**
 * The Managed Database Exception.
 */
public class ManagedDatabaseException extends DatabaseException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2161860568272479874L;

	/**
	 * Instantiates a new managed database exception.
	 */
	public ManagedDatabaseException() {
		super();
	}

	/**
	 * Instantiates a new managed database exception.
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
	public ManagedDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new managed database exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ManagedDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new managed database exception.
	 *
	 * @param message
	 *            the message
	 */
	public ManagedDatabaseException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new managed database exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public ManagedDatabaseException(Throwable cause) {
		super(cause);
	}

}
