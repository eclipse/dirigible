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
package org.eclipse.dirigible.database.ds.model.transfer;

/**
 * The Class InvalidNumberOfElementsException.
 */
public class InvalidNumberOfElementsException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7313570857943932309L;

	/**
	 * Instantiates a new invalid number of elements exception.
	 *
	 * @param message the message
	 */
	public InvalidNumberOfElementsException(String message) {
		super(message);
	}
}
