/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.log;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author shturec (sthrakal@gmail.com)
 */
public class LogFacade implements IScriptingFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogFacade.class);

	private static final String APP_LOGGER_NAME_PREFX = "app";
	private static final String APP_LOGGER_NAME_SEPARATOR = ".";

	/**
	 * @param url
	 * @return Logger
	 */
	public static final Logger getLogger(final String loggerName) {
		/*
		 * logger names are implicitly prefixed with 'app.' to derive from the applications root logger configuration
		 * for severity and appenders. Null arguments for logger name will be treated as reference to the 'app' logger
		 */
		String appLoggerName = loggerName;
		if (appLoggerName == null) {
			appLoggerName = APP_LOGGER_NAME_PREFX;
		} else {
			appLoggerName = APP_LOGGER_NAME_PREFX + APP_LOGGER_NAME_SEPARATOR + appLoggerName;
		}

		final Logger logger = LoggerFactory.getLogger(appLoggerName);

		return logger;
	}

	public static final void setLevel(String loggerName, String level) {

		final org.slf4j.Logger logger = getLogger(loggerName);

		if (!(logger instanceof ch.qos.logback.classic.Logger)) {
			LOGGER.error("Logger with name {} is not of type " + ch.qos.logback.classic.Logger.class.getName(), loggerName);
			return;
		}

		ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
		logbackLogger.setLevel(ch.qos.logback.classic.Level.valueOf(level));
	}

	private static final ObjectMapper om = new ObjectMapper();
	private static final ArrayType objectArrayType = TypeFactory.defaultInstance().constructArrayType(Object.class);

	public static final void log(String loggerName, String level, String message, String logArguments, String errorJson) throws IOException {

		final Logger logger = getLogger(loggerName);

		Object[] args = null;
		if (logArguments != null) {
			try {
				args = om.readValue(logArguments, objectArrayType);
				if (args.length < 1) {
					args = null;
				}
			} catch (IOException e) {
				LOGGER.error("Cannot parse log arguments[" + logArguments + "] for logger[" + loggerName + "]", e);
			}
		}
		// https://www.slf4j.org/faq.html#paramException
		if (errorJson != null) {
			JSServiceException ex = toException(errorJson);
			if (args == null) {
				args = new Object[] { ex };
			} else {
				args = Arrays.copyOf(args, args.length + 1);
				args[args.length - 1] = ex;
			}
		}

		if (ch.qos.logback.classic.Level.DEBUG.toString().equalsIgnoreCase(level)) {
			logger.debug(message, args);
		} else if (ch.qos.logback.classic.Level.TRACE.toString().equalsIgnoreCase(level)) {
			logger.trace(message, args);
		} else if (ch.qos.logback.classic.Level.INFO.toString().equalsIgnoreCase(level)) {
			logger.info(message, args);
		} else if (ch.qos.logback.classic.Level.WARN.toString().equalsIgnoreCase(level)) {
			logger.warn(message, args);
		} else if (ch.qos.logback.classic.Level.ERROR.toString().equalsIgnoreCase(level)) {
			logger.error(message, args);
		}
	}

	static class ErrorObject {
		@JsonProperty("message")
		public String message;
		@JsonProperty("stack")
		public StackTraceEl[] stack;
	}

	static class StackTraceEl {
		@JsonProperty("fileName")
		public String fileName;
		@JsonProperty("lineNumber")
		public int lineNumber;
		@JsonProperty("declaringClass")
		public String declaringClass;
		@JsonProperty("methodName")
		public String methodName;
	}

	private static JSServiceException toException(String errorJson) throws JsonParseException, JsonMappingException, IOException {

		JSServiceException ex = null;

		if (errorJson != null) {

			ErrorObject errObj = om.readValue(errorJson, ErrorObject.class);

			if (errObj.message == null) {
				ex = new JSServiceException();
			} else {
				ex = new JSServiceException(errObj.message);
			}

			if (errObj.stack != null) {
				StackTraceElement[] stackTraceElementArray = new StackTraceElement[errObj.stack.length];
				for (int i = 0; i < errObj.stack.length; i++) {
					StackTraceEl customStackTraceElement = errObj.stack[i];
					StackTraceElement stackTraceElement = new StackTraceElement(customStackTraceElement.declaringClass,
							customStackTraceElement.methodName, customStackTraceElement.fileName, customStackTraceElement.lineNumber);
					stackTraceElementArray[i] = stackTraceElement;
				}
				ex.setStackTrace(stackTraceElementArray);
			} else {
				ex.setStackTrace(new StackTraceElement[] { new StackTraceElement("", "", null, 1) });
			}

		} else {
			ex = new JSServiceException();
			ex.setStackTrace(new StackTraceElement[0]);
		}

		return ex;
	}

}
