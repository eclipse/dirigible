/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.messaging.api;

// TODO: Auto-generated Javadoc
/**
 * The Class MessagingException.
 */
public class MessagingException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5800180600419241248L;

	/**
	 * Instantiates a new messaging exception.
	 */
	public MessagingException() {
		super();
	}

	/**
	 * Instantiates a new messaging exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public MessagingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new messaging exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public MessagingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new messaging exception.
	 *
	 * @param message the message
	 */
	public MessagingException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new messaging exception.
	 *
	 * @param cause the cause
	 */
	public MessagingException(Throwable cause) {
		super(cause);
	}

}
