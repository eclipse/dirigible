package org.eclipse.dirigible.components.listeners.domain;

import javax.persistence.AttributeConverter;

public class ListenerKindConverter implements AttributeConverter<ListenerKind, Character> {

    @Override
    public Character convertToDatabaseColumn(ListenerKind from) {
        return from.asChar();
    }

    @Override
    public ListenerKind convertToEntityAttribute(Character to) {
        if (to == null) {
            throw new IllegalArgumentException("Listener kind char cannot be null");
        }
        return ListenerKind.fromChar(to);
    }
}
