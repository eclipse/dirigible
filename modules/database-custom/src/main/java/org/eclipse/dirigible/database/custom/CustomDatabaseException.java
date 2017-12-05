/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.custom;

import org.eclipse.dirigible.database.api.DatabaseException;

/**
 * The Custom Database Exception.
 */
public class CustomDatabaseException extends DatabaseException {

	private static final long serialVersionUID = -2161860568272479874L;

	/**
	 * Instantiates a new custom database exception.
	 */
	public CustomDatabaseException() {
		super();
	}

	/**
	 * Instantiates a new custom database exception.
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
	public CustomDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new custom database exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public CustomDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new custom database exception.
	 *
	 * @param message
	 *            the message
	 */
	public CustomDatabaseException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new custom database exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public CustomDatabaseException(Throwable cause) {
		super(cause);
	}

}
