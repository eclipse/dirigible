/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.publisher.api;

/**
 * The PublisherException.
 */
public class PublisherException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5800180600419241248L;

	/**
	 * Instantiates a new publisher exception.
	 */
	public PublisherException() {
		super();
	}

	/**
	 * Instantiates a new publisher exception.
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
	public PublisherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new publisher exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public PublisherException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new publisher exception.
	 *
	 * @param message
	 *            the message
	 */
	public PublisherException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new publisher exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public PublisherException(Throwable cause) {
		super(cause);
	}

}
