package org.eclipse.dirigible.api.v3.core;

import java.util.ArrayList;

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

	public static void error(String message) {
		console.error(message);
	}

	public static void info(String message) {
		console.info(message);
	}

	public static void log(String message) {
		console.info(message);
	}

	public static void warn(String message) {
		console.warn(message);
	}

	public static void debug(String message) {
		console.debug(message);
	}

	public static void trace(String message) {
		console.trace(message);
	}

	public static void error(Object message) {
		console.error(message != null ? message.toString() : "null");
	}

	public static void info(Object message) {
		console.info(message != null ? message.toString() : "null");
	}

	public static void log(Object message) {
		console.info(message != null ? message.toString() : "null");
	}

	public static void warn(Object message) {
		console.warn(message != null ? message.toString() : "null");
	}

	public static void debug(Object message) {
		console.debug(message != null ? message.toString() : "null");
	}

	public static void trace(Object message) {
		console.trace(message != null ? message.toString() : "null");
	}

	public static void error(ArrayList message) {
		console.error(message != null ? message.toString() : "null");
	}

	public static void info(ArrayList message) {
		console.info(message != null ? message.toString() : "null");
	}

	public static void log(ArrayList message) {
		console.info(message != null ? message.toString() : "null");
	}

	public static void warn(ArrayList message) {
		console.warn(message != null ? message.toString() : "null");
	}

	public static void debug(ArrayList message) {
		console.debug(message != null ? message.toString() : "null");
	}

	public static void trace(ArrayList message) {
		console.trace(message != null ? message.toString() : "null");
	}

}
