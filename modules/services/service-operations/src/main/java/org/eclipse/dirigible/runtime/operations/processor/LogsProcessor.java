/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.operations.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.runtime.operations.service.LogInfo;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LogsProcessor {
	
	private static final String DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT = "DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT";
	private static final String CATALINA_BASE = "CATALINA_BASE";
	private static final String CATALINA_HOME = "CATALINA_HOME";
	private static final String DEFAULT_LOGS_FOLDER = "logs";
	private static final String DEFAULT_LOGS_LOCATION = ".." + File.separator +DEFAULT_LOGS_FOLDER;
	
	public LogsProcessor() {
		Configuration.loadModuleConfig("/dirigible-operations.properties");
	}
	
	public String list() throws IOException {
		String logsFolder = getLogsLocation();
		List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(logsFolder))) {
            for (Path path : directoryStream) {
                String name = path.toString();
				fileNames.add(name.substring(name.lastIndexOf(File.separator) + 1));
            }
        } catch (IOException e) {
        	throw e;
        }
        return GsonHelper.GSON.toJson(fileNames);
	}
	
	public String get(String file) throws IOException {
		String logsFolder = getLogsLocation();
		Path path = Paths.get(logsFolder, file);
		FileInputStream input = null;
		try {
			input = new FileInputStream(path.toFile());
			String content = new String(IOUtils.toByteArray(input), StandardCharsets.UTF_8);
			return content;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	private String getLogsLocation() {
		String logsFolder = Configuration.get(DIRIGIBLE_OPERATIONS_LOGS_ROOT_FOLDER_DEFAULT);
		if (logsFolder != null && !logsFolder.equals("")) {
			return logsFolder;
		}
		logsFolder = Configuration.get(CATALINA_BASE);
		if (logsFolder != null && !logsFolder.equals("")) {
			return logsFolder + File.separator + DEFAULT_LOGS_FOLDER;
		}
		logsFolder = Configuration.get(CATALINA_HOME);
		if (logsFolder != null && !logsFolder.equals("")) {
			return logsFolder + File.separator + DEFAULT_LOGS_FOLDER;
		}
		return DEFAULT_LOGS_LOCATION;
	}
	
	public Object listLoggers() {
		List<LogInfo> result = new ArrayList<LogInfo>();
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggers = loggerContext.getLoggerList();
        for (Logger logger : loggers) {
        	LogInfo logInfo = new LogInfo(logger.getName(), logger.getLevel() == null ? "-" : logger.getLevel().toString());
        	result.add(logInfo);
        }
        return result;
	}

	public Object getSeverity(String loggerName) {
		if (LoggerFactory.getLogger(loggerName).isTraceEnabled()) {
			return Level.TRACE.toString();
		} else if (LoggerFactory.getLogger(loggerName).isDebugEnabled()) {
			return Level.DEBUG.toString();
		} else if (LoggerFactory.getLogger(loggerName).isWarnEnabled()) {
			return Level.WARN.toString();
		} else if (LoggerFactory.getLogger(loggerName).isInfoEnabled()) {
			return Level.INFO.toString();
		} else if (LoggerFactory.getLogger(loggerName).isErrorEnabled()) {
			return Level.ERROR.toString();
		}
		
		return "Unknown logger: " + loggerName;
	}
	
	public Object setSeverity(String loggerName, String logLevel) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(loggerName);
        logger.setLevel(Level.toLevel(logLevel));
        return getSeverity(loggerName);
	}

}
