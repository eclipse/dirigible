/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.git;

/**
 * The GitConnectorException.
 */
public class GitConnectorException extends Exception {

	private static final long serialVersionUID = 3164412135969838078L;

	/**
	 * Instantiates a new git connector exception.
	 */
	public GitConnectorException() {
		super();
	}

	/**
	 * Instantiates a new git connector exception.
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
	public GitConnectorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new git connector exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public GitConnectorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new git connector exception.
	 *
	 * @param message
	 *            the message
	 */
	public GitConnectorException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new git connector exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public GitConnectorException(Throwable cause) {
		super(cause);
	}

}
