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
package org.eclipse.dirigible.components.engine.camel.invoke;

import org.apache.camel.Message;

import java.util.Map;

public class IntegrationMessage {
    private final Message message;

    public IntegrationMessage(Message message) {
        this.message = message;
    }

    public String getBodyAsString() {
        return message.getBody(String.class);
    }

    public Object getBody() {
        return message.getBody();
    }

    public void setBody(Object body) {
        message.setBody(body);
    }

    public Map<String, Object> getHeaders() {
        return message.getHeaders();
    }

    public void setHeaders(Map<String, Object> headers) {
        message.setHeaders(headers);
    }

    public void setHeader(String key, Object value) {
        message.setHeader(key, value);
    }

    public Message getCamelMessage() {
        return message;
    }
}
