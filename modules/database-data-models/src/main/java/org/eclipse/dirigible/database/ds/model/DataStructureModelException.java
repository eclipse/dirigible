/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.model;

/**
 * Specialized exception for the data models
 */
public class DataStructureModelException extends Exception {

	private static final long serialVersionUID = 8008932847050301958L;

	/**
	 * The default constructor
	 */
	public DataStructureModelException() {
		super();
	}

	/**
	 * Overloaded constructor
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param enableSuppression
	 *            whether to enable suppression
	 * @param writableStackTrace
	 *            whether to enable writable stack trace
	 */
	public DataStructureModelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Overloaded constructor
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public DataStructureModelException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Overloaded constructor
	 *
	 * @param message
	 *            the message
	 */
	public DataStructureModelException(String message) {
		super(message);
	}

	/**
	 * Overloaded constructor
	 *
	 * @param cause
	 *            the cause
	 */
	public DataStructureModelException(Throwable cause) {
		super(cause);
	}

}
