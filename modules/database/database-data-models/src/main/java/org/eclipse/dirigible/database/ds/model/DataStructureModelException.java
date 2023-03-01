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
package org.eclipse.dirigible.database.ds.model;

/**
 * Specialized exception for the data models.
 */
public class DataStructureModelException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8008932847050301958L;

	/**
	 * The default constructor.
	 */
	public DataStructureModelException() {
		super();
	}

	/**
	 * Overloaded constructor.
	 *
	 * @param message            the message
	 * @param cause            the cause
	 * @param enableSuppression            whether to enable suppression
	 * @param writableStackTrace            whether to enable writable stack trace
	 */
	public DataStructureModelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Overloaded constructor.
	 *
	 * @param message            the message
	 * @param cause            the cause
	 */
	public DataStructureModelException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Overloaded constructor.
	 *
	 * @param message            the message
	 */
	public DataStructureModelException(String message) {
		super(message);
	}

	/**
	 * Overloaded constructor.
	 *
	 * @param cause            the cause
	 */
	public DataStructureModelException(Throwable cause) {
		super(cause);
	}

}
