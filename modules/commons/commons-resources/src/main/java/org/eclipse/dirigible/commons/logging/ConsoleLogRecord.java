/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.logging;

/**
 * The Console Log Record transport object.
 */
public class ConsoleLogRecord {

    /** The level. */
    private final String level;

    /** The message. */
    private final String message;

    /** The timestamp. */
    private final long timestamp;

    /** The id of the HTTP request that wrote the line, {@code null} outside a request (#7948). */
    private final String requestId;

    /**
     * Instantiates a new console log record.
     *
     * @param level the level
     * @param message the message
     * @param timestamp the timestamp
     * @param requestId the id of the HTTP request that wrote the line, {@code null} outside a request
     */
    public ConsoleLogRecord(String level, String message, long timestamp, String requestId) {
        super();
        this.level = level;
        this.message = message;
        this.timestamp = timestamp;
        this.requestId = requestId;
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    public String getLevel() {
        return level;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the id of the HTTP request that wrote the line.
     *
     * @return the request id, {@code null} outside a request
     */
    public String getRequestId() {
        return requestId;
    }

}
