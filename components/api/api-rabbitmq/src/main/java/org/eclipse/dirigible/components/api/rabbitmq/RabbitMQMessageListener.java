/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.rabbitmq;

/**
 * The Java handler of a RabbitMQ consumer - the counterpart of the handler script a scripting
 * consumer names. It receives the body of every message on the queue it listens to.
 *
 * <p>
 * The callbacks run on the consumer's delivery thread, one message at a time. A message is
 * acknowledged on delivery, so one whose handling fails is not redelivered.
 */
public interface RabbitMQMessageListener {

    /**
     * Fires for every message on the queue. When it throws, {@link #onError(String)} is told why.
     *
     * @param message the body of the message, as UTF-8 text
     */
    void onMessage(String message);

    /**
     * Fires when handling a message failed. An exception it throws in turn escapes to the RabbitMQ
     * client, which closes the consumer's channel - as it does for a handler script.
     *
     * @param error what went wrong
     */
    default void onError(String error) {
        // intentional no-op default
    }
}
