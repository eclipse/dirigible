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

import java.util.Objects;

class ListenerDescriptor {
    private final ListenerType type;
    private final String destination;
    private final String handlerPath;

    ListenerDescriptor(ListenerType type, String destination, String handlerPath) {
        this.type = type;
        this.destination = destination;
        this.handlerPath = handlerPath;
    }

    ListenerType getType() {
        return type;
    }

    String getDestination() {
        return destination;
    }

    String getHandlerPath() {
        return handlerPath;
    }

    @Override
    public String toString() {
        return "Listener{" + "type=" + type + ", destination='" + destination + '\'' + ", handlerPath='" + handlerPath + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ListenerDescriptor listenerDescriptor = (ListenerDescriptor) o;
        return type == listenerDescriptor.type && Objects.equals(destination, listenerDescriptor.destination)
                && Objects.equals(handlerPath, listenerDescriptor.handlerPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, destination, handlerPath);
    }
}
