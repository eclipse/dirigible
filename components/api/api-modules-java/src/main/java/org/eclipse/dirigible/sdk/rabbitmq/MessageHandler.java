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

import org.eclipse.dirigible.components.api.rabbitmq.RabbitMQMessageListener;

/**
 * Java handler of a RabbitMQ consumer started with
 * {@link Consumer#startListening(String, MessageHandler)} - the Java counterpart of the handler
 * script the scripting API names. It receives the body of every message on the queue in
 * {@link #onMessage(String)}, and the failure of handling one in {@link #onError(String)}, which
 * defaults to a no-op.
 *
 * <p>
 * Unlike {@link org.eclipse.dirigible.sdk.messaging.MessageHandler}, which binds itself to a queue
 * or topic of the platform's broker, this handler is started and stopped explicitly - a listener is
 * told apart by its identity, so stop it with the instance it was started with:
 *
 * <pre>{@code
 * MessageHandler orders = message -> log.info("order {}", message);
 * Consumer.startListening("orders", orders);
 * ...
 * Consumer.stopListening("orders", orders);
 * }</pre>
 */
public interface MessageHandler extends RabbitMQMessageListener {
}
