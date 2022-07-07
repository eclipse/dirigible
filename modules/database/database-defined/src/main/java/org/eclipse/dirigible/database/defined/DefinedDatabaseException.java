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
package org.eclipse.dirigible.database.defined;

import org.eclipse.dirigible.database.api.DatabaseException;

/**
 * The Defined Database Exception.
 */
public class DefinedDatabaseException extends DatabaseException {

	private static final long serialVersionUID = -2161860568272479874L;

	/**
	 * Instantiates a new defined database exception.
	 */
	public DefinedDatabaseException() {
		super();
	}

	/**
	 * Instantiates a new defined database exception.
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
	public DefinedDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new defined database exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public DefinedDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new defined database exception.
	 *
	 * @param message
	 *            the message
	 */
	public DefinedDatabaseException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new defined database exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public DefinedDatabaseException(Throwable cause) {
		super(cause);
	}

}
