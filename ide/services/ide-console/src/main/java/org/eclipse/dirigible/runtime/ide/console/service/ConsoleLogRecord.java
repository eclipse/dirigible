/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.ide.console.service;

/**
 * The Console Log Record transport object.
 */
public class ConsoleLogRecord {

	private String level;

	private String message;

	private long timestamp;

	/**
	 * Instantiates a new console log record.
	 *
	 * @param level
	 *            the level
	 * @param message
	 *            the message
	 * @param timestamp
	 *            the timestamp
	 */
	public ConsoleLogRecord(String level, String message, long timestamp) {
		super();
		this.level = level;
		this.message = message;
		this.timestamp = timestamp;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

}
