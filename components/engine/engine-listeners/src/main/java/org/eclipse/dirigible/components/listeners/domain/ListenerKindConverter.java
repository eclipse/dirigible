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
package org.eclipse.dirigible.components.listeners.domain;

import javax.persistence.AttributeConverter;

public class ListenerKindConverter implements AttributeConverter<ListenerKind, Character> {

    private static final char QUEUE_CHAR = 'Q';
    private static final char TOPIC_CHAR = 'T';

    @Override
    public Character convertToDatabaseColumn(ListenerKind from) {
        if (ListenerKind.QUEUE.equals(from)) {
            return QUEUE_CHAR;
        }
        if (ListenerKind.TOPIC.equals(from)) {
            return TOPIC_CHAR;
        }
        throw new IllegalArgumentException("Unsupported listener kind: " + from);
    }

    @Override
    public ListenerKind convertToEntityAttribute(Character to) {
        if (to == null) {
            throw new IllegalArgumentException("Listener kind char cannot be null");
        }

        char uppercased = Character.toUpperCase(to);
        if (uppercased == QUEUE_CHAR) {
            return ListenerKind.QUEUE;
        }
        if (uppercased == TOPIC_CHAR) {
            return ListenerKind.TOPIC;
        }
        throw new IllegalArgumentException("Unsupported listener kind: " + to);
    }
}
