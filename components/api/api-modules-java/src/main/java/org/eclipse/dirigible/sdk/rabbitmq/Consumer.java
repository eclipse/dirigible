/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.rabbitmq;

import org.eclipse.dirigible.components.api.rabbitmq.RabbitMQFacade;

/**
 * Subscribes a handler to a RabbitMQ queue: a Java {@link MessageHandler}, or the registry path of
 * a TS/JS script (matching the platform listener convention). The platform spawns a consumer
 * thread, deserialises message bodies as strings, and invokes the handler per message.
 * <p>
 * For per-message ack control or for message inspection beyond the body, drop down to the bare
 * RabbitMQ client through a {@code @Component} bean and a {@code .rabbitmq} configuration.
 */
public final class Consumer {

    private Consumer() {}

    public static void startListening(String queue, String handler) {
        RabbitMQFacade.startListening(queue, handler);
    }

    public static void stopListening(String queue, String handler) {
        RabbitMQFacade.stopListening(queue, handler);
    }

    /**
     * Starts consuming the queue with a Java handler - no script engine is involved. A queue and
     * handler pair is consumed once; starting it again is ignored.
     *
     * @param queue the queue, declared when it does not exist
     * @param handler the handler of its messages
     */
    public static void startListening(String queue, MessageHandler handler) {
        RabbitMQFacade.startListening(queue, handler);
    }

    /**
     * Stops consuming the queue with a Java handler started before - the same instance.
     *
     * @param queue the queue
     * @param handler the handler the consumer was started with
     */
    public static void stopListening(String queue, MessageHandler handler) {
        RabbitMQFacade.stopListening(queue, handler);
    }
}
