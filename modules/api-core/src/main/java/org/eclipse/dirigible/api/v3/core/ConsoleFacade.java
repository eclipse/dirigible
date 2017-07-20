package org.eclipse.dirigible.api.v3.core;

public class ConsoleFacade {
	
	private static final Console console = new Console();
	
	public static void error(String message, Object... args) {
		console.error(message, args);
	}

	public static void info(String message, Object... args) {
		console.info(message, args);
	}

	public static void log(String message, Object... args) {
		console.info(message, args);
	}

	public static void warn(String message, Object... args) {
		console.warn(message, args);
	}

	public static void debug(String message, Object... args) {
		console.debug(message, args);
	}

	public static void trace(String message, Object... args) {
		console.trace(message, args);
	}
	
	public static Console getConsole() {
		return console;
	}

}
