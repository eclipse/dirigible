package org.eclipse.dirigible.components.listeners.domain;

import com.google.gson.annotations.SerializedName;

public enum ListenerKind {

    @SerializedName(value = "queue", alternate = {"q", "Q"})
    QUEUE, @SerializedName(value = "topic", alternate = {"t", "T"})
    TOPIC
}
