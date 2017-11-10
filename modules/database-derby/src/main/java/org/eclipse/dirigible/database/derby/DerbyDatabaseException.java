/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.derby;

import org.eclipse.dirigible.database.api.DatabaseException;

// TODO: Auto-generated Javadoc
/**
 * The Class DerbyDatabaseException.
 */
public class DerbyDatabaseException extends DatabaseException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2161860568272479874L;

	/**
	 * Instantiates a new derby database exception.
	 */
	public DerbyDatabaseException() {
		super();
	}

	/**
	 * Instantiates a new derby database exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public DerbyDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new derby database exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public DerbyDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new derby database exception.
	 *
	 * @param message the message
	 */
	public DerbyDatabaseException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new derby database exception.
	 *
	 * @param cause the cause
	 */
	public DerbyDatabaseException(Throwable cause) {
		super(cause);
	}

}
