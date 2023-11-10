package org.eclipse.dirigible.components.listeners.domain;

import javax.persistence.AttributeConverter;

public class ListenerKindConverter implements AttributeConverter<ListenerKind, Character> {

    private static final char QUEUE_CHAR = 'Q';
    private static final char TOPIC_CHAR = 'T';

    @Override
    public Character convertToDatabaseColumn(ListenerKind from) {
        if (ListenerKind.QUEUE.equals(from)) {
            return QUEUE_CHAR;
        } else if (ListenerKind.TOPIC.equals(from)) {
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
        } else if (uppercased == TOPIC_CHAR) {
            return ListenerKind.TOPIC;
        }
        throw new IllegalArgumentException("Unsupported listener kind: " + to);
    }
}
