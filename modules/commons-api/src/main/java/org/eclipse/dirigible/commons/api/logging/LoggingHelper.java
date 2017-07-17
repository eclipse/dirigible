package org.eclipse.dirigible.commons.api.logging;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHelper {
	
//	private static final String SECTION = "=========================================";
	
//	private static final String GROUP = "-----------------------------------------";

	private static final String POINTER = "-> ";

	private Logger logger;
	
	private volatile int indent = 0;
	
	public LoggingHelper() {
		this.logger = LoggerFactory.getLogger(LoggingHelper.class);
	}
	
	public LoggingHelper(Logger logger) {
		this.logger = logger;
	}
	
	public void beginSection(String section) {
//		logger.info(addIndent() + SECTION);
		logger.info(addIndent() + section);
//		logger.info(addIndent() + SECTION);
		indent++;
	}
	
	public void endSection(String section) {
		indent--;
//		logger.info(addIndent() + SECTION);
		logger.info(addIndent() + section);
//		logger.info(addIndent() + SECTION);
	}
	
	public void beginGroup(String section) {
//		logger.info(addIndent() + GROUP);
		logger.info(addIndent() + section);
//		logger.info(addIndent() + GROUP);
		indent++;
	}
	
	public void endGroup(String section) {
		indent--;
//		logger.info(addIndent() + GROUP);
		logger.info(addIndent() + section);
//		logger.info(addIndent() + GROUP);
	}
	
	public void beginGroupDebug(String group) {
//		logger.debug(addIndent() + GROUP);
		logger.debug(addIndent() + group);
//		logger.debug(addIndent() + GROUP);
		indent++;
	}
	
	public void endGroupDebug(String group) {
		indent--;
//		logger.debug(addIndent() + GROUP);
		logger.debug(addIndent() + group);
//		logger.debug(addIndent() + GROUP);
	}
	
	private String addIndent() {
		return Arrays.asList(new Object[indent])
	            .stream()
	            .map(s -> "- ")
	            .collect(Collectors.joining());
	}
	
	public void info(String message, Object...args) {
		logger.info(addIndent() + POINTER + message, args);
	}
	
	public void warn(String message, Object...args) {
		logger.warn(addIndent() + POINTER + message, args);
	}
	
	public void error(String message, Object...args) {
		logger.error(addIndent() + POINTER + message, args);
	}
	
	public void debug(String message, Object...args) {
		logger.debug(addIndent() + POINTER + message, args);
	}

}
