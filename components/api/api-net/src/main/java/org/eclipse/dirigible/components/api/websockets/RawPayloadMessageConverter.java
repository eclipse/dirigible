/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.websockets;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;

/**
 * Hands the body of an inbound frame over as the bytes that arrived, whatever content type it
 * declares, for a handler asking for {@code byte[]} - a {@link WebsocketClientListener} receives
 * every message as text, not only the ones a JSON converter could read.
 *
 * <p>
 * It converts nothing outbound, so a payload sent on the session reaches the converter after it,
 * exactly as it does on a scripting client.
 */
final class RawPayloadMessageConverter implements MessageConverter {

    @Override
    public Object fromMessage(Message<?> message, Class<?> targetClass) {
        return targetClass == byte[].class && message.getPayload() instanceof byte[] bytes ? bytes : null;
    }

    @Override
    public Message<?> toMessage(Object payload, MessageHeaders headers) {
        return null;
    }
}
