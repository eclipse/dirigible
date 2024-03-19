package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.listeners.domain.ListenerKind;

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

    static Listener fromEntity(org.eclipse.dirigible.components.listeners.domain.Listener entity) {
        ListenerType type = fromEntityType(entity.getKind());

        DestinationNameManager destinationNameManager = BeanProvider.getBean(DestinationNameManager.class);
        String destination = destinationNameManager.toTenantName(entity.getName());
        return new Listener(type, destination, entity.getHandler());
    }

    private static ListenerType fromEntityType(ListenerKind kind) {
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
