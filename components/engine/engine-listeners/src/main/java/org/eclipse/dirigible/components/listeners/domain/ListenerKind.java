package org.eclipse.dirigible.components.listeners.domain;

import com.google.gson.annotations.SerializedName;

import javax.persistence.AttributeConverter;

public enum ListenerKind {

    @SerializedName(value = "queue", alternate = {"q", "Q"})
    QUEUE('Q'), @SerializedName(value = "topic", alternate = {"t", "T"})
    TOPIC('T');

    private final char asChar;

    ListenerKind(char asChar) {
        this.asChar = Character.toUpperCase(asChar);
    }

    public char asChar() {
        return asChar;
    }

    public static ListenerKind fromChar(char ch) {
        var uppercased = Character.toUpperCase(ch);
        if (uppercased == 'Q') {
            return ListenerKind.QUEUE;
        } else if (uppercased == 'T') {
            return ListenerKind.TOPIC;
        }
        throw new IllegalArgumentException("Unsupported listener kind: " + ch);
    }


}
