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
package org.eclipse.dirigible.api.v3.documents;

/**
 * The Class PDFException.
 */
public class PDFException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8564673107305212988L;

	/**
	 * Instantiates a new pdf exception.
	 */
	public PDFException() {
        super();
    }

	/**
	 * Instantiates a new pdf exception.
	 *
	 * @param message
	 *            the message
	 */
    public PDFException(String message) {
        super(message);
    }

    /**
	 * Instantiates a new pdf exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
    public PDFException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
	 * Instantiates a new pdf exception.
	 *
	 * @param cause
	 *            the cause
	 */
    public PDFException(Throwable cause) {
        super(cause);
    }
}
