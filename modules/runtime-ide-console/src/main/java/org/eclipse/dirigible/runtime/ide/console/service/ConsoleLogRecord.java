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
