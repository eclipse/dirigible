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
package org.eclipse.dirigible.core.websockets.api;

/**
 * The Class WebsocketsException.
 */
public class WebsocketsException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5800180600419241248L;

	/**
	 * Instantiates a new websockets exception.
	 */
	public WebsocketsException() {
		super();
	}

	/**
	 * Instantiates a new websockets exception.
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
	public WebsocketsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new websockets exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public WebsocketsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new websockets exception.
	 *
	 * @param message
	 *            the message
	 */
	public WebsocketsException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new websockets exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public WebsocketsException(Throwable cause) {
		super(cause);
	}

}
