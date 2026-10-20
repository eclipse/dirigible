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

import org.eclipse.dirigible.components.base.http.request.RequestIds;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

/**
 * Keeps the log lines of HTTP requests in the {@link RequestLogBuffer} (#7948). A line written
 * outside a request - one with no {@value RequestIds#MDC_KEY} in its MDC - is ignored.
 *
 * <p>
 * Configured in {@code logback.xml}; {@code retentionMinutes} and {@code capacity} bound the
 * buffer. Being called by the logging system, it must never log itself.
 */
public class RequestLogAppender extends AppenderBase<ILoggingEvent> {

    /** How many stack frames of a throwable a line keeps. */
    private static final int STACK_FRAMES = 8;

    private long retentionMinutes = RequestLogBuffer.DEFAULT_RETENTION.toMinutes();

    private int capacity = RequestLogBuffer.DEFAULT_CAPACITY;

    /**
     * Sets how long a line is kept.
     *
     * @param retentionMinutes the minutes
     */
    public void setRetentionMinutes(long retentionMinutes) {
        this.retentionMinutes = retentionMinutes;
    }

    /**
     * Sets how many lines are kept.
     *
     * @param capacity the count
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void start() {
        RequestLogBuffer.get()
                        .configure(Duration.ofMinutes(retentionMinutes), capacity);
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        String requestId = event.getMDCPropertyMap()
                                .get(RequestIds.MDC_KEY);
        if (requestId == null) {
            return;
        }
        RequestLogBuffer.get()
                        .add(new RequestLogBuffer.Line(Instant.ofEpochMilli(event.getTimeStamp()), requestId, currentTenantId(),
                                currentPrincipal(), event.getLevel()
                                                         .toString(),
                                event.getLoggerName(), message(event)));
    }

    private static String message(ILoggingEvent event) {
        IThrowableProxy throwable = event.getThrowableProxy();
        if (throwable == null) {
            return event.getFormattedMessage();
        }
        StringBuilder message = new StringBuilder(event.getFormattedMessage());
        for (IThrowableProxy cause = throwable; cause != null; cause = cause.getCause()) {
            message.append(cause == throwable ? "\n" : "\nCaused by: ")
                   .append(cause.getClassName())
                   .append(": ")
                   .append(cause.getMessage());
            StackTraceElementProxy[] frames = cause.getStackTraceElementProxyArray();
            for (int i = 0; frames != null && i < Math.min(STACK_FRAMES, frames.length); i++) {
                message.append("\n\tat ")
                       .append(frames[i].getStackTraceElement());
            }
        }
        return message.toString();
    }

    private static String currentTenantId() {
        if (!BeanProvider.isInitialzed()) {
            return null;
        }
        try {
            TenantContext tenantContext = BeanProvider.getTenantContext();
            return tenantContext.isInitialized() ? tenantContext.getCurrentTenant()
                                                                .getId()
                    : null;
        } catch (RuntimeException closingContext) {
            // A context being closed has no tenant to give. Logging this would re-enter the appender, and a
            // line attributed to no tenant is simply never served to anybody.
            return null;
        }
    }

    private static String currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        return authentication == null ? null : authentication.getName();
    }
}
