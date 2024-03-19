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
package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.components.listeners.domain.ListenerKind;
import org.springframework.stereotype.Component;

@Component
class ListenerCreator {

    private final DestinationNameManager destinationNameManager;

    ListenerCreator(DestinationNameManager destinationNameManager) {
        this.destinationNameManager = destinationNameManager;
    }

    ListenerDescriptor fromEntity(org.eclipse.dirigible.components.listeners.domain.Listener entity) {
        ListenerType type = fromEntityType(entity.getKind());

        String destination = destinationNameManager.toTenantName(entity.getName());
        return new ListenerDescriptor(type, destination, entity.getHandler());
    }

    private ListenerType fromEntityType(ListenerKind kind) {
        if (null == kind) {
            throw new IllegalArgumentException("Listener kind cannot be null");
        }
        return switch (kind) {
            case QUEUE -> ListenerType.QUEUE;
            case TOPIC -> ListenerType.TOPIC;
            default -> throw new IllegalArgumentException("Unsupported listener kind: " + kind);
        };
    }
}
