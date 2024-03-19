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
