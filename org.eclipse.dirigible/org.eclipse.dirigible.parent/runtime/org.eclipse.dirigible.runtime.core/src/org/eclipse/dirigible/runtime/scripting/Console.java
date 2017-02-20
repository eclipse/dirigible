package org.eclipse.dirigible.runtime.scripting;

import org.eclipse.dirigible.repository.logging.Logger;

public class Console {

	private static Logger logger = Logger.getLogger(Console.class);

	public void error(String message, Object... args) {
		if (args.length > 0) {
			logger.error(String.format(message, args));
		} else {
			logger.error(message);
		}
	}

	public void info(String message, Object... args) {
		if (args.length > 0) {
			logger.info(String.format(message, args));
		} else {
			logger.info(message);
		}
	}

	public void log(String message, Object... args) {
		logger.info(message);
		for (Object arg : args) {
			logger.info(arg != null ? arg.toString() : null);
		}
	}

	public void warn(String message, Object... args) {
		if (args.length > 0) {
			logger.warn(String.format(message, args));
		} else {
			logger.warn(message);
		}
	}

	public void debug(String message, Object... args) {
		if (args.length > 0) {
			logger.debug(String.format(message, args));
		} else {
			logger.debug(message);
		}
	}

	public void trace(String message, Object... args) {
		if (args.length > 0) {
			logger.error(String.format(message, args));
		} else {
			logger.error(message);
		}
		StringBuilder buff = new StringBuilder();
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			buff.append(element.toString()).append(System.getProperty("line.separator"));
		}
		logger.error(buff.toString());
	}

}
