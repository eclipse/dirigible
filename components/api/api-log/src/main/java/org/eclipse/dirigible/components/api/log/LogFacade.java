/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.log;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.ArrayType;
import tools.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * The Class LogFacade.
 */
@Component
public class LogFacade {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFacade.class);

    private static final String APP_LOGGER_NAME_PREFIX = "app";

    /** The Constant APP_LOGGER_NAME_SEPARATOR. */
    private static final String APP_LOGGER_NAME_SEPARATOR = ".";

    /** The Constant om. */
    private static final ObjectMapper om = new ObjectMapper();

    /** The Constant objectArrayType. */
    // Jackson 3 dropped TypeFactory.defaultInstance(); a type factory comes from the mapper that will
    // use it, which is also the one whose configuration the type should reflect.
    private static final ArrayType objectArrayType = om.getTypeFactory()
                                                       .constructArrayType(Object.class);

    /** The extractors of the errors scripts hand to the log, one per script engine deployed. */
    private static final List<ScriptErrorExtractor> SCRIPT_ERROR_EXTRACTORS =
            ServiceLoader.load(ScriptErrorExtractor.class, LogFacade.class.getClassLoader())
                         .stream()
                         .map(ServiceLoader.Provider::get)
                         .toList();

    /**
     * Sets the logging level.
     *
     * @param loggerName the logger name
     * @param level the level
     */
    public static void setLevel(String loggerName, String level) {

        final org.slf4j.Logger logger = getLogger(loggerName);

        if (!(logger instanceof ch.qos.logback.classic.Logger logbackLogger)) {
            LOGGER.error("Logger with name {} is not of type {}", loggerName, ch.qos.logback.classic.Logger.class.getName());
            return;
        }

        logbackLogger.setLevel(ch.qos.logback.classic.Level.valueOf(level));
    }

    /**
     * Gets the logger.
     *
     * @param loggerName the logger name
     * @return Logger
     */
    public static Logger getLogger(final String loggerName) {
        /*
         * logger names are implicitly prefixed with 'app.' to derive from the applications root logger
         * configuration for severity and appenders. Null arguments for logger name will be treated as
         * reference to the 'app' logger
         */
        String appLoggerName = loggerName;
        if (appLoggerName == null) {
            appLoggerName = APP_LOGGER_NAME_PREFIX;
        } else {
            appLoggerName = APP_LOGGER_NAME_PREFIX + APP_LOGGER_NAME_SEPARATOR + appLoggerName;
        }

        return LoggerFactory.getLogger(appLoggerName);
    }

    public static boolean isDebugEnabled(String loggerName) throws IOException {
        Logger logger = getLogger(loggerName);
        return logger.isDebugEnabled();
    }

    public static boolean isErrorEnabled(String loggerName) throws IOException {
        Logger logger = getLogger(loggerName);
        return logger.isErrorEnabled();
    }

    public static boolean isWarnEnabled(String loggerName) throws IOException {
        Logger logger = getLogger(loggerName);
        return logger.isWarnEnabled();
    }

    public static boolean isInfoEnabled(String loggerName) throws IOException {
        Logger logger = getLogger(loggerName);
        return logger.isInfoEnabled();
    }

    public static boolean isTraceEnabled(String loggerName) throws IOException {
        Logger logger = getLogger(loggerName);
        return logger.isTraceEnabled();
    }

    /**
     * Log.
     *
     * @param loggerName the logger name
     * @param level the level
     * @param message the message
     * @param logArguments the log arguments
     * @param rawError the error json
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void log(String loggerName, String level, String message, String logArguments, Object rawError) throws IOException {

        final Logger logger = getLogger(loggerName);

        if (ch.qos.logback.classic.Level.DEBUG.toString()
                                              .equalsIgnoreCase(level)
                && logger.isDebugEnabled()) {
            Object[] args = getMessageArgs(loggerName, level, message, logArguments, rawError);
            logger.debug(message, args);
        } else if (ch.qos.logback.classic.Level.TRACE.toString()
                                                     .equalsIgnoreCase(level)
                && logger.isTraceEnabled()) {
            Object[] args = getMessageArgs(loggerName, level, message, logArguments, rawError);
            logger.trace(message, args);
        } else if (ch.qos.logback.classic.Level.INFO.toString()
                                                    .equalsIgnoreCase(level)
                && logger.isInfoEnabled()) {
            Object[] args = getMessageArgs(loggerName, level, message, logArguments, rawError);
            logger.info(message, args);
        } else if (ch.qos.logback.classic.Level.WARN.toString()
                                                    .equalsIgnoreCase(level)
                && logger.isWarnEnabled()) {
            Object[] args = getMessageArgs(loggerName, level, message, logArguments, rawError);
            logger.warn(message, args);
        } else if (ch.qos.logback.classic.Level.ERROR.toString()
                                                     .equalsIgnoreCase(level)
                && logger.isErrorEnabled()) {
            Object[] args = getMessageArgs(loggerName, level, message, logArguments, rawError);
            logger.error(message, args);
        } else {
            LOGGER.debug("Logging using logger [{}] for message [{}] will be skipped. Log level [{}].", loggerName, message, level);
        }
    }

    private static Object[] getMessageArgs(String loggerName, String level, String message, String logArguments, Object rawError) {
        Object[] args = null;
        if (logArguments != null) {
            try {
                args = om.readValue(logArguments, objectArrayType);
                if (args.length < 1) {
                    args = null;
                }
            } catch (JacksonException e) {
                // Jackson 3 exceptions are unchecked, so readValue no longer declares IOException;
                // JacksonException is the successor and keeps the behaviour - unparseable arguments
                // are logged and the message is still written without them.
                LOGGER.error("Cannot parse log arguments for logger [{}] for message [{}] at level [{}]", loggerName, message, level, e);
            }
        }

        Throwable error = extractError(rawError);
        if (error != null) {
            if (args == null) {
                args = new Object[] {error};
            } else {
                args = Arrays.copyOf(args, args.length + 1);
                args[args.length - 1] = error;
            }
        }

        return args;
    }

    private static Throwable extractError(Object rawError) {
        if (null == rawError) {
            return null;
        }

        // in case the error is thrown in a java code (called by script code)
        if (rawError instanceof Throwable throwable) {
            return throwable;
        }

        // in case the error is thrown in a script code
        for (ScriptErrorExtractor extractor : SCRIPT_ERROR_EXTRACTORS) {
            Optional<Throwable> error = extractor.extract(rawError);
            if (error.isPresent()) {
                return error.get();
            }
        }

        LOGGER.warn("An error cannot be extracted from [{}] of class [{}]", rawError, rawError.getClass());

        return null;
    }

}
