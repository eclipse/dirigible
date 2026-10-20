/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.logging;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The last minutes of log lines written while serving a request, indexed by its request id (#7948).
 *
 * <p>
 * {@link RequestLogAppender} fills it; an issue report reads the excerpt of its own request ids
 * from it. Every line records the tenant and the principal of the request that wrote it, and a read
 * returns only the lines of the asking tenant and principal, so a request id quoted by somebody
 * else reveals nothing. The buffer is bounded twice: by age and by count, whichever is reached
 * first.
 */
public final class RequestLogBuffer {

    /** How long a line is kept by default. */
    static final Duration DEFAULT_RETENTION = Duration.ofMinutes(10);

    /** How many lines are kept by default. */
    static final int DEFAULT_CAPACITY = 10_000;

    private static final RequestLogBuffer INSTANCE = new RequestLogBuffer();

    private final Deque<Line> lines = new ArrayDeque<>();

    private Duration retention = DEFAULT_RETENTION;

    private int capacity = DEFAULT_CAPACITY;

    RequestLogBuffer() {}

    /**
     * The buffer the logging configuration writes into.
     *
     * @return the buffer
     */
    public static RequestLogBuffer get() {
        return INSTANCE;
    }

    /**
     * Bounds the buffer.
     *
     * @param retention how long a line is kept
     * @param capacity how many lines are kept
     */
    synchronized void configure(Duration retention, int capacity) {
        this.retention = retention;
        this.capacity = capacity;
        evict(Instant.now());
    }

    /**
     * Records a line.
     *
     * @param line the line
     */
    synchronized void add(Line line) {
        lines.addLast(line);
        evict(line.at());
    }

    /**
     * The lines a principal's requests wrote in a tenant, oldest first.
     *
     * @param requestIds the requests
     * @param tenantId the tenant the lines must have been written in
     * @param principal the principal the requests must have been served for
     * @return the lines
     */
    public synchronized List<Line> excerpt(Collection<String> requestIds, String tenantId, String principal) {
        evict(Instant.now());
        Set<String> wanted = Set.copyOf(requestIds);
        return lines.stream()
                    .filter(line -> wanted.contains(line.requestId()))
                    .filter(line -> Objects.equals(tenantId, line.tenantId()) && Objects.equals(principal, line.principal()))
                    .toList();
    }

    private void evict(Instant now) {
        Instant oldest = now.minus(retention);
        while (!lines.isEmpty() && (lines.size() > capacity || lines.peekFirst()
                                                                    .at()
                                                                    .isBefore(oldest))) {
            lines.removeFirst();
        }
    }

    /**
     * One log line written while serving a request.
     *
     * @param at when
     * @param requestId the request
     * @param tenantId the tenant the request ran in, {@code null} before it was resolved
     * @param principal who the request was served for, {@code null} before authentication
     * @param level the level
     * @param logger the logger
     * @param message the formatted message, with the throwable's summary when there was one
     */
    public record Line(Instant at, String requestId, String tenantId, String principal, String level, String logger, String message) {
    }
}
