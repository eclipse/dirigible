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

// TODO: Auto-generated Javadoc
/**
 * The Class ConsoleFacade.
 */
public class ConsoleFacade {

	/** The Constant console. */
	private static final Console console = new Console();

	/**
	 * Error.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public static void error(String message, Object... args) {
		console.error(message, args);
	}

	/**
	 * Info.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public static void info(String message, Object... args) {
		console.info(message, args);
	}

	/**
	 * Log.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public static void log(String message, Object... args) {
		console.info(message, args);
	}

	/**
	 * Warn.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public static void warn(String message, Object... args) {
		console.warn(message, args);
	}

	/**
	 * Debug.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public static void debug(String message, Object... args) {
		console.debug(message, args);
	}

	/**
	 * Trace.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public static void trace(String message, Object... args) {
		console.trace(message, args);
	}

	/**
	 * Gets the console.
	 *
	 * @return the console
	 */
	public static Console getConsole() {
		return console;
	}

	/**
	 * Error.
	 *
	 * @param message the message
	 */
	public static void error(String message) {
		console.error(message);
	}

	/**
	 * Info.
	 *
	 * @param message the message
	 */
	public static void info(String message) {
		console.info(message);
	}

	/**
	 * Log.
	 *
	 * @param message the message
	 */
	public static void log(String message) {
		console.log(message);
	}

	/**
	 * Warn.
	 *
	 * @param message the message
	 */
	public static void warn(String message) {
		console.warn(message);
	}

	/**
	 * Debug.
	 *
	 * @param message the message
	 */
	public static void debug(String message) {
		console.debug(message);
	}

	/**
	 * Trace.
	 *
	 * @param message the message
	 */
	public static void trace(String message) {
		console.trace(message);
	}

	/**
	 * Error.
	 *
	 * @param message the message
	 */
	public static void error(Object message) {
		console.error(message != null ? message.toString() : "null");
	}

	/**
	 * Info.
	 *
	 * @param message the message
	 */
	public static void info(Object message) {
		console.info(message != null ? message.toString() : "null");
	}

	/**
	 * Log.
	 *
	 * @param message the message
	 */
	public static void log(Object message) {
		console.log(message != null ? message.toString() : "null");
	}

	/**
	 * Warn.
	 *
	 * @param message the message
	 */
	public static void warn(Object message) {
		console.warn(message != null ? message.toString() : "null");
	}

	/**
	 * Debug.
	 *
	 * @param message the message
	 */
	public static void debug(Object message) {
		console.debug(message != null ? message.toString() : "null");
	}

	/**
	 * Trace.
	 *
	 * @param message the message
	 */
	public static void trace(Object message) {
		console.trace(message != null ? message.toString() : "null");
	}

	/**
	 * Error.
	 *
	 * @param message the message
	 */
	public static void error(ArrayList message) {
		console.error(message != null ? message.toString() : "null");
	}

	/**
	 * Info.
	 *
	 * @param message the message
	 */
	public static void info(ArrayList message) {
		console.info(message != null ? message.toString() : "null");
	}

	/**
	 * Log.
	 *
	 * @param message the message
	 */
	public static void log(ArrayList message) {
		console.log(message != null ? message.toString() : "null");
	}

	/**
	 * Warn.
	 *
	 * @param message the message
	 */
	public static void warn(ArrayList message) {
		console.warn(message != null ? message.toString() : "null");
	}

	/**
	 * Debug.
	 *
	 * @param message the message
	 */
	public static void debug(ArrayList message) {
		console.debug(message != null ? message.toString() : "null");
	}

	/**
	 * Trace.
	 *
	 * @param message the message
	 */
	public static void trace(ArrayList message) {
		console.trace(message != null ? message.toString() : "null");
	}

}
