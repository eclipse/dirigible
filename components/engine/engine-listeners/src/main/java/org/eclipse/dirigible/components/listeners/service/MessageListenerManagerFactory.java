package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageListenerManagerFactory {

    private final JavascriptService javascriptService;

    @Autowired
    MessageListenerManagerFactory(JavascriptService javascriptService) {
        this.javascriptService = javascriptService;
    }

    public MessageListenerManager create(Listener listener) {
        return new MessageListenerManager(listener, javascriptService);
    }
}
