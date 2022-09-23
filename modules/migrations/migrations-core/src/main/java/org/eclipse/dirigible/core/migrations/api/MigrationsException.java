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
package org.eclipse.dirigible.core.migrations.api;

/**
 * The Migrations Exception.
 */
public class MigrationsException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5800180600419241248L;

	/**
	 * Instantiates a new migrations exception.
	 */
	public MigrationsException() {
		super();
	}

	/**
	 * Instantiates a new migrations exception.
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
	public MigrationsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new migrations exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public MigrationsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new migrations exception.
	 *
	 * @param message
	 *            the message
	 */
	public MigrationsException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new migrations exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public MigrationsException(Throwable cause) {
		super(cause);
	}

}
