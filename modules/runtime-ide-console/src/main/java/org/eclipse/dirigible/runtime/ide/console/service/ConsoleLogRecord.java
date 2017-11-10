/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.ide.console.service;

public class ConsoleLogRecord {
	
	private String level;
	
	private String message;
	
	private long timestamp;

	public ConsoleLogRecord(String level, String message, long timestamp) {
		super();
		this.level = level;
		this.message = message;
		this.timestamp = timestamp;
	}

	public String getLevel() {
		return level;
	}
	
	public String getMessage() {
		return message;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
}
