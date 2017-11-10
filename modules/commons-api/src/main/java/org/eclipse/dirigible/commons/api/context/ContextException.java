/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.api.context;

// TODO: Auto-generated Javadoc
/**
 * The Class ContextException.
 */
public class ContextException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5039208772641246649L;

	/**
	 * Instantiates a new context exception.
	 */
	public ContextException() {
		super();
	}

	/**
	 * Instantiates a new context exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public ContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new context exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ContextException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new context exception.
	 *
	 * @param message the message
	 */
	public ContextException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new context exception.
	 *
	 * @param cause the cause
	 */
	public ContextException(Throwable cause) {
		super(cause);
	}

}
