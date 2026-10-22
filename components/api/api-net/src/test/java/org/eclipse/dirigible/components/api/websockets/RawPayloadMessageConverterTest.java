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

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

/**
 * A frame's body is handed over whatever content type it declares, and nothing is converted
 * outbound - a payload sent by a listener's client is converted as a scripting client's is.
 */
class RawPayloadMessageConverterTest {

    private final RawPayloadMessageConverter converter = new RawPayloadMessageConverter();

    @Test
    void theBodyIsHandedOverWhateverItsContentType() {
        byte[] body = "{\"text\":\"hello\"}".getBytes(StandardCharsets.UTF_8);
        Message<byte[]> json = MessageBuilder.withPayload(body)
                                             .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                                             .build();

        assertThat(converter.fromMessage(json, byte[].class)).isSameAs(body);
        assertThat(converter.fromMessage(json, String.class)).isNull();
    }

    @Test
    void nothingIsConvertedOutbound() {
        assertThat(converter.toMessage("hello", new MessageHeaders(Map.of()))).isNull();
    }
}
