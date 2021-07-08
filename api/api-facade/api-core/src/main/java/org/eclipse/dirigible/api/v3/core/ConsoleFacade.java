/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.core;

/**
 * The ConsoleFacade is used for debug purposes to trace messages into the standard outputs
 */
public class ConsoleFacade {

	private static final Console console = new Console();

	/**
	 * Prints a log message.
	 *
	 * @param message
	 *            the message
	 */
	public static void log(String message) {
		console.log(message);
	}

	/**
	 * Prints an info message.
	 *
	 * @param message
	 *            the message
	 */
	public static void info(String message) {
		console.info(message);
	}

	/**
	 * Prints a warning message.
	 *
	 * @param message
	 *            the message
	 */
	public static void warn(String message) {
		console.warn(message);
	}

	/**
	 * Prints an error message.
	 *
	 * @param message
	 *            the message
	 */
	public static void error(String message) {
		console.error(message);
	}
	

	/**
	 * Prints a debug message.
	 *
	 * @param message
	 *            the message
	 */
	public static void debug(String message) {
		console.debug(message);
	}
	
	/**
	 * Prints a trace message.
	 *
	 * @param message
	 *            the message
	 */
	public static void trace(String message) {
		console.trace(message);
	}
}
