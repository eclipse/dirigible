/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.api.v3.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Console logger class used in the {@link ConsoleFacade}
 */
public class Console {

	Console() {
	}

	private static Logger logger = LoggerFactory.getLogger(Console.class);

	/**
	 * Prints an error message
	 *
	 * @param message
	 *            the log message
	 * @param args
	 *            the message arguments
	 */
	public void error(String message, Object... args) {
		logger.error(String.format(message, args));
	}

	/**
	 * Prints an information message
	 *
	 * @param message
	 *            the log message
	 * @param args
	 *            the message arguments
	 */
	public void info(String message, Object... args) {
		logger.info(String.format(message, args));
	}

	/**
	 * Prints a warning message
	 *
	 * @param message
	 *            the log message
	 * @param args
	 *            the message arguments
	 */
	public void warn(String message, Object... args) {
		logger.warn(String.format(message, args));
	}

	/**
	 * Prints a debug message
	 *
	 * @param message
	 *            the log message
	 * @param args
	 *            the message arguments
	 */
	public void debug(String message, Object... args) {
		logger.debug(String.format(message, args));
	}

	/**
	 * Prints a trace message
	 *
	 * @param message
	 *            the log message
	 * @param args
	 *            the message arguments
	 */
	public void trace(String message, Object... args) {
		logger.error(String.format(message, args));
		StringBuilder buff = new StringBuilder();
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			buff.append(element.toString()).append(System.getProperty("line.separator"));
		}
		logger.error(buff.toString());
	}

	/**
	 * Prints a raw log message
	 *
	 * @param message
	 *            the log message
	 */
	public void log(String message) {
		logger.info(message);
	}

}
