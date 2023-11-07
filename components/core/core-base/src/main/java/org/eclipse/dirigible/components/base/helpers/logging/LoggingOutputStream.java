/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.helpers.logging;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;

public class LoggingOutputStream extends OutputStream {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
    private final Logger logger;
    private final LogLevel level;

    public enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR,
    }

    public LoggingOutputStream(Logger logger, LogLevel level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void write(int b) {
        if (b == '\n') {
            String line = baos.toString();
            baos.reset();

            switch (level) {
                case TRACE:
                    logger.trace(line);
                    break;
                case DEBUG:
                    logger.debug(line);
                    break;
                case ERROR:
                    logger.error(line);
                    break;
                case INFO:
                    logger.info(line);
                    break;
                case WARN:
                    logger.warn(line);
                    break;
            }
        } else {
            baos.write(b);
        }
    }

}
