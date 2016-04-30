package org.eclipse.dirigible.runtime.scripting;

import org.eclipse.dirigible.repository.logging.Logger;

public class Console {

	private static Logger logger = Logger.getLogger(Console.class);

	public void error(String message, Object... args) {
		logger.error(String.format(message, args));
	}

	public void info(String message, Object... args) {
		logger.info(String.format(message, args));
	}

	public void log(String message, Object... args) {
		logger.info(String.format(message, args));
	}

	public void warn(String message, Object... args) {
		logger.warn(String.format(message, args));
	}

	public void trace(String message, Object... args) {
		logger.error(String.format(message, args));
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			logger.error(element.toString());
		}
	}

}
