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
package org.eclipse.dirigible.api.v3.log;

/**
 * The Class JSServiceException.
 */
public class JSServiceException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7966059602671226771L;

	/**
	 * Instantiates a new JS service exception.
	 */
	public JSServiceException() {
	}

	/**
	 * Instantiates a new JS service exception.
	 *
	 * @param message the message
	 */
	public JSServiceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new JS service exception.
	 *
	 * @param message the message
	 * @param t the throwable
	 */
	public JSServiceException(String message, Throwable t) {
		super(message, t);
	}

}
