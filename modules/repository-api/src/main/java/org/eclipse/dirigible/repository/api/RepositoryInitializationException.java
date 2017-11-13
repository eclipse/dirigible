/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.api;

/**
 * The Class RepositoryInitializationException.
 */
public class RepositoryInitializationException extends RepositoryException {

	private static final long serialVersionUID = -163847774919514248L;

	/**
	 * Instantiates a new repository initialization exception.
	 */
	public RepositoryInitializationException() {
		super();
	}

	/**
	 * Instantiates a new repository initialization exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public RepositoryInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new repository initialization exception.
	 *
	 * @param message
	 *            the message
	 */
	public RepositoryInitializationException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new repository initialization exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public RepositoryInitializationException(Throwable cause) {
		super(cause);
	}

}
