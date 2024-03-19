package org.eclipse.dirigible.components.listeners.service;

import java.util.Objects;

class Listener {
    private final ListenerType type;
    private final String destination;
    private final String handlerPath;

    Listener(ListenerType type, String destination, String handlerPath) {
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
        Listener listener = (Listener) o;
        return type == listener.type && Objects.equals(destination, listener.destination)
                && Objects.equals(handlerPath, listener.handlerPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, destination, handlerPath);
    }
}
