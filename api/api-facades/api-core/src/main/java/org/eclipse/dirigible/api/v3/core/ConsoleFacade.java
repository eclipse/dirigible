/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.core;

import java.util.ArrayList;

/**
 * The ConsoleFacade is used for debug purposes to trace messages into the standard outputs
 */
public class ConsoleFacade {

	private static final Console console = new Console();

	/**
	 * Prints an error message.
	 *
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 */
	public static void error(String message, Object... args) {
		console.error(message, args);
	}

	/**
	 * Prints an info message.
	 *
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 */
	public static void info(String message, Object... args) {
		console.info(message, args);
	}

	/**
	 * Prints a raw log message.
	 *
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 */
	public static void log(String message, Object... args) {
		console.info(message, args);
	}

	/**
	 * Prints a warning message.
	 *
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 */
	public static void warn(String message, Object... args) {
		console.warn(message, args);
	}

	/**
	 * Prints a debug message.
	 *
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 */
	public static void debug(String message, Object... args) {
		console.debug(message, args);
	}

	/**
	 * Prints a trace message.
	 *
	 * @param message
	 *            the message
	 * @param args
	 *            the args
	 */
	public static void trace(String message, Object... args) {
		console.trace(message, args);
	}

	/**
	 * Gets the console instance.
	 *
	 * @return the console
	 */
	public static Console getConsole() {
		return console;
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
	 * Prints an info message.
	 *
	 * @param message
	 *            the message
	 */
	public static void info(String message) {
		console.info(message);
	}

	/**
	 * Prints a raw log message.
	 *
	 * @param message
	 *            the message
	 */
	public static void log(String message) {
		console.log(message);
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

	/**
	 * Prints an error message.
	 *
	 * @param message
	 *            the message
	 */
	public static void error(Object message) {
		console.error(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an info message.
	 *
	 * @param message
	 *            the message
	 */
	public static void info(Object message) {
		console.info(message != null ? message.toString() : "null");
	}

	/**
	 * Prints a raw log message.
	 *
	 * @param message
	 *            the message
	 */
	public static void log(Object message) {
		console.log(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an warning message.
	 *
	 * @param message
	 *            the message
	 */
	public static void warn(Object message) {
		console.warn(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an debug message.
	 *
	 * @param message
	 *            the message
	 */
	public static void debug(Object message) {
		console.debug(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an trace message.
	 *
	 * @param message
	 *            the message
	 */
	public static void trace(Object message) {
		console.trace(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an error message.
	 *
	 * @param message
	 *            the message
	 */
	public static void error(ArrayList message) {
		console.error(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an info message.
	 *
	 * @param message
	 *            the message
	 */
	public static void info(ArrayList message) {
		console.info(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an raw message.
	 *
	 * @param message
	 *            the message
	 */
	public static void log(ArrayList message) {
		console.log(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an warning message.
	 *
	 * @param message
	 *            the message
	 */
	public static void warn(ArrayList message) {
		console.warn(message != null ? message.toString() : "null");
	}

	/**
	 * Prints an debug message.
	 *
	 * @param message
	 *            the message
	 */
	public static void debug(ArrayList message) {
		console.debug(message != null ? message.toString() : "null");
	}

	/**
	 * Prints a trace message.
	 *
	 * @param message
	 *            the message
	 */
	public static void trace(ArrayList message) {
		console.trace(message != null ? message.toString() : "null");
	}

}
