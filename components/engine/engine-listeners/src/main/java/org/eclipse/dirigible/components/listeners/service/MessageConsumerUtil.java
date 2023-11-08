package org.eclipse.dirigible.components.listeners.service;

import java.util.HashMap;
import java.util.Map;

final class MessageConsumerUtil {

    private MessageConsumerUtil() {}

    static Map<Object, Object> createMessagingContext(String handler) {
        Map<Object, Object> context = new HashMap<>();
        context.put("handler", handler);
        return context;
    }

    static String escapeCodeString(String raw) {
        return raw.replace("'", "&amp;");
    }
}
