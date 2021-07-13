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
	 * Prints a log message
	 *
	 * @param message
	 *            the log message
	 */
	public void log(String message) {
		logger.info(message);
	}

	/**
	 * Prints an information message
	 *
	 * @param message
	 *            the log message
	 */
	public void info(String message) {
		logger.info(message);
	}

	/**
	 * Prints a warning message
	 *
	 * @param message
	 *            the log message
	 */
	public void warn(String message) {
		logger.warn(message);
	}

	/**
	 * Prints an error message
	 *
	 * @param message
	 *            the log message
	 */
	public void error(String message) {
		logger.error(message);
	}

	/**
	 * Prints a debug message
	 *
	 * @param message
	 *            the log message
	 */
	public void debug(String message) {
		logger.debug(message);
	}

	/**
	 * Prints a trace message
	 *
	 * @param message
	 *            the log message
	 */
	public void trace(String message) {
		logger.trace(message);
	}

}
